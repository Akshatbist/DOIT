package com.example.hellofx;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SplashScreen extends StackPane {

    public SplashScreen(Stage stage) {
        Label logo = new Label("Doit");
        logo.setStyle("-fx-font-size: 48px; -fx-text-fill: white;");

        this.setStyle("-fx-background-color: linear-gradient(to bottom, #1e0036, #2a0070);");
        this.getChildren().add(logo);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));

        delay.setOnFinished(e -> {
            AppState state = new AppState(); // ✅ shared state instance
            CalendarScreen calendarScreen = new CalendarScreen(stage, state);
            stage.setScene(new Scene(calendarScreen, 375, 812));
        });

        delay.play();
    }
}
