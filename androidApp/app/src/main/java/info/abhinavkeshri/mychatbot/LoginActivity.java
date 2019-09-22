package info.abhinavkeshri.mychatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.abhinavkeshri.mychatbot.Networking.MySingletonJSONRequestQueue;
import info.abhinavkeshri.mychatbot.Networking.VolleyMultipartRequest;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    EditText mEmailET , mPasswordET;
    Button mLoginBT;
    TextView mNoAccountTV;
    Map<String, String> params;
    ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmailET = findViewById(R.id.emailInputLogin);
        mPasswordET = findViewById(R.id.passInputLogin);
        mLoginBT = findViewById(R.id.loginBtn);
        mNoAccountTV = findViewById(R.id.noAccountText);
        mProgressBar = findViewById(R.id.progressBarLogin);
        mLoginBT.setOnClickListener(this);
        mNoAccountTV.setOnClickListener(this);

    }
    private void makeLoginRequest(){
        mProgressBar.setVisibility(View.VISIBLE);

        String email = mEmailET.getText().toString();
        String password = mPasswordET.getText().toString();
        params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        String url = "http://13.235.71.220:3001/signup/verifyUser";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try{
                    JSONObject result = new JSONObject(resultResponse);
                    String message = result.getString("message");

                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor  editor = sp.edit();
                    editor.putString("token", result.getString("token"));
                    editor.putString("email", result.getString("email"));
                    editor.putString("username", result.getString("username"));
                    editor.putInt("_id", 1);
                    Toast.makeText(getApplicationContext(), result.getString("username"), Toast.LENGTH_LONG).show();
                    editor.commit();
                    finish();
                }catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "an error occured while putting in sharedpref", Toast.LENGTH_LONG).show();
                }
                mProgressBar.setVisibility(View.GONE);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Server is not Running";
                if(networkResponse == null){
                    if(error.getClass().equals(TimeoutError.class)){
                        errorMessage = "Request timeout";
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }else if(error.getClass().equals(NoConnectionError.class)){
                        errorMessage = "Failed to connect to Server";
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    }
                }else{
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        //String status = response.getString("status");
                        String message = response.getString("message");
                        //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        //Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                Log.i("Error", errorMessage);
                error.printStackTrace();
                mProgressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(9000, 5, 2));
        MySingletonJSONRequestQueue.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);

    }
    private void startSignupActivity(){
        Intent i = new Intent(this,SignUpActivity.class );
        startActivity(i);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginBtn:
                mLoginBT.setEnabled(false);
                makeLoginRequest();
                mLoginBT.setEnabled(true);
                break;
            case R.id.noAccountText:
                startSignupActivity();
                break;
        }
    }
}
