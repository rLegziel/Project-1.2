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

    public double startingDistance = 1.4E+10;
    public Body chosenOne;
    public String currentTime;

    //8.862395206670821E8
    //2.223667170921659E9
    // from titan 3.2670147095408136E8
    //6.874331296673225E8
    // 6.874331296673225E8
    //4.822473458108047E7

    private long firstLaunch =185238000  ;
    private long intervalTime = (long) SEC_IN_DAY/8;  //how long between launches
    private long interval = firstLaunch;

    private long perfectTime; //change this to date format once we can

    private long minDistance = Long.MAX_VALUE;
    private int minDistanceProbe = -1;

    private boolean print = true;

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



        if(elapsedSeconds > firstLaunch+intervalTime && elapsedSeconds<timeClosestToTitan) {

            double differenceInTime = timeClosestToTitan - elapsedSeconds;
            double timeToGetThere = differenceInTime;
            useEngine(xTitan,yTitan,zTitan,timeToGetThere,timeSlice);
        }



        /*
        if (elapsedSeconds>=stayInOrbitSaturn){
            //System.out.println("VELOCITY NEEDED FOR PROBE TO STAY IN ORBIT SATURN: ");
            double distanceSaturnProbe = realProbesList.get(1).location.probeDistance(bodies.get(6).location);
            double velocitySquared = (Physics.G*bodies.get(6).mass)/distanceSaturnProbe;
            double velocity = Math.sqrt(velocitySquared);
            //System.out.println(velocity);
            //System.out.println("THE ANGLE OF THE PROBE: ");
            double angle = Math.tan(realProbesList.get(1).location.y/realProbesList.get(1).location.x);
            //System.out.println(angle);
            double neededVelocityX = velocity*(Math.cos(angle));
            double neededVelocityY = velocity*(Math.sin(angle));
            //System.out.println("Needed velocity x and y: ");
            //System.out.println("X: " + neededVelocityX);
            //System.out.println("Y: " + neededVelocityY);
            realProbesList.get(1).acceleration.x = 0;
            realProbesList.get(1).acceleration.y = 0;
            realProbesList.get(1).acceleration.z = 0;
            realProbesList.get(1).velocity.x = neededVelocityX;
            realProbesList.get(1).velocity.y = neededVelocityY;
            realProbesList.get(1).velocity.z = 0;
            realProbesList.get(1).location.z = 0;
            bodies.get(6).velocity.z = 0;
            bodies.get(6).location.z = 0;
        }

         */





        // update velocity and location for each body
        bodies.stream().forEach(i -> i.updateVelocityAndLocation(timeSlice));
        //157786200

        if(elapsedSeconds == interval &&  realProbesList.size() <=probesLimit){
//        if(elapsedSeconds == interval && probesList.size() <= probesLimit) {
            launchNewProbe();
        }


        //time to start checking the distance between the probe and titan
        if(elapsedSeconds >= saturnTime-2*timeSlice && elapsedSeconds <= closestToSaturn + timeSlice*3){
            check1(bodies.get(10));
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
    public void check1(Body other){
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

        //difference in distance
        double xdif = xTitan - realProbesList.get(1).location.x;
        double ydif = yTitan - realProbesList.get(1).location.y;
        double zdif = zTitan - realProbesList.get(1).location.z;

        //speed necessary to get to that position of titan
        double xvel = xdif/(timeNeeded);
        double yvel = ydif/(timeNeeded);
        double zvel = zdif/(timeNeeded);

        //the amount of acceleration needed to get that velocity
        double xacc = xvel - realProbesList.get(1).velocity.x;
        double yacc = yvel - realProbesList.get(1).velocity.y;
        double zacc = zvel - realProbesList.get(1).velocity.z;


        //give the probe the right acceleration
        for (int i = 0; i < realProbesList.size(); i++) {
            realProbesList.get(i).acceleration = new Vector3D(xacc/timeSlice, yacc/timeSlice, zacc/timeSlice);
        }
    }

}

