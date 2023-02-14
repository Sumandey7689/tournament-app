package com.omsidh.huntsmanwar.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.cardview.widget.CardView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.omsidh.huntsmanwar.activity.FAQActivity;
import com.omsidh.huntsmanwar.activity.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.omsidh.huntsmanwar.BuildConfig;
import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.activity.ContactUsActivity;
import com.omsidh.huntsmanwar.activity.MyProfileActivity;
import com.omsidh.huntsmanwar.activity.MyStatisticsActivity;
import com.omsidh.huntsmanwar.activity.MyWalletActivity;
import com.omsidh.huntsmanwar.activity.NotificationActivity;
import com.omsidh.huntsmanwar.activity.SettingActivity;
import com.omsidh.huntsmanwar.activity.TopPlayersActivity;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;
import com.omsidh.huntsmanwar.session.SessionManager;
import com.omsidh.huntsmanwar.utils.ExtraOperations;
import com.omsidh.huntsmanwar.utils.MySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private View view;
    private boolean isNavigationHide = false;

    private CardView customerSupport;
    private CardView importantUpdates;
    private CardView myProfile;
    private CardView myStatistics;
    private CardView myWallet;
    private CardView topPlayers;
    private CardView settingsCard;

    private LinearLayout amountWonLayout;
    private LinearLayout totalKillsLayout;
    private LinearLayout matchesPlayedLayout;

    private TextView appVersion;
    private TextView myAmountWon;
    private TextView myBalance;
    private TextView myKills;
    private TextView myMatchesNumber;
    private TextView myname;
    private TextView myusername;
    private CircleImageView profileImage;

    private String id;
    private String firstname;
    private String lastname;
    private String name;
    private String email;
    private String mnumber;
    private String username;
    private String password;
    private String profileSt;

    private SessionManager session;
    private String matches_played;
    private String total_amount_won;
    private String total_kills;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_account, container, false);

        initView();
        initListener();
        initSession();

        loadSummery();
        loadProfile();

        try {
            this.appVersion.setText("App Version : v"+BuildConfig.VERSION_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        NestedScrollView nested_content = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        nested_content.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY < oldScrollY) { // up
                    animateNavigation(false);
                }
                if (scrollY > oldScrollY) { // down
                    animateNavigation(true);
                }
            }
        });

        return view;
    }

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * MainActivity.navigation.getHeight()) : 0;
        MainActivity.navigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }


    private void initSession() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        firstname = user.get(SessionManager.KEY_FIRST_NAME);
        lastname = user.get(SessionManager.KEY_LAST_NAME);
        username= user.get(SessionManager.KEY_USERNAME);
        password = user.get(SessionManager.KEY_PASSWORD);
        email = user.get(SessionManager.KEY_EMAIL);
        mnumber = user.get(SessionManager.KEY_MOBILE);

        try {
            if (!firstname.equals("null") && !lastname.equals("null")){
                name = firstname+" "+lastname;
            } else if (!firstname.equals("null")) {
                name = firstname;
            }
            else {
                name = "Guest User";
            }
        }catch (NullPointerException e){
            e.printStackTrace();
            name = "Guest User";
        }
        this.myname.setText(this.name);
        this.myusername.setText(this.username);
    }

    private void initListener() {
        this.matchesPlayedLayout.setOnClickListener(this);
        this.totalKillsLayout.setOnClickListener(this);
        this.amountWonLayout.setOnClickListener(this);
        this.myProfile.setOnClickListener(this);
        this.myWallet.setOnClickListener(this);
        this.myStatistics.setOnClickListener(this);
        this.topPlayers.setOnClickListener(this);
        this.importantUpdates.setOnClickListener(this);
        this.customerSupport.setOnClickListener(this);
        this.settingsCard.setOnClickListener(this);
    }

    private void initView() {
        this.myname = (TextView) view.findViewById(R.id.name);
        this.myusername = (TextView) view.findViewById(R.id.myusername);
        this.myBalance = (TextView) view.findViewById(R.id.myBalance);
        this.myMatchesNumber = (TextView) view.findViewById(R.id.matchesPlayed);
        this.myKills = (TextView) view.findViewById(R.id.myKills);
        this.myAmountWon = (TextView) view.findViewById(R.id.amountWon);
        this.myProfile = (CardView) view.findViewById(R.id.profileCard);
        this.myWallet = (CardView) view.findViewById(R.id.myWalletCard);
        this.myStatistics = (CardView) view.findViewById(R.id.statsCard);
        this.topPlayers = (CardView) view.findViewById(R.id.topPlayersCard);
        this.importantUpdates = (CardView) view.findViewById(R.id.impUpdates);
        this.appVersion = (TextView) view.findViewById(R.id.appVersion);
        this.customerSupport = (CardView) view.findViewById(R.id.customerSupportCard);
        this.settingsCard = (CardView) view.findViewById(R.id.settingsCard);
        this.matchesPlayedLayout = (LinearLayout) view.findViewById(R.id.matchesPlayedLL);
        this.totalKillsLayout = (LinearLayout) view.findViewById(R.id.totalKillsLL);
        this.amountWonLayout = (LinearLayout) view.findViewById(R.id.amountWonLL);
        this.profileImage = (CircleImageView) view.findViewById(R.id.profileImage);
    }

    private void loadSummery() {
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            Uri.Builder builder = Uri.parse(Constant.MY_SUMMARY_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("user_id",id);
            StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject=new JSONObject(response);
                        JSONArray jsonArray=jsonObject.getJSONArray("result");
                        JSONObject jsonObject1=jsonArray.getJSONObject(0);

                        String success = jsonObject1.getString("success");

                        if (success.equals("1")) {
                            matches_played = jsonObject1.getString("maches_played");
                            total_kills = jsonObject1.getString("total_kills");
                            total_amount_won = jsonObject1.getString("amount_won");

                            if (matches_played == null|| matches_played.equals("null") || matches_played.equals("")){
                                myMatchesNumber.setText("0");
                            }
                            else {
                                myMatchesNumber.setText(matches_played);
                            }
                            if (total_kills == null || total_kills.equals("null") || total_kills.equals("")){
                                myKills.setText("0");
                            }
                            else {
                                myKills.setText(total_kills);
                            }
                            if (total_amount_won == null || total_amount_won.equals("null") || total_amount_won.equals("")){
                                myAmountWon.setText("0");
                            }
                            else {
                                myAmountWon.setText(total_amount_won);
                            }
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
            MySingleton.getInstance(getActivity()).addToRequestque(request);
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProfile() {
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
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
                            firstname = jsonObject1.getString("fname");
                            lastname = jsonObject1.getString("lname");
                            profileSt = jsonObject1.getString("user_profile");

                            if (!profileSt.equals("null")){
                                Glide.with(getActivity()).load(Config.FILE_PATH_URL+profileSt)
                                        .apply(new RequestOptions().override(100,100))
                                        .apply(new RequestOptions().placeholder(R.drawable.profile).error(R.drawable.profile))
                                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                                        .apply(RequestOptions.skipMemoryCacheOf(true))
                                        .into(profileImage);
                            }
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
            MySingleton.getInstance(getActivity()).addToRequestque(request);
        } else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id==R.id.profileCard){
            this.startActivity(new Intent(this.getActivity(), MyProfileActivity.class));
        }
        else if (id==R.id.myWalletCard){
            this.startActivity(new Intent(this.getActivity(), MyWalletActivity.class));
        }
        else if (id==R.id.statsCard){
            this.startActivity(new Intent(this.getActivity(), MyStatisticsActivity.class));
        }
        else if (id==R.id.topPlayersCard){
            this.startActivity(new Intent(this.getActivity(), TopPlayersActivity.class));
        }
        else if (id==R.id.faqCard){
            this.startActivity(new Intent(this.getActivity(), FAQActivity.class));
        }
        else if (id==R.id.impUpdates){
            this.startActivity(new Intent(this.getActivity(), NotificationActivity.class));
        }
        else if (id==R.id.customerSupportCard){
            this.startActivity(new Intent(this.getActivity(), ContactUsActivity.class));
        }
        else if (id==R.id.settingsCard){
            this.startActivity(new Intent(this.getActivity(), SettingActivity.class));
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
        initSession();
        loadSummery();
    }


}
