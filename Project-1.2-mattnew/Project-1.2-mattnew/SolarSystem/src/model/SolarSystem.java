package model;

import java.util.Arrays;

import static model.CelestialBody.*;

/**
 * class for our solar system initialised with the celestial bodies enum
 */
public class SolarSystem extends BodySystem {

    // the list of celestial bodies in our system, use names from enum!
    private static CelestialBody[] CELESTIAL_BODIES_IN_SYSTEM = new CelestialBody[] {SUN, MERCURY, VENUS, EARTH, MARS, JUPITER, SATURN, NEPTUNE, URANUS, PLUTO,TITAN};
    private static CelestialBodyReturn[] CELESTIAL_BODIES_IN_SYSTEM_2 = new CelestialBodyReturn[]{CelestialBodyReturn.SUN, CelestialBodyReturn.MERCURY,
            CelestialBodyReturn.VENUS, CelestialBodyReturn.EARTH, CelestialBodyReturn.MARS, CelestialBodyReturn.JUPITER, CelestialBodyReturn.SATURN,
            CelestialBodyReturn.NEPTUNE, CelestialBodyReturn.URANUS, CelestialBodyReturn.PLUTO,CelestialBodyReturn.TITAN,CelestialBodyReturn.MOON};

    private int planet;

    //our constructor, creates a solar system using the method below.
    public SolarSystem(int planet) {
        super(planet);
        this.planet = planet;
        createSolarSystem();
    }

    //a method that creates the solar system based on the CB array at the top of the page
    private  void createSolarSystem() {
        //depending on the plante we are flying to, we use different enum for different planet arrangement
        if(planet == 0){
            System.out.println("using normal arrangement");
            Arrays.stream(CELESTIAL_BODIES_IN_SYSTEM).forEach((celestialBody) -> {
                final Body body = celestialBody.getAsBody();
                addBody(body);
            });
        }else{
            System.out.println("using return arrangement");
            Arrays.stream(CELESTIAL_BODIES_IN_SYSTEM_2).forEach((celestialBody) -> {
                final Body body = celestialBody.getAsBody();
                addBody(body);
            });
        }

    }

}
