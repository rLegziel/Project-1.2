package model;

import java.util.Date;

public class TimeConverter {

    private static final int SEC_IN_MINUTE = 60;
    private static final int SEC_IN_HOUR = SEC_IN_MINUTE * 60;
    private static final int SEC_IN_DAY = SEC_IN_HOUR * 24;
    private static final int SEC_IN_YEAR = 31556926;

    private double initMillis = 1.486249200000E+12;

    public String currentDate(long elapsedSeconds){
        long currentMillis = (long) (initMillis+elapsedSeconds*1000);
        Date d = new Date(currentMillis);
        //System.out.println(d.toString());
        return(""+d.toString());
    }

}
