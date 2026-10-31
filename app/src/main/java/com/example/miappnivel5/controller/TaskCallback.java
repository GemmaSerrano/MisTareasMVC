package com.example.miappnivel5.controller;

import com.example.miappnivel5.model.Todo;

import java.util.List;

// Interfaz para avisar a la Vista cuando los datos cambien o haya errores
public interface TaskCallback {
    void onTasksLoaded(List<String> tasks);
    void onError(String message);
}