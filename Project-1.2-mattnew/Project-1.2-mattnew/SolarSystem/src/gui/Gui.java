package gui;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Gui representing the solar system using javafx.
 */
public class Gui extends Application {

    //Number of seconds to update the model with for each iteration (delta T)
    public static final double TIME_SLICE = 60 * 20; // used to be 60*30

    //initial scale pixel/meter
    public static final double INITIAL_SCALE = 13e8; // was 5e9

    //radius in pixels of body in gui
    public static final double BODY_RADIUS_GUI = 2;

    private double spaceshipSize = 4.5;

    //bodies in system rendered by gui
    private BodySystem bodySystem;

    //transforms between coordinates in model and coordinates in gui
    private CoordinatesTransformer transformer = new CoordinatesTransformer();

    private javafx.scene.image.Image spaceshipImg = new Image("res/spaceship.png");

    private double canvasWidth = 0;
    private double canvasHeight = 0;
    private Vector3D dragPosStart;

    private Timeline timeline;
    private Stage stage;

    private int destination;//0 = titan, 1 = earth;
    private long elapsedTime;
    private long firstLaunch;

    private PauseTransition delay;

    public Gui(int destination, long elapsedTime, long firstLaunch) {
        this.destination = destination;
        this.elapsedTime = elapsedTime;
        this.firstLaunch = firstLaunch;
        System.out.println(destination);
    }

    public Gui() {
    }


    @Override
    public void start(Stage stage) {
        this.stage = stage;
        createBodies();
        transformer.setScale(INITIAL_SCALE);
        transformer.setOriginXForOther(500);
        transformer.setOriginYForOther(500);
        GraphicsContext gc = createGui(stage);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        KeyFrame kf = new KeyFrame(
                Duration.millis(0.1),
                new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent ae) {
                        //call update frame every 0.1 milliseconds
                        updateFrame(gc);
                    }
                });
        timeline.getKeyFrames().add(kf);
        timeline.play();
        stage.show();
    }

    //drawing the frame
    protected void updateFrame(GraphicsContext gc) {
        //distance in which PID starts trying to put pribe in orbit
        double distanceToClose; //8E9
        if(destination == 0){
            distanceToClose = 1.79E9;
        }else{
            distanceToClose = 8E9;
        }
        if (bodySystem.minDistanceToTarget < distanceToClose) {
            //delays when to switch from the different gui's
            //this is done to see that the probe is actually in orbit
            if (delay == null) {
                System.out.println(TimeConverter.getElapsedTimeAsString(bodySystem.getElapsedTime()));
                //you can set the delay here
                delay = new PauseTransition(Duration.seconds(2));
                delay.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        timeline.stop();
                        stage.close();
                        System.out.println(TimeConverter.getElapsedTimeAsString(bodySystem.getElapsedTime()));
                        //System.out.println("The change in velocity due to the engines: " + bodySystem.getProbeList().get(0).getFuelConsumption().toString());
                        try {
                            //change to landing gui
                            if(destination == 0){
                                new LandingGui(0).start(new Stage());
                            }else{
                                new LandingGui(1).start(new Stage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("not working");
                        }
                    }
                });
                delay.play();
            }

        }
        this.canvasWidth = gc.getCanvas().getWidth();
        this.canvasHeight = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        TimeConverter tc = new TimeConverter();

        //info to display in gui
        gc.fillText("currentTime:" + tc.currentDate(bodySystem.getElapsedTime()), canvasWidth - 300, 200);
        gc.fillText("elapsedTime: " + bodySystem.getElapsedTime(), canvasWidth - 300, 250);
        gc.fillText("elapsedTimeString" + TimeConverter.getElapsedTimeAsString(bodySystem.getElapsedTime()), canvasWidth - 300, 300);
        gc.fillText("timeToLaunch: " + bodySystem.getTimeToLaunch(), canvasWidth - 300, 350);
        gc.fillText("distance: " + bodySystem.getMinDistanceToTarget(), canvasWidth - 300, 400);

        for (int i = 0; i < bodySystem.getBodies().size(); i++) {

            double otherX = transformer.modelToOtherX(bodySystem.getBodies().get(i).location.x);
            double otherY = transformer.modelToOtherY(bodySystem.getBodies().get(i).location.y);

            double scale = bodySystem.getBodies().get(i).scale * 5;
            // draw circle
            gc.setFill(bodySystem.getBodies().get(i).color);
            if (i == bodySystem.getBodies().size()-1&&bodySystem.getProbeList().size()>=1) {
                gc.setFill(Color.TRANSPARENT);
                gc.drawImage(spaceshipImg, otherX - BODY_RADIUS_GUI, otherY - BODY_RADIUS_GUI, spaceshipSize * 2 + scale, spaceshipSize * 2 + scale);
            }
            gc.fillOval(otherX - BODY_RADIUS_GUI, otherY - BODY_RADIUS_GUI, BODY_RADIUS_GUI * 2 + scale, BODY_RADIUS_GUI * 2 + scale);

            gc.setFill(Color.WHITE);
            //  label
            Text text = new Text(bodySystem.getBodies().get(i).name);
            gc.fillText(bodySystem.getBodies().get(i).name, otherX - (text.getLayoutBounds().getWidth() / 2), otherY - BODY_RADIUS_GUI - (text.getLayoutBounds().getHeight() / 2));
        }


        bodySystem.update(TIME_SLICE, timeline);
    }

    //creating the bodies in the solar system
    protected void createBodies() {
        //if destination is earth change to different planet arrangement to show change in time
        if (destination == 1) {
            this.bodySystem = new SolarSystem(destination);
            bodySystem.setFirstLaunch(firstLaunch + elapsedTime);
            bodySystem.setElapsedSeconds(elapsedTime);
            bodySystem.setInterval(firstLaunch+elapsedTime);
        } else {
            this.bodySystem = new SolarSystem(destination);
        }
    }

    //creating the gui
    private GraphicsContext createGui(Stage stage) {
        BorderPane border = new BorderPane();

        String image = "res/stars_background.jpg";
        border.setStyle("-fx-background-image: url('" + image + "'); ");

        Canvas canvas = createCanvas();
        border.setCenter(canvas);
        stage.setTitle("NBody simulation");
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setMaximized(true);

        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());
        return canvas.getGraphicsContext2D();
    }

    //create the resizable canvas
    private Canvas createCanvas() {
        Canvas canvas = new ResizableCanvas();

        // dragging of map
        canvas.setOnDragDetected((event) -> this.dragPosStart = new Vector3D(event.getX(), event.getY(), 0));
        canvas.setOnMouseDragged((event) -> {
            if (this.dragPosStart != null) {
                Vector3D dragPosCurrent = new Vector3D(event.getX(), event.getY(), 0);
                dragPosCurrent.sub(this.dragPosStart);
                dragPosStart = new Vector3D(event.getX(), event.getY(), 0);
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


    public static void main(String[] args) {
        launch(args);
    }
}
