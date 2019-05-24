package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

public class Lander extends Body {

    private double scalingFactor;
    private double titanDistance = 250000;

    private double canvasHeight;
    private double canvasWidth;
    private int windCounter;

    private Vector3D realLandingLocation;
    private Vector3D scaledLandingLocation;
    private final double realLandingLocationx;

    private double windSpeed;
    private boolean windDirection; // if true, wind direction is west(will go positive), if negative- wind direction will go east(negative x values)

    private double angle;
    public double dispLocX;
    public double dispLocY;

    private ArrayList<Double> PIDinput = new ArrayList<>();
    private ArrayList<Double> PIDoutput = new ArrayList<>();

    //PID controllers to control the speed and landing speed
    private PIDController anglePIDx;
    private PIDController landingPositionX;
    private PIDController decelerateX;
    private PIDController decelerateY;


    //not sure if this correct
    private double timeSlice = 2;


    private double terminalVelocity = 6.5;

    /*
    ending PID controllers for a soft landing:
    landingPositionX = new PIDController(timeSlice, 0.0001, 0, 0.1);
        landingPositionY = new PIDController(timeSlice, 0.0001, 0.1, 1);
        decelerateX = new PIDController(timeSlice, -1, -0.000000001, 0.000000000001);
        decelerateY = new PIDController(timeSlice, 0.5, 0.0000000001, 10); // was 0.5

        TODO:
        1)make the xPID controllers less aggressive to start with, and gradually make them more and more aggressive,
        to do this, can use the current boolean flags in the GUI(changed1,2,3...) to change the xPID values also.
        do this using the method
        Not sure if this is possible, effects rotation verry much.

        2) distance to titan or speed are way off. meaning that even at very slow speed we are still moving very fast in real life.

     */



    public Lander(String name, double mass, double radius, Vector3D location, Vector3D velocity, Color color, double scalingFactor, double canvasWidth, double canvasHeight) {
        super(name, mass, radius, location, velocity, color);
        this.scalingFactor = scalingFactor;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        dispLocX = canvasWidth / 2;
        dispLocY = 1;
        //dispLocX = 1;
        //dispLocY = canvasHeight/2;
        scaledLandingLocation = new Vector3D(canvasWidth/2, canvasHeight-50, 0);
        realLandingLocation = location.add(scaledLandingLocation);
        realLandingLocationx = location.x + scaledLandingLocation.x;


        double angleLander = Math.atan(dispLocY / dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
//        System.out.println((angleLander - angleTitan) * (180 / Math.PI));

        //you can adjust the k here for every controller
        //this is the old x controller but it only changes the angle
        anglePIDx = new PIDController(timeSlice, 0.0001, 0, 0.1);
        //this is the new controller that sets where to land
        landingPositionX = new PIDController(timeSlice,-0.00005,0,0);
        decelerateX = new PIDController(timeSlice, -1, -0.000000001, 0.000000000001);
        decelerateY = new PIDController(timeSlice, 0.5, 0.0000000001, 10); // was 0.5
    }


    public void changeYPID(double timeSlice,double kp,double ki,double kd){
        decelerateY = new PIDController(timeSlice,kp,ki,kd);
    }

    public void changeXLPID(double timeSlice,double kp,double ki,double kd){
        anglePIDx = new PIDController(timeSlice,kp,ki,kd);
    }

    public void changeXDPID(double timeSlice,double kp,double ki,double kd){
        decelerateX = new PIDController(timeSlice,kp,ki,kd);
    }


    public double getScalingFactor() {
        return scalingFactor;
    }

    public double getTitanDistance() {
        return titanDistance;
    }

    public double getWindSpeed() {
        return windSpeed;
    }


    public double calculateWindSpeed(double height,int windCounter) {
        double wind;
        double highAltWind = 120;
        double medAltWind = 60;
        double lowAltWind = 56;
        double groundLevelWind = 2;
        Random rand = new Random();
        Random ra = new Random();
        if (windCounter % 1000== 0) {
            int windDir = ra.nextInt(6) + 0;
            if (height < 210000 && height > 160000) {
                wind = (highAltWind * 0.8) + (highAltWind-(highAltWind * 0.8)) * rand.nextDouble();
                System.out.println("we now in high altitude!");
            } else if (height < 159999 & height > 30000) {
                wind = (medAltWind * 0.8) + (medAltWind-(medAltWind * 0.8)) *rand.nextDouble() ;
                System.out.println("we now in med altitude!");
            } else if (height < 30000 && height > 20000) {
                wind = (lowAltWind * 0.5) + (lowAltWind -(lowAltWind * 0.5) ) *  rand.nextDouble() ;
                System.out.println("we now in low altitude!");
            } else {
                wind =(groundLevelWind * 0.1) + (groundLevelWind-(groundLevelWind * 0.1)) *  rand.nextDouble() ;
                System.out.println("we now in ground altitude!");
            }
            // to convert to m/ms
            if (windDir == 1) {
                windDirection = false;
                wind = wind * (0 - 1);
            } else {
                windDirection = true;

            }
            windSpeed = wind;
            return wind;
        }
        return windSpeed;
    }



    /**
     * uses the physics system and PID controller
     *
     * @param thrusterForce the force from the thrusters calculated by the PID
     */
    public void calculateAccelerationLanding(Vector3D thrusterForce) {
//        System.out.println(velocity.x + " is my xVelocity right now, pre reset.");
        resetAcceleration();
        velocity.x = 0;
//        System.out.println(velocity.x + " is my xVelocity right now, after reset reset.");
        windCounter++;
        double angleLander = Math.atan(dispLocY / dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
        //System.out.println((angleLander-angleTitan)*(180/Math.PI));
        double angle = angleLander - angleTitan;
        double wind = calculateWindSpeed(titanDistance,windCounter);
        System.out.println(" we are now at height: " + titanDistance + " and the wind speed is " + wind);
        double xacc = 0;
        xacc = (thrusterForce.x * Math.sin(angle)) + wind;
//        System.out.println(xacc + " is the xAccelration");
        double yacc = (thrusterForce.y * Math.cos(angle)) - gTitan;

        //makes sure velocity doesnt pass terminal, when yacc is bigger than 0 velocity will decrease
        if (velocity.y < terminalVelocity || yacc > 0) {
            yacc = (thrusterForce.y * Math.cos(angle)) - gTitan;
        } else {
            yacc = 0;
            velocity.y = terminalVelocity;
        }

        Vector3D accelerationForce = new Vector3D(xacc, yacc, 0);
        addAccelerationByForce(accelerationForce);
        Vector3D delta = updateVelocityAndLocation(timeSlice);

        titanDistance += delta.y;

        //System.out.println(velocity.x-oldVelocity.x);
        dispLocX -= (delta.x) / scalingFactor;
        dispLocY -= (delta.y) / scalingFactor;
//        System.out.println(velocity.x + " is my xVelocity right now, end of method reset.");


    }

    //the actual force by the thruster calculated by the PID
    public Vector3D thrusterForce() {
        //does the angle need to be 90 or 0?
        //we can have an error of 0.1;
        double angleNeeded = 0.01 * (Math.PI / 180);


        double angleLander = Math.atan(dispLocY / dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
        angle = angleLander - angleTitan;
        double angleChange = angleNeeded - angle;

        double xAngle = (location.y / Math.tan(angleChange));


        //to make sure it lands in the correct position
        double xAccAngle = anglePIDx.compute(location.x, xAngle);
        PIDinput.add(location.x);
        PIDoutput.add(xAccAngle);
        double xAccPosition = landingPositionX.compute(location.x,realLandingLocationx);
        //System.out.println(xAccPosition);


        //to make sure the landing velocity is 0;
        double xAccVelocity = decelerateX.compute(velocity.x, 0);
        double yAccVelocity = decelerateY.compute(velocity.y, 0);

        Vector3D accelerationVector = new Vector3D(xAccAngle, 0, 0);
        //System.out.println(accelerationVector.y);

        Vector3D reduceVelocityVector = new Vector3D(xAccVelocity, yAccVelocity, 0);


        Vector3D PID = accelerationVector.add(reduceVelocityVector);
        PID.add(new Vector3D(xAccPosition,0,0));
        return PID;
    }

    public double getAngle(){
        //System.out.println(angle*(180/Math.PI));
        return angle*(180/Math.PI);
    }

    public ArrayList<Double> getPIDoutput(){
        return PIDoutput;
    }

    public ArrayList<Double> getPIDinput(){
        return PIDinput;
    }
}




