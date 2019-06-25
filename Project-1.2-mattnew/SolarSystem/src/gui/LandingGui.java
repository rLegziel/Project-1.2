package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;

public class LandingGui extends Application {

    private int planet = 0; //0 = titan, 1 = earth

    //scale  1 pixel = 375 meters
    //actual distance from titan
    private double scalingFactor = 375;

    private Timeline timeline;
    private Stage stage;
    private double canvasHeight = 700;
    private double canvasWidth = 1000;

    //info for design
    private Color planetColor = Color.POWDERBLUE;
    private double landerSize = 20; //in reality around 10 meters
    private Image landerImg = new Image("res/lander.png");
    private Image spaceshipImg = new Image("res/spaceship.png");
    private Image chosenLander = landerImg;

    private model.Lander lander;
    private model.Lander openLoopLander;

    private long elapsedTime = 238972800; //this is the elapsed time from when we enter the orbit
    private double timeSlice = 2;

    private int missionStage = 0; //0 = landing, 1 = ascending, 2 = switch back to orbit gui

    //for going back
    private long intitialLaunch = 60380000;

    private boolean changed1 = false;
    private boolean changed2 = false;
    private boolean changed3 = false;
    private boolean changed4 = false;

    public LandingGui(int planet){
        super();
        this.planet = planet;
    }

    public LandingGui(){
        super();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        //enterVelocity = bodySystem.getProbe().velocity;

        Vector3D initLanderLoc = new Vector3D(2790, 250000, 0);
        Vector3D initLanderVelocity = new Vector3D(0, 0, 0); //

        if(planet == 0){
            openLoopLander = new Lander("openLoopLander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.WHITE, scalingFactor, canvasWidth, canvasHeight,0);
            lander = new Lander("lander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.RED, scalingFactor, canvasWidth, canvasHeight,0);
        }else{
            openLoopLander = new Lander("openLoopLander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.WHITE, scalingFactor, canvasWidth, canvasHeight,1);
            lander = new Lander("lander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.RED, scalingFactor, canvasWidth, canvasHeight,1);
        }

        GraphicsContext gc = createGui(primaryStage);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(
                Duration.millis(1),
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent ae) {
                        updateFrame(gc,lander);
                    }
                });
        timeline.getKeyFrames().add(kf);
        timeline.play();
        primaryStage.show();
    }

    private GraphicsContext createGui(Stage stage) {

        String image = "res/stars_background.jpg";

        BorderPane border = new BorderPane();
        border.setStyle("-fx-background-image: url('" + image + "'); ");

        HBox hbox = new HBox();
        border.setBottom(hbox);
        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        border.setCenter(canvas);
        stage.setTitle("Landing simulation");

        Scene scene = new Scene(border);
        stage.setScene(scene);

        //stage.setMaximized(true);

        return canvas.getGraphicsContext2D();
    }

    protected void updateFrame(GraphicsContext gc, Lander chosenLander) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        //draw the spaceship
        if(planet == 0){
            gc.drawImage(spaceshipImg,canvasWidth/2-80,-35,350,80);
        }

        //draw the ground;
        gc.setFill(planetColor);
        gc.fillRect(0, canvasHeight - 40, canvasWidth, 40);
        //gc.fillOval((canvasWidth/2 - titanRad) ,canvasHeight-140,titanRad*2,titanRad*2);

//        gc.setFill(Color.RED);
//        gc.fillRect(lander.dispLocX,lander.dispLocY,landerSize,landerSize);
//        gc.rotate(30);

        gc.save();
        drawLander(gc, chosenLander.getAngle(),chosenLander);
        //drawLander(gc,openLoopLander.getAngle(),openLoopLander);
        //drawLander(gc,90);

        //text for info
        gc.setFill(Color.WHITE);
        gc.fillText("wind speed: " + chosenLander.getWindSpeed() + " mph", canvasWidth - 200, 100);
        gc.fillText("lander speedX: " + chosenLander.velocity.x, canvasWidth - 200, 150);
        gc.fillText("lander speedY: " + chosenLander.velocity.y, canvasWidth - 200, 200);
        gc.fillText("lander locationX: " + chosenLander.location.x, canvasWidth - 200, 250);
        gc.fillText("lander locationY: " + chosenLander.location.y, canvasWidth - 200, 300);
        //gc.fillText("distance from titan: " + chosenLander.getTitanDistance(), canvasWidth - 200, 350);
        gc.fillText("actual y:" + chosenLander.dispLocY, canvasWidth - 200, 350);
        gc.fillText("lander angle: " + chosenLander.getAngle(), canvasWidth - 200, 400);

        updateLanderPosition(gc,chosenLander);
        //System.out.println(lander.dispLocX+" "+lander.dispLocY);

        elapsedTime+=timeSlice;
    }

    private void drawLander(GraphicsContext gc, double rotationAngle, Lander chosenLander) {

        double delta = 0;

        if (chosenLander.dispLocX <= 0) {
            gc.fillText("not displaying change in y", 10, 100);
            delta = 0 - chosenLander.dispLocX;
        }

        double rotationCenterX = (chosenLander.dispLocX + landerSize / 2) + delta;
        double rotationCenterY = chosenLander.dispLocY + landerSize / 2;

        gc.save();
        gc.restore();

        gc.setFill(Color.TRANSPARENT);
        gc.transform(new Affine(new Rotate(rotationAngle, rotationCenterX, rotationCenterY)));
        gc.fillRect(chosenLander.dispLocX + delta, chosenLander.dispLocY, landerSize, landerSize);
        gc.drawImage(landerImg,chosenLander.dispLocX + delta, chosenLander.dispLocY, landerSize, landerSize);
        gc.restore();
    }

  private void updateLanderPosition(GraphicsContext gc, Lander chosenLander) {
      //uses the pid for rotating
      //lander.calculateAccelerationLanding(lander.thrusterForce());
      //openLoopLander.calculateAccelerationLanding(openLoopLander.getOLCThrusterForce());
      if (chosenLander.equals(lander)) {
          if(planet == 0){
              switch (missionStage){
                  case 0:
                      if(lander.getTitanDistance() > 28 * scalingFactor && openLoopLander.getTitanDistance() > 28 * scalingFactor){
                          lander.calculateAccelerationLanding(lander.thrusterForceLanding());
                      }else{
                          gc.fillText("Landed successfully!", canvasWidth / 2 - 50, canvasHeight / 2);
                          gc.clearRect(canvasWidth / 2 - 50,canvasHeight/2-50,150,100);
                          missionStage = 1;
                          lander.markAsLanded();
                      }
                      break;
                  case 1:
                      if(lander.dispLocY > 40){
                          lander.calculateAccelerationLanding(lander.thrusterForceAscending());
                          //lander.calculateAccelerationLanding(new Vector3D(0,0,0));
                      }else{
                          //et = 2.39133398E8;
                          missionStage = 2;
                      }
                      break;
                  case 2:
                      System.out.println(elapsedTime);
                      stage.close();
                      timeline.stop();
                      System.out.println("change in velocity for getting back to ship: " + lander.getTotalChangeInVelocity().toString());
                      try {
                          new Gui(1,elapsedTime,intitialLaunch).start(new Stage());
                      } catch (Exception e) {
                          e.printStackTrace();
                          System.out.println("not working");
                      }
              }

              //uncomment this if you want to switch for different PID
              //works better but is MUCH slower
//              if (lander.getTitanDistance() < 17500 && changed1 == false) {
//                  lander.changeYPID(1, 7, 0.0001, 10); // seems like the optimal, will lead to landing at y speed of about 0.27 m/s which is about 1 km/h.
//                  changed1 = true;
//                  // the ki is 0.0000001 , kp is 7
//              }
//              if (lander.getTitanDistance() < 40000 && changed2 == false) {
//                  lander.changeYPID(1, 1, 0.0000000001, 10);
////                lander.changeXLPID(1,0.0001,0,0.1); // optimal
//                  changed2 = true;
//
//              }
//              if (lander.getTitanDistance() < 30000 && changed3 == false) {
//                  lander.changeYPID(1, 2, 0.0000000001, 10);
//                  changed3 = true;
//              }

          }else{
              if(lander.getTitanDistance() > 28 * scalingFactor && openLoopLander.getTitanDistance() > 28 * scalingFactor){
                  lander.calculateAccelerationLanding(lander.thrusterForceLanding());
              }else{
                  gc.fillText("Landed successfully!", canvasWidth / 2 - 50, canvasHeight / 2);
                  gc.clearRect(canvasWidth / 2 - 50,canvasHeight/2-50,150,100);
                  lander.markAsLanded();
              }
          }



      } else {
          openLoopLander.calculateAccelerationLanding(openLoopLander.getOLCThrusterForce());
      }
  }

}
