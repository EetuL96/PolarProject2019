package com.example.polarproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener {

    TextView textViewRegister;
    Button buttonLogin;
    EditText editTextEmail;
    EditText editTextPassword;
    RequestQueue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(this);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        mQueue = Volley.newRequestQueue(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonLogin.setEnabled(true);
    }

    @Override
    public void onClick(View v) {

        if (v == findViewById(R.id.textViewRegister))
        {
            Log.d("LOL", "Register link pressed");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            LoginActivity.this.startActivity(intent);
        }
        else if (v == findViewById(R.id.buttonLogin))
        {
            Log.d("LOL", "Login pressed!");
            buttonLogin.setEnabled(false);
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();
            loginToServer(email, password);
            //Intent i = new Intent(LoginActivity.this, MainActivity.class);
            //startActivity(i);
            //finish();

        }
    }

    public void loginToServer(String email, String password)
    {
        Log.d("LOL", "START");
        String url = "https://polarapp-oamk.herokuapp.com/login";
        JSONObject js = new JSONObject();
        try {
            js.put("email", email);
            js.put("password", password);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try
                        {

                            boolean auth = jsonObject.getBoolean("auth");
                            String token = jsonObject.getString("token");
                            Log.d("TOKEN", "Token: " + token);
                            if (auth)
                            {
                                Log.d("LOL", "Login success!");
                            }
                            else
                            {
                                Log.d("LOL", "Login Failed");
                            }
                            Log.d("LOL", jsonObject.toString());
                            /*((Application) LoginActivity.this.getApplication()).setUser(user);
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();*/
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("LOL", e.toString());
                            buttonLogin.setEnabled(true);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("LOL", error.getMessage().toString());
                        buttonLogin.setEnabled(true);
                    }
                });
        mQueue.add(jsonObjectRequest);
        Log.d("LOL", "FINISH");
    }
}
