package nbody.model;


/**
 *  implementation of Vector3D class,
 */
public class Vector3D {
    public double x;
    public double y;
    public double z;

    public Vector3D() {
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D(Vector3D other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector3D add(Vector3D other) {
        if (other != null) {
            x += other.x;
            y += other.y;
            z += other.z;
        }
        return this;
    }

    /**
     * @param other the vector we want to substract
     * @return origin vector-the other
     */
    public Vector3D sub(Vector3D other) {
        x -= other.x;
        y -= other.y;
        z -= other.z;
        return this;
    }

    /**
     * @param factor to what factor we multiply the vector
     * @return the multiplied vector.
     */
    public Vector3D mul(double factor) {
        if (x != 0) {
            this.x *= factor;
        }
        if (y != 0) {
            this.y *= factor;
        }
        if (z != 0) {
            this.z *= factor;
        }
        return this;
    }

    /**
     * @param factor, the factor we divide in.
     * @return
     */
    public Vector3D div(double factor) {
        if (x != 0) {
            this.x /= factor;
        }
        if (y != 0) {
            this.y /= factor;
        }
        if (z != 0) {
            this.z /= factor;
        }
        return this;
    }

    /**
     * @return normalize the vector, multiplying by 1/its length.
     */
    public Vector3D normalize() {
        return mul(1.0/length());
    }

    public double lengthSquared() {
        double xx = 0;
        double yy = 0;
        double zz = 0;
        if (this.x != 0 ) {
            xx = this.x*this.x;
        }
        if (this.y != 0 ) {
            yy = this.y*this.y;
        }
        if (this.z != 0 ) {
            zz = this.z*this.z;
        }
        return xx + yy + zz;
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double distanceSquared(Vector3D other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        double dz = this.z - other.z;
        return dx*dx+dy*dy+dz*dz;
    }
    public double distance(Vector3D other) {
        return Math.sqrt(distanceSquared(other));
    }

    public double probeDistance(Vector3D other){
        double dx = Math.pow((this.x - other.x),2);
        double dy = Math.pow((this.y - other.y),2);

        return Math.sqrt(dx+dy);
    }

    @Override
    public String toString() {
        return String.format("x = %f, y = %f, z = %f", x, y, z);
    }

    public static void main (String[] args) {
        Vector3D v = new Vector3D(3,1,2);
        v.normalize();
        System.out.println(v.toString());
    }

}
