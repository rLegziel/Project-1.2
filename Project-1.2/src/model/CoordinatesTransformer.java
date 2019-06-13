package model;

/**
 * transforms the coordinates to another coordinate system
 */
public class CoordinatesTransformer {

    private  double scale;
    private double originXForOther;
    private double originYForOther;

    /**
     * @return getter for the scale used.
     */
    public double getScale() {
        return scale;
    }

    /**
     * @param scale a scale we want to set for the canvas
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    public double getOriginXForOther() {
        return originXForOther;
    }

    public void setOriginXForOther(double originXForOther) {
        this.originXForOther = originXForOther;
    }

    public double getOriginYForOther() {
        return originYForOther;
    }

    public void setOriginYForOther(double originYForOther) {
        this.originYForOther = originYForOther;
    }

    /**
     * Converts a model x coordinate to a x coordinate in another coordinate system.
     * @param x
     * @return
     */
    public double modelToOtherX(double x) {
        return this.originXForOther + getModelToOtherDistance(x);
    }

    /**
     * Converts a model y coordinate to a y coordinate in another coordinate system.
     *
     * @param y
     * @return
     */
    public double modelToOtherY(double y) {
        return this.originYForOther + getModelToOtherDistance(y);
    }

    /**
     * Scales a distance in the model to a number of units in the other coordinate system.
     *
     * @param distance
     * @return
     */
    public double getModelToOtherDistance(double distance) {
        return distance / scale;
    }

}
