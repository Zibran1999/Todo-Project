package com.todolist;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {
    List<Model> modelList;

    public TodoAdapter(List<Model> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String type = modelList.get(position).getTaskType();
        String task = modelList.get(position).getTask();
        String time = modelList.get(position).getTaskTime();
        String date = modelList.get(position).getTaskDate();

        holder.setData(type, task, time, date);
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskType, task, time, date;
        CheckBox checkBox;
        String delete = "https://thisismymall.000webhostapp.com/To-Do%20List/delete_task_api.php";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskType = itemView.findViewById(R.id.task_type);
            task = itemView.findViewById(R.id.task);
            time = itemView.findViewById(R.id.task_time);
            date = itemView.findViewById(R.id.task_date);
            checkBox = itemView.findViewById(R.id.checkBox);


        }

        public void setData(String sType, String sTask, String sTime, String sDate) {
            taskType.setText(sType);
            task.setText(sTask);
            time.setText(sTime);
            date.setText(sDate);

            checkBox.setOnClickListener(v -> {
                StringRequest request = new StringRequest(Request.Method.POST, delete, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("Task Deleted")) {
                            Toast.makeText(itemView.getContext(), "Task Completed", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(itemView.getContext(),MainActivity2.class);
                            itemView.getContext().startActivity(intent);
                            ((Activity)itemView.getContext()).finish();
                        }else {
                            Toast.makeText(itemView.getContext(), "Try Again", Toast.LENGTH_SHORT).show();
                        }

                    }
                }, error -> {

                }) {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("task", sTask);
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(itemView.getContext());
                queue.add(request);
            });
        }
    }


}
