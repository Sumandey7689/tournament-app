package com.omsidh.huntsmanwar.activity;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.cardview.widget.CardView;
import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;
import com.omsidh.huntsmanwar.session.SessionManager;
import com.omsidh.huntsmanwar.utils.ExtraOperations;
import com.omsidh.huntsmanwar.utils.MySingleton;

public class SignInActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "PhoneAuth";
    private SessionManager session;

    private View bg;
    private TextView txt_show, txt_fgt_pass, txt_email_login, txt_mobile_login, txt_signup, img_remove_coupon_social, promo_applied_text_social;
    private LinearLayout lyt_email_signin, bottom_sheet_number, lyt_mobile_login;
    private CountryCodePicker ccp;
    private ImageView img_check_social, img_close;
    private CardView cv_fb, cv_google, card_submit_number, cv_mobile, cv_email, cv_pass, cv_apply_coupon_social;
    private EditText et_coupon_social, et_phnone_botom, et_email, et_phn, et_pwd;
    private ScrollView parent_lyt;
    private RelativeLayout lyt_sign_in, lyt_support;
    private ProgressBar progressBar;

    private String id;
    private String balance;
    private String dateOfbirth;
    private String email;
    private String firstname;
    private String fullname;
    private String username;
    private String gender;
    private String lastname;
    private String message;
    private String mobilenumber;
    private String password;
    private TextInputEditText cCode;
    private String countryCode;
    private String strCodePhone;
    private String uname;
    private String strDeviceID;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth auth;


    private SignInButton btnSignInGoogle;
    private GoogleApiClient googleApiClient;

    private static final int REQ_CODE =9001;
    private static final String TAGS = "SignInActivity";

    private String strName;
    private String strReferralCode, profile_pic, user_id, google_id, google_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        auth = FirebaseAuth.getInstance();
        session = new SessionManager(getApplicationContext());
        strDeviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        txt_show = (TextView) findViewById(R.id.txt_show);
        lyt_email_signin = (LinearLayout) findViewById(R.id.lyt_email_signin);
        txt_fgt_pass = (TextView) findViewById(R.id.txt_fgt_pass);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);
        img_check_social = (ImageView) findViewById(R.id.img_check_social);
        et_coupon_social = (EditText) findViewById(R.id.et_coupon_social);
        bg = findViewById(R.id.bg);
        cv_google = (CardView) findViewById(R.id.cv_google);
        card_submit_number = (CardView) findViewById(R.id.card_submit_number);
        et_phnone_botom = (EditText) findViewById(R.id.et_phnone_botom);
        cv_mobile = (CardView) findViewById(R.id.cv_mobile);
        cv_email = (CardView) findViewById(R.id.cv_email);
        cv_pass = (CardView) findViewById(R.id.cv_pass);
        img_close = (ImageView) findViewById(R.id.img_close);
        bottom_sheet_number = (LinearLayout) findViewById(R.id.bottom_sheet_number);
        lyt_mobile_login = (LinearLayout) findViewById(R.id.lyt_mobile_login);
        et_email = (EditText) findViewById(R.id.et_email);
        et_phn = (EditText) findViewById(R.id.et_phn);
        txt_email_login = (TextView) findViewById(R.id.txt_email_login);
        txt_mobile_login = (TextView) findViewById(R.id.txt_mobile_login);
        lyt_sign_in = (RelativeLayout) findViewById(R.id.lyt_sign_in);
        txt_signup = (TextView) findViewById(R.id.txt_signup);
        parent_lyt = (ScrollView) findViewById(R.id.parent_lyt);
        cv_apply_coupon_social = (CardView) findViewById(R.id.cv_apply_coupon_social);
        img_remove_coupon_social = (TextView) findViewById(R.id.img_remove_coupon_social);
        promo_applied_text_social = (TextView) findViewById(R.id.promo_applied_text_social);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btnSignInGoogle = (SignInButton) findViewById(R.id.btnSignInGoogle);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        lyt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regulerLogin();
            }
        });

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        txt_fgt_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        txt_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_pwd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    txt_show.setText("Show");
                    et_pwd.setInputType( InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }else {
                    txt_show.setText("Hide");
                    et_pwd.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                }
                et_pwd.setSelection(et_pwd.getText().length());
            }
        });




        cv_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
                    try {
                        if (googleApiClient != null && googleApiClient.isConnected()) {
                            googleApiClient.clearDefaultAccountAndReconnect().setResultCallback(new ResultCallback<Status>() {

                                @Override
                                public void onResult(Status status) {
                                    googleApiClient.disconnect();
                                    signIn();
                                }
                            });
                        }
                        else {
                            signIn();
                        }
                    } catch (Exception e) {
                        Log.d("DISCONNECT ERROR", e.toString());
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please check your connection", Toast.LENGTH_LONG).show();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this /* FragmentActivity */,0, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public boolean validateusername() {
        if (!this.uname.isEmpty()) {
            return true;
        } else {
            Snackbar.make(parent_lyt,"Please enter valid email id or mobile number.",Snackbar.LENGTH_LONG).show();
            return false;
        }
    }

    public boolean validatepassword() {
        if (!this.password.isEmpty()) {
            return true;
        }
        Snackbar.make(parent_lyt,"Please enter valid Password.",Snackbar.LENGTH_LONG).show();
        return false;
    }

    public void regulerLogin() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        this.uname = this.et_email.getText().toString().trim();
        this.password = this.et_pwd.getText().toString().trim();

        if (validateusername() && validatepassword()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_LOGIN_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("username", uname);
                builder.appendQueryParameter("password", password);
                builder.appendQueryParameter("device_id",strDeviceID);
                StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");

                            if (success.equals("0")) {
                                progressBar.setVisibility(View.GONE);
                                String msg = jsonObject1.getString("msg");
                                Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else if (success.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignInActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();

                                id = jsonObject1.getString("id");
                                profile_pic = jsonObject1.getString("user_profile");
                                firstname = jsonObject1.getString("fname");
                                lastname = jsonObject1.getString("lname");
                                username = jsonObject1.getString("username");
                                email = jsonObject1.getString("email");
                                countryCode = jsonObject1.getString("country_code");
                                mobilenumber = jsonObject1.getString("mobile");
                                session.createLoginSession(id,profile_pic,firstname,lastname,username,password,email,countryCode,mobilenumber);

                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
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
                request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            } else {
                Snackbar.make(parent_lyt,"No internet connection",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void socialLogin(final String uname, final String password) {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }

        if (!uname.isEmpty()) {
            if (new ExtraOperations().haveNetworkConnection(this)) {
                progressBar.setVisibility(View.VISIBLE);
                Uri.Builder builder = Uri.parse(Constant.USER_LOGIN_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("username", uname);
                builder.appendQueryParameter("device_id",strDeviceID);
                builder.appendQueryParameter("social", "true");
                StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray=jsonObject.getJSONArray("result");
                            JSONObject jsonObject1=jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");

                            if (success.equals("0")) {
                                progressBar.setVisibility(View.GONE);
                                String msg = jsonObject1.getString("msg");
                                Snackbar.make(parent_lyt,""+msg,Snackbar.LENGTH_LONG).show();
                            }
                            else if (success.equals("1")) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(SignInActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();

                                id = jsonObject1.getString("id");
                                profile_pic = jsonObject1.getString("user_profile");
                                firstname = jsonObject1.getString("fname");
                                lastname = jsonObject1.getString("lname");
                                username = jsonObject1.getString("username");
                                email = jsonObject1.getString("email");
                                countryCode = jsonObject1.getString("country_code");
                                mobilenumber = jsonObject1.getString("mobile");
                                session.createLoginSession(id,profile_pic,firstname,lastname,username,password,email,countryCode,mobilenumber);

                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
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
                request.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(getApplicationContext()).addToRequestque(request);
            } else {
                Snackbar.make(parent_lyt,"No internet connection",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void setProfileToView( String strName, String eMail) {
        try{
            String[] resArray1 = eMail.split("@");
            username = resArray1[0];
        }catch (ArrayIndexOutOfBoundsException | NullPointerException e){
            e.printStackTrace();
        }

        try{
            String[] resArray2 = strName.split(" ");
            firstname = resArray2[0];
            lastname = resArray2[1];
        }catch (ArrayIndexOutOfBoundsException | NullPointerException e){
            e.printStackTrace();
        }
    }

    public void disconnectFromGoogle() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    //hideProgressDialog();
                    handleResult(googleSignInResult);
                }
            });
        }
    }
    public void handleResult(GoogleSignInResult result){
        try {
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();

                assert account != null;
                google_id = account.getId();
                google_username = account.getEmail();
                strName = account.getDisplayName();
                email = account.getEmail();

                try {
                    if (email != null){
                        String[] resArray1 = email.split("@");
                        uname = resArray1[0];
                    } else {
                        uname = google_id;
                    }
                } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
                    e.printStackTrace();
                    uname = google_id;
                }

                updateUI(true,uname,google_id);
            }
            else {
                updateUI(false, uname, google_id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);
    }

    public void signOut(){
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUI(false, uname, google_id);
            }
        });
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false, uname, google_id);
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    public void updateUI(boolean isLogin, String uname, String google_id){
        if (isLogin){
            socialLogin(uname,google_id);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(this);
        googleApiClient.disconnect();
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {

        //Google login
        if (requestCode == REQ_CODE){
            GoogleSignInResult result  = Auth.GoogleSignInApi.getSignInResultFromIntent(intent);
            handleResult(result);
        }

        super.onActivityResult(requestCode, responseCode, intent);
    }

}
