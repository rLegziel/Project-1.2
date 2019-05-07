package nbody.model;
public class PIDController {
    /* pseudocode from Wikipedia:
    previous_error = 0
    integral = 0
    loop:
      error = setpoint - measured_value
      integral = integral + error * dt
      derivative = (error - previous_error) / dt
      output = Kp * error + Ki * integral + Kd * derivative
      previous_error = error
      wait(dt)
      goto loop
     */

    private double previousErrorX;
    private double previousErrorY;
    private double integralX;
    private double integralY;
    private double error;
    private double setpointX;  // desired location x
    private double setpointY;  // desired location y
    private double measuredValue;  // actual location ?
    private double time;  // starting time
    private double timeSlice;  // timestep ?
    private double derivative;
    private double output;
    private double kp;
    private double ki;
    private double kd;

    public PIDController(double timeSlice, double setpointX, double setpointY) {
        this.setpointX = setpointX;
        this.setpointY = setpointY;
        this.timeSlice = timeSlice;
        previousErrorX = 0;
        previousErrorY = 0;
        integralX = 0;
        integralY = 0;
        kp = 2;
        ki = 0.5;
        kd = 0;
        //this.time = time;
    }

    public double computeNewX(double currentX,double titanX) {
        error = titanX - currentX;
        integralX = integralX + error * timeSlice;
        derivative = (error - previousErrorX) / timeSlice;
        output = kp * error + ki * integralX + kd * derivative;
        previousErrorX = error;
        return output;
    }

    public double computeNewY(double currentY,double titanY) {
        error = titanY - currentY;
        integralY = integralY + error * timeSlice;
        derivative = (error - previousErrorY) / timeSlice;
        output = kp * error + ki * integralY + kd * derivative;
        previousErrorY = error;
        return output;
    }
/*
    public double computeControllerOutput(double error) {
        //error = setpoint - measuredValue;
        integral = integral + error * dt;
        derivative = (error - previousError) / dt;
        output = kp * error + ki * integral + kd * derivative;
        previousError = error;
        //time = time + dt;
        return output;
    }
*/
}
