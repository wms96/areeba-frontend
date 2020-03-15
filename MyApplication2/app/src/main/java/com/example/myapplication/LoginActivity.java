package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class LoginActivity extends AppCompatActivity {
    private EditText phoneET, passwordET;
    private Button loginBTN;
    private SharedPreferences mPreferences;
    // i know this isn't the correct way to do this
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StrictMode.setThreadPolicy(policy);
        mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
        String token = mPreferences.getString("token", "");
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void login(View view) {
        phoneET = findViewById(R.id.phoneET);
        passwordET = findViewById(R.id.passwordET);
        loginBTN = findViewById(R.id.loginBTN);
        if (phoneET.getText().toString().matches("")) {
            phoneET.setError(getResources().getString(R.string.value_required));
            return;
        }
        if (passwordET.getText().toString().matches("")) {
            passwordET.setError(getResources().getString(R.string.value_required));
            return;
        }
        loginBTN.setClickable(false);
        RequestBody formBody = new FormBody.Builder()
                .add("phone_number", phoneET.getText().toString().trim())
                .add("password", passwordET.getText().toString().trim())
                .build();

        try {
            JSONObject response =  JSON.run("/users/login", formBody);
            JSONObject getAuth =  JSON.get("/users/getAuth");
            if(response.getInt("status") == 0){
                Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
                loginBTN.setClickable(true);
                return;
            }
            mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
            SharedPreferences.Editor editor = mPreferences.edit();
            editor.putString("token",  response.getString("token")); // Storing string
            editor.putString("results",  response.getString("results")); // Storing string
            editor.putString("getAuth",  getAuth.getString("access_token")); // Storing string
            editor.commit(); // commit changes

            Toast.makeText(getApplicationContext(), "login Done",  Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            loginBTN.setClickable(true);
            Log.e("Check String", e.toString() );
            e.printStackTrace();
        }
    }
    public void goToRegisterPage(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void goToResetPasswordPage(View view) {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        startActivity(intent);
    }

}
