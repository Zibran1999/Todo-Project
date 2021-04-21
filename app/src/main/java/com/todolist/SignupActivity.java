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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

public class SignupActivity extends AppCompatActivity {

    EditText name, userEmail, userPass;
    TextView skipBtn, alreadyAccount;
    Button signUpBtn;
    String url = "https://thisismymall.000webhostapp.com/To-Do%20List/registration_api.php";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        Paper.init(this);
        name = findViewById(R.id.full_name);
        userEmail = findViewById(R.id.user_email);
        userPass = findViewById(R.id.user_password);

        skipBtn = findViewById(R.id.skip_btn);
        alreadyAccount = findViewById(R.id.already_have_account);
        progressDialog = new ProgressDialog(this);

        signUpBtn = findViewById(R.id.signUp_btn);

        skipBtn.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity2.class)));
        alreadyAccount.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), LoginActivity.class)));

        signUpBtn.setOnClickListener(v -> {
            progressDialog.setMessage("Creating user....");
            progressDialog.show();
            final String _fullName = name.getText().toString().trim();
            final String _userEmail = userEmail.getText().toString().trim();
            final String _userPass = userPass.getText().toString().trim();

            Paper.book().write(Prevelent.userPass, _userPass);
            Paper.book().write(Prevelent.fullName, _fullName);
            Paper.book().write(Prevelent.userEmail, _userEmail);

            if (TextUtils.isEmpty(_fullName)) {
                name.setError("Can't empty!");
                if (TextUtils.isEmpty(_userEmail)) {
                    userEmail.setError("Can't empty!");
                    if (TextUtils.isEmpty(_userPass)) {
                        userPass.setError("Can't empty!");
                    }
                }
            }
            if (!validatePassword()) {
                return;
            }

            StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
                String res = response.trim();
                if (res.equalsIgnoreCase("Registered successfully.")) {
                    startActivity(new Intent(SignupActivity.this, MainActivity2.class));
                    Toast.makeText(SignupActivity.this, response, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, response, Toast.LENGTH_LONG).show();
                }
                progressDialog.dismiss();

            }, error -> {
                Toast.makeText(SignupActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> map = new HashMap<>();
                    map.put("fullName", _fullName);
                    map.put("userName", _userEmail);
                    map.put("userPass", _userPass);
                    return map;
                }
            };

            RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
            requestQueue.add(request);
        });
    }

    private boolean validateEmail() {

        String valName = userEmail.getText().toString();
        String checkEmail = "" +
                "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (valName.isEmpty()) {
            userEmail.setError("Field can not be empty");
            return false;
        } else if (!valName.matches(checkEmail)) {

            userEmail.setError("Invalid Email!");
            return false;
        } else {
            userEmail.setError(null);
            userEmail.setVisibility(View.VISIBLE);

            return true;
        }

    }

    private boolean validatePassword() {

        String valPassword = userPass.getText().toString();
        String checkPassword = "^" +
                "(?=.*[a-zA-Z])" +
                "(?=.*[@#$%^&+=])" +
                "(?=\\S+$)" +
                ".{4,}" +
                "$";

        // String checkPassword = "((?=.*[a-z])(?=.*\\\\d)(?=.*[A-Z])(?=.*[@#$%!]).{4,})";

        if (valPassword.isEmpty()) {

            userPass.setError("Field can not be empty");
            return false;
        } else if (!valPassword.matches(checkPassword)) {


            userPass.setError("Password should contain 4 characters!");
            return false;
        } else {
            userPass.setError(null);
            userPass.setVisibility(View.VISIBLE);
            return true;
        }

    }
}