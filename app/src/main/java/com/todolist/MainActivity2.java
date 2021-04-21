package com.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton floatingActionButton;
    Dialog dialog;
    EditText taskType, task;
    TextView taskTime, taskDate;
    Button addTask;
    int year, month, day, hour, minutes;
    String uploadTaskUrl = "https://thisismymall.000webhostapp.com/To-Do%20List/upload_task.php";
    String fetchTask = "https://thisismymall.000webhostapp.com/To-Do%20List/fetch_task.php";
    List<Model> modelList;
    TodoAdapter todoAdapter;
    RecyclerView todoRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        floatingActionButton = findViewById(R.id.floatingActionButton);
        todoRecyclerView = findViewById(R.id.todo_recyclerView);
        floatingActionButton.setOnClickListener(v -> addData());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        todoRecyclerView.setLayoutManager(layoutManager);

        modelList = new ArrayList<>();
        fetchUserTask();


    }

    private void fetchUserTask() {
        StringRequest request = new StringRequest(Request.Method.POST, fetchTask, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("llllllllllll", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (success.equals("1")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            String tType = jsonObject1.getString("tasktype");
                            String task = jsonObject1.getString("task");
                            String tTime = jsonObject1.getString("time");
                            String tData = jsonObject1.getString("date");
                            modelList.add(new Model(tType, task, tTime, tData));
                            todoAdapter = new TodoAdapter(modelList);
                            todoRecyclerView.setAdapter(todoAdapter);
                            todoAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("ttttttttttttttt", String.valueOf(modelList));
            }
        }, error -> {

        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("uId", MainActivity.id);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void addData() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_your_task);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.show();
        taskType = dialog.findViewById(R.id.task_type);
        task = dialog.findViewById(R.id.task);
        taskTime = dialog.findViewById(R.id.task_time);
        taskDate = dialog.findViewById(R.id.task_date);
        addTask = dialog.findViewById(R.id.add_task_btn);

        taskTime.setOnClickListener(this);
        taskDate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == taskDate) {
            final Calendar calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    taskDate.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
                }
            }, year, month, day);
            datePickerDialog.show();
        }
        if (v == taskTime) {
            final Calendar calendar = Calendar.getInstance();
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    boolean isPM = (hourOfDay >= 12);
                    taskTime.setText(String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM"));
                }
            }, hour, minutes, false);
            timePickerDialog.show();


        }

        addTask.setOnClickListener(v1 -> {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            dialog.dismiss();
            progressDialog.show();

            String tType = taskType.getText().toString();
            String mTask = task.getText().toString();
            String mTime = taskTime.getText().toString();
            String mDate = taskDate.getText().toString();

            StringRequest request = new StringRequest(Request.Method.POST, uploadTaskUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(MainActivity2.this, response.trim(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity2.class));
                    finish();
                    progressDialog.dismiss();
                }
            }, error -> {

            }) {
                @Nullable
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("uId", MainActivity.id);
                    map.put("taskType", tType);
                    map.put("task", mTask);
                    map.put("time", mTime);
                    map.put("date", mDate);
                    return map;
                }
            };
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        });
    }
}