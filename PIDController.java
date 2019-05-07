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

    private double previousError;
    private double integral;
    private double error;
    private double setpointX;  // desired location x
    private double setpointY;  // desired location y
    private double measuredValue;  // actual location ?
    private double time;  // starting time
    private double dt;  // timestep ?
    private double derivative;
    private double output;
    private double kp;
    private double ki;
    private double kd;

    public PIDController(double dt, double setpointX, double setpointY) {
        this.setpointX = setpointX;
        this.setpointY = setpointY;
        this.dt = dt;
        previousError = 0;
        integral = 0;
        kp = 1;
        ki = 0;
        kd = 0;
        //this.time = time;
    }

    public double computeNewX(double currentX) {
        error = setpointX - currentX;
        return computeControllerOutput(error);
    }

    public double computeNewY(double currentY) {
        error = setpointY - currentY;
        return computeControllerOutput(error);
    }

    public double computeControllerOutput(double error) {
        //error = setpoint - measuredValue;
        integral = integral + error * dt;
        derivative = (error - previousError) / dt;
        output = kp * error + ki * integral + kd * derivative;
        previousError = error;
        //time = time + dt;
        return output;
    }

}
