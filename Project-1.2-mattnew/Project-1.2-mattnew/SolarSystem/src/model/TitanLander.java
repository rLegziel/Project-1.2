package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;

/**
 * class for lander that descends on titan
 */
public class TitanLander extends Body implements Lander {

    //scale  1 pixel = 375 meters
    private double scalingFactor = 375;
    private double titanDistance = 250000;

    private double canvasHeight;
    private double canvasWidth;
    private int windCounter;

    private Vector3D realLandingLocation;
    private Vector3D scaledLandingLocation;
    private Vector3D scaledSpaceshipLocation;
    private Vector3D realSpaceshipLocation;
    private final double realLandingLocationx;

    private Vector3D totalChangeInVelocity;

    private boolean isLanding;

    private double windSpeed;
    private boolean windDirection;// if true, wind direction is west(will go positive), if negative- wind direction will go east(negative x values)



    private double angle;
    public double dispLocX;
    public double dispLocY;

    private ArrayList<Double> PIDinput = new ArrayList<>();
    private ArrayList<Double> PIDoutput = new ArrayList<>();

    //PID controllers to control the speed and landing speed
    private PIDController anglePIDx;
    private PIDController landingPositionX;
    private PIDController spaceshipPositionY;
    private PIDController decelerateX;
    private PIDController decelerateY;


    //not sure if this correct
    private double timeSlice = 2;

    private double terminalVelocity = 6.5;

    private double fuelUsed;
    private double moneySpent;

    /*
    ending PID controllers for a soft landing:
    landingPositionX = new PIDController(timeSlice, 0.0001, 0, 0.1);
        landingPositionY = new PIDController(timeSlice, 0.0001, 0.1, 1);
        decelerateX = new PIDController(timeSlice, -1, -0.000000001, 0.000000000001);
        decelerateY = new PIDController(timeSlice, 0.5, 0.0000000001, 10); // was 0.5
   */

    public TitanLander(String name, double mass, double radius, Vector3D location, Vector3D velocity, Color color, double canvasWidth, double canvasHeight) {
        super(name, mass, radius, location, velocity, color, 1);
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        dispLocX = canvasWidth / 2;
        dispLocY = 1;
        //dispLocX = 1;
        //dispLocY = canvasHeight/2;
        scaledLandingLocation = new Vector3D(canvasWidth/2, canvasHeight-50, 0);
        scaledSpaceshipLocation = new Vector3D(canvasWidth/2, 30, 0);
        realSpaceshipLocation = new Vector3D(location);
        realLandingLocation = location.add(scaledLandingLocation);
        realLandingLocationx = location.x + scaledLandingLocation.x;

        totalChangeInVelocity = new Vector3D();


        isLanding = true;

        double angleLander = Math.atan(dispLocY / dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
//        System.out.println((angleLander - angleTitan) * (180 / Math.PI));

        //you can adjust the k here for every controller
        //this is the old x controller but it only changes the angle
        anglePIDx = new PIDController(timeSlice, 0.0001, 0, 0.1);
        //this is the new controller that sets where to land
        landingPositionX = new PIDController(timeSlice,-75,0,0);
        spaceshipPositionY = new PIDController(timeSlice,0.00001,0,0);
        decelerateX = new PIDController(timeSlice, -1, -0.000000001, 0.000000000001);
        decelerateY = new PIDController(timeSlice, 0.5, 0.0000000001, 10); // was 0.5
    }


    @Override
    public void changeYPID(double timeSlice,double kp,double ki,double kd){
        decelerateY = new PIDController(timeSlice,kp,ki,kd);
    }

    @Override
    public void changeXLPID(double timeSlice,double kp,double ki,double kd){
        anglePIDx = new PIDController(timeSlice,kp,ki,kd);
    }

    @Override
    public void changeXDPID(double timeSlice,double kp,double ki,double kd){
        decelerateX = new PIDController(timeSlice,kp,ki,kd);
    }

    @Override
    public void changeYLocationPID(double timeSlice,double kp,double ki,double kd){
        spaceshipPositionY = new PIDController(timeSlice,kp,ki,kd);
    }

    @Override
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
                //System.out.println("we now in high altitude!");
            } else if (height < 159999 & height > 30000) {
                wind = (medAltWind * 0.8) + (medAltWind-(medAltWind * 0.8)) *rand.nextDouble() ;
                //System.out.println("we now in med altitude!");
            } else if (height < 30000 && height > 20000) {
                wind = (lowAltWind * 0.5) + (lowAltWind -(lowAltWind * 0.5) ) *  rand.nextDouble() ;
                //System.out.println("we now in low altitude!");
            } else {
                wind =(groundLevelWind * 0.1) + (groundLevelWind-(groundLevelWind * 0.1)) *  rand.nextDouble() ;
                //System.out.println("we now in ground altitude!");
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



    @Override
    public void calculateAccelerationLanding(Vector3D thrusterForce) {
//        System.out.println(velocity.x + " is my xVelocity right now, pre reset.");
        resetAcceleration();
        velocity.x = 0;
//        System.out.println(velocity.x + " is my xVelocity right now, after reset reset.");
        windCounter++;
        if (isLanding){
            double angleLander = Math.atan(dispLocY / dispLocX);
            double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
            angle = angleLander - angleTitan;
        } else{
            double angleLander = Math.atan(dispLocY / dispLocX);
            double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
            double angleSpaceship = Math.atan(scaledSpaceshipLocation.y / scaledSpaceshipLocation.x);
            angle = 0.25*Math.PI - (angleLander - angleSpaceship);
            if (Math.abs(angle)>=0.12*Math.PI){
                angle = (angleLander - angleSpaceship);
            }
        }

        double wind = calculateWindSpeed(titanDistance,windCounter);
        //System.out.println(" we are now at height: " + titanDistance + " and the wind speed is " + wind);
        double xacc = 0;
        xacc = (thrusterForce.x * Math.sin(angle)) + wind;
//        System.out.println(xacc + " is the xAccelration");
        double yacc = (thrusterForce.y * Math.cos(angle)) - Consts.G_TITAN;

        //makes sure velocity doesnt pass terminal, when yacc is bigger than 0 velocity will decrease
        if (velocity.y < terminalVelocity || yacc > 0) {
            yacc = (thrusterForce.y * Math.cos(angle)) - Consts.G_TITAN;
        } else if (isLanding) {
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

        Vector3D fuelConsumption = new Vector3D(thrusterForce);
        fuelConsumption.x = fuelConsumption.x*Math.sin(angle);
        fuelConsumption.y = fuelConsumption.y*Math.cos(angle);
        calculateFuelConsumption(fuelConsumption,timeSlice);


    }


    //PID for the landing
    @Override
    public Vector3D thrusterForceLanding() {
        //we can have an error of 0.1;
        double angleNeeded = 0.01 * (Math.PI / 180);


        double angleLander = Math.atan(dispLocY / dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
        double angle = angleLander - angleTitan;
        double angleChange = angleNeeded - angle;

        double xAngle = (location.y / Math.tan(angleChange));


        //to make sure it lands in the correct position
        double xAccAngle = anglePIDx.compute(location.x, xAngle);
        //PIDinput.add(location.x);
        //PIDoutput.add(xAccAngle);
        double xAccPosition = landingPositionX.compute(location.x,realLandingLocationx);
        //System.out.println(xAccPosition);
        PIDoutput.add(xAccPosition);


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

    //this is the method called for ascending
    public Vector3D thrusterForceAscending() {
        //does the angle need to be 90 or 0?
        //we can have an error of 0.1;

        if (velocity.y<terminalVelocity&&titanDistance>50000){
            changeYLocationPID(timeSlice,0.00001,0.00000000000000000001,0.01);
        }

        double angleNeeded = 0.01 * (Math.PI / 180);


        double angleLander = Math.atan(dispLocY / dispLocX);
        double angleTitan = Math.atan(scaledSpaceshipLocation.y / scaledSpaceshipLocation.x);
        double angle = angleLander - angleTitan;
        double angleChange = angleNeeded - angle;

        double xAngle = (location.y / Math.tan(angleChange));


        //to make sure it lands in the correct angle
        double xAccAngle = anglePIDx.compute(location.x, xAngle);



        double yAccPosition = spaceshipPositionY.compute(location.y,realSpaceshipLocation.y);


        Vector3D accelerationVector = new Vector3D(xAccAngle, yAccPosition, 0);


        return accelerationVector;
    }

    //open-loop controller
    public Vector3D getOLCThrusterForce() {

        double angleLander = Math.atan(dispLocY / dispLocX);
        double angleTitan = Math.atan(scaledLandingLocation.y / scaledLandingLocation.x);
        angle = angleLander - angleTitan;

        if(titanDistance > 200000){
            return new Vector3D(20.976217403347164/2,3.4283613115597915/2,0);
        }else if(titanDistance < 200000 && titanDistance > 150000){
            return new Vector3D(9.9633216396605/2,2.966545361729677/2,0);
        }else if(titanDistance < 150000 && titanDistance > 100000){
            return new Vector3D(-29.207840811491764/2,2.780115504234349/2,0);
        }else if(titanDistance < 100000 && titanDistance > 50000){
            return new Vector3D(-244.08186148024728/2,1.5108,0);
        }else if(titanDistance < 50000 && titanDistance > 10000){
            return new Vector3D(-20,1.48708,0);
        }else{
            return new Vector3D(0,0,0);
        }

    }

    private void calculateSpending(double fuelAmmount){ }

    public double getTitanDistance() {
        return titanDistance;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getAngle(){ return angle*(180/Math.PI); }

    public double getScalingFactor(){return scalingFactor;}

    public Vector3D getVelocity(){ return velocity;}

    public Vector3D getLocation(){ return location;}

    public double getDispLocX(){return dispLocX;}

    public double getDispLocY(){return dispLocY;}

    public ArrayList<Double> getPIDoutput(){
        return PIDoutput;
    }

    public ArrayList<Double> getPIDinput(){
        return PIDinput;
    }

    public void markAsLanded() {
        isLanding = false;
        System.out.println("change in velocity for landing: " + totalChangeInVelocity.toString());
        totalChangeInVelocity = new Vector3D();
    }

    public boolean getIsLanding(){
        return isLanding;
    }
    public void calculateFuelConsumption(Vector3D thrusterForce, double timeSlice){
        Vector3D acceleration = new Vector3D(thrusterForce).div(mass);
        Vector3D velocityByAcc = new Vector3D(acceleration).mul(timeSlice);
        totalChangeInVelocity.add(velocityByAcc);
    }

    public Vector3D getTotalChangeInVelocity(){
        return totalChangeInVelocity;
    }
}