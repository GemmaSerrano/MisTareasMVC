package com.example.miappnivel5.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.miappnivel5.model.ApiService;
import com.example.miappnivel5.model.Todo;
import com.example.miappnivel5.model.TodosResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TaskController {

    private Context context;
    private ApiService apiService;
    private List<String> taskList;


    public TaskController(Context context, List<String> taskList) {
        this.context = context;
        this.taskList = taskList;

        // Inicializamos Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummyjson.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    // 1. Guardar tareas en SharedPreferences
    public void saveTasks() {
        SharedPreferences preferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        editor.putString("task_list", json);
        editor.apply();
    }

    // 2. Cargar tareas (Local o API)
    public void loadTasks(TaskCallback callback) {
        SharedPreferences preferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE);
        String json = preferences.getString("task_list", "");

        if (!json.isEmpty()) {
            try {
                Type type = new TypeToken<ArrayList<String>>() {}.getType();
                ArrayList<String> savedTasks = new Gson().fromJson(json, type);

                if (savedTasks != null && !savedTasks.isEmpty()) {
                    taskList.clear();
                    taskList.addAll(savedTasks);
                    callback.onTasksLoaded(taskList);
                    return;
                }
            } catch (Exception e) {
                preferences.edit().remove("task_list").apply();
            }
        }

        // Si no hay nada en local, tiramos de la API
        loadTasksFromApi(callback);
    }

    // 3. Descargar desde la API de DummyJson
    public void loadTasksFromApi(TaskCallback callback) {
        apiService.getTasks().enqueue(new Callback<TodosResponse>() {
            @Override
            public void onResponse(Call<TodosResponse> call, Response<TodosResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getTodos() != null) {
                    taskList.clear();
                    for (Todo todo : response.body().getTodos()) {
                        taskList.add(todo.getTodo());
                    }
                    saveTasks(); // Guardamos en local tras descargar
                    callback.onTasksLoaded(taskList);
                } else {
                    callback.onError("Error al obtener los datos del servidor.");
                }
            }

            @Override
            public void onFailure(Call<TodosResponse> call, Throwable t) {
                callback.onError("No se pudieron cargar las tareas.");
            }
        });
    }

    // 4. Añadir tarea a la API
    public void addTaskToApi(String taskTitle, Runnable onSuccessCallback) {
        Todo newTodo = new Todo(taskTitle, false, 1);

        apiService.addTask(newTodo).enqueue(new Callback<Todo>() {
            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {
                if (response.isSuccessful() && response.body() != null) {
                    taskList.add(response.body().getTodo());
                    saveTasks();
                    onSuccessCallback.run(); // Avisa a la vista para limpiar el input y refrescar
                } else {
                    // Si falla la red temporalmente, la añadimos localmente por UX
                    taskList.add(taskTitle);
                    saveTasks();
                    onSuccessCallback.run();
                }
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                taskList.add(taskTitle); // Fallback local
                saveTasks();
                onSuccessCallback.run();
            }
        });
    }

    // 5. Borrar lista completa
    public void clearTasks() {
        taskList.clear();
        SharedPreferences preferences = context.getSharedPreferences("tasks", Context.MODE_PRIVATE);
        preferences.edit().remove("task_list").apply();
    }
}