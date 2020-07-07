package com.example.mediassignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.mediassignment.room.DatabaseClient;
import com.example.mediassignment.room.UserEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<UserEntity> mUserList = new ArrayList<UserEntity>();
    RecyclerView mRecyclerView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.rcy_user);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMax(100);
        progressDialog.setMessage("Its loading....");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(true);
        showData();
    }

    public void callToApi() {
//        VolleyLog.DEBUG = true;

        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        //this token expires!!!please enter the latest valid token from wensite link
        String token = "CZrNzUx6LYWs1K3dJENy-f7GaTkJekL728hB";
        String url = String.format("https://gorest.co.in/public-api/users?access-token=" + token);

        StringRequest req = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //success
                        Log.d("string url response", response);
                        try {
                            JSONObject myResponse = new JSONObject(response);
                            int checkIfTokenValid = myResponse.getJSONObject("_meta").getInt("code");
                            if (checkIfTokenValid == 200) {
                                JSONArray responseArray = myResponse.getJSONArray("result");
                                for (int i = 0; i < responseArray.length(); i++) {
                                    UserEntity user = new UserEntity();
//                                user.setId(responseArray.getJSONObject(i).optInt("id"));
                                    user.setFirstName(responseArray.getJSONObject(i).optString("first_name"));
                                    user.setLastName(responseArray.getJSONObject(i).optString("last_name"));
                                    user.setGender(responseArray.getJSONObject(i).optString("gender"));
                                    mUserList.add(user);
                                    onResponseReceived.responseSuccess(user);
                                }
                                showData();
                                progressDialog.cancel();
                            } else {
                                progressDialog.cancel();
                                Toast.makeText(MainActivity.this, "token expired", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //do add to db
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // handle error response
                        Log.d("error response", error.toString());

                    }
                }
        );

        queue.add(req);
    }

    //show data in recycler view
    private void showData() {
        class showUsers extends AsyncTask<Void, Void, List<UserEntity>> {

            @Override
            protected List<UserEntity> doInBackground(Void... voids) {
                List<UserEntity> userList = DatabaseClient
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .userDao()
                        .getAll();
                return userList;
            }

            @Override
            protected void onPostExecute(List<UserEntity> users) {
                super.onPostExecute(users);
                if (users.size() != 0) {
                    Log.d("users", "!null");
                    progressDialog.cancel();
                    UserAdapter adapter = new UserAdapter(MainActivity.this, users);
                    mRecyclerView.setAdapter(adapter);
                } else {
                    Log.d("users", "null");
                    if (isNetworkAvailable()) {
                        Log.d("network available ", "true");

                        //   Toast.makeText(MainActivity.this,"making api call",Toast.LENGTH_SHORT).show();
                        callToApi();
                    } else {
                        Log.d("network available ", "false");
                        progressDialog.cancel();
                        Toast.makeText(MainActivity.this, "no internet access", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        }

        showUsers su = new showUsers();
        su.execute();
    }


    void insertDataToDb(UserEntity userEntity) {
        class insertDBData extends AsyncTask<Void, Void, Void> {
            Context context;
            UserEntity userEntity;

            insertDBData(Context context, UserEntity userEntity) {
                this.context = context;
                this.userEntity = userEntity;
            }

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                        .userDao()
                        .insert(userEntity);
                return null;
            }


        }

        insertDBData su = new insertDBData(this, userEntity);
        su.execute();
    }

    OnResponseReceived onResponseReceived = new OnResponseReceived() {
        @Override
        public void responseSuccess(UserEntity userEntity) {
            insertDataToDb(userEntity);
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}