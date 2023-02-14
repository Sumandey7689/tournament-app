package com.omsidh.huntsmanwar.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;
import com.omsidh.huntsmanwar.session.SessionManager;
import com.omsidh.huntsmanwar.utils.ExtraOperations;
import com.omsidh.huntsmanwar.utils.MySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTPpActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private LinearLayout parent_layout;
    private ProgressBar progressBar;

    private String eMail;
    private String firstname;
    private String lastname;
    private String countryCode;
    private String mobileNumber;
    private String password;
    private String promocode;
    private String username;
    private String strDeviceID;
    private String strTag;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ot);
        auth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());

        parent_layout = findViewById(R.id.parent_layout);
        progressBar = findViewById(R.id.progressBar);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            strTag = extras.getString("tag");
            if (strTag.equals("regulerSignUp")) {
                firstname = extras.getString("fname");
                lastname = extras.getString("lname");
                username = extras.getString("uname");
                eMail = extras.getString("email");
                countryCode = extras.getString("ccode");
                mobileNumber = extras.getString("phone");
                password = extras.getString("pass");
                promocode = extras.getString("rcode");
                strDeviceID = extras.getString("device");

                submitData();
            } else if (strTag.equals("forgetPassword")) {
                finish();
            }

        }

    }


    public void submitData() {
        if (new ExtraOperations().haveNetworkConnection(this)) {
            if (promocode.length() >= 2) {
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("fname", firstname);
                builder.appendQueryParameter("lname", lastname);
                builder.appendQueryParameter("username", username);
                builder.appendQueryParameter("password", password);
                builder.appendQueryParameter("email", eMail);
                builder.appendQueryParameter("country_code", countryCode);
                builder.appendQueryParameter("mobile", mobileNumber);
                builder.appendQueryParameter("device_id", strDeviceID);
                builder.appendQueryParameter("referer", promocode);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(),
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                String success = jsonObject1.getString("success");
                                String msg = jsonObject1.getString("msg");

                                if (success.equals("0")) {
                                    onBackPressed();
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(parent_layout, "" + msg, Snackbar.LENGTH_LONG).show();
                                } else if (success.equals("1")) {
                                    onBackPressed();
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(parent_layout, "" + msg, Snackbar.LENGTH_LONG).show();
                                } else if (success.equals("2")) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(OTPpActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                                    //session.createLoginSession(jsonObject1.getString("data"),null,firstname,lastname,username,password,eMail,countryCode,mobileNumber);
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            } else {
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_REGISTER_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("fname", firstname);
                builder.appendQueryParameter("lname", lastname);
                builder.appendQueryParameter("username", username);
                builder.appendQueryParameter("password", password);
                builder.appendQueryParameter("email", eMail);
                builder.appendQueryParameter("country_code", countryCode);
                builder.appendQueryParameter("mobile", mobileNumber);
                builder.appendQueryParameter("device_id", strDeviceID);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(),
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("result");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                String success = jsonObject1.getString("success");
                                String msg = jsonObject1.getString("msg");

                                if (success.equals("0")) {
                                    onBackPressed();
                                    progressBar.setVisibility(View.GONE);
                                    Snackbar.make(parent_layout, "" + msg, Snackbar.LENGTH_LONG).show();
                                } else if (success.equals("1")) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(OTPpActivity.this, "" + msg, Toast.LENGTH_SHORT).show();
                                    //session.createLoginSession(jsonObject1.getString("data"),null,firstname,lastname,username,password,eMail,countryCode,mobileNumber);
                                    Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressBar.setVisibility(View.GONE);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
//                        parameters.put("fname", firstname);
//                        parameters.put("lname", lastname);
//                        parameters.put("username", uname);
//                        parameters.put("password", md5pass);
//                        parameters.put("email", eMail);
//                        parameters.put("mobile", mobileNumber);
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            }
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


}
