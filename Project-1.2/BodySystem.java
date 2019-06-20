package nbody.model;


import javafx.animation.Timeline;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static nbody.model.CelestialBody.EARTH;
import static nbody.model.CelestialBody.SATURN;

public class BodySystem {
    private static final int SEC_IN_MINUTE = 60;
    private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
    private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
    private static final int SEC_IN_YEAR = 31556926;
    private long elapsedSeconds = 0;


    private long firstLaunch = 184190400-SEC_IN_DAY*12;
    private long intervalTime = SEC_IN_DAY; //how long between launches
    private long interval = firstLaunch;

    private long perfectTime; //change this to date format once we can

    private long minDistance = Long.MAX_VALUE;
    private int minDistanceProbe = -1;

    private boolean print = true;

    Timeline timeline;

    public double currentDistance;

    private int probesNum = 1; //number of probes we send at a time
    private int probesLimit = 6; //limit the number of probes we send
    private ArrayList<Body> probesList = new ArrayList<>();//stores all the probes we launch
    private ArrayList<Probe> realProbesList = new ArrayList<>();
    ArrayList<Long> launchTimesList = new ArrayList<>();//stores the times in which we launch the probes, we should change this to a date in the future

    private List<Body> bodies;

    public BodySystem() {
        bodies = new ArrayList<>();
    }

    public List<Body> getBodies() {
        return bodies;
    }

    public void addBody(Body body) {
        bodies.add(body);
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
            launchNewProbe();
        }

        if(elapsedSeconds > interval){
            //System.out.println("probe" + probesList.get(0).location.toString());
            //System.out.println("saturn" + SATURN.location.toString());
            //System.out.println("earth" + EARTH.location.toString());

            double x1 = probesList.get(0).location.x;
            double x2 = SATURN.location.x;
            double y1 = probesList.get(0).location.x;
            double y2 = SATURN.location.y;

            currentDistance = Math.sqrt((x1*x2) - (y1*y2));
        }

        //SEC_IN_YEAR * 7.82
        if(elapsedSeconds > SEC_IN_YEAR * 7.83 && print){
            checkCollision();
            System.out.println(minDistance + " " + minDistanceProbe);
            print = false;
            timeline.pause();
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

        Vector3D location = EARTH.getAsBody().location;//because we are always launching from same site
        Vector3D velocity = EARTH.getAsBody().velocity; //just as a reference for now, change to something more realistic

        velocity.x = velocity.x*1.5;
        velocity.y = velocity.y*1.5;

        //velocity.x = velocity.x*scalarx;//9461.660698284308;//
        //velocity.y = velocity.y*scalary;//-34212.48485768854;//

        Body probe = new Body(location,velocity,radius,mass);
        Probe rProbe = new Probe(location,velocity,radius,mass,"prob" + probesList.size(),elapsedSeconds,
                velocity.x,velocity.y);

        realProbesList.add(rProbe);
        probesList.add(probe);
        probe.name = "prob" + probesList.indexOf(probe);
        launchTimesList.add(elapsedSeconds);

        addBody(probe);
        System.out.println("probe " + probesList.indexOf(probe) + " launched at " + elapsedSeconds +
                " vx: " + velocity.x + " vy: " + velocity.y) ;

        //change this so it doesn't send multiple probes at one time
        interval+=intervalTime;
    }

    public void checkCollision(){
        //System.out.println("checking collisions");
        if(probesList.size() > 0){
            //System.out.println("probe 0" + " distance: " + probesList.get(0).location.probeDistance(SATURN.location));
        }
        for(Body probe: probesList){
            //System.out.println("checking probe" + probeList.indexOf(probe);
            if(probe.location.probeDistance(SATURN.location) < minDistance){
                //System.out.println("is smaller");
                minDistance = (long)probe.location.probeDistance(SATURN.location);
                minDistanceProbe = probesList.indexOf(probe);
            }
            //System.out.println("probe " + probe + SATURN.getAsBody() +" distance: " + probe.location.distance(SATURN.location) + " " +elapsedSeconds);
            if(probe.location.probeDistance(SATURN.location) < 1000000000){ //we can narrow the distance once we figure out which dates get closer
                System.out.println("probe with index " + probesList.indexOf(probe) + " succeeded");
                System.out.println("this probe was launched at: " +
                        launchTimesList.get(probesList.indexOf(probe))); // change this to a date format
                perfectTime = launchTimesList.get(probesList.indexOf(probe));
            }
        }
        //System.out.println("checked");
    }

    private double randomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }


    public Optional<Body> getBody(String name) {
        return bodies.stream().filter(i -> i.name.equals(name)).findFirst();
    }

    public String getElapsedTimeAsString() {
        long years = elapsedSeconds / SEC_IN_YEAR;
        long days = (elapsedSeconds % SEC_IN_YEAR) / SEC_IN_DAY;
        long hours = ( (elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) / SEC_IN_HOUR;
        long minutes = ( ((elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) / SEC_IN_MINUTE;
        long seconds = ( ((elapsedSeconds % SEC_IN_YEAR) % SEC_IN_DAY) % SEC_IN_HOUR) % SEC_IN_MINUTE;
        return String.format("Years:%08d, Days:%03d, Hours:%02d, Minutes:%02d, Seconds:%02d", years, days, hours, minutes, seconds);
    }

    public long getElapsedTime(){
        return elapsedSeconds;
    }

}
