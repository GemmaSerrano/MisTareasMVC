package com.example.miappnivel5.view;

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

import com.example.miappnivel5.controller.TaskCallback;
import com.example.miappnivel5.controller.TaskController;
import com.example.miappnivel5.model.ApiService;
import com.example.miappnivel5.R;
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

public class MainActivity extends AppCompatActivity {

    private EditText etNewTask;
    private RecyclerView rvTasks;
    private Button btnAddTask, btnRemoveTask;
    private TaskAdapter taskAdapter;
    private List<String> taskList;
    private ImageView icExit;
    private TaskController taskController; // Instancia del controlado

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

        // Inicializamos el controlador pasándole el contexto y la lista
        taskController = new TaskController(this, taskList);


        // Adaptador con listener para borrado por toque largo
        taskAdapter = new TaskAdapter(taskList, position -> {
            taskController.saveTasks();
            Toast.makeText(MainActivity.this, "Tarea eliminada y guardada", Toast.LENGTH_SHORT).show();
        });



        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        rvTasks.setAdapter(taskAdapter);


        // Restaurar estado o cargar datos mediante el controlador
        if (savedInstanceState != null) {
            ArrayList<String> restoredTasks = savedInstanceState.getStringArrayList("tasks");
            if (restoredTasks != null) {
                taskList.clear();
                taskList.addAll(restoredTasks);
                taskAdapter.notifyDataSetChanged();
            }
        } else {
            taskController.loadTasks(new TaskCallback() {
                @Override
                public void onTasksLoaded(List<String> tasks) {
                    taskAdapter.notifyDataSetChanged();
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
        }



        // Botón para añadir tarea
        btnAddTask.setOnClickListener(v -> {
            String task = etNewTask.getText().toString().trim();
            if (!TextUtils.isEmpty(task)) {
                taskController.addTaskToApi(task, () -> {
                    taskAdapter.notifyDataSetChanged();
                    etNewTask.setText("");
                });
            } else {
                Toast.makeText(MainActivity.this, "Introduce la tarea", Toast.LENGTH_SHORT).show();
            }
        });

        // Botón para vaciar la lista
        btnRemoveTask.setOnClickListener(v -> {
            taskController.clearTasks();
            taskAdapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, "Lista borrada. Al reiniciar se cargará DummyJSON", Toast.LENGTH_SHORT).show();
        });

        icExit.setOnClickListener(v -> finishAffinity());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("tasks", new ArrayList<>(taskList));
    }
}







