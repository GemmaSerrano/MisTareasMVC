package com.example.miappnivel4;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class MainActivity extends AppCompatActivity {

    private EditText etNewTask;
    private RecyclerView rvTasks;
    private Button btnAddTask, btnRemoveTask;
    private TaskAdapter taskAdapter;
    private List<String> taskList;
    private ApiService apiService;
    private ImageView icExit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Para dar movimiento al logo y título
        ImageView ivLogo = findViewById(R.id.ivLogo);
        TextView tvTitle = findViewById(R.id.tvTitle);

        ivLogo.setAlpha(0f); tvTitle.setAlpha(0f);
        ivLogo.setScaleX(0f); tvTitle.setScaleX(0f);
        ivLogo.setScaleY(0f); tvTitle.setScaleY(0f);

        ivLogo.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setDuration(2400)
                .start();
        tvTitle.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotation(360f)
                .setDuration(2400)
                .start();


        etNewTask = findViewById(R.id.etNewTask);
        rvTasks = findViewById(R.id.rvTasks);
        btnAddTask = findViewById(R.id.btnAddTask);
        btnRemoveTask = findViewById(R.id.btnRemoveTask);
        icExit = findViewById(R.id.ic_exit);

        taskList = new ArrayList<>();

        // Inicializamos el adaptador pasando el listener
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskLongClickListener() {
            @Override
            public void onTaskLongClick(int position) {
                saveTasks(); // <--- ¡Aquí guardamos en SharedPreferences!
                Toast.makeText(MainActivity.this, "Tarea eliminada y guardada", Toast.LENGTH_SHORT).show();
            }
        });

        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);

        //Para guardar y recuperar las tareas del servidor que ponga
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummyjson.com/") //simulador de servidor real y BD para pruebas
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        if (savedInstanceState != null) {
            ArrayList<String> restoredTasks = savedInstanceState.getStringArrayList("tasks");
            if (restoredTasks != null) {
                taskList.clear();
                taskList.addAll(restoredTasks);
                taskAdapter.notifyDataSetChanged();
            }
        } else {
            loadTasks();   // Carga de local o API de forma segura
        }

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = etNewTask.getText().toString().trim();

                if (!TextUtils.isEmpty(task)) {
                    addTaskToApi(task);  //del servidor

                } else {
                    Toast.makeText(MainActivity.this,
                            "Introduce la tarea",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRemoveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskList.clear();
                taskAdapter.notifyDataSetChanged();

                // Borra la clave en SharedPreferences para que al reabrir la app cargue de la API
                SharedPreferences preferences = getSharedPreferences("tasks", MODE_PRIVATE);
                preferences.edit().remove("task_list").apply();

                Toast.makeText(MainActivity.this,
                        "Lista borrada. Al reiniciar se cargará DummyJSON",
                        Toast.LENGTH_SHORT).show();
            }
        });

        icExit.setOnClickListener(v -> finishAffinity());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("tasks", new ArrayList<>(taskList));
    }

    //Guarda las tareas en el móvil con SharedPreferences y con Gson
    private void saveTasks() {
        SharedPreferences preferences = getSharedPreferences("tasks", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        editor.putString("task_list", json);
        editor.apply();
    }

    //Recupera las tareas guardadas en el móvil
    private void loadTasks() {
        SharedPreferences preferences = getSharedPreferences("tasks", MODE_PRIVATE);
        String json = preferences.getString("task_list", "");

        if (!json.isEmpty()) {
            try {

                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();

                ArrayList<String> savedTasks = new Gson().fromJson(json, type);

                if (savedTasks != null && !savedTasks.isEmpty()) {
                    taskList.clear();
                    taskList.addAll(savedTasks);
                    taskAdapter.notifyDataSetChanged();
                }else{
                    preferences.edit().remove("task_list").apply();
                    loadTasksFromApi();
                }

            }catch (Exception e){
                preferences.edit().remove("task_list").apply();
                loadTasksFromApi();
            }
        } else {
            loadTasksFromApi();

        }
    }


   //Descarga las tareas desde el servidor DummyJson
    private void loadTasksFromApi() {

        apiService.getTasks().enqueue(new Callback<TodosResponse>() {

            @Override
            public void onResponse(Call<TodosResponse> call, Response<TodosResponse> response) {
                // Comprobamos si la respuesta es exitosa
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getTodos() != null) {
                        android.util.Log.d("API_DEBUG", "¡Conexión OK! Tareas recibidas: " + response.body().getTodos().size());

                        taskList.clear();
                        for (Todo todo : response.body().getTodos()) {
                            taskList.add(todo.getTodo());
                        }

                        taskAdapter.notifyDataSetChanged();
                        saveTasks(); // Guardamos en local las nuevas tareas descargadas
                    } else {
                        android.util.Log.d("API_DEBUG", "Error: La respuesta del servidor vino vacía (body o todos es null)");
                    }
                } else {
                    android.util.Log.d("API_DEBUG", "Error HTTP del servidor: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TodosResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "No se pudieron cargar las tareas",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Envía una tarea al servidor para añadirla en DummyJson
    private void addTaskToApi(String task) {

        Todo newTodo = new Todo(task, false, 1);

        apiService.addTask(newTodo).enqueue(new Callback<Todo>() {

            @Override
            public void onResponse(Call<Todo> call, Response<Todo> response) {

                if (response.isSuccessful() && response.body() != null) {

                    taskList.add(response.body().getTodo());
                    taskAdapter.notifyDataSetChanged();
                    saveTasks();

                    etNewTask.setText("");

                } else {
                    Toast.makeText(MainActivity.this,
                            "Error al añadir la tarea",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Todo> call, Throwable t) {
                Toast.makeText(MainActivity.this,
                        "No se pudo enviar la tarea",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}


