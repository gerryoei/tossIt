package com.google.firebase.example.fireeats;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        String username= getIntent().getStringExtra("username");
        Log.d("UserInfo username", username);
        TextView u_view = findViewById(R.id.username);
        u_view.setText(username);
        requestBalance(username);
        ImageButton close_btn = findViewById(R.id.close_profile);
        close_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.close_profile:
                closeProfile();
                break;
        }
    }
    private void closeProfile() {
        Log.d("Closing Profile", "Success");

        Intent profile_intent=new Intent(UserInfoActivity.this, MainActivity.class);
        startActivity(profile_intent);
    }
    private void requestBalance(String username){
        String auth_key = "ba3960564eccc01469a256e4fec3af3d";
        String user_key = "5db48c813c8c2216c9fcb63d";
        String url;
        if (username == "Ezequiel") {
            Log.d("requestBalance", "Ezequiel");
            user_key = "5db48c813c8c2216c9fcb63d";
            url ="http://api.reimaginebanking.com/accounts/" + user_key + "?key=" + auth_key;
        } else {
            //username == "Elbert"
            Log.d("requestBalance", "Elbert");
            user_key = "5db48daf3c8c2216c9fcb63e";
            url = "http://api.reimaginebanking.com/accounts/" + user_key + "?key=" + auth_key;
        }

        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET, url,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("VolleyResponse" ,response.toString());
                        try {

                            JSONObject obj = new JSONObject(response.toString());

                            TextView balance_view = findViewById(R.id.balance);
                            balance_view.setText("Balance: $" + obj.getString("balance"));
                            TextView nickname_view = findViewById(R.id.nickname);
                            nickname_view.setText(obj.getString("nickname"));

                        } catch (Throwable t) {
                            Log.e("My App", "Could not parse malformed JSON: \"" + response.toString() + "\"");
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errResponse", "That didn't work! "+ error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
