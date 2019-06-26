package gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Consts;

public class InitialGui extends Application {
    // launch the application
    public void start(Stage s)
    {
        // set title for the stage
        s.setTitle("Choose starting option");

        Button cmBtn = new Button("Complete Mission");
        Button tlBtn = new Button("Titan Landing");
        Button rBtn = new Button("Return");

        TilePane r = new TilePane();

        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    new Gui().start(new Stage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    System.out.println("not working");
                }
            }
        };
        EventHandler<ActionEvent> event1 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    new LandingGui(0).start(new Stage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    System.out.println("not working");
                }
            }
        };
        EventHandler<ActionEvent> event2 = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    new Gui(1, Consts.RETURN_DATE,Consts.RETURN_LAUNCH).start(new Stage());
                } catch (Exception e1) {
                    e1.printStackTrace();
                    System.out.println("not working");
                }
            }
        };

        cmBtn.setOnAction(event);
        tlBtn.setOnAction(event1);
        rBtn.setOnAction(event2);

        Label l = new Label("Titan Mission Group 11");
        l.setTextFill(Color.WHITE);

        r.getChildren().add(l);
        r.getChildren().add(cmBtn);
        r.getChildren().add(tlBtn);
        r.getChildren().add(rBtn);
        r.setAlignment(Pos.CENTER);

        String image = "res/stars_background.jpg";
        r.setStyle("-fx-background-image: url('" + image + "'); ");

        Scene sc = new Scene(r,200,200);
        s.setScene(sc);
        s.show();
    }

    public static void main(String args[])
    {
        // launch the application
        launch(args);
    }
}
