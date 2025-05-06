package com.example.hellofx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Consumer;

public class TaskFormScreen extends VBox {
    private final TextField title = new TextField();
    private final CheckBox fullDay = new CheckBox("Full Day");
    private final TextField startTime = new TextField();
    private final TextField endTime = new TextField();
    private final ComboBox<String> month = new ComboBox<>();
    private final ComboBox<String> day = new ComboBox<>();
    private final ComboBox<String> year = new ComboBox<>();
    private final TextField location = new TextField();
    private final TextField details = new TextField();
    private final ColorPicker importanceColor = new ColorPicker(Color.RED);

    public TaskFormScreen(Stage stage, AppState state, Consumer<LocalDate> onSaveCallback) {
        this(stage, state, null, onSaveCallback);
    }

    public TaskFormScreen(Stage stage, AppState state, Task taskToEdit, Consumer<LocalDate> onSaveCallback) {
        setPadding(new Insets(20));
        setSpacing(10);
        setStyle("-fx-background-color: linear-gradient(to bottom, #1e0036, #2a0070);");

        HBox topBar = new HBox();
        Button back = new Button("←");
        Button save = new Button("✔");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topBar.getChildren().addAll(back, spacer, save);
        back.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        save.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");

        title.setPromptText("Task Title");
        fullDay.setTextFill(Color.WHITE);
        startTime.setPromptText("Start Time");
        endTime.setPromptText("End Time");

        for (int i = 1; i <= 12; i++) month.getItems().add(String.format("%02d", i));
        month.setPromptText("Month");

        int currentYear = java.time.Year.now().getValue();
        for (int y = currentYear; y >= 1980; y--) year.getItems().add(String.valueOf(y));
        year.setPromptText("Year");

        month.setOnAction(e -> updateDayList());
        year.setOnAction(e -> updateDayList());

        HBox dateBox = new HBox(10, month, day, year);
        dateBox.setAlignment(Pos.CENTER_LEFT);

        ComboBox<String> repeat = new ComboBox<>();
        repeat.getItems().addAll("None", "Daily", "Weekly");
        repeat.setPromptText("Repeat");

        ComboBox<String> reminder = new ComboBox<>();
        reminder.getItems().addAll("Before 30 minutes", "Before 1 hour");
        reminder.setPromptText("Reminder");

        location.setPromptText("Location");
        details.setPromptText("Details");

        getChildren().addAll(
                topBar, title, fullDay, startTime, endTime,
                dateBox, repeat, reminder, importanceColor,
                location, details
        );

        // Pre-fill if editing
        if (taskToEdit != null) {
            title.setText(taskToEdit.title);
            fullDay.setSelected("All Day".equals(taskToEdit.time));
            if (!fullDay.isSelected() && taskToEdit.time.contains(" - ")) {
                String[] parts = taskToEdit.time.split(" - ");
                startTime.setText(parts[0]);
                endTime.setText(parts[1]);
            }

            month.setValue(String.format("%02d", taskToEdit.date.getMonthValue()));
            year.setValue(String.valueOf(taskToEdit.date.getYear()));
            updateDayList();
            day.setValue(String.format("%02d", taskToEdit.date.getDayOfMonth()));
            location.setText(taskToEdit.location);
            details.setText(taskToEdit.notes);
            importanceColor.setValue(taskToEdit.color);
        } else {
            LocalDate date = state.selectedDate;
            month.setValue(String.format("%02d", date.getMonthValue()));
            year.setValue(String.valueOf(date.getYear()));
            updateDayList();
            day.setValue(String.format("%02d", date.getDayOfMonth()));
        }

        back.setOnAction(e -> stage.setScene(new Scene(new CalendarScreen(stage, state), 375, 812)));

        save.setOnAction(e -> {
            LocalDate selectedDate = LocalDate.of(
                    Integer.parseInt(year.getValue()),
                    Integer.parseInt(month.getValue()),
                    Integer.parseInt(day.getValue())
            );

            String time = fullDay.isSelected() ? "All Day" : startTime.getText() + " - " + endTime.getText();

            if (taskToEdit != null) {
                taskToEdit.title = title.getText();
                taskToEdit.time = time;
                taskToEdit.location = location.getText();
                taskToEdit.notes = details.getText();
                taskToEdit.color = importanceColor.getValue();
                taskToEdit.date = selectedDate;
            } else {
                Task newTask = new Task(title.getText(), time, location.getText(), details.getText(), importanceColor.getValue(), selectedDate);
                state.taskMap.computeIfAbsent(selectedDate, d -> new java.util.ArrayList<>()).add(newTask);
            }

            onSaveCallback.accept(selectedDate);
            stage.setScene(new Scene(new CalendarScreen(stage, state), 375, 812));
        });
    }

    private void updateDayList() {
        day.getItems().clear();
        int maxDays = 31;
        String mVal = month.getValue();
        String yVal = year.getValue();

        if (mVal == null) return;

        if (mVal.equals("02")) {
            if (yVal != null) {
                int y = Integer.parseInt(yVal);
                maxDays = (y % 4 == 0 && y % 100 != 0) || (y % 400 == 0) ? 29 : 28;
            } else maxDays = 28;
        } else if (Arrays.asList("04", "06", "09", "11").contains(mVal)) {
            maxDays = 30;
        }

        for (int i = 1; i <= maxDays; i++) {
            day.getItems().add(String.format("%02d", i));
        }

        day.setPromptText("Day");
    }
}




