package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private EditText amountET, detailsET;
    private TextView title;
    private ImageView qrImage;
    private String amountValue, detailsValue, client_id;
    private String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    private Bitmap bitmap;
    private QRGEncoder qrgEncoder;
    private AppCompatActivity activity;
    private SharedPreferences mPreferences;
    private  JSONObject results;
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.setThreadPolicy(policy);

        qrImage = findViewById(R.id.qr_image);
        amountET = findViewById(R.id.amountET);
        detailsET = findViewById(R.id.detailsET);
        activity = this;
        mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
        String d = mPreferences.getString("token", "");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void generateQr(View view){
        amountValue = amountET.getText().toString().trim();
        detailsValue = detailsET.getText().toString();
        if (amountET.getText().toString().matches("")) {
            amountET.setError(getResources().getString(R.string.value_required));
            return;
        }
        if (detailsET.getText().toString().matches("")) {
            detailsET.setError(getResources().getString(R.string.value_required));
            return;
        }
        mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
        try {
            results = new JSONObject( mPreferences.getString("results", "{}"));
            client_id =  results.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject qrInfo = new JSONObject();
        RequestBody formBody = new FormBody.Builder()
                .add("client_id", client_id)
                .add("amount", amountValue)
                .add("details", detailsValue)
                .build();
        try {
            JSONObject response =  JSON.run("/debit/insert", formBody);
            try {
                qrInfo.put("qr_ref", results.getString("qr_ref"));
                qrInfo.put("name", results.getString("first_name") +" "+ results.getString("last_name"));
                qrInfo.put("amount", amountValue);
                qrInfo.put("details", detailsValue);
                qrInfo.put("transaction", response.getString("transaction"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(response.getInt("status") == 0){
                Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(getApplicationContext(), response.getString("message"),  Toast.LENGTH_LONG).show();
            amountET.setText("");
            detailsET.setText("");

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        qrgEncoder = new QRGEncoder(
                qrInfo.toString(), null,
                QRGContents.Type.TEXT,
                smallerDimension);
        try {
            qrgEncoder.getTitle().toString();
            bitmap = qrgEncoder.encodeAsBitmap();
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        } catch (Exception e) {
            Log.e("Check String", e.toString() );
            e.printStackTrace();
        }

    }
    public void goToAnotherActivity(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void goToLoginActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }
    public void goToCreditActivity(View view) {
        Intent intent = new Intent(this, CreditActivity.class);
        startActivity(intent);

    }
    public void gotToCreateCardActivity(View view) {
        Intent intent = new Intent(this, CreateCardActivity.class);
        startActivity(intent);
    }
    public void goToCreateCardActivity(View view) {
        Intent intent = new Intent(this, CreateNewCard.class);
        startActivity(intent);
    }
    public void goToHistoryActivity(View view) {
        Intent intent = new Intent(this, History.class);
        startActivity(intent);
    }


}