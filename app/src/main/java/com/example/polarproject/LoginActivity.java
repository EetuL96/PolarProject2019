package com.example.polarproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity  implements View.OnClickListener, HerokuDataBase.DataBaseLoginListener {

    TextView textViewRegister;
    Button buttonLogin;
    EditText editTextEmail;
    EditText editTextPassword;
    HerokuDataBase herokuDataBase = null;

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
        herokuDataBase = new HerokuDataBase(this);
        herokuDataBase.setDatabaseLoginListener(this);
    }

    @Override
    public void onResume(){
        super.onResume();
        buttonLogin.setEnabled(true);
    }

    private void closeKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == findViewById(R.id.textViewRegister))
        {
            Log.d("LOL", "Register link pressed");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            LoginActivity.this.startActivity(intent);
            LoginActivity.this.finish();
        }
        else if (v == findViewById(R.id.buttonLogin))
        {
            Log.d("LOL", "Login pressed!");
            closeKeyboard();
            buttonLogin.setEnabled(false);
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            herokuDataBase.loginToServer(email, password);
        }
    }


    @Override
    public void userByEmailSuccess(User user) {
        ((Application) LoginActivity.this.getApplication()).setUser(user);
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void userByEmailFailed() {
        buttonLogin.setEnabled(true);
    }

    @Override
    public void userByEmailError() {
        buttonLogin.setEnabled(true);
    }

    @Override
    public void loginError() {
        Toast toast = Toast.makeText(LoginActivity.this, "Check password and email!", Toast.LENGTH_LONG);
        toast.show();
        buttonLogin.setEnabled(true);
    }
}
