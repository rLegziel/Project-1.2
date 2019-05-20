package model;

import javafx.scene.paint.Color;

import java.util.Random;

public class Lander extends Body {

    private double scalingFactor;
    private double titanDistance = 250000;

    private double canvasHeight;
    private double canvasWidth;

    private Vector3D realLandingLocation;
    private Vector3D scaledLandingLocation;

    private double windSpeed;
    private boolean windDirection; // if true, wind direction is west(will go positive), if negative- wind direction will go east(negative x values)


    public double dispLocX;
    public double dispLocY;

    //PID controllers to control the speed and landing speed
    private PIDController landingPositionX;
    private PIDController landingPositionY;
    private PIDController decelerateX;
    private PIDController decelerateY;




    //not sure if this correct
    private double timeSlice = 0.5;



    private double terminalVelocity = 6.5;

    public Lander(String name, double mass, double radius, Vector3D location, Vector3D velocity, Color color, double scalingFactor, double canvasWidth,double canvasHeight) {
        super(name, mass, radius, location, velocity, color);
        this.scalingFactor = scalingFactor;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        dispLocX = canvasWidth/2;
        dispLocY = 1;
        //dispLocX = 1;
        //dispLocY = canvasHeight/2;
        scaledLandingLocation = new Vector3D(canvasWidth/2,canvasHeight,0);
        realLandingLocation = location.add(scaledLandingLocation);


        double angleLander = Math.atan(dispLocY/dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y/scaledLandingLocation.x);
        System.out.println((angleLander-angleTitan)*(180/Math.PI));

        //you can adjust the k here for every controller
        landingPositionX = new PIDController(timeSlice,0.0000001,0,0);
        landingPositionY = new PIDController(timeSlice,100,0,0);
        decelerateX = new PIDController(timeSlice,-1,0,0);
        decelerateY = new PIDController(timeSlice,0.5,0,0);
    }

    public Lander(Vector3D location, Vector3D velocity, double radius, double mass, Color color, double scalingFactor) {
        super(location, velocity, radius, mass, color);
        this.scalingFactor = scalingFactor;
        realLandingLocation = location.add(new Vector3D(500*scalingFactor,700*scalingFactor,0));
    }

    public double getScalingFactor() {
        return scalingFactor;
    }

    public double getTitanDistance() {
        return titanDistance;
    }

    public double getWindSpeed(){
        return windSpeed;
    }

    public void applyGravity() {
        if(velocity.y < terminalVelocity){
            double tempY = location.y;
            location.y -= (0.5*gTitan*(0.1*0.1))+velocity.y;
            velocity.y = velocity.y + gTitan;
            double delta = (tempY - location.y);
            dispLocY += delta/scalingFactor;
            titanDistance -= delta;
        }else{
            double tempY = location.y;
            location.y -= (0.5*gTitan*(0.1*0.1))+velocity.y;
            //only difference is velocity stays constant

            double delta = (tempY - location.y);
            dispLocY += delta/scalingFactor;
            titanDistance -= delta;
        }

        //0.5*gravity*(fallingTime*fallingTime)) + (initialVelocity*fallingTime) + initialPosition

        //System.out.println(location.y + " " + dispLocY);
    }



    public void applyWind(){
        windSpeed = calculateWindSpeed(titanDistance);
        location.x = location.x + (windSpeed);
        velocity.x = velocity.x + windSpeed;
        dispLocX += windSpeed/scalingFactor;

        System.out.println(windSpeed);
    }

    public double calculateWindSpeed(double height){
        double wind;
        int highAltWind = 120000;
        int medAltWind = highAltWind-(60000);
        int lowAltWind = 5556;
        int groundLevelWind = 19444;
        Random rand = new Random();
        Random ra = new Random();
        int windDir = ra.nextInt(3) + 0;
        if (height<150000&& height>60000){
            wind = rand.nextInt(highAltWind+22222) + (highAltWind*0.8);
        }else if(height<60000 & height>20000){
            wind = rand.nextInt(medAltWind) + (medAltWind*0.8);
        }else if(height < 20000 && height>10000){
            wind = rand.nextInt(lowAltWind) + lowAltWind*0.5;
        } else{
            wind = rand.nextInt(groundLevelWind) +groundLevelWind*0.1;
        }
        wind = wind*0.00044704; // to convert to m/ms
        if (windDir == 1){
            windDirection = false;
            wind = wind*(0-1);
        } else{
            windDirection = true;

        }
        windSpeed = wind;
        return wind;
    }
    /**
     * uses the physics system and PID controller
     * @param thrusterForce the force from the thrusters calculated by the PID
     */
    public void calculateAccelerationLanding(Vector3D thrusterForce){
        resetAcceleration();
        double angleLander = Math.atan(dispLocY/dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y/scaledLandingLocation.x);
        //System.out.println((angleLander-angleTitan)*(180/Math.PI));
        double angle = angleLander-angleTitan;
        double wind = calculateWindSpeed(titanDistance);
        double xacc = 0;
        xacc = (thrusterForce.x*Math.sin(angle)) + wind;
        double yacc = (thrusterForce.y * Math.cos(angle)) - gTitan;

        //makes sure velocity doesnt pass terminal, when yacc is bigger than 0 velocity will decrease
        if (velocity.y<terminalVelocity || yacc > 0) {
            yacc = (thrusterForce.y * Math.cos(angle)) - gTitan;
        } else{
            yacc = 0;
        }

        Vector3D accelerationForce = new Vector3D(xacc,yacc,0);
        addAccelerationByForce(accelerationForce);
        Vector3D delta = updateVelocityAndLocation(timeSlice);

        titanDistance += delta.y;

        //System.out.println(velocity.x-oldVelocity.x);
        dispLocX -= (delta.x)/scalingFactor;
        dispLocY -= (delta.y)/scalingFactor;


    }

    //the actual force by the thruster calculated by the PID
    public Vector3D thrusterForce() {
        //does the angle need to be 90 or 0?
        //we can have an error of 0.1;
        double angleNeeded = 0.01*(Math.PI/180);



        double angleLander = Math.atan(dispLocY/dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y/scaledLandingLocation.x);
        double angle = angleLander-angleTitan;
        double angleChange = angleNeeded-angle;
        double xneeded = (location.y/Math.tan(angleChange));
        double yneeded = realLandingLocation.y;

        //to make sure it lands in the correct position
        double xAccPosition = landingPositionX.compute(location.x,xneeded);
        double yAccPosition = landingPositionY.compute(location.y,yneeded);

        //to make sure the landing velocity is 0;
        double xAccVelocity = decelerateX.compute(velocity.x,0);
        double yAccVelocity = decelerateY.compute(velocity.y,0);

        Vector3D accelerationVector = new Vector3D(xAccPosition,yAccPosition,0);
        //System.out.println(accelerationVector.y);

        Vector3D reduceVelocityVector = new Vector3D(xAccVelocity,yAccVelocity,0);


        Vector3D PID = accelerationVector.add(reduceVelocityVector);
        return PID;
    }




}
