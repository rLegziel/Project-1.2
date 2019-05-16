package model;

import javafx.animation.Timeline;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

import static model.Body.CelestialBody.EARTH;
import static model.Body.CelestialBody.SATURN;

public class BodySystem {

    public static final double G = 6.67300E-11;  // universal gravity constant

    private static final int SEC_IN_MINUTE = 60;
    private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
    private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
    private static final int SEC_IN_YEAR = 31556926;
    private long elapsedSeconds = 0;

    private static Body.CelestialBody[] bodiesInSystem = {Body.CelestialBody.SUN, Body.CelestialBody.MERCURY, Body.CelestialBody.VENUS, EARTH,
            Body.CelestialBody.MARS, Body.CelestialBody.JUPITER, SATURN, Body.CelestialBody.NEPTUNE, Body.CelestialBody.URANUS,
            Body.CelestialBody.PLUTO, Body.CelestialBody.TITAN, Body.CelestialBody.PROBE};

    private List<Body> bodies;
    private boolean probeAdded = false;

    private long firstLaunch = 183672000-SEC_IN_DAY*10;
    private long intervalTime = SEC_IN_DAY; //how long between launches
    private long interval = firstLaunch;

    private long perfectTime; //change this to date format once we can

    private long minDistance = Long.MAX_VALUE;
    private int minDistanceProbe = -1;

    private boolean print = true;

    Timeline timeline;

    public double currentDistance;

    private int probesNum = 1; //number of probes we send at a time
    private int probesLimit =20; //limit the number of probes we send
    private ArrayList<Body> probesList = new ArrayList<>();//stores all the probes we launch
    private ArrayList<Double> minDistancesList = new ArrayList<>();
    ArrayList<Long> launchTimesList = new ArrayList<>();//stores the times in which we launch the probes, we should change this to a date in the future


    public BodySystem() {
        bodies = new ArrayList<>();
        createSystem();
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public Body getProbe(){
        return bodies.get(11);
    }

    public void addBody(Body body) {
        bodies.add(body);
    }

    public void createSystem() {
        for (Body.CelestialBody cbody : bodiesInSystem) {
            if (!cbody.equals(Body.CelestialBody.PROBE)) {
                Body asBody = cbody.getAsBody();
                addBody(asBody);
            }
        }
    }

    public void addProbe() {
        Body probe = Body.CelestialBody.PROBE.getAsBody();
        addBody(probe);
        probe.location.add(EARTH.getAsBody().location);
        // System.out.println(probe.location);
    }

    // called in GUI updateFrame
    public double updateSystem(double timeSlice) {

        // reset acceleration for all bodies to then update it based on gravitational forces
        for (Body body : bodies) {
            body.resetAcceleration();
        }

        // todo: change this part to send out many probes
        if (!probeAdded && elapsedSeconds >= 4 * SEC_IN_YEAR) {
            addProbe();
            probeAdded = true;
        }

        // n-body problem
        // add gravitation force from each body to each body
        for (int i = 0; i < bodies.size(); i++) {
            Body current = bodies.get(i);
            for (int j = i+1; j < bodies.size(); j++) {
                Body other = bodies.get(j);
                current.addAccelerationByGravForce(other);
                other.addAccelerationByGravForce(current);
            }
        }

        for (Body body : bodies) {
            body.updateVelocityAndLocation(timeSlice);
        }
        elapsedSeconds += timeSlice;

        return timeSlice;
    }

    public double update(double timeSlice, Timeline timeline) {
        this.timeline = timeline;
        // reset acceleration so we can accumulate acceleration due to gravitation from all bodies
        bodies.stream().forEach(i -> i.resetAcceleration()) ;

        // add gravitation force to each body from each body
        for (int i = 0; i < bodies.size(); i++) {
            Body current = bodies.get(i);
            for (int j = i+1; j < bodies.size(); j++) {
                Body other = bodies.get(j);
                current.addAccelerationByGravityForce(other);
                other.addAccelerationByGravityForce(current);
            }
        }
        // update velocity and location for each body
        bodies.stream().forEach(i -> i.updateVelocityAndLocation(timeSlice));
        elapsedSeconds += timeSlice;
        //157786200
        if(elapsedSeconds == interval && probesList.size() <= probesLimit) {
            //launchNewProbe();
        }

        if(elapsedSeconds > interval){
            //System.out.println("probe" + probesList.get(0).location.toString());
            //System.out.println("saturn" + SATURN.location.toString());
            //System.out.println("earth" + EARTH.location.toString());

            double x1 = probesList.get(0).location.x;
            double x2 = SATURN.location.x;
            double y1 = probesList.get(0).location.x;
            double y2 = SATURN.location.y;

            currentDistance = bodies.get(bodies.size()-1).location.probeDistance(bodies.get(6).location);

            //System.out.println(bodies.get(bodies.size()-1).location.probeDistance(bodies.get(6).location));
            //System.out.println(minDistance);
        }

        //SEC_IN_YEAR * 7.82
        if(elapsedSeconds > SEC_IN_YEAR * 8 && print){
            //checkCollision();
            //System.out.println(minDistance + " " + minDistanceProbe);
            for(double d: minDistancesList){
                System.out.println(d + " " + minDistancesList.indexOf(d));
                //System.out.println(minDistancesList.size());
            }
            print = false;
            //timeline.pause();
            //timeline.playFrom();

            System.out.println(minDistancesList.get(10));
        }

        return timeSlice;
    }

    private void launchNewProbe(){

        /*for(Body probe: probesList){
            System.out.println("probe " + probesList.indexOf(probe) + " distance: " + probe.location.probeDistance(SATURN.location));
        }*/

        //info for the probe
        double radius = 100; //in meters
        double mass = 5000; //in kg

        //launch multiple probes at same time but different velocities
        for(int x = 0; x <= probesNum-1; x++){


        }

        //double scalarx = 2;//randomDouble(31536.19970825835,33489.908868949606);//Math.random() * 32489.908868949606 + 32381.42239494954;
        //double scalary = 2;//randomDouble(-38729.05154906459,-37440.490183461545);//Math.random() * -37603.07114738251 + -37729.05154906459;

        //System.out.println(""+scalar);

        Vector3D location = EARTH.getAsBody().location;//bodies.get(3).getLocation();//EARTH.getAsBody().location;//because we are always launching from same site
        Vector3D velocity = EARTH.getAsBody().velocity; //bodies.get(3).getVelocity();//EARTH.getAsBody().velocity; //just as a reference for now, change to something more realistic

        Vector3D v = new Vector3D(6371.008E+3,0,0);
        v.add(bodies.get(3).location);

        location = v;

        velocity.x = velocity.x*2;
        velocity.y = velocity.y*2;

        //velocity.x = velocity.x*scalarx;//9461.660698284308;//
        //velocity.y = velocity.y*scalary;//-34212.48485768854;//

        Body probe = new Body(location,velocity,radius,mass, Color.BLACK);

        probesList.add(probe);
        minDistancesList.add(Double.MAX_VALUE);
        probe.name = "prob" + probesList.indexOf(probe);
        launchTimesList.add(elapsedSeconds);

        addBody(probe);
        System.out.println("probe " + probesList.indexOf(probe) + " launched at " + elapsedSeconds +
                " vx: " + velocity.x + " vy: " + velocity.y) ;

        //change this so it doesn't send multiple probes at one time
        interval+=intervalTime;
    }

}
