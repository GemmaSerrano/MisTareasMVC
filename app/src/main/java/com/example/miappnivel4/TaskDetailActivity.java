package com.example.miappnivel4;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        String task = getIntent().getStringExtra("TASK");
        TextView taskDetailTextView = findViewById(R.id.taskDetailTextView);
        taskDetailTextView.setText(task);
    }

}
