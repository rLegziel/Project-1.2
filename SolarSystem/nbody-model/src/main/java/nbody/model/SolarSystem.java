package nbody.model;

import java.util.Arrays;
import java.util.Random;

import static nbody.model.CelestialBody.*;

public class SolarSystem extends BodySystem {

    // the list of celestial bodies in our system, use names from enum!
    private static CelestialBody[] CELESTIAL_BODIES_IN_SYSTEM = new CelestialBody[] {SUN, MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, NEPTUNE, URANUS, PLUTO,TITAN};


    /**
     * our constructor, creates a solar system using the method below.
     */
    public SolarSystem() {
        super();
        createSolarSystem();
    }

    /**
     * a method that creates the solar system based on the CB array at the top of the page.
     */
    private  void createSolarSystem() {

        Arrays.stream(CELESTIAL_BODIES_IN_SYSTEM).forEach((celestialBody) -> {
            final Body body = celestialBody.getAsBody();
            addBody(body);
        });
    }

}
