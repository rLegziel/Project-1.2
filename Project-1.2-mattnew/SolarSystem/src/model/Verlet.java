package model;

import java.util.List;
import java.util.Vector;

public class Verlet {
    private static final double G = 6.67300E-11;


    protected Vector3D newtonSecondFormula(double mass1, double mass2, double distance, Vector3D directionVector) {

        Vector3D result = (directionVector.mul(G).mul(mass1).mul(mass2)).div(Math.pow(Math.abs(distance), 2));

        return result;
    }

    protected void calculateAcceleration(Body current, Body other) {

        // in this method we compute the basic acceleration based on Newton's second law of gravity
        // creating the direction vector by taking the location of the current location, subtracting the other location from it and normalizing the vector(multiplying it by 1/length)
        Vector3D directionVector = new Vector3D(current.location);
        Vector3D otherLocation = new Vector3D(other.location);
        directionVector = directionVector.sub(otherLocation).normalize().mul(-1);
        Vector3D currentLocation = new Vector3D(current.location);
        double distance = currentLocation.distance(otherLocation);
        double mass1 = new Double(current.mass);
        double mass2 = new Double(other.mass);


        Vector3D acc = newtonSecondFormula(mass1, mass2, distance, directionVector);
        //testing something

        current.addAccelerationByForce(acc);

    }

    protected void halfStepVelocity(Body current, double timeSlice) {
        // calculates half-step of the velocity for the current body. based on : https://www.algorithm-archive.org/contents/verlet_integration/verlet_integration.html
        // this needs to be done after we calculate the acceleration based on all the planets in the system

        Vector3D currentVelocity = new Vector3D(current.velocity);
        Vector3D currentAcceleration = current.getAcceleration();
        currentAcceleration = currentAcceleration.div(2);
        currentAcceleration = currentAcceleration.mul(timeSlice);

        Vector3D halfStepVel = currentVelocity.add(currentAcceleration);
        //TODO MAJOR CHANGE ONE LINE BELOW
//        halfStepVel = halfStepVel.div(timeSlice);

        current.setVelocity(halfStepVel);
    }

    protected void calculateNewLocation(Body current, double timeSlice) {
        // calculates new location, used half step velocity calculation and needs to happen after that calculation
        Vector3D currentLocation =  new Vector3D(current.location);
        Vector3D modifiedHalfStepVelc =  new Vector3D(current.velocity).mul(timeSlice);




        Vector3D newLocation = currentLocation.add(modifiedHalfStepVelc);

        current.setLocation(newLocation);

    }

    /*
    TODO : do we need to change all locations first? because we need the new location in order to calculate the new velocity at the end of the timeSlice
    this method needs to run only after we computed and set the new location based on the half-step velocity, can also be modified if the halfstep velocity is already set as the velocity of the current body.
     */
    protected void calculateFullVelocity(Body current, double timeSlice) {
        Vector3D currentAcc =  new Vector3D(current.acceleration);
        Vector3D currentVelocity = new Vector3D(current.velocity);
        currentAcc = currentAcc.div(2);
        currentAcc = currentAcc.mul(timeSlice);
        Vector3D newVelocity = currentVelocity.add(currentAcc);

        current.setVelocity(newVelocity);

    }


    public void calculateVerletForce(List<Body> bodies, double timeSlice) {
        /*
        calculating and setting the acceleration for all bodies
         */

        // calculating the acceleration for each body
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).resetAcceleration();
            for (int j = 0; j < bodies.size(); j++) {
                calculateAcceleration(bodies.get(i), bodies.get(j));
            }
        }
        // calculating the half step velocity, then the new location, then resetting the acceleration as we changed the location
        for (int i = 0; i < bodies.size(); i++) {
            if (bodies.get(i).name != "Sun") {
                // testing not moving the sun, so not setting a new location and not changing the velocity
                halfStepVelocity(bodies.get(i), timeSlice);
                calculateNewLocation(bodies.get(i), timeSlice);
            }
//            halfStepVelocity(bodies.get(i), timeSlice);
//            calculateNewLocation(bodies.get(i), timeSlice);
            bodies.get(i).resetAcceleration();
        }



        // calculating new acceleration based on the new location
        for (int i = 0; i < bodies.size(); i++) {
            for (int j = 0; j < bodies.size(); j++) {
                calculateAcceleration(bodies.get(i), bodies.get(j));
            }
        }
        // calculate the velocity at the end of the time-slice based on the half-step velocity and the new location and acceleration.
        for (int i = 0; i < bodies.size(); i++) {
            if (bodies.get(i).name != "Sun") {
                calculateFullVelocity(bodies.get(i), timeSlice);
            }
        }

    }

    public void alternativeLocation(Body current, double timeslice) {
        Vector3D currentLoc = current.location;
        Vector3D currentVel = current.velocity;

        currentVel = currentVel.mul(timeslice);

        Vector3D currentAcc = current.acceleration;

        currentAcc = currentAcc.mul((0.5*timeslice * timeslice));

        Vector3D newLoc = currentLoc.add(currentVel).add(currentAcc);
        current.setLocation(newLoc);

    }

    public void altVelocity(Body current, double timeslice) {
        Vector3D currentVel = current.velocity;
        Vector3D oldAcc = current.oldAcc;
        Vector3D currentAcc = current.acceleration;
        Vector3D accelerations = oldAcc.add(currentAcc);
        accelerations = accelerations.mul(0.5);
        accelerations = accelerations.mul(timeslice);
        Vector3D newVelocity = currentVel.add(accelerations);
        current.setVelocity(newVelocity);
    }


    public void altCalculateForces(List<Body> bodies, double timeSlice) {
        /*
        calculating and setting the acceleration for all bodies
         */

        // calculating the acceleration for each body
        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).resetAcceleration();
            for (int j = 0; j < bodies.size(); j++) {
                calculateAcceleration(bodies.get(i), bodies.get(j));
            }
        }

        for (int i = 0; i < bodies.size(); i++) {
            if (bodies.get(i).name != "Sun") {
                // testing not moving the sun, so not setting a new location and not changing the velocity
                alternativeLocation(bodies.get(i), timeSlice);
                bodies.get(i).setOldAcc();
            }
        }

        for (int i = 0; i < bodies.size(); i++) {
            bodies.get(i).resetAcceleration();
            for (int j = 0; j < bodies.size(); j++) {
                calculateAcceleration(bodies.get(i), bodies.get(j));
                altVelocity(bodies.get(i), timeSlice);
            }
        }


    }
}
