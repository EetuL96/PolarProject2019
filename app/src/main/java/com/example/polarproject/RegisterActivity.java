package com.example.polarproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonRegister;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextFirstname;
    EditText editTextLastname;

    String url = "https://polarapp-oamk.herokuapp.com/users";
    private String email;
    private String password;
    private String firstname;
    private String lastname;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);

        editTextEmail = (EditText) findViewById(R.id.emailEditText);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextFirstname = (EditText) findViewById(R.id.firstnameEditText);
        editTextLastname = (EditText) findViewById(R.id.lastnameEditText);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(intent);
        RegisterActivity.this.finish();
    }

    @Override
    public void onClick(View v) {

        if (v == findViewById(R.id.buttonRegister))
        {
            email = editTextEmail.getText().toString();
            password = editTextPassword.getText().toString();
            firstname = editTextFirstname.getText().toString();
            lastname = editTextLastname.getText().toString();
            createUserToDB();
        }
    }

    public void createUserToDB()
    {
        JSONObject js = new JSONObject();
        try {
            js.put("email", email);
            js.put("password", password);
            js.put("firstname", firstname);
            js.put("lastname", lastname);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast toast = Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(jsonObjReq);

    }
}
