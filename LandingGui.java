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
import javafx.stage.Stage;
import javafx.util.Duration;
import model.BodySystem;
import model.Vector3D;

import java.util.Random;

public class LandingGui extends Application {

    private Timeline timeline;
    private double canvasHeight = 600;
    private double canvasWidth = 800;

    private double landerSize = 10;
    private double xPos;
    private double yPos;

    private model.BodySystem bodySystem;
    private Vector3D enterVelocity;
    private double vY;
    private double vX;

    private double windSpeed = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //enterVelocity = bodySystem.getProbe().velocity;

        xPos = canvasWidth/2;
        yPos = 0;

        GraphicsContext gc = createGui(primaryStage);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(
                Duration.millis(1),
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

        return canvas.getGraphicsContext2D();
    }

    protected void updateFrame(GraphicsContext gc) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        //draw the ground;
        gc.setFill(Color.ORANGE);
        gc.fillRect(0,canvasHeight-30,canvasWidth,30);

        gc.setFill(Color.SILVER);
        gc.fillRect(xPos,yPos,landerSize,landerSize);

        //text for info
        gc.fillText("wind speed: " + windSpeed + " mph",canvasWidth-200,100);
        gc.fillText("lander speedX: " + vX, canvasWidth-200,150);
        gc.fillText("lander speedY: " + vY, canvasWidth-200,200);

        updateLanderPosition(gc);
        //System.out.println(xPos+" "+yPos);

    }

    private void updateLanderPosition(GraphicsContext gc){
        if(yPos < canvasHeight - 40){
            yPos += 0.1;// TODO: simulate gravity and air resistance in applyGravity() and applyAirResistance()
            applyWind();
        }else{
            gc.fillText("Landed successfully!",canvasWidth/2 - 50,canvasHeight/2);
            timeline.stop();
        }

    }

    private void downThruster(){

    }

    private void upThruster(){

    }

    private void leftThruster(){

    }

    private void rightThruster(){

    }

    private void applyGravity(){

    }

    private void applyWind(){ //TODO: make a stochastic model here
        Random rand = new Random();

        double deltaX = rand.nextDouble();

        //different wind speeds at different altitudes
        if(yPos < 100){
            deltaX = deltaX/10;
        }else if(yPos < 200){
            deltaX = deltaX/30;
        }else if(yPos < 500){
            deltaX = deltaX/50;
        }else{
            deltaX = deltaX/80;
        }

        xPos += deltaX;

        windSpeed = deltaX;

    }

    private void applyAirResistance(){

    }

}
