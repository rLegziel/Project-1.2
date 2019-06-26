package model;

import java.util.Date;

/**
 * class for facilitating time conversion when we need to
 */
public class TimeConverter {

    private double initMillis = 1.486249200000E+12;

    //if we want to get the date from the elapsed seconds
    public String currentDate(long elapsedSeconds){
        long currentMillis = (long) (initMillis+elapsedSeconds*1000);
        Date d = new Date(currentMillis);
        //System.out.println(d.toString());
        return(""+d.toString());
    }

    //if we want to get the elapsed time in a string format
    public static String getElapsedTimeAsString(long elapsedSeconds) {
        long years = elapsedSeconds / Consts.SEC_IN_YEAR;
        long days = (elapsedSeconds % Consts.SEC_IN_YEAR) / Consts.SEC_IN_DAY;
        long hours = ( (elapsedSeconds % Consts.SEC_IN_YEAR) % Consts.SEC_IN_DAY) / Consts.SEC_IN_HOUR;
        long minutes = ( ((elapsedSeconds % Consts.SEC_IN_YEAR) % Consts.SEC_IN_DAY) % Consts.SEC_IN_HOUR) / Consts.SEC_IN_MINUTE;
        long seconds = ( ((elapsedSeconds % Consts.SEC_IN_YEAR) % Consts.SEC_IN_DAY) % Consts.SEC_IN_HOUR) % Consts.SEC_IN_MINUTE;
        return String.format("Years:%04d, Days:%03d, Hours:%02d, Minutes:%02d, Seconds:%02d", years, days, hours, minutes, seconds);
    }

}
