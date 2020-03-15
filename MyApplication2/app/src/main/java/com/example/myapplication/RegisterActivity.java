package com.example.myapplication;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class RegisterActivity extends AppCompatActivity {
    private EditText firstNameET, lastNameET, emailET, phoneET, addressET, passwordET, repeatPasswordET;
    private Button registerBTN;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        StrictMode.setThreadPolicy(policy);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void register(View view) {
    firstNameET = findViewById(R.id.firstNameET);
    lastNameET = findViewById(R.id.lastNameET);
    emailET = findViewById(R.id.emailET);
    phoneET = findViewById(R.id.phoneET);
    addressET = findViewById(R.id.addressET);
    passwordET = findViewById(R.id.passwordET);
    repeatPasswordET = findViewById(R.id.repeatPasswordET);
    registerBTN = findViewById(R.id.registerBTN);

    if (firstNameET.getText().toString().matches("")) {
        firstNameET.setError(getResources().getString(R.string.value_required));
        return;
    }
    if (lastNameET.getText().toString().matches("")) {
        lastNameET.setError(getResources().getString(R.string.value_required));
        return;
    }
    if (emailET.getText().toString().matches("")) {
        emailET.setError(getResources().getString(R.string.value_required));
        return;
    }
    if (! emailET.getText().toString().trim().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
        emailET.setError("Invalid email address");
    }
    if (phoneET.getText().toString().matches("")) {
        phoneET.setError(getResources().getString(R.string.value_required));
        return;
    }
    if (addressET.getText().toString().matches("")) {
        addressET.setError(getResources().getString(R.string.value_required));
        return;
    }
    if (passwordET.getText().toString().matches("")) {
        passwordET.setError(getResources().getString(R.string.value_required));
        return;
    }
    if (repeatPasswordET.getText().toString().matches("")) {
        repeatPasswordET.setError(getResources().getString(R.string.value_required));
        return;
    }
    if(!passwordET.getText().toString().equals( repeatPasswordET.getText().toString() )){
        repeatPasswordET.setError("Password does not match!");
        return;
    }

    RequestBody formBody = new FormBody.Builder()
            .add("first_name", firstNameET.getText().toString().trim())
            .add("last_name", lastNameET.getText().toString().trim())
            .add("email", emailET.getText().toString().trim())
            .add("phone_number", phoneET.getText().toString().trim())
            .add("address", addressET.getText().toString().trim())
            .add("password", passwordET.getText().toString().trim())
            .build();
    registerBTN.setClickable(false);
    try {
        JSONObject response =  JSON.run("/users/register", formBody);
        if(response.getInt("status") == 0){
            registerBTN.setClickable(true);
            Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(getApplicationContext(), "Registration Done",  Toast.LENGTH_LONG).show();
        firstNameET.setText("");
        lastNameET.setText("");
        emailET.setText("");
        phoneET.setText("");
        addressET.setText("");
        passwordET.setText("");
        repeatPasswordET.setText("");
    } catch (Exception e) {
        registerBTN.setClickable(true);
        Log.e("Check String", e.toString() );
        e.printStackTrace();
    }
}

}
