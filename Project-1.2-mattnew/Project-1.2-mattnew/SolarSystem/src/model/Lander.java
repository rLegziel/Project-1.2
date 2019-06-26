package model;


public interface Lander {
    void changeYPID(double timeSlice,double kp,double ki,double kd);

    void changeXLPID(double timeSlice,double kp,double ki,double kd);

    void changeXDPID(double timeSlice,double kp,double ki,double kd);

    void changeYLocationPID(double timeSlice,double kp,double ki,double kd);

    //calculates wind speen based on altitude
    double calculateWindSpeed(double height,int windCounter);

    //uses the physics system and PID controller based on the thrusters calculated by the PID
    void calculateAccelerationLanding(Vector3D thrusterForce);

    //calculates the thruster for needed at that moment for changing to correct location and angle
    Vector3D thrusterForceLanding();

    double getWindSpeed();

    double getAngle();

    double getScalingFactor();

    Vector3D getVelocity();

    Vector3D getLocation();

    double getDispLocX();

    double getDispLocY();

}
