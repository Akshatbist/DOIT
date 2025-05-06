package com.example.hellofx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        SplashScreen splash = new SplashScreen(stage);
        Scene splashScene = new Scene(splash, 375, 812);
        stage.setScene(splashScene);         // ✅ SHOW SPLASH FIRST
        stage.setTitle("Doit App");
        stage.show();                         // ✅ Don't forget this!
    }

    public static void main(String[] args) {
        launch(args);
    }
}


