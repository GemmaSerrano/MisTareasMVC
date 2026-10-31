package com.example.miappnivel5.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.miappnivel5.R;

public class TaskDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);


        TextView taskDetailTextView = findViewById(R.id.taskDetailTextView);
        ImageView ic_back = findViewById(R.id.ic_back);

        String task = getIntent().getStringExtra("task_text");

        taskDetailTextView.setText("DESCRIPCIÓN: \n\n" + task);

        ic_back.setOnClickListener(v -> finish());


    }

}
