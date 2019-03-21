import java.io.*;
import java.util.*;
import java.lang.Object;
import java.math.*;

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
    public double degreesFromSun;
    public double difference;
    public double distancePerDay;
    public double xLocation;
    public double yLocation;
    public boolean increasing;

    public Body(double vPDistance,double vADistance,double vYear,double degreesFromSun){
        PDistance = vPDistance;
        ADistance = vADistance;
        year = vYear;
        difference = differenceBetweenDistances(ADistance,PDistance);
        distancePerDay = distancePerDay(difference,year);
        degreesFromSun = 0;
        xLocation = vADistance;
        yLocation =0;
    }

    public Body(){

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


    public  void checkIncreasing(){
        if (xLocation <=PDistance){
            increasing =true;
        }if(xLocation >= ADistance){
            increasing = false;
        }
    }

    public double getxLocation(){
        return xLocation;
    }

    public double getyLocation(){
        return yLocation;
    }

    public double getDegreesFromSun(){
        return degreesFromSun;
    }


//    public void SaturnMovement(){
//        checkIncreasing();
//        if(increasing == true) {
//            xLocation = xLocation + distancePerDay;
//        }else{
//            xLocation = xLocation - distancePerDay;
//        }
//        double firstOne = Math.pow(1.427E+09,2);
//        double topSecond = (Math.pow(xLocation-(7.6955E+07),2) * (Math.pow(1.427E+09,2)));
//        double bottomSecond = Math.pow((1.4293E+09),2);
//        System.out.println(firstOne + " this is the first object");
//        System.out.println(topSecond/bottomSecond + " is the second");
//        double yLocationSquared = firstOne - (topSecond/bottomSecond);
//        System.out.println(yLocationSquared +" is y squared");
//        double margin = 3.406182845718627E7/(year/2);
//         yLocation = Math.sqrt(0-yLocationSquared) ;
//         degreesFromSun = degreesFromSun + degreesChange(year);
//
//    }

    public void SaturnMovement(){
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        System.out.println(xLocation + " is our x location");
        double firstOne = Math.pow(1.427E+09,2);
        double topSecond = (Math.pow(xLocation-(7.6955E+07),2) * (Math.pow(1.427E+09,2)));
        double bottomSecond = Math.pow((1.4293E+09),2);
        System.out.println(firstOne + " this is the first object");
        System.out.println(topSecond/bottomSecond + " is the second");
        double yLocationSquared = firstOne - (topSecond/bottomSecond);
        System.out.println(yLocationSquared +" is y squared");
        double margin = 3.406182845718627E7/(year/2);
        yLocation = Math.sqrt(0-yLocationSquared) ;
        degreesFromSun = degreesFromSun + degreesChange(year);

    }

    public void mercuryMovement(){
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;

        }else{
            xLocation = xLocation - distancePerDay;

        }
        double yLocationSquared = Math.pow((5.67*Math.pow(10,6)),2) - Math.pow((xLocation - (1.119*Math.pow(10,6))),2) / Math.pow(5.67*Math.pow(10,6),2);
        yLocation = Math.sqrt(yLocationSquared);
    }

    public void venusMovement(){
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        double yLocationSquared = Math.pow((1.0744*Math.pow(10,8)),2) - Math.pow((xLocation - (7.52*Math.pow(10,5))),2) / Math.pow(1.0745*Math.pow(10,8),2);
        yLocation = Math.sqrt(yLocationSquared);
    }

    public void earthMovement(){ //// fix equations
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        double yLocationSquared = Math.pow((1.4954*Math.pow(10,8)),2) - Math.pow((xLocation - (2.54*Math.pow(10,6))),2) / Math.pow(1.4956*Math.pow(10,8),2);
        yLocation = Math.sqrt(yLocationSquared);
        degreesFromSun = degreesFromSun + degreesChange(year);
    }

    public void marsMovement(){
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        double yLocationSquared = Math.pow((2.27*Math.pow(10,8)),2) - Math.pow((xLocation - (2.14*Math.pow(10,7))),2) / Math.pow(2.28*Math.pow(10,8),2);
        yLocation = Math.sqrt(yLocationSquared);
    }

    public void jupiterMovement(){
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        double yLocationSquared = Math.pow((7.75*Math.pow(10,8)),2) - Math.pow((xLocation - (3.81*Math.pow(10,7))),2) / Math.pow(7.785*Math.pow(10,8),2);
        yLocation = Math.sqrt(yLocationSquared);
    }

    public void uranusMovement(){ // make sure with luc
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        double yLocationSquared = Math.pow((2.888*Math.pow(10,9)),2) - Math.pow((xLocation - (1.32*Math.pow(10,8))),2) / Math.pow(2.872*Math.pow(10,9),2);
        yLocation = Math.sqrt(yLocationSquared);
    }

    public void neptuneMovement(){
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        double yLocationSquared = Math.pow((4.960*Math.pow(10,9)),2) - Math.pow((xLocation - (4.946*Math.pow(10,7))),2) / Math.pow(4.4962*Math.pow(10,9),2);
        yLocation = Math.sqrt(yLocationSquared);
    }

    public void plutoMovement(){
        checkIncreasing();
        if(increasing == true) {
            xLocation = xLocation + distancePerDay;
        }else{
            xLocation = xLocation - distancePerDay;
        }
        double yLocationSquared = Math.pow((5.570*Math.pow(10,9)),2) - Math.pow((xLocation - (1.447*Math.pow(10,9))),2) / Math.pow(5.929*Math.pow(10,9),2);
        yLocation = Math.sqrt(yLocationSquared);
    }

//
//
//
//
//
//    public double saturnPDistance = 1352544268.211;
//    public double saturnADistance = 1514498922.988;
//    public double saturnYear = 10759.22;
//    public double saturnDifference = saturnADistance-saturnPDistance;
//    public double saturnDegreesChange = 180/(saturnYear/2);
//    public double saturnDistancePerDay = saturnDifference/(saturnYear/2);
//    public double saturnKMDifference = saturnDifference/180; // movement per degree
//    public double saturnAmountOfDegreestoStartDate = 4559*saturnDegreesChange;
//
//    public double jupiterPDistance = 816624856.3588;
//    public double jupiterADistance = 740524419.5541;
//    public double jupiterYear = 4332.59;
//    public double jupiterDifference = jupiterADistance-jupiterPDistance;
//    public double jupiterDegreesChange = 180/(jupiterYear/2);
//    public double jupiterDistancePerDay = jupiterDifference/(jupiterYear/2);
//    public double jupiterKMDifference = jupiterDifference/180; // movement per degree
//    public double jupiterAmountOfDegreestoStartDate = 4559*jupiterDegreesChange;
//
//
//
//    public double marsPDistance = 206654498.5297;
//    public double marsADistance = 248332465.2956;
//    public double marsYear = 686.98;
//    public double marsDifference = marsADistance-marsPDistance;
//    public double marsDegreesChange = 180/(marsYear/2);
//    public double marsDistancePerDay = marsDifference/(marsYear/2);
//    public double marsKMDifference = marsDifference/180; // movement per degree
//    public double marsAmountOfDegreestoStartDate = 4559*marsDegreesChange;
//
//
//    public double earthPDistance = 152141034.4612;
//    public double earthADistance = 147095098.2839;
//    public double earthYear = 365.25;
//    public double earthDifference = earthADistance-earthPDistance;
//    public double earthDegreeChange = 180/(earthYear/2);
//    public double earthDistancePerDay = earthDifference/(earthYear/2);
//    public double earthKMDifference = earthDifference/180; // movement per degree
//    public double earthAmountOfDegreestoStartDate = 4559*earthDegreeChange;


public static void main(String[] args){

    Body Saturn = new Body(1352544268.211,1514498922.988,10759.22,0);
    Body Saturn2 = new Body(1352544268.211,1514498922.988,10759.22,0);
    Body earth = new Body(147095098.2839,152141034.4612,365.25,0);
    Body body = new Body();
    body.run();



    Body Jupiter = new Body(816624856.3588,740524419.5541,4332.59,959);
    Body Mars = new Body(206654498.5297,248332465.2956,686.98,-628);
    Body Earth = new Body(152141034.4612,147095098.2839,365.25,-152);


}

    public void run() {
        Body Saturn = new Body(-1352544268.211, 1514498922.988, 10759.22, 0);
        Body Saturn2 = new Body(1352544268.211, 1514498922.988, 10759.22, 0);
        Body earth = new Body(147095098.2839, 152141034.4612, 365.25, 0);
        for (int i = 0; i < 470; i++) {
            Saturn.SaturnMovement();
        }
        double currentLocationX = Saturn.getxLocation();
        double currentLocationY = Saturn.getyLocation();
        double degreesFromSun = Saturn.getDegreesFromSun();
        System.out.println(currentLocationX + " is the x[saturn] location " + degreesFromSun + " is the degrees from the sun");

        for (int j = 0; j < 15; j++) {
            earth.earthMovement();
        }
        System.out.println(earth.getxLocation() + " is the x[earth] location " + earth.getDegreesFromSun() + " is the degrees from the sun");
    }


    }








