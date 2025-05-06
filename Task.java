package com.example.hellofx;

import javafx.scene.paint.Color;
import java.time.LocalDate;

public class Task {
    public String title;
    public String time;
    public String location;
    public String notes;
    public Color color;
    public LocalDate date;

    public Task(String title, String time, String location, String notes, Color color, LocalDate date) {
        this.title = title;
        this.time = time;
        this.location = location;
        this.notes = notes;
        this.color = color;
        this.date = date;
    }
}



