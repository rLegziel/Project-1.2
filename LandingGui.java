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
import model.Body;
import model.BodySystem;
import model.CoordinatesTransformer;
import model.Vector3D;

import java.util.Random;

public class LandingGui extends Application {

    //scale  1 pixel = 250 meters

    public static double TIME_SLICE = 60*30;
    private model.Vector3D dragPosStart;
    private model.CoordinatesTransformer transformer = new CoordinatesTransformer();
    public static final double INITIAL_SCALE = 280;

    private Timeline timeline;
    private double canvasHeight = 800;
    private double canvasWidth = 800;

    private double landerSize = 2; //in reality around 2 meters
    private double xPos;
    private double yPos;

    private double titanRad = 257470;

    private model.BodySystem bodySystem;
    private Vector3D enterVelocity;
    private double vY;
    private double vX;

    private model.Body lander;
    private model.Body titan;

    private double windSpeed = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //enterVelocity = bodySystem.getProbe().velocity;

        xPos = canvasWidth/2;
        yPos = 0;

        Vector3D initLanderLoc = new Vector3D(xPos,yPos,0);
        Vector3D initLanderVelocity = new Vector3D(0,0.0001,0);

        lander = new Body("lander",100,0.01,initLanderLoc,initLanderVelocity,Color.WHITE);

        Vector3D initTitanLoc = new Vector3D(canvasWidth/2 - 2575.73,5.0E+20,0);
        Vector3D initTitanVelocity = new Vector3D(0,0,0);

        titan = new Body("titan",0.3452E+23,2575.73,initTitanLoc,initTitanVelocity,Color.ORANGE);

        transformer.setScale(INITIAL_SCALE);
        //transformer.setOriginXForOther(500);
        //transformer.setOriginYForOther(500);

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

        stage.setMaximized(true);

        return canvas.getGraphicsContext2D();
    }

    private Canvas createCanvas() {
        Canvas canvas = new ResizableCanvas();
        // dragging of map
        canvas.setOnDragDetected((event) -> this.dragPosStart = new model.Vector3D(event.getX(), event.getY(), 0));
        canvas.setOnMouseDragged((event) -> {
            if (this.dragPosStart != null) {
                model.Vector3D dragPosCurrent = new model.Vector3D(event.getX(), event.getY(), 0);
                dragPosCurrent.sub(this.dragPosStart);
                dragPosStart = new model.Vector3D(event.getX(), event.getY(), 0);
                transformer.setOriginXForOther(transformer.getOriginXForOther() + dragPosCurrent.x);
                transformer.setOriginYForOther(transformer.getOriginYForOther() + dragPosCurrent.y);
            }
        });
        canvas.setOnMouseReleased((event) -> this.dragPosStart = null);

        // zooming (scaling)
        canvas.setOnScroll((event) -> {
            if (event.getDeltaY() > 0) {
                transformer.setScale(transformer.getScale() * 0.9);
            } else {
                transformer.setScale(transformer.getScale() * 1.1);
            }
        });
        return canvas;
    }

    protected void updateFrame(GraphicsContext gc) {
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        //draw the ground;
        gc.setFill(Color.ORANGE);
        //gc.fillRect(0,canvasHeight-30,canvasWidth,30);
        gc.fillOval((canvasWidth/2 - titanRad) ,canvasHeight-150,titanRad*2,titanRad*2);

        gc.setFill(Color.SILVER);
        gc.fillOval(xPos,yPos,landerSize,landerSize);

        //text for info
        gc.fillText("wind speed: " + windSpeed + " mph",canvasWidth-200,100);
        gc.fillText("lander speedX: " + vX, canvasWidth-200,150);
        gc.fillText("lander speedY: " + vY, canvasWidth-200,200);

        updateLanderPosition(gc);
        //System.out.println(xPos+" "+yPos);

    }

    private void updateLanderPosition(GraphicsContext gc){
        if(yPos < 5.0E+20){ //-35
            //yPos += 0.1;// TODO: simulate gravity and air resistance in applyGravity() and applyAirResistance()
            applyGravity();
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
        lander.addAccelerationByGravForce(titan);
        lander.updateVelocityAndLocation(TIME_SLICE);

        yPos = lander.location.y;
        //xPos = lander.location.x;
        vY = lander.velocity.y;

        System.out.println(lander.velocity.y);

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

    public double calculateWindSpeed(double height){
        double wind;
        int highAltWind = 432000;
        int medAltWind = highAltWind-216000;
        int lowAltWind = 20000;
        int groundLevelWind = 70000;
        Random rand = new Random();
        if (height<150000&& height>60000){
            wind = rand.nextInt(highAltWind+80000) + (highAltWind*0.8);
        }else if(height<60000 & height>20000){
            wind = rand.nextInt(medAltWind) + (medAltWind*0.8);
        }else if(height < 20000 && height>10000){
            wind = rand.nextInt(lowAltWind) + lowAltWind*0.5;
        } else{
            wind = rand.nextInt(groundLevelWind) +groundLevelWind*0.1;
        }
        return wind;
    }

    private void applyAirResistance(){

    }

}
