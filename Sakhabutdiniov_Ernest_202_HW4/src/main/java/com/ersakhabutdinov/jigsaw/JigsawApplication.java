package com.ersakhabutdinov.jigsaw;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JigsawApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(JigsawApplication.class.getResource("jigsaw_view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 400);
        stage.setMaxHeight(450);
        stage.setMaxWidth(690);
        stage.setMinHeight(450);
        stage.setMinWidth(690);
        stage.sizeToScene();
        stage.setTitle("Jigsaw");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}