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
    private double setpoint;  // desired location ?
    private double measuredValue;  // actual location ?
    private double time;  // starting time
    private double dt;  // timestep ?
    private double derivative;
    private double output;
    private double kp;
    private double ki;
    private double kd;

    public PIDController(double time, double dt) {
        previousError = 0;
        integral = 0;
        this.time = time;
        this.dt = dt;
    }

    public double computeControllerOutput(double endLocation) {
        // endLocation is the desired ending location so Titan's orbit ?

        // todo : getSetpoint(time)
        // todo : getMeasuredValue(time)

        // not sure if this is the correct stopping condition
        while (measuredValue < endLocation + 1000) {
            error = setpoint - measuredValue;
            integral = integral + error * dt;
            derivative = (error - previousError) / dt;
            output = kp * error + ki * integral + kd * derivative;
            previousError = error;
            time = time + dt;
        }
        return output;
    }

}
