package model;

import java.awt.*;
import javafx.scene.paint.Color;

public class Body {

    // mass, radius, positions, velocity vectors from:
    // https://ssd.jpl.nasa.gov/horizons.cgi

    enum CelestialBody {

        //Name                  Mass            Radius         X                        Y                        Z                       VX                       VY                      VZ
        SUN("Sun",              1.98855E+30,    695700000.0,   0,                       0,                       0,                      0,                       0,                      0, Color.DARKGOLDENROD),

        MERCURY("Mercury", 0.33011E+24, 2439.7E+3, -2.914712614752511E+07, -6.199930532058012E+07, -2.431019447296567E+06, 3.424177060854215E+01, -1.840752506021413E+01, -4.646878989229692E+00, Color.RED),

        VENUS("Venus", 4.8675e24, 6051.8E+3, -4.540221440982638E+07, -9.761702225397852E+07, 1.275022972156055E+06, 3.149189089973654E+01, -1.494411854718451E+01, -2.022651128423027E+00, Color.ORANGERED),

        EARTH("Earth", 5.9723E+24, 6371.008E+3, -1.123742743143437E+08, -9.929473113856807E+07, -1.719909168424457E+04, 1.927910017573211E+01, -2.238794104605604E+01, 7.654215803398756E-05, Color.BLUE),

        MARS("Mars", 0.64171E+24, 3389.5E+3, 2.769489375730218E+07, 2.314083002586504E+08, 4.143650166228279E+06, -2.315120132716055E+01, 4.913658465424496E+00, 6.708963405868693E-01, Color.DARKRED),

        JUPITER("Jupiter", 1898.19E+24, 69911E+3, -7.670423564156541E+08, -2.760915741715643E+08, 1.830090612901382E+07, 4.273937961349422E+00, -1.167254830604601E+01, -4.700656450718288E-02, Color.DARKORANGE),

        SATURN("Saturn", 568.34E+24, 58232E+3, -1.847985294188508E+08, -1.491810077910302E+09, 3.329327731071341E+07, 9.057064141245075E+00, -1.217469136865241E+00, -3.397713403584295E-01, Color.LIGHTGOLDENRODYELLOW),

        NEPTUNE("Neptune", 102.413E+24, 24622E+3, 4.257186540489833E+09, -1.395306114349077E+09, -6.937752828586924E+07, 1.655967756688154E+00, 5.197288115337841E+00, -1.446099865733843E-01, Color.DARKBLUE),

        URANUS("Uranus", 86.813e24, 25362E+3, 2.714797267277413E+09, 1.233207333719350E+09, -3.059052863518852E+07, -2.866519506810219E+00, 5.882836573485944E+00, 5.882566688337931E-02, Color.PURPLE),

        PLUTO("Pluto", 0.01303E+24, 1187E+3, 1.501099735943540E+09, -4.752004467812544E+09, 7.428723010835433E+07, 5.302832537658332E+00, 4.978205559367923E-01, -1.608658326954050E+00, Color.SLATEGREY),

        TITAN("Titan", 0.3452E+23, 2575.73, -1.857968240129294E+08, -1.491099031663066E+09, 3.302585356476218E+07, 5.761606665660599E+00, -4.906852920324597E+00, 1.888834075303969E+00, Color.CYAN),

        PROBE("Probe", 5E+3, 1E+1, 6371.008E+3, 0, 0, -3.217469136865241E+00, 3.057064141245075E+00, 1.888834075303969E+00, Color.HOTPINK);


        public final String name;
        public final double mass;   // kg
        public final double radius; // meters
        public Vector3D location; // meters
        public Vector3D velocity; // m/s
        public Color color;

        /**
         * @param name   (String)
         * @param mass   kg
         * @param radius m
         * @param x      m
         * @param y      m
         * @param z      m
         * @param x_vel  km/s
         * @param y_vel  km/s
         * @param z_vel  km/s
         */
        CelestialBody(String name, double mass, double radius, double x, double y, double z, double x_vel, double y_vel, double z_vel, Color color) {
            this.name = name;
            this.mass = mass;
            this.radius = radius;
            this.location = new Vector3D(x * 1000, y * 1000, z * 1000);
            this.velocity = new Vector3D(x_vel * 1000, y_vel * 1000, z_vel * 1000);
            this.color = color;
        }

        public Body getAsBody() {
            Body body = new Body(name, mass, radius, location, velocity, color);
            return body;
        }

    }

    public String name;
    public double mass;
    public double radius;
    public Vector3D location;
    public Vector3D velocity;
    public Vector3D acceleration;
    public Color color;

    public static final double gTitan = 1.352;


    public Body(String name, double mass, double radius, Vector3D location, Vector3D velocity, Color color) {
        this.name = name;
        this.mass = mass;
        this.radius = radius;
        this.location = location;
        this.velocity = velocity;
        this.acceleration = new Vector3D();
        this.color = color;
    }

    public Body(Vector3D location, Vector3D velocity, double radius, double mass, Color color) {
        this.location = location;
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;
        this.color = color;
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

    /**
     * Resets acceleration vector so that addAccelerationByGravForce() can accumulate forces during a new timeslice.
     */
    public void resetAcceleration() {
        acceleration = new Vector3D();
    }

    /**
     * Calculates the gravitational force between this body and another body and
     * accumulates the force.
     *
     * @param other celestial body
     */
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
        grativationalForce.mul(BodySystem.G).mul(this.mass).mul(other.mass).mul(r).div(Math.pow(Math.abs(r), 3));

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

    public Vector3D calculateAccelerationLanding(Body titan, Vector3D thrusterForce, Vector3D wind){
        double angle = Math.atan(location.y/location.x);
        double height = location.y - titan.location.y;
        double xacc = thrusterForce.x*Math.sin(angle) + wind.x;
        double yacc = thrusterForce.y*Math.cos(angle) - gTitan;

        Vector3D acceleration = new Vector3D(xacc,yacc,0);
        return acceleration;
    }

    //only to be used when landing, and on the lander
    public void landingGravCalculator(){
        location.y = ((0.5*gTitan*(1)) + (velocity.y) + location.y) / 10;
        velocity.y = velocity.y + gTitan;
    }


}
