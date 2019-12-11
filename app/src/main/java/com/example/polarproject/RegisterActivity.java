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
import com.example.polarproject.Classes.HerokuDataBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, HerokuDataBase.DatabaseRegisterListener {

    Button buttonRegister;
    EditText editTextEmail;
    EditText editTextPassword;
    EditText editTextFirstname;
    EditText editTextLastname;

    private String email;
    private String password;
    private String firstname;
    private String lastname;

    HerokuDataBase herokuDataBase = null;

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

        herokuDataBase = new HerokuDataBase(this);
        herokuDataBase.setDatabaseRegisterListener(this);
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
            herokuDataBase.createUserToDB(email, password, firstname, lastname);
        }
    }

    @Override
    public void registerSuccess() {
        Toast toast = Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void registerError() {
        Toast toast = Toast.makeText(RegisterActivity.this, "Error while registering...", Toast.LENGTH_LONG);
        toast.show();
    }
}
