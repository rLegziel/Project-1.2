package gui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;

import java.util.Random;

public class LandingGui extends Application {

    //scale  1 pixel = 375 meters
    //actual distance from titan
    private double scalingFactor = 375;

    private Timeline timeline;
    private double canvasHeight = 700;
    private double canvasWidth = 1000;
    private boolean changed1 = false;
    private boolean changed2 = false;
    private boolean changed3 = false;
    private boolean changed4 = false;

    private double landerSize = 30; //in reality around 10 meters

    private double titanRad = 257470;

    private model.Lander lander;
    private model.Lander openLoopLander;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //enterVelocity = bodySystem.getProbe().velocity;

        Vector3D initLanderLoc = new Vector3D(2790, 250000, 0);
        Vector3D initLanderVelocity = new Vector3D(0, 0, 0); //

        lander = new Lander("lander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.RED, scalingFactor, canvasWidth, canvasHeight);
        openLoopLander = new Lander("openLoopLander", 100, 0.01, initLanderLoc, initLanderVelocity, Color.WHITE, scalingFactor, canvasWidth, canvasHeight);

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

        //draw the ground;
        gc.setFill(Color.ORANGE);
        gc.fillRect(0, canvasHeight - 30, canvasWidth, 30);
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
        gc.fillText("distance from titan: " + chosenLander.getTitanDistance(), canvasWidth - 200, 350);
        gc.fillText("actual y:" + chosenLander.dispLocY, canvasWidth - 200, 400);
        gc.fillText("lander angle: " + chosenLander.getAngle(), canvasWidth - 200, 450);

        updateLanderPosition(gc,chosenLander);
        //System.out.println(lander.dispLocX+" "+lander.dispLocY);

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

        gc.setFill(chosenLander.color);
        gc.transform(new Affine(new Rotate(rotationAngle, rotationCenterX, rotationCenterY)));
        gc.fillRect(chosenLander.dispLocX + delta, chosenLander.dispLocY, landerSize, landerSize);
        gc.restore();
    }

    /*
    private void updateLanderPosition(GraphicsContext gc) {
//        System.out.println(lander.getTitanDistance());
        if (lander.getTitanDistance() > 28 * scalingFactor) {
            //uses the pid for rotating
            lander.calculateAccelerationLanding(lander.thrusterForce());
            //simulate only the forces
            //lander.calculateAccelerationLanding(new Vector3D(0,0,0));
            if(lander.getTitanDistance() < 17500 && changed1 == false){
                lander.changeYPID(1,7,0.0001,10); // seems like the optimal, will lead to landing at y speed of about 0.27 m/s which is about 1 km/h.
                changed1 = true;
                // the ki is 0.0000001 , kp is 7
            }
            if (lander.getTitanDistance()<40000 && changed2 == false){
                lander.changeYPID(1,1,0.0000000001,10);
//                lander.changeXLPID(1,0.0001,0,0.1); // optimal
                changed2 = true;
            }
            if(lander.getTitanDistance()<30000&& changed3 == false){
                lander.changeYPID(1,2,0.0000000001,10);
                changed3 = true;
            }
            if (lander.getTitanDistance()<60000){
                //System.out.println(lander.getPIDoutput());
            }


        } else {
            gc.fillText("Landed successfully!", canvasWidth / 2 - 50, canvasHeight / 2);
            //System.out.println(lander.getTitanDistance() + " " + lander.dispLocY);
        }

    }

     */

  private void updateLanderPosition(GraphicsContext gc, Lander chosenLander) {

      double c1x, c2x, c3x, c4x, c5x, c6x;
      double c1y, c2y, c3y, c4y, c5y, c6y;
      double m1x, m2x, m3x, m4x, m5x, m6x;
      double m1y, m2y, m3y, m4y, m5y, m6y;

      c1x = 0;
      c2x = 0;
      c3x = 0;
      c4x = 0;
      c5x = 0;
      c6x = 0;
      c1y = 0;
      c2y = 0;
      c3y = 0;
      c4y = 0;
      c5y = 0;
      c6y = 0;

      double i1, i2, i3, i4, i5, i6;

      i1 = 0;
      i2 = 0;
      i3 = 0;
      i4 = 0;
      i5 = 0;
      i6 = 0;

      boolean print = true;


//        System.out.println(lander.getTitanDistance());
      if (lander.getTitanDistance() > 28 * scalingFactor && openLoopLander.getTitanDistance() > 28 * scalingFactor) {
          //uses the pid for rotating
          //lander.calculateAccelerationLanding(lander.thrusterForce());
          //openLoopLander.calculateAccelerationLanding(openLoopLander.getOLCThrusterForce());
          if (chosenLander.equals(lander)) {
              lander.calculateAccelerationLanding(lander.thrusterForce());
          } else {
              openLoopLander.calculateAccelerationLanding(openLoopLander.getOLCThrusterForce());
          }

          //simulate only the forces
          //lander.calculateAccelerationLanding(new Vector3D(0,0,0));
          if (lander.getTitanDistance() < 17500 && changed1 == false) {
              lander.changeYPID(1, 7, 0.0001, 10); // seems like the optimal, will lead to landing at y speed of about 0.27 m/s which is about 1 km/h.
              changed1 = true;
              // the ki is 0.0000001 , kp is 7
              i1++;
          }
          if (lander.getTitanDistance() < 40000 && changed2 == false) {
              lander.changeYPID(1, 1, 0.0000000001, 10);
//                lander.changeXLPID(1,0.0001,0,0.1); // optimal
              changed2 = true;

          }
          if (lander.getTitanDistance() < 30000 && changed3 == false) {
              lander.changeYPID(1, 2, 0.0000000001, 10);
              changed3 = true;

          }

          if (lander.getTitanDistance() > 200000) {
              i1++;
              c1x += lander.thrusterForce().x;
              c1y += lander.thrusterForce().y;

              //System.out.println(c1x + " " + c1y);

              if (lander.getTitanDistance() < 200100) {
                  m1x = c1x / i1;
                  m1y = c1y / i1;

                  System.out.println("x1: " + m1x + " y1: " + m1y);
              }

          } else if (lander.getTitanDistance() < 200000 && lander.getTitanDistance() > 150000) {
              i2++;

              c2x += lander.thrusterForce().x;
              c2y += lander.thrusterForce().y;

              if (lander.getTitanDistance() < 150100) {
                  m2x = c2x / i2;
                  m2y = c2y / i2;

                  System.out.println("x2: " + m2x + " y2: " + m2y);
              }

          } else if (lander.getTitanDistance() < 150000 && lander.getTitanDistance() > 100000) {
              i3++;

              c3x += lander.thrusterForce().x;
              c3y += lander.thrusterForce().y;

              if (lander.getTitanDistance() < 100100) {
                  m3x = c3x / i3;
                  m3y = c3y / i3;

                  System.out.println("x3: " + m3x + " y3: " + m3y);
              }

          } else if (lander.getTitanDistance() < 100000 && lander.getTitanDistance() > 50000) {
              i4++;

              c4x += lander.thrusterForce().x;
              c4y += lander.thrusterForce().y;

              if (lander.getTitanDistance() < 50100) {
                  m4x = c4x / i4;
                  m4y = c4y / i4;

                  System.out.println("x4: " + m4x + " y4: " + m4y);
              }

          } else if (lander.getTitanDistance() < 50000 && lander.getTitanDistance() > 10000) {
              i5++;

              c5x += lander.thrusterForce().x;
              c5y += lander.thrusterForce().y;

              if (lander.getTitanDistance() < 10200) {
                  m5x = c5x / i5;
                  m5y = c5y / i5;

                  System.out.println("x5: " + m5x + " y5: " + m5y);
              }

          } else if (lander.getTitanDistance() < 10000 && lander.getTitanDistance() > 500) {
              i6++;

              c6x += lander.thrusterForce().x;
              c6y += lander.thrusterForce().y;

              if (lander.getTitanDistance() < 500) {
                  m6x = c6x / i6;
                  m6y = c6y / i6;

                  System.out.println("x6: " + m6x + " y6: " + m6y);
              }
          }

      } else {
          gc.fillText("Landed successfully!", canvasWidth / 2 - 50, canvasHeight / 2);

          //System.out.println(lander.getTitanDistance() + " " + lander.dispLocY);
      }
  }

}
