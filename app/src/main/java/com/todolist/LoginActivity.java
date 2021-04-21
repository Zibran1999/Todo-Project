package com.todolist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {
    EditText loginUserEmail, loginPass;
    TextView createAccount, forgetPass;
    Button loginBtn;
    String loginUrl = "https://thisismymall.000webhostapp.com/To-Do%20List/login_api.php";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        progressDialog = new ProgressDialog(this);
        Paper.init(this);

        loginUserEmail = findViewById(R.id.login_username);
        loginPass = findViewById(R.id.login_password);
        createAccount = findViewById(R.id.create_account);
        forgetPass = findViewById(R.id.forget_password);

        loginBtn = findViewById(R.id.login_btn);

        createAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignupActivity.class)));

        loginBtn.setOnClickListener(v -> {
            progressDialog.setMessage("Login...");
            progressDialog.show();
            final String userEmail = loginUserEmail.getText().toString().trim();
            final String uPass = loginPass.getText().toString().trim();

            Paper.book().write(Prevelent.userEmail, userEmail);
            Paper.book().write(Prevelent.userPass, uPass);


            if (TextUtils.isEmpty(userEmail)) {
                loginUserEmail.setError("Field Required");
                if (TextUtils.isEmpty(uPass)) {
                    loginUserEmail.setError("Field Required");
                }
            }
            if (!validatePassword()) {
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, loginUrl, response -> {
                String logResponse = response.trim();
                if (logResponse.equalsIgnoreCase("Login Successfully")) {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity2.class));
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }, error -> {
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> loginMap = new HashMap<>();
                    loginMap.put("userName", userEmail);
                    loginMap.put("userPass", uPass);
                    return loginMap;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            requestQueue.add(request);

        });
    }

    private boolean validatePassword() {

        String valPassword = loginPass.getText().toString();
        String checkPassword = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)" +
                ".{4,}" +
                "$";

        // String checkPassword = "((?=.*[a-z])(?=.*\\\\d)(?=.*[A-Z])(?=.*[@#$%!]).{4,})";

        if (valPassword.isEmpty()) {

            loginPass.setError("Field can not be empty");
            return false;
        } else if (!valPassword.matches(checkPassword)) {


            loginPass.setError("Password should contain 4 characters!");
            return false;
        } else {
            loginPass.setError(null);
            loginPass.setVisibility(View.VISIBLE);
            return true;
        }
    }
}