package com.todolist;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    String uName, uPass;
    String loginUrl = "https://thisismymall.000webhostapp.com/To-Do%20List/login_api.php";
    String fetchDataUrl = "https://thisismymall.000webhostapp.com/To-Do%20List/fetchData_api.php";
    public static String id;
    String ssh ="qwqwq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);
        uName = Paper.book().read(Prevelent.userEmail);
        uPass = Paper.book().read(Prevelent.userPass);


        int TIME = 5000;
        new Handler().postDelayed(this::checkInternetConnection, TIME);

    }

    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
                connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
        ) {
            if (uName != null && uPass != null) {
                checkUser(uName, uPass);
                fetchUserData(uName);
            }else {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        }
    }

    private void fetchUserData(String uName) {
        StringRequest request = new StringRequest(Request.Method.POST, fetchDataUrl, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String success = jsonObject.getString("success");
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if (success.equals("1")) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        id = jsonObject1.getString("Id");
                        String fullName = jsonObject1.getString("FullName");
                        String userName = jsonObject1.getString("UserName");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> loginMap = new HashMap<>();
                loginMap.put("userName", uName);
                return loginMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }

    private void checkUser(String uName, String uPass) {

        StringRequest request = new StringRequest(Request.Method.POST, loginUrl, response -> {
            String logResponse = response.trim();
            if (logResponse.equalsIgnoreCase("Login Successfully")) {
                Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
            }


        }, error -> {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> loginMap = new HashMap<>();
                loginMap.put("userName", uName);
                loginMap.put("userPass", uPass);
                return loginMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);
    }
}