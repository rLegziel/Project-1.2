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

    private double landerSize = 30; //in reality around 10 meters

    private double titanRad = 257470;

    private model.Lander lander;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //enterVelocity = bodySystem.getProbe().velocity;

        Vector3D initLanderLoc = new Vector3D(2790,250000,0);
        Vector3D initLanderVelocity = new Vector3D(0,0,0); //

        lander = new Lander("lander",100,0.01,initLanderLoc,initLanderVelocity,Color.WHITE,scalingFactor,canvasWidth,canvasHeight);
        GraphicsContext gc = createGui(primaryStage);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(
                Duration.millis(3),
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent ae) {
                        updateFrame(gc);
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
        Canvas canvas = new Canvas(canvasWidth,canvasHeight);
        border.setCenter(canvas);
        stage.setTitle("Landing simulation");

        Scene scene = new Scene(border);
        stage.setScene(scene);

        //stage.setMaximized(true);

        return canvas.getGraphicsContext2D();
    }

    protected void updateFrame(GraphicsContext gc) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        //draw the ground;
        gc.setFill(Color.ORANGE);
        gc.fillRect(0,canvasHeight-30,canvasWidth,30);
        //gc.fillOval((canvasWidth/2 - titanRad) ,canvasHeight-140,titanRad*2,titanRad*2);

//        gc.setFill(Color.RED);
//        gc.fillRect(lander.dispLocX,lander.dispLocY,landerSize,landerSize);
//        gc.rotate(30);

        gc.save();
        drawLander(gc,30 );

        //text for info
        gc.setFill(Color.WHITE);
        gc.fillText("wind speed: " + lander.getWindSpeed() + " mph",canvasWidth-200,100);
        gc.fillText("lander speedX: " + lander.velocity.x, canvasWidth-200,150);
        gc.fillText("lander speedY: " + lander.velocity.y, canvasWidth-200,200);
        gc.fillText("lander locationX: " + lander.location.x, canvasWidth-200,250);
        gc.fillText("lander locationY: " + lander.location.y, canvasWidth-200,300);
        gc.fillText("distance from titan: " + lander.getTitanDistance(),canvasWidth-200,350);
        gc.fillText("actual y:" + lander.dispLocY,canvasWidth-200,400);

        updateLanderPosition(gc);
        //System.out.println(lander.dispLocX+" "+lander.dispLocY);

    }

    private void drawLander(GraphicsContext gc ,double rotationAngle){
        double rotationCenterX = lander.dispLocX + landerSize/2;
        double rotationCenterY = lander.dispLocY + landerSize/2;


        gc.setFill(Color.RED);
        gc.transform(new Affine(new Rotate(rotationAngle, rotationCenterX, rotationCenterY)));
        gc.fillRect(lander.dispLocX,lander.dispLocY,landerSize,landerSize);
        gc.restore();
    }

    private void updateLanderPosition(GraphicsContext gc){
        if(lander.getTitanDistance() > 0){
            //uses the pid for rotating
            lander.calculateAccelerationLanding(lander.thrusterForce());
            //simulate only the forces
            //lander.calculateAccelerationLanding(new Vector3D(0,0,0));
        }else{
            gc.fillText("Landed successfully!",canvasWidth/2 - 50,canvasHeight/2);
            timeline.stop();
        }

    }

}
