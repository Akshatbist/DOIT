package com.example.hellofx;

import java.time.LocalDate;
import java.util.*;

public class AppState {
    public final Map<LocalDate, List<Task>> taskMap = new HashMap<>();
    public LocalDate selectedDate = LocalDate.now();
}
