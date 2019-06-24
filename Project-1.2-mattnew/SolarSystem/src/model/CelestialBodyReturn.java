package model;



/*

Mass and radius are from NASA
Initial positions and speed generated from NASA Horizons system

    Date: 2457875.500000000 = A.D. 2024-Nov-28 00:00:00.0000 TDB
    Units: km, km /sek, @0 (sun barycenter)


*/


import javafx.scene.paint.Color;

/** starting enum to include starting information about all bodies in our system
 *
 */
public enum CelestialBodyReturn {

    //Name                  Mass            Radius         X                        Y                        Z                       VX                       VY                      VZ
    SUN("Sun",              1.98855E+30,    695700000.0,   0,                       0,                       0,                      0,                       0,                      0, Color.ORANGE, 5),

    MERCURY("Mercury",      0.33011E+24,    2439.7E+3,    4.347399770964374E+07,  2.050979353147511E+07,  -2.307501436666414E+06,  -3.050073497777568E+01,  4.608809935899536E+01, 6.565471919172483E+00, Color.YELLOW, 2),

    VENUS("Venus",          4.8675e24,      6051.8E+3,    1.074777438886537E+08,  -6.710836996726556E+06,   -6.308021148522174E+06,  1.792316510441160E+00,  3.480040825290161E+01, 3.751239610662847E-01, Color.DARKRED, 2),

    EARTH("Earth",          5.9723E+24,     6371.008E+3,  5.938022284200083E+07,  1.340117154496255E+08,  1.967137021987885E+04,  -2.767089338752126E+01,  1.205569407419115E+01,  2.240905913160063E-04, Color.BLUE, 2),

    MARS("Mars",            0.64171E+24,    3389.5E+3,     -1.195365355037747E+07,   2.347581647951706E+08,   5.233047574111208E+06, -2.327511633520385E+01,   9.147209729582990E-01,  5.902439968468398E-01, Color.RED, 2),

    JUPITER("Jupiter",      1898.19E+24,    69911E+3,     1.948892219338753E+08,  7.322471790683782E+08,   -7.397591881554604E+06,  -1.277261001438617E+01,  3.983871162024438E+00, 2.693619350677068E-01, Color.LIGHTGOLDENRODYELLOW, 4),

    SATURN("Saturn",        568.34E+24,     58232E+3,     1.410584492179444E+09,  -2.924974939151531E+08,   -5.107672081928881E+07,  1.423678269128572E+00,  9.437409978945865E+00, -2.202386088108272E-01, Color.LIGHTYELLOW, 3),

    NEPTUNE("Neptune",      102.413E+24,    24622E+3,      4.468851339430182E+09,  -1.116651341198400E+08,  -1.006896064998923E+08,  9.923437633095275E-02,   5.465177028616881E+00, -1.148881171928064E-01, Color.DARKBLUE, 3),

    URANUS("Uranus",        86.813e24,      25362E+3,      1.676799547063509E+09,   2.396484916285450E+09,  -1.282261364591289E+07, -5.629669301436437E+00,   3.586659725251753E+00,  8.616834148973740E-02, Color.MEDIUMPURPLE, 3),

    PLUTO("Pluto",          0.01303E+24,     1187E+3,      2.712050354821905E+09,  -4.494611054846544E+09,   -3.035377698321524E+08 , 4.784107164824263E+00,   1.589336199829583E+00, -1.564361388343911E+00, Color.LIGHTYELLOW.DEEPSKYBLUE, 1),

    TITAN("Titan",         0.3452E+23, 2575.73, 1.411624016670157E+09, -2.930442769369250E+08, -5.089824660769621E+07, 4.111707988396002E+00, 1.381985223032062E+01, -2.748107666351749E+00, Color.SILVER, 1),
    MOON("Moon",7.349E+22,1738.0,5.902859089816485E+07,1.338129873435270E+08,4.563732095085084E+03,-2.717646529200106E+01,1.122055385094676E+01,-7.731098341867604E-02,Color.GREY,1);

    public final String celestialName;
    public final double mass;   // mass in kg
    public final double radius; // radius in meters
    public Vector3D location; // location in meters, 3D vector that contains X,Y,Z coordinates taken by horizons
    public Vector3D velocity; // velocity in meters per second, also a 3DVector with XV,YV,ZV
    public Color color;
    public int scale;


    /**
     *
     * @param celestialName
     * @param mass       in kg
     * @param radius    in m
     * @param x         in m
     * @param y         in m
     * @param z         in m
     * @param x_vel     in km/s
     * @param y_vel     in km/s
     * @param z_vel     in km/s
     */
    CelestialBodyReturn(String celestialName, double mass, double radius, double x, double y, double z, double x_vel, double y_vel, double z_vel, Color color, int scale) {
        this.celestialName = celestialName;
        this.mass = mass;
        this.radius = radius;
        this.location = new Vector3D(x*1000, y*1000, z*1000); // scaling to meters.
        this.velocity = new Vector3D(x_vel*1000, y_vel*1000, z_vel*1000); // scaling to meters.
        this.color = color;
        this.scale = scale;
    }

    /**
     * @return Body, which will be any of the celestial body we want.
     */
    public Body getAsBody() {
        Body body = new Body();
        body.location = new Vector3D(this.location);
        body.mass = this.mass;
        body.name = this.celestialName;
        body.velocity = new Vector3D(this.velocity);
        body.radius = this.radius;
        body.color = this.color;
        body.scale = scale;
        return body;
    }
}

