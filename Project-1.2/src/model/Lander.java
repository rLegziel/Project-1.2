package model;

import javafx.scene.paint.Color;

import java.util.Random;

public class Lander extends Body {

    private double scalingFactor;
    private double titanDistance = 250000;

    private Vector3D landingLocation;

    private double windSpeed;



    public double dispLocY = 1;
    public double dispLocX = 500;


    //not sure if this correct
    private double timeSlice = 0.5;

    private PIDController controller = new PIDController(timeSlice);


    private double terminalVelocity = 6.5;

    public Lander(String name, double mass, double radius, Vector3D location, Vector3D velocity, Color color, double scalingFactor) {
        super(name, mass, radius, location, velocity, color);
        this.scalingFactor = scalingFactor;
        landingLocation = location.add(new Vector3D(0,titanDistance,0));
    }

    public Lander(Vector3D location, Vector3D velocity, double radius, double mass, Color color, double scalingFactor) {
        super(location, velocity, radius, mass, color);
        this.scalingFactor = scalingFactor;
        landingLocation = location.add(new Vector3D(0,titanDistance,0));
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
        int highAltWind = 432000;
        int medAltWind = highAltWind-216000;
        int lowAltWind = 20000;
        int groundLevelWind = 70000;
        Random rand = new Random();
        if (height<150000&& height>60000){
            wind = rand.nextInt(highAltWind+80000) + (highAltWind*0.8);
        }else if(height<60000 & height>20000){
            wind = rand.nextInt(medAltWind) + (medAltWind*0.8);
        }else if(height < 20000 && height>10000){
            wind = rand.nextInt(lowAltWind) + lowAltWind*0.5;
        } else{
            wind = rand.nextInt(groundLevelWind) +groundLevelWind*0.1;
        }
        wind = wind*0.00044704; // to convert to m/ms
        windSpeed = wind;
        return wind;
    }

    /**
     * uses the physics system and PID controller
     * @param thrusterForce the force from the thrusters calculated by the PID
     */
    public void calculateAccelerationLanding(Vector3D thrusterForce){
        resetAcceleration();
        double angle = Math.atan(location.y/location.x);
        System.out.println(angle);
        double wind = calculateWindSpeed(titanDistance);
        double xacc = 0;
        xacc = thrusterForce.x*Math.sin(angle) + wind;
        double yacc = thrusterForce.y * Math.cos(angle) - gTitan;

        //makes sure velocity doesnt pass terminal, when yacc is bigger than 0 velocity will decrease
        if (velocity.y<terminalVelocity || yacc > 0) {
            yacc = thrusterForce.y * Math.cos(angle) - gTitan;
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
        double angleNeeded = 90;

            double angle = Math.atan(location.y/location.x);
            System.out.println(angle);
            double angleChange = angle-angleNeeded;
            double xneeded = location.y/Math.tan(angleChange);
            double yneeded = landingLocation.y;

            //to make sure it lands in the correct position
            double xAccPosition = controller.computeNewX(location.x,xneeded);
            double yAccPosition = controller.computeNewY(location.y,yneeded);

            //to make sure the landing velocity is 0;
            double xAccVelocity = controller.computeNewX(velocity.x,0);
            double yAccVelocity = controller.computeNewY(velocity.y,0);

            Vector3D accelerationVector = new Vector3D(xAccPosition,yAccPosition,0);
            accelerationVector = accelerationVector.normalize();

            Vector3D reduceVelocityVector = new Vector3D(xAccVelocity,yAccVelocity,0);
            accelerationVector = accelerationVector.normalize();

            Vector3D PID = accelerationVector.add(reduceVelocityVector);
            PID = accelerationVector;


        return PID;
    }




}
