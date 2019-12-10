package com.example.polarproject.Classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.polarproject.Application;
import com.example.polarproject.LoginActivity;
import com.example.polarproject.MainActivity;
import com.example.polarproject.User;

import org.json.JSONException;
import org.json.JSONObject;

public class HerokuDataBase {

    RequestQueue mQueue;

    public HerokuDataBase(Context context)
    {
        this.callbackInterface = (DataBaseListener) context;
        mQueue = Volley.newRequestQueue(context);
    }



    public interface DataBaseListener
    {
        void userByEmailSuccess(User user);
        void userByEmailFailed();
        void userByEmailError();
        void loginError();
    }
    DataBaseListener callbackInterface = null;

    public void getUserByEmail(String email, String token)
    {
        String url = " https://polarapp-oamk.herokuapp.com/users/email/" + email;
        JSONObject js = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try
                        {

                            String id = jsonObject.getString("_id");
                            String email = jsonObject.getString("email");
                            String firstname = jsonObject.getString("firstname");
                            String lastname = jsonObject.getString("lastname");

                            User user = new User();
                            user.setID(id);
                            user.setFirstName(firstname);
                            user.setLastName(lastname);

                            callbackInterface.userByEmailSuccess(user);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("LOL", e.toString());
                            callbackInterface.userByEmailFailed();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("LOL", error.getMessage().toString());
                        callbackInterface.userByEmailError();
                    }
                });
        mQueue.add(jsonObjectRequest);
        Log.d("LOL", "FINISH");
    }

    public void loginToServer(String email, String password)
    {
        Log.d("LOL", "START");
        Log.d("LOL", "email: " +  email + " password: " + password);
        String url = "https://polarapp-oamk.herokuapp.com/login";
        JSONObject js = new JSONObject();
        try {
            js.put("email", email);
            js.put("password", password);
            Log.d("LOL", "AAAAAA");
        }catch (JSONException e) {
            e.printStackTrace();
            callbackInterface.loginError();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, js, new Response.Listener<JSONObject>() {

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
                                getUserByEmail(email, token);
                            }
                            else
                            {
                                Log.d("LOL", "Login Failed");
                                callbackInterface.loginError();
                            }
                            Log.d("LOL", jsonObject.toString());

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("LOL", e.toString());
                            callbackInterface.loginError();

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.d("LOL", error.getMessage().toString());
                        callbackInterface.loginError();
                    }
                });
        mQueue.add(jsonObjectRequest);
        Log.d("LOL", "FINISH");
    }
}
