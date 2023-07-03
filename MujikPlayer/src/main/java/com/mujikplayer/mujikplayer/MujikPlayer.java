package com.mujikplayer.mujikplayer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class MujikPlayer extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    Image iconImage = new Image(getClass().getResourceAsStream("imgs/mujikplayericon.png"));

    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(getClass().getResource("lookFeel.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(MujikPlayer.class.getResource("lookFeel.fxml"));
        //Scene scene = new Scene(fxmlLoader.load());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Mujik Player");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.getIcons().add(iconImage);
        stage.initStyle(StageStyle.TRANSPARENT);

        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });

        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {

                Platform.exit();
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {

        launch();
    }
}