package nbody.model;


import javafx.animation.Timeline;

import java.sql.SQLOutput;
import java.sql.Time;
import java.util.*;

import static nbody.model.CelestialBody.EARTH;
import static nbody.model.CelestialBody.SATURN;

public class BodySystem {
    private static final int SEC_IN_MINUTE = 60;
    private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
    private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
    private static final int SEC_IN_YEAR = 31556926;
    private long elapsedSeconds = 0;

    //the time when I start to adjust the trajectory with the engines
    private long saturnTime = 238608000;
    private long closestToSaturn = 238996800;
    private long stayInOrbitSaturn = 238999200;
    private double xSaturn =1407803030373.561800;
    private double ySaturn = -290357895704.930240;
    private double zSaturn = -51070777398.352970;

    private long timeClosestToTitan = 239000400;
    private double xTitan = 1409198048006.711000;
    private double yTitan = -287772211103.425350;
    private double zTitan = -52532091692.418510;
    private PIDController controller = new PIDController(1200,xTitan,yTitan);

    public double startingDistance = 1.4E+10;
    public Body chosenOne;
    public String currentTime;

    //8.862395206670821E8
    //2.223667170921659E9
    // from titan 3.2670147095408136E8
    //6.874331296673225E8
    // 6.874331296673225E8
    //4.822473458108047E7

    private long firstLaunch =185238000;
    private long intervalTime = (long) SEC_IN_DAY/8;  //how long between launches
    private long interval = firstLaunch;

    private long perfectTime; //change this to date format once we can

    private long minDistance = Long.MAX_VALUE;
    private int minDistanceProbe = -1;


    Timeline timeline;

    public double currentDistance;

    private int probesNum = 1; //number of probes we send at a time
    private int probesLimit =1 ; //limit the number of probes we send
    private ArrayList<Probe> realProbesList = new ArrayList<>();
    private ArrayList<Double> minDistancesList = new ArrayList<>();
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

        elapsedSeconds += timeSlice;


        if(elapsedSeconds > firstLaunch+intervalTime + timeSlice && elapsedSeconds<timeClosestToTitan) {

            if (realProbesList.size()>=2) {
                double differenceInTime = timeClosestToTitan - elapsedSeconds;
                double timeToGetThere = differenceInTime;
                //useEngine(xTitan, yTitan, zTitan, timeToGetThere, timeSlice);
            }
        }

        /**
         * this is where the PIDController is used, you can change the interval
         * probe 1 is the one that is controlled by the PID and probe 0 is commented
         * I did this so you can clearly see the difference with and without PID
         */
        if (elapsedSeconds> firstLaunch + timeSlice && elapsedSeconds<timeClosestToTitan+timeSlice){

            if (realProbesList.size()>=2){
                double xAcc = controller.computeNewX(realProbesList.get(1).location.x,bodies.get(10).location.x);
                double yAcc = controller.computeNewY(realProbesList.get(1).location.y,bodies.get(10).location.y);
                Vector3D accelerationVector = new Vector3D(xAcc,yAcc,10);
                accelerationVector = accelerationVector.normalize();
                //System.out.println(accelerationVector.x);
                realProbesList.get(1).addAccelerationByForce(accelerationVector);
            }
            if (realProbesList.size()>=2){
                double xAcc = controller.computeNewX(realProbesList.get(0).location.x,bodies.get(10).location.x);
                double yAcc = controller.computeNewY(realProbesList.get(0).location.y,bodies.get(10).location.y);
                Vector3D accelerationVector = new Vector3D(xAcc,yAcc,0);
                accelerationVector = accelerationVector.normalize();
                //System.out.println(accelerationVector.x);
                //realProbesList.get(0).addAccelerationByForce(accelerationVector);
            }

        }

        if (realProbesList.size()>=2) {
            //distance to start checking how close the probe is to titan
            //if there is no output increase the distance when to check
            double distance = realProbesList.get(1).location.probeDistance(bodies.get(10).location);
            if (distance < 1000000000) {
                checkLocationtoTitan(bodies.get(10));
                //updateForLanding(timeSlice,timeline);
            }
        }



        // update velocity and location for each body
        bodies.stream().forEach(i -> i.updateVelocityAndLocation(timeSlice));
        //157786200

        if(elapsedSeconds == interval  &&  realProbesList.size() <=probesLimit){
            launchNewProbe();
        }



        return timeSlice;
    }

    /**
     * this will be the method to call when the landing GUI pops up
     * it's still very basic because it's hard to program when there is no GUI to test on
     */
    public double updateForLanding(double timeSlice, Timeline timeline) {
        //does the angle need to be 90 or 0?
        //we can have an error of 0.1;
        double angleNeeded = 0.1;
        for (int i = 0; i<realProbesList.size();i++){
            double angle = Math.atan(realProbesList.get(i).location.y/realProbesList.get(i).location.x);
            double angleChange = angle-angleNeeded;
            double xneeded = realProbesList.get(i).location.y/Math.tan(angleChange);
            double yneeded = bodies.get(10).location.y;

            double xAcc = controller.computeNewX(realProbesList.get(i).location.x,xneeded);
            double yAcc = controller.computeNewY(realProbesList.get(i).location.y,yneeded);

            Vector3D accelerationVector = new Vector3D(xAcc,yAcc,0);
            accelerationVector = accelerationVector.normalize();
            //System.out.println(accelerationVector.x);

            Vector3D actualAcceleration = realProbesList.get(0).calculateAccelerationLanding(bodies.get(10),accelerationVector,new Vector3D());
            realProbesList.get(i).acceleration=actualAcceleration;

        }

        return timeSlice;
    }


    /**
     * launches a new probe, with earth's location. in this method you can choose its velocity, mass etc.
     */
    private void launchNewProbe(){



        //info for the probe
        double radius = 100; //in meters
        double mass = 5000; //in kg

        //launch multiple probes at same time but different velocities
        for(int x = 0; x <= probesNum-1; x++){


        }

        Vector3D location = EARTH.getAsBody().location;//bodies.get(3).getLocation();//EARTH.getAsBody().location;//because we are always launching from same site
        Vector3D velocity = EARTH.getAsBody().velocity; //bodies.get(3).getVelocity();//EARTH.getAsBody().velocity; //just as a reference for now, change to something more realistic

        Vector3D v = new Vector3D(6371.008E+3,0,0);
        v.add(bodies.get(3).location);

        // creates random scalars for probes, for optimization purposes.
        double rangeMax = 2.75;
        double rangeMin = 1.5;
        Random rand = new Random();
        double scalarX = rangeMin +(rangeMax-rangeMin)*rand.nextDouble();
        double scalarY = rangeMin +(rangeMax-rangeMin)*rand.nextDouble();

        location = v;

        // X and Y velocity of the probe.
        velocity.x = velocity.x*1.978;
        velocity.y = velocity.y*1.745;



        Probe rProbe = new Probe(location,velocity,radius,mass,"prob" + realProbesList.size(),elapsedSeconds,
                velocity.x,velocity.y);

        realProbesList.add(rProbe);
        minDistancesList.add(Double.MAX_VALUE);
        rProbe.name = "probe" + realProbesList.indexOf(rProbe);
        System.out.println(rProbe.name + " launched on " + getElapsedTimeAsString());
        launchTimesList.add(elapsedSeconds);

        addBody(rProbe);
        //change this so it doesn't send multiple probes at one time
        interval+=intervalTime;
    }


    /**
     * checks the distance between each probe and titan, will print the probe that gets closest to titan.
     */
    public void checkLocationtoTitan(Body other){
        startingDistance = 5E15;
            double distance = realProbesList.get(1).location.probeDistance(other.location); // location of the probe, distance to Titan
            //System.out.println(distance);
            if (distance<startingDistance){
                chosenOne = realProbesList.get(1);
                System.out.println(realProbesList.get(1));
                System.out.println(other);
                startingDistance = distance;
                System.out.println(" The minimum distance is(in meters) : " + startingDistance);
                currentTime = getElapsedTimeAsString();
                //System.out.println(currentTime);
                System.out.println(elapsedSeconds);
            }




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


    /**
     * adjust the trajectory of the probe for one timeslice
     * method can be changed to adjust it for a longer period
     * @param xTitan the position of the titan one timeclise further
     * @param yTitan
     * @param zTitan
     * @param timeSlice
     */
    public void useEngine(double xTitan, double yTitan, double zTitan, double timeNeeded,double timeSlice){

        //give the probe the right acceleration
        for (int i = 0; i < realProbesList.size(); i++) {

            //difference in distance
            double xdif = xTitan - realProbesList.get(i).location.x;
            double ydif = yTitan - realProbesList.get(i).location.y;
            double zdif = zTitan - realProbesList.get(i).location.z;

            //speed necessary to get to that position of titan
            double xvel = xdif/(timeNeeded);
            double yvel = ydif/(timeNeeded);
            double zvel = zdif/(timeNeeded);
            //the amount of acceleration needed to get that velocity
            double xacc = xvel - realProbesList.get(i).velocity.x;
            double yacc = yvel - realProbesList.get(i).velocity.y;
            double zacc = zvel - realProbesList.get(i).velocity.z;
            //realProbesList.get(i).acceleration = new Vector3D(xacc/timeSlice, yacc/timeSlice, zacc/timeSlice);
            realProbesList.get(i).addAccelerationByForce(new Vector3D((xacc/timeSlice)*realProbesList.get(i).mass,(yacc/timeSlice)*realProbesList.get(i).mass,(zacc/timeSlice)*realProbesList.get(i).mass));
        }
    }

    public List getBodieslist(){
        return bodies;
    }

}

