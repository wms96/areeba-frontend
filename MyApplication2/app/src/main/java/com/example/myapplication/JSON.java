package com.example.myapplication;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class JSON {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static JSONObject run(String url, RequestBody formBody) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url( "http://7b37fb95.ngrok.io" + url)
                .post(formBody)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBodyStr = response.body().string();
            JSONObject responseBody = new JSONObject(responseBodyStr);
            return responseBody;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static JSONObject get(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url( "http://7b37fb95.ngrok.io" + url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseBodyStr = response.body().string();
             responseBodyStr = responseBodyStr.replace("\\\"","'");
            JSONObject responseBody = new JSONObject(responseBodyStr.substring(1,responseBodyStr.length()-1));
            return responseBody;
            }
    }


    //        OkHttpClient client = new OkHttpClient();
//        String url = "https://reqres.in/api/users?page=2";
//
//
//        Request request = new Request.Builder()
//                .url(url)
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    final String myResponse = response.body().string();
//
//                    Main2Activity.this.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mTextViewResult.setText(myResponse);
//                        }
//                    });
//                }
//            }
//        });
}
