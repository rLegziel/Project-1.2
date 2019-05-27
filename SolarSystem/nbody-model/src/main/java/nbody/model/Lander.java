package nbody.model;

import javafx.scene.paint.Color;

import java.util.Random;

public class Lander extends Body {

    private double scalingFactor;
    private double titanDistance = 250000;

    private double windSpeed;

    public double dispLocY = 1;
    public double dispLocX = 500;

    //not sure if this correct
    private double timeSlice = 1200;


    private double terminalVelocity = 6.5;

    public Lander(String name, double mass, double radius, Vector3D location, Vector3D velocity, Color color, double scalingFactor) {
        //uper(name, mass, radius, location, velocity, color);
        super(location,velocity,radius,mass);
        this.scalingFactor = scalingFactor;
    }

    public Lander(Vector3D location, Vector3D velocity, double radius, double mass, Color color, double scalingFactor) {
        //super(location, velocity, radius, mass, color);
        super(location,velocity,radius,mass);
        this.scalingFactor = scalingFactor;
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
            location.y -= (0.5*Physics.gTitan*(0.1*0.1))+velocity.y;
            velocity.y = velocity.y + Physics.gTitan;
            double delta = (tempY - location.y);
            dispLocY += delta/scalingFactor;
            titanDistance -= delta;
        }else{
            double tempY = location.y;
            location.y -= (0.5*Physics.gTitan*(0.1*0.1))+velocity.y;
            //only difference is velocity stays constant

            double delta = (tempY - location.y);
            dispLocY += delta/scalingFactor;
            titanDistance -= delta;
        }

        //0.5*gravity*(fallingTime*fallingTime)) + (initialVelocity*fallingTime) + initialPosition

        //System.out.println(location.y + " " + dispLocY);
    }

    public void calculateAccelerationLanding(Vector3D thrusterForce){
        resetAcceleration();
        double angle = Math.atan(location.y/location.x);
        double height = titanDistance;
        double wind = calculateWindSpeed(titanDistance);
        double xacc = thrusterForce.x*Math.sin(angle) + wind;
        double yacc = thrusterForce.y*Math.cos(angle) - Physics.gTitan;

        Vector3D accelerationForce = new Vector3D(xacc,yacc,0);
        addAccelerationByForce(accelerationForce);
        System.out.println(accelerationForce.x);
        System.out.println(acceleration.x);
        Vector3D oldLocation = location;
        Vector3D oldVelocity = velocity;
        updateVelocityAndLocation(timeSlice);

        //System.out.println(location.x-oldLocation.x);
        //System.out.println(velocity.x-oldVelocity.x);
        dispLocX += (location.x-oldLocation.x)/scalingFactor;
        dispLocY += (location.y-oldLocation.y)/scalingFactor;


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

}
