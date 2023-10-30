package com.thebinarybandits.drawr;

import com.thebinarybandits.drawr.controllers.AppController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layout/view_app.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        String css = Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm();
        scene.getStylesheets().add(css);

        AppController appController = fxmlLoader.getController();
        appController.setStage(stage);

        stage.setTitle("Drawr");
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/drawable/ic_logo.png")).openStream()));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
