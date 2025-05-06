package com.example.hellofx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class CalendarScreen extends VBox {

    private final AppState state;
    private final Stage stage;
    private final Map<LocalDate, Button> dateButtonMap = new HashMap<>();
    private final VBox scheduleList = new VBox(10);
    private final Label calendarTitle = new Label();
    private final GridPane calendarGrid = new GridPane();
    private YearMonth currentYearMonth;

    public CalendarScreen(Stage stage, AppState state) {
        this.stage = stage;
        this.state = state;
        this.currentYearMonth = YearMonth.of(state.selectedDate.getYear(), state.selectedDate.getMonth());

        setPadding(new Insets(20));
        setSpacing(20);
        setStyle("-fx-background-color: linear-gradient(to bottom, #1e0036, #2a0070);");

        HBox header = new HBox();
        Label logo = new Label("Doit");
        logo.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        Region spacer = new Region();
        Button bellIcon = new Button("🔔");
        Button menuIcon = new Button("⋮");
        bellIcon.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        menuIcon.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(logo, spacer, bellIcon, menuIcon);

        HBox nav = new HBox(10);
        Button prevMonth = new Button("←");
        Button nextMonth = new Button("→");
        prevMonth.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        nextMonth.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        calendarTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        Region navSpacer = new Region();
        HBox.setHgrow(navSpacer, Priority.ALWAYS);
        nav.getChildren().addAll(prevMonth, navSpacer, calendarTitle, nextMonth);
        nav.setAlignment(Pos.CENTER_LEFT);

        prevMonth.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendarGrid();
        });

        nextMonth.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendarGrid();
        });

        Button scheduleButton = new Button("Add Task");
        scheduleButton.setStyle("-fx-background-color: #6f00ff; -fx-text-fill: white;");
        scheduleButton.setOnAction(e -> {
            TaskFormScreen form = new TaskFormScreen(stage, state, updatedDate -> {
                state.selectedDate = updatedDate;
                currentYearMonth = YearMonth.of(updatedDate.getYear(), updatedDate.getMonth());
                updateCalendarGrid();
            });
            stage.setScene(new Scene(form, 375, 812));
        });

        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setAlignment(Pos.CENTER);
        scheduleList.setPadding(new Insets(10));

        updateCalendarGrid();

        getChildren().addAll(header, nav, scheduleButton, calendarGrid, scheduleList);
    }

    private void updateCalendarGrid() {
        calendarGrid.getChildren().clear();
        dateButtonMap.clear();
        scheduleList.getChildren().clear();

        int year = currentYearMonth.getYear();
        int month = currentYearMonth.getMonthValue();

        calendarTitle.setText(currentYearMonth.getMonth() + " " + year);

        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate today = LocalDate.now();

        for (int day = 1; day <= daysInMonth; day++) {
            int col = (day - 1) % 7;
            int row = (day - 1) / 7;

            LocalDate thisDate = LocalDate.of(year, month, day);

            Button dayBtn = new Button(String.valueOf(day));
            dayBtn.setPrefSize(35, 35);
            dayBtn.setStyle("-fx-background-color: #4e1a95; -fx-text-fill: white;");

            if (thisDate.equals(today)) {
                dayBtn.setStyle("-fx-background-color: #6f00ff; -fx-text-fill: white; -fx-font-weight: bold;");
            }

            dayBtn.setOnAction(e -> {
                state.selectedDate = thisDate;
                highlightSelectedDate();
                refreshScheduleList();
            });

            calendarGrid.add(dayBtn, col, row);
            dateButtonMap.put(thisDate, dayBtn);
        }

        if (!dateButtonMap.containsKey(state.selectedDate)) {
            state.selectedDate = LocalDate.of(year, month, 1);
        }

        highlightSelectedDate();
        refreshScheduleList();
    }

    private void highlightSelectedDate() {
        for (Map.Entry<LocalDate, Button> entry : dateButtonMap.entrySet()) {
            Button btn = entry.getValue();
            LocalDate date = entry.getKey();
            if (date.equals(state.selectedDate)) {
                btn.setStyle("-fx-background-color: #00ffcc; -fx-text-fill: black; -fx-effect: dropshadow(gaussian, white, 10, 0, 0, 0);");
            } else if (date.equals(LocalDate.now())) {
                btn.setStyle("-fx-background-color: #6f00ff; -fx-text-fill: white; -fx-font-weight: bold;");
            } else {
                btn.setStyle("-fx-background-color: #4e1a95; -fx-text-fill: white;");
            }
        }
    }

    public void refreshScheduleList() {
        scheduleList.getChildren().clear();

        Label dateLabel = new Label("Tasks for " + state.selectedDate.toString());
        dateLabel.setTextFill(Color.WHITE);
        dateLabel.setStyle("-fx-font-size: 16px;");
        scheduleList.getChildren().add(dateLabel);

        List<Task> tasks = state.taskMap.getOrDefault(state.selectedDate, new ArrayList<>());

        if (tasks.isEmpty()) {
            Label none = new Label("No tasks scheduled.");
            none.setTextFill(Color.WHITE);
            scheduleList.getChildren().add(none);
        } else {
            for (Task t : tasks) {
                scheduleList.getChildren().add(createTaskCard(t));
            }
        }
    }

    private HBox createTaskCard(Task task) {
        VBox info = new VBox(
                new Label(task.title),
                new Label("Time: " + task.time),
                new Label("Place: " + task.location),
                new Label("Notes: " + task.notes)
        );
        info.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-padding: 10; -fx-border-radius: 10;");
        info.setPrefWidth(280);
        info.setSpacing(5);
        info.getChildren().forEach(node -> ((Label) node).setTextFill(Color.WHITE));

        Rectangle colorTag = new Rectangle(10, 10, task.color);
        HBox taskCard = new HBox(info, colorTag);

        // ✅ Click to open edit form
        taskCard.setOnMouseClicked(e -> {
            TaskFormScreen editForm = new TaskFormScreen(stage, state, task, updatedDate -> {
                state.selectedDate = updatedDate;
                updateCalendarGrid();
            });
            stage.setScene(new Scene(editForm, 375, 812));
        });

        return taskCard;
    }
}
