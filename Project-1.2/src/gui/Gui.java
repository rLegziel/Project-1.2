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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.CoordinatesTransformer;
import model.SolarSystem;

import java.awt.*;

import static com.sun.javafx.scene.control.skin.Utils.getResource;


public class Gui extends Application {

    public static double TIME_SLICE = 60*30;
    public static final double INITIAL_SCALE = 5e9;
    public static final double BODY_RADIUS_GUI = 2;
    private static final int BOTTOM_AREA_HEIGHT = 100;

    private model.BodySystem bodySystem;
    private model.CoordinatesTransformer transformer = new CoordinatesTransformer();

    private double canvasWidth = 0;
    private double canvasHeight = 0;
    private model.Vector3D dragPosStart;
    private Label dateLabel;
    private Label launchDateLabel;
    private Label elapsedTimeLabel;
    private Label probeSpeedLabel;
    private Label distanceLabel;
    private Label fuelStatusLabel;
    private Label missionStatusLabel;

    private boolean simStarted = false;
    private GraphicsContext gc2;

    private Timeline timeline;

    private int interval = 31556926/4;

    @Override
    public void start(Stage stage) {
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
        EventHandler<javafx.scene.input.MouseEvent> eventHandlerBox =
                new EventHandler<javafx.scene.input.MouseEvent>() {
                    @Override
                    public void handle(javafx.scene.input.MouseEvent e) {
                        timeline.stop();
                    }
                };
        stage.show();
        startLandingSim(stage);
    }

    protected void updateFrame(GraphicsContext gc) {
        this.canvasWidth = gc.getCanvas().getWidth();
        this.canvasHeight = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        for (model.Body body : bodySystem.getBodies()) {

            double otherX = transformer.modelToOtherX(body.location.x);
            double otherY = transformer.modelToOtherY(body.location.y);

            // draw object circle
            gc.setFill(body.getColor());

            if(body.getColor().equals(Color.HOTPINK)){//one way to know its a probe
                gc.fillOval(otherX - BODY_RADIUS_GUI, otherY - BODY_RADIUS_GUI, BODY_RADIUS_GUI * 2, BODY_RADIUS_GUI); //so its more oval and looks like a ship
            }else{
                gc.fillOval(otherX - BODY_RADIUS_GUI, otherY - BODY_RADIUS_GUI, BODY_RADIUS_GUI * 2, BODY_RADIUS_GUI * 2);

            }
            // draw label
            Text text = new Text(body.name);
            gc.fillText(body.name, otherX - (text.getLayoutBounds().getWidth() / 2), otherY - BODY_RADIUS_GUI - (text.getLayoutBounds().getHeight() / 2));
        }

        bodySystem.update(TIME_SLICE, timeline);
        //timeLabel.setText(bodySystem.getElapsedTimeAsString() + " Distance " + bodySystem.currentDistance);
    }

    protected void createBodies() {
        this.bodySystem = new SolarSystem();
    }

    private GraphicsContext createGui(Stage stage) {

        String image = "res/stars_background.jpg";


        BorderPane border = new BorderPane();
        //border.setStyle("-fx-background-color: #000000;");
        border.setStyle("-fx-background-image: url('" + image + "'); ");

        createDateLabel();
        createSpeedLabel();
        createDistanceLabel();
        createFuelLabel();
        createMissionStatusLabelLabel();

        HBox hbox = createHBox();
        border.setBottom(hbox);
        Canvas canvas = createCanvas();
        border.setCenter(canvas);
        stage.setTitle("NBody simulation");
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setMaximized(true);


        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind(stage.widthProperty());
        canvas.heightProperty().bind(stage.heightProperty().subtract(BOTTOM_AREA_HEIGHT));
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

    private HBox createHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between nodes
        hbox.setStyle("-fx-background-color: #d3d3d3;");
        hbox.setFillHeight(true);
        hbox.getChildren().add(this.dateLabel);
        hbox.getChildren().add(this.probeSpeedLabel);
        hbox.getChildren().add(this.distanceLabel);
        hbox.getChildren().add(this.fuelStatusLabel);
        hbox.getChildren().add(this.missionStatusLabel);
        return hbox;
    }

    private void createDateLabel() {
        dateLabel = new Label();
        dateLabel.setPrefSize(100, 20);
        dateLabel.setText("date: ");
    }

    private void createSpeedLabel() {
        probeSpeedLabel = new Label();
        probeSpeedLabel.setPrefSize(100, 20);
        probeSpeedLabel.setText("speed: ");
    }

    private void createDistanceLabel() {
        distanceLabel = new Label();
        distanceLabel.setPrefSize(100, 20);
        distanceLabel.setText("distance: ");
    }

    private void createFuelLabel() {
        fuelStatusLabel = new Label();
        fuelStatusLabel.setPrefSize(100, 20);
        fuelStatusLabel.setText("fuel: ");
    }

    private void createMissionStatusLabelLabel() {
        missionStatusLabel = new Label();
        missionStatusLabel.setPrefSize(100, 20);
        missionStatusLabel.setText("status: ");
    }

    private void createLaunchDateLabel() {
        launchDateLabel = new Label();
        launchDateLabel.setPrefSize(100, 20);
        launchDateLabel.setText("launch date: ");
    }

    private void createElapesedTimeLabel() {
        elapsedTimeLabel = new Label();
        elapsedTimeLabel.setPrefSize(100, 20);
        elapsedTimeLabel.setText("elapsed time: ");
    }

    private void startLandingSim(Stage primaryStage){
        //Label secondLabel = new Label("I'm a Label on new Window");

        StackPane pane = new StackPane();

        Scene secondScene = new Scene(pane, 600, 600);

        // New window (Stage)
        Stage landingStage = new Stage();
        landingStage.setTitle("Second Stage");
        landingStage.setScene(secondScene);

        GraphicsContext gc2 = createLandingGUI(landingStage);
        landingStage.show();
        simStarted = true;
    }

    private GraphicsContext createLandingGUI(Stage stage){
        BorderPane border = new BorderPane();

        Canvas canvas = createCanvas();
        border.setCenter(canvas);
        stage.setTitle("sim");
        Scene scene = new Scene(border);
        stage.setScene(scene);
        stage.setMaximized(true);

        return canvas.getGraphicsContext2D();
    }

    private void landingSimUpdate(GraphicsContext gc){
        gc.fillOval(100,100,200,200);
    }

    public static void main(String[] args) {
        launch(args);
    }

}
