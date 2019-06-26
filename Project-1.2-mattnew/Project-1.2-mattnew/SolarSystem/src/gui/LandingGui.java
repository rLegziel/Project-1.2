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

/**
 * Gui representing the landing using javafx.
 */
public class LandingGui extends Application {

    private int planet = 1; //0 = titan, 1 = earth

    private Timeline timeline;
    private Stage stage;
    private double canvasHeight = 700;
    private double canvasWidth = 1000;

    //info for design
    private double landerSize = 20; //in reality around 10 meters
    private Image landerImg = new Image("res/lander.png");
    private Image spaceshipImg = new Image("res/spaceship.png");
    private Image wavesImg = new Image("res/waves.png");

    private TitanLander titanLander;
    private TitanLander openLoopTitanLander;
    private EarthLander earthLander;

    private int missionStage = 0; //0 = landing, 1 = ascending, 2 = switch back to orbit gui

    //for when switching between PID's
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

        Vector3D initLanderLoc = new Vector3D(2790, 250000, 0);
        Vector3D initLanderVelocity = new Vector3D(0, 0, 0); //

        if(planet == 0){
            openLoopTitanLander = new TitanLander("openLoopTitanLander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.WHITE, canvasWidth, canvasHeight);
            titanLander = new TitanLander("titanLander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.RED, canvasWidth, canvasHeight);
        }else{
            earthLander = new EarthLander("titanLander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.RED, canvasWidth, canvasHeight);
        }

        GraphicsContext gc = createGui(primaryStage);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(
                Duration.millis(1),
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent ae) {
                        if(planet == 0){
                            updateFrame(gc, titanLander);
                        }else{
                            updateFrame(gc, earthLander);
                        }
                    }
                });
        timeline.getKeyFrames().add(kf);
        timeline.play();
        primaryStage.show();
    }

    //create the gui
    private GraphicsContext createGui(Stage stage) {
        BorderPane border = new BorderPane();

        String image = "res/stars_background.jpg";
        border.setStyle("-fx-background-image: url('" + image + "'); ");

        Canvas canvas = new Canvas(canvasWidth, canvasHeight);
        border.setCenter(canvas);
        stage.setTitle("Landing simulation");

        Scene scene = new Scene(border);
        stage.setScene(scene);

        return canvas.getGraphicsContext2D();
    }

    protected void updateFrame(GraphicsContext gc, Lander chosenTitanLander) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        //draw the spaceship and the ground
        if(planet == 0){
            gc.drawImage(spaceshipImg,canvasWidth/2-80,-35,350,80);
            gc.setFill(Consts.TITAN_COLOR);
            gc.fillRect(0, canvasHeight - 40, canvasWidth, 40);
        }else{
            gc.drawImage(wavesImg,0,canvasHeight-80,canvasWidth,80);
        }

        gc.save();

        drawLander(gc, chosenTitanLander.getAngle(), chosenTitanLander);

        //text for info
        gc.setFill(Color.WHITE);
        gc.fillText("wind speed: " + chosenTitanLander.getWindSpeed(), canvasWidth - 200, 100);
        gc.fillText("Lander speedX: " + chosenTitanLander.getVelocity().x, canvasWidth - 200, 150);
        gc.fillText("Lander speedY: " + chosenTitanLander.getVelocity().y, canvasWidth - 200, 200);
        gc.fillText("Lander locationX: " + chosenTitanLander.getLocation().x, canvasWidth - 200, 250);
        gc.fillText("Lander locationY: " + chosenTitanLander.getLocation().y, canvasWidth - 200, 300);
        gc.fillText("Lander angle: " + chosenTitanLander.getAngle(), canvasWidth - 200, 350);

        updateLanderPosition(gc, chosenTitanLander);
    }

    private void drawLander(GraphicsContext gc, double rotationAngle, Lander chosenTitanLander) {

        double delta = 0;

        //if lander goes of screen
        if (chosenTitanLander.getDispLocX() <= 0) {
            gc.fillText("not displaying change in y", 10, 100);
            delta = 0 - chosenTitanLander.getDispLocX();
        }

        double rotationCenterX = (chosenTitanLander.getDispLocX() + landerSize / 2) + delta;
        double rotationCenterY = chosenTitanLander.getDispLocY() + landerSize / 2;

        gc.save();
        gc.restore();

        gc.setFill(Color.TRANSPARENT);
        //rotate lander accordingly
        gc.transform(new Affine(new Rotate(rotationAngle, rotationCenterX, rotationCenterY)));
        gc.fillRect(chosenTitanLander.getDispLocX() + delta, chosenTitanLander.getDispLocY(), landerSize, landerSize);

        //draw lander for titan but ship for earth
        if(planet == 0){
            gc.drawImage(landerImg, chosenTitanLander.getDispLocX() + delta, chosenTitanLander.getDispLocY(), landerSize, landerSize);
        }else{
            gc.drawImage(spaceshipImg, chosenTitanLander.getDispLocX() + delta, chosenTitanLander.getDispLocY(), landerSize, landerSize);

        }

        gc.restore();
    }


  private void updateLanderPosition(GraphicsContext gc, Lander chosenLander) {
      //uses the pid for rotating
      if (chosenLander.equals(titanLander)) {
          switch (missionStage){
              case 0:
                  if(titanLander.getTitanDistance() > 28 * titanLander.getScalingFactor() && openLoopTitanLander.getTitanDistance() > 28 * titanLander.getScalingFactor()){
                      titanLander.calculateAccelerationLanding(titanLander.thrusterForceLanding());
                  }else{
                      gc.fillText("Landed successfully!", canvasWidth / 2 - 50, canvasHeight / 2);
                      gc.clearRect(canvasWidth / 2 - 50,canvasHeight/2-50,150,100);
                      missionStage = 1;
                      titanLander.markAsLanded();
                  }
                  break;
              case 1:
                  if(titanLander.dispLocY > 40){
                      titanLander.calculateAccelerationLanding(titanLander.thrusterForceAscending());
                      //titanLander.calculateAccelerationLanding(new Vector3D(0,0,0));
                  }else{
                      //et = 2.39133398E8;
                      missionStage = 2;
                  }
                  break;
              case 2:
                  //System.out.println(elapsedTime);
                  stage.close();
                  timeline.stop();
                  System.out.println("change in velocity for getting back to ship: " + titanLander.getTotalChangeInVelocity().toString());
                  try {
                      new Gui(1,Consts.RETURN_DATE,Consts.RETURN_LAUNCH).start(new Stage());
                  } catch (Exception e) {
                      e.printStackTrace();
                      System.out.println("not working");
                  }
          }

          //uncomment this if you want to switch for different PID
          //works better but is MUCH slower
//              if (titanLander.getTitanDistance() < 17500 && changed1 == false) {
//                  titanLander.changeYPID(1, 7, 0.0001, 10); // seems like the optimal, will lead to landing at y speed of about 0.27 m/s which is about 1 km/h.
//                  changed1 = true;
//                  // the ki is 0.0000001 , kp is 7
//              }
//              if (titanLander.getTitanDistance() < 40000 && changed2 == false) {
//                  titanLander.changeYPID(1, 1, 0.0000000001, 10);
////                titanLander.changeXLPID(1,0.0001,0,0.1); // optimal
//                  changed2 = true;
//
//              }
//              if (titanLander.getTitanDistance() < 30000 && changed3 == false) {
//                  titanLander.changeYPID(1, 2, 0.0000000001, 10);
//                  changed3 = true;
//              }

      } else if(chosenLander.equals(earthLander)){
          if(earthLander.getDispLocY() < canvasHeight-50){
              //System.out.println(earthLander.getEarthDistance());
              earthLander.calculateAccelerationLanding(earthLander.thrusterForceLanding());
          }else{
              gc.fillText("Landed successfully!", canvasWidth / 2 - 50, canvasHeight / 2);
          }
      } else if(chosenLander.equals(openLoopTitanLander)){
          if(openLoopTitanLander.getTitanDistance() > 28 * openLoopTitanLander.getScalingFactor()){
              openLoopTitanLander.calculateAccelerationLanding(openLoopTitanLander.getOLCThrusterForce());
          }else{
              gc.fillText("Landed successfully!", canvasWidth / 2 - 50, canvasHeight / 2);
              gc.clearRect(canvasWidth / 2 - 50,canvasHeight/2-50,150,100);
              titanLander.markAsLanded();
          }

      }
  }

}
