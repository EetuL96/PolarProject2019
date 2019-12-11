package com.example.polarproject.Classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.polarproject.Application;
import com.example.polarproject.LoginActivity;
import com.example.polarproject.MainActivity;
import com.example.polarproject.RegisterActivity;
import com.example.polarproject.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HerokuDataBase {

    RequestQueue mQueue;

    public HerokuDataBase(Context context)
    {
        mQueue = Volley.newRequestQueue(context);
    }

    public void setDatabaseLoginListener(Context context)
    {
        this.callbackInterface = (DataBaseLoginListener) context;
    }

    public void setDatabaseRegisterListener(Context context)
    {
        this.callbackInterface2 = (DatabaseRegisterListener) context;
    }

    public void setDatabaseAllUsersListener(DataBaseAllUsersListener context)
    {
        this.callbackInterface3 =  context;
    }

    public void setDatabaseSearchListener(DataBaseSearchUserListener listener)
    {
        callbackInterface4 = listener;
    }

    public void setDatabaseFollowListener(DatabaseFollowUserListener listener)
    {
        callbackInterface5 = listener;
    }

    public interface DatabaseFollowUserListener
    {
        void userFollowed();
        void userFollowError();
    }
    public interface DataBaseSearchUserListener
    {
        void userSearchFound(User user);
        void userSeacrhNotFound();
    }
    public interface DataBaseAllUsersListener
    {
        void userFound(User user);
        void allUsersError();
    }

    public interface DataBaseLoginListener
    {
        void userByEmailSuccess(User user);
        void userByEmailFailed();
        void userByEmailError();
        void loginError();
    }

    public interface DatabaseRegisterListener
    {
        void registerSuccess();
        void registerError();
    }
    DataBaseLoginListener callbackInterface = null;
    DatabaseRegisterListener callbackInterface2 = null;
    DataBaseAllUsersListener callbackInterface3 = null;
    DataBaseSearchUserListener callbackInterface4 = null;
    DatabaseFollowUserListener callbackInterface5 = null;

    public void getUserByEmail(String email, String token)
    {
        String url = "https://polarapp-oamk.herokuapp.com/users/email/" + email;
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

    public void createUserToDB(String email, String password, String firstname, String lastname)
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
        String url = "https://polarapp-oamk.herokuapp.com/users";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callbackInterface2.registerSuccess();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                callbackInterface2.registerError();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        mQueue.add(jsonObjReq);

    }

    public void getAllUsers()
    {
        String url = "https://polarapp-oamk.herokuapp.com/users";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try
                        {
                            int i = 0;
                            while (i < response.length())
                            {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String id = jsonObject.getString("_id");
                                String email = jsonObject.getString("email");
                                User user = new User();
                                user.setID(id);
                                user.setEmail(email);
                                callbackInterface3.userFound(user);
                                i++;
                            }
                            /*
                            String id = jsonObject.getString("_id");
                            String email = jsonObject.getString("email");
                            String firstname = jsonObject.getString("firstname");
                            String lastname = jsonObject.getString("lastname");

                            User user = new User();
                            user.setEmail(email);
                            user.setID(id);
                            user.setFirstName(firstname);
                            user.setLastName(lastname);
                            Log.d("GGGGG", firstname + lastname);*/
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("GGGGG", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("GGGGG", error.toString());
                        callbackInterface3.allUsersError();
                    }
                });
        mQueue.add(jsonArrayRequest);
        Log.d("LOL", "FINISH");
    }

    public void searchUserByEmail(String email)
    {
        String url = "https://polarapp-oamk.herokuapp.com/users/email/" + email;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            Log.d("RRRR", response.toString());

                            String email = response.getString("email");
                            String id = response.getString("_id");
                            User user = new User();
                            user.setEmail(email);
                            user.setID(id);
                            callbackInterface4.userSearchFound(user);
                            //Log.d("RRRR", "User fond: " + email);

                            //User user = new User();
                            //user.setID(id);
                            //user.setFirstName(firstname);
                            //user.setLastName(lastname);

                            //callbackInterface.userByEmailSuccess(user);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("RRRR", e.toString());
                            callbackInterface4.userSeacrhNotFound();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("RRRR", error.getMessage());
                        callbackInterface4.userSeacrhNotFound();
                    }
                });
        mQueue.add(jsonObjectRequest);
        Log.d("LOL", "FINISH");
    }

    public void createNewFollow(String ownId, String targetId)
    {
        JSONObject js = new JSONObject();
        try {
            js.put("myId", ownId);
            js.put("targetId", targetId);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "https://polarapp-oamk.herokuapp.com/follows";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST,url, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //callbackInterface2.registerSuccess();
                        Log.d("BBBB", "New Follow Created!");
                        callbackInterface5.userFollowed();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //callbackInterface2.registerError();
                Log.d("BBBB", error.getMessage());
                callbackInterface5.userFollowError();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        mQueue.add(jsonObjReq);
    }




}
