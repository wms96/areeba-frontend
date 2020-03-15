package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class History extends AppCompatActivity {
    private SharedPreferences mPreferences;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ArrayList userList = null;
        try {
            userList = getListData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ListView lv = (ListView) findViewById(R.id.user_list);
        lv.setAdapter(new CustomListAdapter(this, userList));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                ListItem user = (ListItem) lv.getItemAtPosition(position);
                Toast.makeText(History.this, "Selected :" + " " + user.getName()+", "+ user.getLocation(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private ArrayList getListData() throws Exception {
        ArrayList<ListItem> results = new ArrayList<>();

        mPreferences = getSharedPreferences("areeba", MODE_PRIVATE);
        JSONObject usersData = new JSONObject( mPreferences.getString("results", "{}"));

        RequestBody formBody = null;
        try {
            formBody = new FormBody.Builder()
                    .add("id", usersData.getString("id"))
                    .build();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject response =  JSON.run("/users/creditHistory", formBody);
        JSONArray keys = response.names ();
        String X = response.getString("item");
        JSONArray items = new JSONArray(response.getString("item"));
        for (int i = 0; i < items.length (); ++i) {
            ListItem user = new ListItem();
           JSONObject obj = (JSONObject) items.get(i);
            user.setName(obj.getString ("debiter"));
            user.setDesignation(obj.getString ("amount"));
            user.setLocation(obj.getString ("created_at"));
            results.add(user);
        }
        return results;
    }
}
