package nbody.model;

public class Probe extends Body {


    public long launchDate;
    public double startVX;
    public double startVY;

    public long getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(long launchDate) {
        this.launchDate = launchDate;
    }

    public double getStartVX() {
        return startVX;
    }

    public void setStartVX(double startVX) {
        this.startVX = startVX;
    }

    public double getStartVY() {
        return startVY;
    }

    public void setStartVY(double startVY) {
        this.startVY = startVY;
    }

    public double getEndVX() {
        return endVX;
    }

    public void setEndVX(double endVX) {
        this.endVX = endVX;
    }

    public double getEndVY() {
        return endVY;
    }

    public void setEndVY(double endVY) {
        this.endVY = endVY;
    }

    public double getMinDistance() {
        return minDistance;
    }

    public void setMinDistance(double minDistance) {
        this.minDistance = minDistance;
    }

    public double endVX;
    public double endVY;
    public double minDistance;


    public Probe() {
        super();
        if (acceleration == null) {
            acceleration = new Vector3D();
        }
        if (velocity == null) {
            velocity = new Vector3D();
        }
        if (location == null) {
            location = new Vector3D();
        }
    }

    public Probe(Vector3D location, Vector3D velocity, double radius, double mass, String name, long launchDate,
                 double startVX, double startVY) {
        super(location,velocity,radius,mass);
        this.launchDate = launchDate;
        this.name = name;
        this.startVX = startVX;
        this.startVY = startVY;
    }


}
