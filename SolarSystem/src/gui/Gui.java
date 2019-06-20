package gui;
import javafx.animation.KeyFrame;
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


/**
 * Gui representing the solar system using javafx.
 *
 * Note that javafx uses a coordinate system with origin top left.
 */
public class Gui extends Application {
    /** Number of seconds to update the model with for each iteration (delta T) */
    public static final double TIME_SLICE = 60 * 20; // used to be 60*30

    /** initial scale pixel/meter */
    public static final double INITIAL_SCALE = 13e8 ; // was 5e9

    /** radius in pixels of body in gui */
    public static final double BODY_RADIUS_GUI = 2;

    private double spaceshipSize = 4.5;

    private static final int BOTTOM_AREA_HEIGHT = 100;

    /** bodies in system rendered by gui */
    private BodySystem bodySystem;

    /** transforms between coordinates in model and coordinates in gui */
    private CoordinatesTransformer transformer = new CoordinatesTransformer();

    private javafx.scene.image.Image spaceshipImg = new Image("res/spaceship.png");

//    /** utility for counting frames per second */
//    private FPSCounter fps = new FPSCounter();

    private double canvasWidth = 0;
    private double canvasHeight = 0;
    private Vector3D dragPosStart;
    private Label timeLabel;
    private Label fpsLabel;
    private Label scaleLabel;

    private Timeline timeline;
    private Stage stage;

    private int destination; //0 = titan, 1 = earth;
    private long elapsedTime;
    private long firstLaunch;

    public Gui(int destination, long elapsedTime, long firstLaunch){
        this.destination = destination;
        this.elapsedTime = elapsedTime;
        this.firstLaunch = firstLaunch;
        System.out.println(destination);
    }

    public Gui(){}


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
                        updateFrame(gc);
                    }
                });
        timeline.getKeyFrames().add(kf);
        timeline.play();
        stage.show();
    }

    /**
     * Draw a frame
     *
     * @param gc, a graphicsContext object.
     *
     */
    protected void updateFrame(GraphicsContext gc) {
        //System.out.println(bodySystem.minDistanceTitan);
        if(bodySystem.minDistanceTitan < 1.0E9 && destination == 0){
            System.out.println(bodySystem.getElapsedTime());
            //timeline.stop();
            //stage.close();
            try {
                //new LandingGui(0).start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("not working");
            }
        }
        this.canvasWidth = gc.getCanvas().getWidth();
        this.canvasHeight = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        TimeConverter tc = new TimeConverter();

        gc.fillText("currentTime:"+tc.currentDate(bodySystem.getElapsedTime()),canvasWidth-300,200);
        gc.fillText("elapsedTime: "+bodySystem.getElapsedTime(),canvasWidth-300,250);
        gc.fillText("elapsedTimeString"+bodySystem.getElapsedTimeAsString(), canvasWidth-300,300);
        gc.fillText("timeToLaunch: "+bodySystem.getTimeToLaunch(),canvasWidth-300,350);
        gc.fillText("distance: "+bodySystem.minDistanceTitan,canvasWidth-300,400);


        for (int i = 0; i<bodySystem.getBodies().size();i++) {


            double otherX = transformer.modelToOtherX(bodySystem.getBodies().get(i).location.x);
            double otherY = transformer.modelToOtherY(bodySystem.getBodies().get(i).location.y);

            double scale = bodySystem.getBodies().get(i).scale * 5;
            // draw circle
            gc.setFill(bodySystem.getBodies().get(i).color);
            if(i == 12){
                gc.setFill(Color.TRANSPARENT);
                gc.drawImage(spaceshipImg,otherX - BODY_RADIUS_GUI, otherY - BODY_RADIUS_GUI, spaceshipSize * 2 + scale, spaceshipSize * 2 + scale);
            }
            gc.fillOval(otherX - BODY_RADIUS_GUI, otherY - BODY_RADIUS_GUI, BODY_RADIUS_GUI * 2 + scale, BODY_RADIUS_GUI * 2 + scale);

            gc.setFill(Color.WHITE);
            //  label
            Text text = new Text(bodySystem.getBodies().get(i).name);
            gc.fillText(bodySystem.getBodies().get(i).name, otherX - (text.getLayoutBounds().getWidth() / 2), otherY - BODY_RADIUS_GUI - (text.getLayoutBounds().getHeight() / 2));
        }



        bodySystem.update(TIME_SLICE, timeline);
        //timeLabel.setText(bodySystem.getElapsedTimeAsString() + " Distance " + bodySystem.currentDistance);
        //bodySystem.check1(); ////// REPLACE WITH COLLISION
        /*if(bodySystem.getElapsedTime() > interval){
            bodySystem.checkCollision();
            interval += 31556926/4;
        }*/

        //fpsLabel.setText("Distance: " + bodySystem.currentDistance);
        //scaleLabel.setText(String.format("Scale: %d km/pixel", Math.round(transformer.getScale()/1000)));
    }

    protected void createBodies() {
        if(destination == 1){
            this.bodySystem = new SolarSystem(destination);
            bodySystem.setFirstLaunch(firstLaunch + elapsedTime);
            bodySystem.setElapsedSeconds(elapsedTime);
        }else{
            this.bodySystem = new SolarSystem(destination);
        }
    }

    private GraphicsContext createGui(Stage stage) {
        String image = "res/stars_background.jpg";

        BorderPane border = new BorderPane();
        border.setStyle("-fx-background-image: url('" + image + "'); ");

        createTimeLabel();
//        createFPSLabel();
        createScaleLabel();
        //HBox hbox = createHBox();
       // border.setBottom(hbox);
        Canvas canvas = createCanvas();
        border.setCenter(canvas);
        stage.setTitle("NBody simulation");
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setMaximized(true);

        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty());
        //canvas.heightProperty().bind(stage.heightProperty().subtract(BOTTOM_AREA_HEIGHT));
        return canvas.getGraphicsContext2D();
    }

    /**
     * @return a resizable canvas
     */
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


    /**
     * creates the time label, to be added to the HBox.
     */
    private void createTimeLabel() {
        timeLabel = new Label();
        timeLabel.setPrefSize(500, 20);
    }

    private void createScaleLabel() {
        scaleLabel = new Label();
        scaleLabel.setPrefSize(300, 20);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
