package info.abhinavkeshri.mychatbot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText mFirstNameET, mLastNameET, mEmailET, mStreetET, mCityET, mPasswordET, mConfPasswordET, mConfCodeET;
    Spinner mCountrySpinner;
    TextView mErrorTV;
    ProgressBar mProgressBar;
    Button mSignUpBT, mConfirmationCodeBT;
    String DisplayError;
    HashMap<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mFirstNameET = findViewById(R.id.firstNameInput);
        mLastNameET = findViewById(R.id.lastNameInput);
        mEmailET = findViewById(R.id.emailInputSignUP);
        mStreetET = findViewById(R.id.streetInput);
        mCityET = findViewById(R.id.cityInput);
        mPasswordET = findViewById(R.id.passwordInput);
        mConfPasswordET = findViewById(R.id.confPassInput);
        mConfCodeET = findViewById(R.id.emailConfirmCode);


        mErrorTV = findViewById(R.id.errorMessage);

        mProgressBar = findViewById(R.id.progressBar);

        mSignUpBT = findViewById(R.id.signupBtn);
        mConfirmationCodeBT = findViewById(R.id.sendConfirmationCode);

        mCountrySpinner = findViewById(R.id.countryInput);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.countries, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountrySpinner.setAdapter(adapter);
        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String text = parent.getItemAtPosition(position).toString();
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });

        mSignUpBT.setOnClickListener(this);
        mConfirmationCodeBT.setOnClickListener(this);

    }

    private void makeEmailConfCodeRequest() {
        if (mPasswordET.getText().toString().trim().equals(mConfPasswordET.getText().toString().trim()) && mPasswordET.getText().toString().trim().length() >= 8) {
            mProgressBar.setVisibility(View.VISIBLE);
            String FN = mFirstNameET.getText().toString().trim();
            String LN = mLastNameET.getText().toString().trim();
            String emailStr = mEmailET.getText().toString().trim();
            String streetStr = mStreetET.getText().toString().trim();
            String cityStr = mCityET.getText().toString().trim();
            String countryStr = mCountrySpinner.getSelectedItem().toString().trim();
            String passStr = mPasswordET.getText().toString().trim();

            if (FN.equals("") || LN.equals("") || emailStr.equals("") || streetStr.equals("") || cityStr.equals("")) {
                DisplayError = "Form Not Filled Properly";
                mErrorTV.setText(DisplayError);
                mErrorTV.setVisibility(TextView.VISIBLE);
                return;
            }
            params = new HashMap<>();
            params.put("email", emailStr);
            params.put("password", passStr);
            params.put("first_name", FN);
            params.put("last_name", LN);
            params.put("city", cityStr);
            params.put("street", streetStr);
            params.put("country", countryStr);
            Toast.makeText(this, params.toString(), Toast.LENGTH_LONG).show();
            String url = "http://13.235.71.220:3001/signup";
            mConfCodeET.setVisibility(View.VISIBLE);
            VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                    String resultResponse = new String(response.data);
                    try {
                        JSONObject result = new JSONObject(resultResponse);
                        String message = result.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("_id", Integer.parseInt(message));
                        editor.commit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mProgressBar.setVisibility(View.GONE);
                    mConfCodeET.setVisibility(View.VISIBLE);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    String errorMessage = "Server is not Running";
                    //Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect to Server";
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                            DisplayError = errorMessage;
                        }
                    } else {
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
                                errorMessage = message + " Please login again";
                            } else if (networkResponse.statusCode == 400) {
                                errorMessage = message + " Check your inputs";
                            } else if (networkResponse.statusCode == 500) {
                                errorMessage = message + " Something is getting wrong";
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
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }
            };
            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(9000, 5, 2));
            MySingletonJSONRequestQueue.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
        } else {
            DisplayError = "Either password is too short or Passwords did not match.";
            mErrorTV.setVisibility(TextView.VISIBLE);
            mErrorTV.setText(DisplayError);
        }
    }
    void makeSignupRequest(String Code){
        mProgressBar.setVisibility(View.VISIBLE);
        params = new HashMap<>();
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        int _id = sp.getInt("_id", -1);
        if(_id== -1){ return;}
        params.put("_id", Integer.valueOf(_id).toString() );
        params.put("code", Code);
        String url = "http://13.235.71.220:3001/signup/verify";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try{
                    JSONObject result = new JSONObject(resultResponse);
                    String message = result.getString("message");
                    Toast.makeText(getApplicationContext(), "Varified your Emai successfully", Toast.LENGTH_LONG).show();
                }catch (JSONException e){
                    e.printStackTrace();
                }
                mProgressBar.setVisibility(View.GONE);
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessageString = "Server is not Running";
                //Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                if(networkResponse == null){
                    if(error.getClass().equals(TimeoutError.class)){
                        errorMessageString = "Request timeout";
                        Toast.makeText(getApplicationContext(), errorMessageString, Toast.LENGTH_LONG).show();
                    }else if(error.getClass().equals(NoConnectionError.class)){
                        errorMessageString = "Failed to connect to Server";
                        Toast.makeText(getApplicationContext(), errorMessageString, Toast.LENGTH_LONG).show();
                        DisplayError = errorMessageString;
                        mErrorTV.setText(DisplayError);
                        mErrorTV.setVisibility(View.VISIBLE);

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
                            errorMessageString = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessageString = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessageString = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessageString = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(getApplicationContext(), errorMessageString, Toast.LENGTH_SHORT).show();
                Log.i("Error", errorMessageString);
                error.printStackTrace();
                mErrorTV.setText(errorMessageString);
                mErrorTV.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                //Map<String, String> params = new HashMap<>();
                //params.put("api_token", "gh659gjhvdyudo973823tt9gvjf7i6ric75r76");
                //params.put("email", email.getText().toString());
                //params.put("location", mLocationInput.getText().toString());
                //params.put("about", mAvatarInput.getText().toString());
                //params.put("fileName", "abc.png" );
                return params;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(9000, 5, 2));
        MySingletonJSONRequestQueue.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signupBtn:
                String code = mConfCodeET.getText().toString();
                mSignUpBT.setEnabled(false);
                if(code == "" || code.length()< 6){
                    Toast.makeText(this,"Please enter Confirmation code ", Toast.LENGTH_LONG).show();
                    break;
                }
                makeSignupRequest(code);
                mSignUpBT.setEnabled(true);
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                break;
            case R.id.sendConfirmationCode:
                mConfCodeET.setVisibility(EditText.VISIBLE);
                makeEmailConfCodeRequest();
                break;
        }

    }
}
