package nbody.gui;

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
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import nbody.model.*;


/**
 * Gui representing the solar system using javafx.
 *
 * Note that javafx uses a coordinate system with origin top left.
 */
public class NBodyFxGui extends Application {
    /** Number of seconds to update the model with for each iteration (delta T) */
    public static final double TIME_SLICE = 60 * 20; // used to be 60*30

    /** initial scale pixel/meter */
    public static final double INITIAL_SCALE = 13e8 ; // was 5e9

    /** radius in pixels of body in gui */
    public static final double BODY_RADIUS_GUI = 2;

    private static final int BOTTOM_AREA_HEIGHT = 100;

    /** bodies in system rendered by gui */
    private BodySystem bodySystem;

    /** transforms between coordinates in model and coordinates in gui */
    private CoordinatesTransformer transformer = new CoordinatesTransformer();

//    /** utility for counting frames per second */
//    private FPSCounter fps = new FPSCounter();

    private double canvasWidth = 0;
    private double canvasHeight = 0;
    private Vector3D dragPosStart;
    private Label timeLabel;
    private Label fpsLabel;
    private Label scaleLabel;

    private Timeline timeline;


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
    }

    /**
     * Draw a frame
     *
     * @param gc, a graphicsContext object.
     *
     */
    protected void updateFrame(GraphicsContext gc) {
        this.canvasWidth = gc.getCanvas().getWidth();
        this.canvasHeight = gc.getCanvas().getHeight();
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        for (int i = 0; i<bodySystem.getBodies().size();i++) {


            double otherX = transformer.modelToOtherX(bodySystem.getBodies().get(i).location.x);
            double otherY = transformer.modelToOtherY(bodySystem.getBodies().get(i).location.y);

            // draw circle
            gc.setFill(Color.BLACK);
            gc.fillOval(otherX - BODY_RADIUS_GUI, otherY - BODY_RADIUS_GUI, BODY_RADIUS_GUI * 2, BODY_RADIUS_GUI * 2);

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
        this.bodySystem = new SolarSystem();
    }

    private GraphicsContext createGui(Stage stage) {
        BorderPane border = new BorderPane();
        createTimeLabel();
//        createFPSLabel();
        createScaleLabel();
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
     * @return HBox, containing all the data in the bottom of the screen
     */
    private HBox createHBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);   // Gap between objects
        hbox.setStyle("-fx-background-color: #336699;");
        hbox.setFillHeight(true);
        hbox.getChildren().add(this.timeLabel);
        hbox.getChildren().add(this.scaleLabel);
        return hbox;
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
