package model;

import java.awt.*;
import javafx.scene.paint.Color;

//Class for the planets and probes/lander objects
public class Body {

    public String name;
    public double mass;
    public double radius;
    public Vector3D location;
    public Vector3D velocity;
    public Vector3D acceleration;
    public Color color;
    public double scale; //1 smallest 5 biggest to keep it simple

    public Body(String name, double mass, double radius, Vector3D location, Vector3D velocity, Color color, int scale) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.location = location;
        this.velocity = velocity;
        this.acceleration = new Vector3D();
        this.color = color;
        this.scale = scale;
    }

    public Body(Vector3D location, Vector3D velocity, double radius, double mass) {
        name = "noName";
        this.location = location;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;
        this.color = color;
    }

    public Body(){
        name = "noName";

    }

    public Vector3D getAcceleration() {
        return acceleration;
    }

    public Vector3D getVelocity() {
        return velocity;
    }

    public Vector3D getLocation() {
        return location;
    }

    public javafx.scene.paint.Color getColor(){return color;}

    //Resets acceleration vector so that addAccelerationByGravForce() can accumulate forces during a new timeslice.
    public void resetAcceleration() {
        acceleration = new Vector3D();
    }

    //Calculates the gravitational force between this body and another body and accumulates the force.
    public void addAccelerationByGravForce(Body other) {
        addAccelerationByForce(calculateGravitationalForce(other));
    }

    /**
     * Calculates the gravitational force between this body and another body.
     *
     * Newton's law of universal gravitation: F = G * (m1 * m2)/r²
     *  - F = force between bodies in newtons. The force is a vector pointing towards the current body (attracting)
     *  - m1 = mass of body1 in kilograms
     *  - m2 = mass of body2 in kilograms
     *  - G = gravitaional constant (6.674×10−11 N · (m/kg)² )
     *  - r = distance between the centers of the masses in meters
     *
     *
     * @param other
     */
    protected Vector3D calculateGravitationalForce(Body other) {
        // get direction vector
        Vector3D directionVect = new Vector3D(this.location);
        directionVect.sub(other.location).normalize().mul(-1);

        // distance between bodies
        double r = this.location.distance(other.location);

        // calculate force
        Vector3D grativationalForce = new Vector3D(directionVect);
        grativationalForce.mul(Consts.G).mul(this.mass).mul(other.mass).mul(r).div(Math.pow(Math.abs(r), 3));

        return grativationalForce;
    }

    /**
     * Adds a force vector to the body wich implies a change in acceleration depending on mass.
     *
     *  Newton's second law: F = m * a, a = F/m
     *
     *  - F = force acting on the body in newtons
     *  - m = mass of body in kilograms
     *  - a = acceleration in m/s²
     *
     * @param force
     *
     */
    public void addAccelerationByForce(Vector3D force) {
        Vector3D accByForce = new Vector3D(force);
        accByForce.div(mass);
        acceleration.add(accByForce);
    }

    /**
     * Calculates a new velocity and location for the body for current acceleration and
     * a given timeslice.
     *
     * Note that the velocity calculated by applying the acceleration during the timeslice
     * will be the final velocity. To calculate the new location we will use the average
     * velocity instead in order to get a better approximation.
     *
     * The accuracy of the calculated velocity and location will be dependent on how how long
     * the timeslice is and how constant the acceleration is during the timeslice.
     *
     * @param timeSlice the timeslice for which current acceleration should affect the body.
     */
    public Vector3D updateVelocityAndLocation(double timeSlice) {
        // caluclate final velocity when the time slice has occurred
        Vector3D oldVelocity = new Vector3D(this.velocity);
        updateVelocity(timeSlice);

        // updateVelocityAndLocation location using average velocity
        Vector3D changedVelocityAverage = new Vector3D(this.velocity).sub(oldVelocity).div(2.0);
        Vector3D averageVelocity = new Vector3D(oldVelocity).add(changedVelocityAverage);
        return updateLocation(timeSlice, averageVelocity);
    }

    /**
     * Calculates the final velocity when current accumulated acceleration has been applied during a timeslice.
     *
     * @param timeSlice
     */
    protected void updateVelocity(double timeSlice) {
        Vector3D velocityByAcc = new Vector3D(acceleration).mul(timeSlice);
        velocity.add(velocityByAcc);
    }

    /**
     * Calulates a new location given that an velocity has been applied a given timeslice.
     *
     * @param timeSlice the timeslice for which given average velocity should affect the body.
     * @param averageVelocity the average velocity during the timeslice
     */
    protected Vector3D updateLocation(double timeSlice, Vector3D averageVelocity) {
        //orbit.add(location);
        Vector3D locationByVelocity = new Vector3D(averageVelocity).mul(timeSlice);
        location.add(locationByVelocity);
        return locationByVelocity;

    }

    public void addAccelerationByGravityForce(Body other) {
        addAccelerationByForce(calculateGravitationalForce(other));
    }

    @Override
    public String toString() {
        String string = ("name = " + name + ", x = " + location.x + ", y = " + location.y + ", z = " + location.z);
        //return String.format("name = %f, x = %f, y = %f, z = %f, vx = %f, vy = %f, vz = %f", name, location.x, location.y, location.z,velocity.x,velocity.y,velocity.z);
        return string;
    }



}
