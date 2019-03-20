import java.util.ArrayList;
import java.util.List;

public class BodySystem {

    public static final double G = 6.67300E-11;  // universal gravity constant

    private static final int SEC_IN_MINUTE = 60;
    private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
    private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
    private static final int SEC_IN_YEAR = 31556926;
    private long elapsedSeconds = 0;

    private static Body.CelestialBody[] bodiesInSystem = {Body.CelestialBody.SUN, Body.CelestialBody.MERCURY, Body.CelestialBody.VENUS, Body.CelestialBody.EARTH,
            Body.CelestialBody.MARS, Body.CelestialBody.JUPITER, Body.CelestialBody.SATURN, Body.CelestialBody.NEPTUNE, Body.CelestialBody.URANUS,
            Body.CelestialBody.PLUTO, Body.CelestialBody.TITAN, Body.CelestialBody.PROBE};

    private List<Body> bodies;
    private boolean probeAdded = false;

    public BodySystem() {
        bodies = new ArrayList<>();
        createSystem();
    }

    public List<Body> getBodies() {
        return bodies;
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
        probe.location.add(Body.CelestialBody.EARTH.getAsBody().location);
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


}
