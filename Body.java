import java.io.*;
import java.util.*;
import java.lang.Object;

public class Body{

    // starting date 05.06.2020
    /*
    /Saturn Perihelion Date: 28.11.2032  || Saturn Year is 10,759.22 days long || 4559 days after start date
     Jupiter Perihelion Date: January 20, 2023 || Jupiter year is  4,332.59 days long || 959 days after start date
     Mars Perihelion Date: September 16, 2018 || Mars year is 686.98 days long || 628 before starting date
     Earth Perihelion Date: 2020 Jan 05 || Earth year is 365.25 days long || 152 days before start date


      distances are in km, years are in days.
     */
    public double PDistance;
    public double ADistance;
    public double year;
    public double timeToStart;

    public Body(double vPDistance,double vADistance,double vYear,double vTimeToStart){
        PDistance = vPDistance;
        ADistance = vADistance;
        year = vYear;
        timeToStart = vTimeToStart;
    }

    public static double differenceBetweenDistances(double aDistance,double pDistance){ // calculates the difference between perihelion to aphelion
        return aDistance-pDistance;
    }

    public static double degreesChange(double year){ // calculates how many degrees per day
        return 180/(year/2);
    }

    public static double distancePerDay(double difference,double year){ // calculates the how the distance of the body changes per day in relation to the sun
        return difference/(year/2);
    }

    public static double movementPerDegree(double difference){ // calculates how much the star moves in relation to the sun per degree
        return difference/180;
    }
    public static double degreestoStartDate(double toStart,double year){
        double degrees =  (toStart/year) * 360;
        if(degrees>0){
            return degrees;
        }
        else{
            return (360+degrees)-180;
        }
    }




    public double saturnPDistance = 1352544268.211;
    public double saturnADistance = 1514498922.988;
    public double saturnYear = 10759.22;
    public double saturnDifference = saturnADistance-saturnPDistance;
    public double saturnDegreesChange = 180/(saturnYear/2);
    public double saturnDistancePerDay = saturnDifference/(saturnYear/2);
    public double saturnKMDifference = saturnDifference/180; // movement per degree
    public double saturnAmountOfDegreestoStartDate = 4559*saturnDegreesChange;

    public double jupiterPDistance = 816624856.3588;
    public double jupiterADistance = 740524419.5541;
    public double jupiterYear = 4332.59;
    public double jupiterDifference = jupiterADistance-jupiterPDistance;
    public double jupiterDegreesChange = 180/(jupiterYear/2);
    public double jupiterDistancePerDay = jupiterDifference/(jupiterYear/2);
    public double jupiterKMDifference = jupiterDifference/180; // movement per degree
    public double jupiterAmountOfDegreestoStartDate = 4559*jupiterDegreesChange;



    public double marsPDistance = 206654498.5297;
    public double marsADistance = 248332465.2956;
    public double marsYear = 686.98;
    public double marsDifference = marsADistance-marsPDistance;
    public double marsDegreesChange = 180/(marsYear/2);
    public double marsDistancePerDay = marsDifference/(marsYear/2);
    public double marsKMDifference = marsDifference/180; // movement per degree
    public double marsAmountOfDegreestoStartDate = 4559*marsDegreesChange;


    public double earthPDistance = 152141034.4612;
    public double earthADistance = 147095098.2839;
    public double earthYear = 365.25;
    public double earthDifference = earthADistance-earthPDistance;
    public double earthDegreeChange = 180/(earthYear/2);
    public double earthDistancePerDay = earthDifference/(earthYear/2);
    public double earthKMDifference = earthDifference/180; // movement per degree
    public double earthAmountOfDegreestoStartDate = 4559*earthDegreeChange;


public static void main(String[] args){

    Body Saturn = new Body(1352544268.211,1514498922.988,10759.22,4559);
    Body Jupiter = new Body(816624856.3588,740524419.5541,4332.59,959);
    Body Mars = new Body(206654498.5297,248332465.2956,686.98,-628);
    Body Earth = new Body(152141034.4612,147095098.2839,365.25,-152);

}






}
