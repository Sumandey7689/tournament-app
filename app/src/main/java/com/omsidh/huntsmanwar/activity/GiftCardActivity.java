package com.omsidh.huntsmanwar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class GiftCardActivity extends AppCompatActivity {

    private Button submitBt;
    private EditText transactionEt;
    private TextView accountNameTv,accountIdTv,remarkTv;

    private String id,name,firstname,lastname,email,mnumber,username,token, is_active;
    private int tot_coins, won_coins, bonus_coins;
    private String transactionSt, amountSt, noteSt;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_card);
        initToolbar();
        initView();
        initSession();
        loadProfile();

        submitBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBt.setEnabled(false);
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                transactionSt = transactionEt.getText().toString().trim();

                if (is_active.equals("0")) {
                    submitBt.setEnabled(true);
                    remarkTv.setVisibility(View.VISIBLE);
                    remarkTv.setText("You cant't add money. Your account is blocked.");
                    remarkTv.setTextColor(Color.parseColor("#ff0000"));
                }
                else if (transactionSt.isEmpty()) {
                    submitBt.setEnabled(true);
                    remarkTv.setVisibility(View.VISIBLE);
                    remarkTv.setText("Enter valid gift card voucher number.");
                    remarkTv.setTextColor(Color.parseColor("#ff0000"));
                } else if (transactionSt.length() == 10) {
                    remarkTv.setVisibility(View.GONE);
                    if (!new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                        submitBt.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    } else {
                        submitBt.setEnabled(false);
                        addMoney();
                    }
                }
                else {
                    submitBt.setEnabled(true);
                    remarkTv.setVisibility(View.VISIBLE);
                    remarkTv.setText("Gift card voucher number must be 10 digit.");
                    remarkTv.setTextColor(Color.parseColor("#ff0000"));
                }
            }
        });
    }

    private void loadProfile() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.GET_PROFILE_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("id", id);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            tot_coins = jsonObject1.getInt("cur_balance");
                            won_coins = jsonObject1.getInt("won_balance");
                            bonus_coins = jsonObject1.getInt("bonus_balance");
                            is_active = jsonObject1.getString("status");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
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
            request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSession() {
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);

        name = firstname+" "+lastname;
    }

    private void initView() {
        transactionEt = (EditText) findViewById(R.id.transactionEt);

        accountNameTv = (TextView) findViewById(R.id.accountNameTv);
        accountIdTv = (TextView) findViewById(R.id.accountIdTv);
        remarkTv = (TextView) findViewById(R.id.remarkTv);

        submitBt = (Button) findViewById(R.id.submitBt);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "Add PlayCoins");
        //toolbar.setTitleTextAppearance(this, R.style.FontLato);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    private void addMoney() {
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            Uri.Builder builder = Uri.parse(Constant.VERIFY_CARD_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("user_id",id);
            builder.appendQueryParameter("voucher_code", transactionSt);
            StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");
                        String msg = jsonObject1.getString("msg");

                        if (success.equals("0")) {
                            submitBt.setEnabled(true);
                            Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                        }
                        else  if (success.equals("1")) {
                            submitBt.setEnabled(true);
                            Toast.makeText(getApplicationContext(),msg+"", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(GiftCardActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    submitBt.setEnabled(true);
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
            request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            request.setShouldCache(false);
            MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

}

