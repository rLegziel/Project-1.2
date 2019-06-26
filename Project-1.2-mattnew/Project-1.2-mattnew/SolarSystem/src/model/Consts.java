package model;

import javafx.scene.paint.Color;

/**
 * class containing all constants used
 */
public class Consts {
    public static final double G = 6.67300E-11;

    public static final double G_TITAN = 1.352;

    public static final double G_EARTH = 9.807;

    public static final Color TITAN_COLOR = Color.POWDERBLUE;

    public static final int SEC_IN_MINUTE = 60;
    public static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
    public static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
    public static final int SEC_IN_YEAR = 31556926;
    public static final int SEC_in_360_DAYS = 31104000;

    public static final long FIRST_LAUNCH = 185238000 + SEC_IN_DAY/8;

    public static final long RETURN_DATE = 239064838;

    public static final long RETURN_LAUNCH = 60380000;
}
