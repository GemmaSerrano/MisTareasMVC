package com.example.miappnivel4;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {


    // Declaramos la interfaz para el click largo
    public interface OnTaskLongClickListener {
        void onTaskLongClick(int position);
    }
    private List<String> taskList;
    private OnTaskLongClickListener deleteListener;

    public TaskAdapter(List<String> taskList) {
        this.taskList = taskList;
    }

    // Modificamos el constructor para recibir el listener
    public TaskAdapter(List<String> taskList, OnTaskLongClickListener deleteListener) {
        this.taskList = taskList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        String task = taskList.get(position);
        holder.textViewTask.setText(task);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
                intent.putExtra("TASK", task);
                v.getContext().startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = holder.getBindingAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    taskList.remove(position);
                    notifyItemRemoved(position);
                }
                if (deleteListener != null) {
                    deleteListener.onTaskLongClick(position);
                }
                return true;
            }
        });

    }
    @Override
    public int getItemCount() {
        return taskList.size();
    }
    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTask;
        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTask = itemView.findViewById(R.id.tvItemTask);
        }



    }
}
