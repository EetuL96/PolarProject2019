package com.example.polarproject.Classes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
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

    public void setDatabaseGetFollowedListener(DatabaseGetFollowedUsersListener listener)
    {
        callbackInterface6 = listener;
    }

    public void setDatabaseSearchUserByEmailAndGetFollowedLister(DatabaseSearchUserByEmailAndGetFollowedLister lister)
    {
        this.callbackInterface8 = lister;
    }

    public interface DatabaseSearchUserByEmailAndGetFollowedLister
    {
        void searchSuccess(User user);
        void searchFailed();
    }

    public interface DatabaseGetFollowedUsersListener
    {
        void userGetFollowed(User user);
        void userGetFollowError();
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

    public interface DataBaseGetRoutesByIdListener
    {
        void getRoute(Route route);
        void getRoutesReady();
        void getRouteError();
    }

    public void setDatabaseGetRoutesListener(DataBaseGetRoutesByIdListener listener)
    {
        this.callbackInterface11 = listener;
    }

    public void setDatabaseGetAllUserAndCheckIfFollowedListener(DatabaseGetAllUserAndCheckIfFollowedListener listener)
    {
        this.callbackInterface7 = listener;
    }
    public interface DatabaseGetAllUserAndCheckIfFollowedListener
    {
        void getUserAndCheckIfFollowed(User user);
    }

    public void setDatabaseSetImageListener(DatabaseSetImageListener listener)
    {
        this.callbackInterface10 = listener;
    }

    public interface DatabaseSetImageListener
    {
        void imageSetSuccess();
        void imageSetError();
    }
    DataBaseLoginListener callbackInterface = null;
    DatabaseRegisterListener callbackInterface2 = null;
    DataBaseAllUsersListener callbackInterface3 = null;
    DataBaseSearchUserListener callbackInterface4 = null;
    DatabaseFollowUserListener callbackInterface5 = null;
    DatabaseGetFollowedUsersListener callbackInterface6 = null;
    DatabaseGetAllUserAndCheckIfFollowedListener callbackInterface7 = null;
    DatabaseSearchUserByEmailAndGetFollowedLister callbackInterface8 = null;
    DatabaseUnfollowListener callbackInterface9 = null;
    DatabaseSetImageListener callbackInterface10 = null;
    DataBaseGetRoutesByIdListener callbackInterface11 = null;

    public interface DatabaseUnfollowListener
    {
        void userUnfollowed();
        void userUnfollowedError();
    }

    public void setDatabaseUnfollowListener(DatabaseUnfollowListener listener)
    {
        this.callbackInterface9 = listener;
    }

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

    public void getFollowedUsers(String ownId)
    {
        String url = "https://polarapp-oamk.herokuapp.com/follows/myId/" + ownId + "/users";
        Log.d("XDXD", ownId);
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
                                user.setIsFollowed(true);
                                callbackInterface6.userGetFollowed(user);
                                Log.d("11111", user.getEmail());
                                i++;
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("11111", e.toString());
                            callbackInterface6.userGetFollowError();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("11111", error.toString());
                        callbackInterface6.userGetFollowError();
                    }
                });
        mQueue.add(jsonArrayRequest);
    }

    public void getAllUsersAndCheckIfFollowed(String ownId)
    {
        String url = "https://polarapp-oamk.herokuapp.com/users/followCheck/" + ownId;
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
                                boolean followed = jsonObject.getBoolean("followed");
                                User user = new User();
                                user.setID(id);
                                user.setEmail(email);
                                user.setIsFollowed(followed);
                                if (followed)
                                {
                                    Log.d("VBVB", "Following: " + user.getEmail());
                                }
                                callbackInterface7.getUserAndCheckIfFollowed(user);
                                i++;
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("VBVB", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VBVB", error.toString());
                    }
                });
        mQueue.add(jsonArrayRequest);
    }

    public void searchUserByEmailAndGetFollowed(String email, String ownId)
    {
        String url = "https://polarapp-oamk.herokuapp.com/users/email/followCheck";
        JSONObject js = new JSONObject();
        try {
            js.put("email", email);
            js.put("myId", ownId);
        }catch (JSONException e) {
            e.printStackTrace();
            callbackInterface8.searchFailed();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, js, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try
                        {
                            User user = new User();
                            user.setIsFollowed(jsonObject.getBoolean("followed"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setID(jsonObject.getString("_id"));
                            callbackInterface8.searchSuccess(user);
                            Log.d("MNMNMN", "User: " + user.getEmail() + " " + user.getIsFollowed());

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("LOL", e.toString());
                            callbackInterface8.searchFailed();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callbackInterface8.searchFailed();
                    }
                });
        mQueue.add(jsonObjectRequest);
        Log.d("LOL", "FINISH");
    }

    public void sendPicture(Bitmap bitmap, String myId)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        Log.d("HDHDHD", "Bitmap encoding completed!");
        Log.d("HDHDHD", encodedImage);


        String url = "https://polarapp-oamk.herokuapp.com/image-upload";
        JSONObject js = new JSONObject();
        try
        {
            js.put("image", encodedImage);
            js.put("myId", myId);
            Log.d("MPMPMP", "1 LOL");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.d("MPMPMP", e.getMessage());
        }
        Log.d("MPMPMP", "1 LOL");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, js, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try
                        {
                            String imageUrl = jsonObject.getString("msg");
                            Log.d("MPMPMP", "RESPONSE");
                            Log.d("MPMPMP", imageUrl);
                            callbackInterface10.imageSetSuccess();
                        }
                        catch (JSONException e)
                        {
                            Log.d("MPMPMP", "RESPONSE ERROR");
                            Log.d("MPMPMP", e.getMessage());
                            callbackInterface10.imageSetError();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError e) {
                        e.printStackTrace();
                    }
                });
        mQueue.add(jsonObjectRequest);
    }


    public void unFollow(String myId, String targetId)
    {
        Log.d("BBBB", "myId: " + myId + " targetId: " + targetId);
        String url = "https://polarapp-oamk.herokuapp.com/follows/myId/"+myId+ "/targetId/" + targetId;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.DELETE,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String msg = response.getString("msg");
                            Log.d("BBBB", "UnFollowed! " + msg);
                            callbackInterface9.userUnfollowed();
                        }
                        catch (Exception e){

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BBBB", error.getMessage());
                callbackInterface9.userUnfollowedError();
            }
        })
        {
            /*@Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-HTTP-Method-Override", "DELETE");
                return headers;
            }*/
        };
        mQueue.add(jsonObjReq);
    }


    //TODO CREATE METHOD AND INTERFACE THAT GETROUTES BY ID
    public void getRoutesByUserId(String id)
    {
        Log.d("JGJGJG", "START");
        String url = "https://polarapp-oamk.herokuapp.com/routes/owner/" + id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        try
                        {
                            Log.d("JGJGJG", "RESPONSE START");
                            int i = 0;
                            while (i < response.length())
                            {
                                JSONObject jsonObject = response.getJSONObject(i);

                                String routeId = jsonObject.getString("_id");
                                String ownerId = jsonObject.getString("owner");
                                String date = jsonObject.getString("date");
                                double distance = jsonObject.getDouble("distance");
                                int time = jsonObject.getInt("time");

                                //TEST
                                //int test = jsonObject.getInt("");

                                Route route = new Route();
                                route.setDate(date);
                                route.setDistance(distance);
                                route.setId(routeId);
                                route.setTime(time);
                                callbackInterface11.getRoute(route);
                                i++;
                            }
                            callbackInterface11.getRoutesReady();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Log.d("JGJGJG", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("JGJGJG", error.toString());
                    }
                });
        mQueue.add(jsonArrayRequest);
    }

    public void deleteUser(String myId)
    {
        Log.d("DQDQDQ", myId);
        String url = "https://polarapp-oamk.herokuapp.com/delete...." + myId;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.DELETE,url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String msg = response.getString("msg");
                            Log.d("BBBB", "UnFollowed! " + msg);
                            callbackInterface9.userUnfollowed();
                        }
                        catch (Exception e){

                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("BBBB", error.getMessage());
                callbackInterface9.userUnfollowedError();
            }
        })
        {
            /*@Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("X-HTTP-Method-Override", "DELETE");
                return headers;
            }*/
        };
        mQueue.add(jsonObjReq);
    }
}


