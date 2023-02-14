package com.omsidh.huntsmanwar.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.omsidh.huntsmanwar.activity.NotificationActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.activity.MainActivity;
import com.omsidh.huntsmanwar.adapter.GameAdapter;
import com.omsidh.huntsmanwar.adapter.Notice2Adapter;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;
import com.omsidh.huntsmanwar.model.GamePojo;
import com.omsidh.huntsmanwar.model.Notice2Class;
import com.omsidh.huntsmanwar.utils.ExtraOperations;
import com.omsidh.huntsmanwar.utils.MySingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class GameFragment extends Fragment {

    private View view;
    private boolean isNavigationHide = false;
    private ViewPager  notice2Pager;
    private List<Notice2Class> notice2ImageList;
    public SharedPreferences preferences;
    public String prefName = "Sky_Winner";
    public int count = 0;

    private RecyclerView recyclerView;
    private LinearLayout gameLinearLayout;
    private RecyclerView.Adapter gameAdapter;

    private ArrayList<GamePojo> gamePojoList;
    private RequestQueue gameRequestQueue;
    private JsonArrayRequest gameJsonArrayRequest ;

    private ShimmerFrameLayout shimmer_view_container;
    private NestedScrollView nestedScrollView;
    private TextView announceText;
    private LinearLayout noMatchesLL;
    private LinearLayout upcomingLL;
    private CardView notificationCard;

    private Timer timer;
    private int page = 0;

    public GameFragment() {
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
        view = inflater.inflate(R.layout.fragment_game, container, false);

        initView();

        timer = new Timer();
        gamePojoList = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            loadAnnouncements();
            loadGames();
            loadSlider();
        }else {
            Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

        NestedScrollView nested_content = view.findViewById(R.id.nestedScrollView);
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

        notificationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCounter();

                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void loadSlider() {

//        String slider_url = sessionManager.getUserDetails().get(SessionManager.BASE_URL) + "notice2.php";
        String slider_url = "https://ecoshirt.xyz/huntsmanwar/admin/slider.php";

        StringRequest apiRequest = new StringRequest(Request.Method.POST,
                slider_url,

                ServerResponse -> {


                    try {
                        JSONArray jsonArray = new JSONArray(ServerResponse);

                        notice2ImageList = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            notice2ImageList.add(new Notice2Class(jsonArray.getJSONObject(i).getString("image"), jsonArray.getJSONObject(i).getString("url")));

                        }

                        notice2Pager.setAdapter(new Notice2Adapter(getActivity(), notice2ImageList));

                        Timer timer = new Timer();
                        timer.scheduleAtFixedRate(new Notice2Timer(), 4444, 6666);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                },
                volleyError -> {
                });

        RequestQueue requestQueue2 = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
        requestQueue2.add(apiRequest);


    }

    private class Notice2Timer extends TimerTask {
        @Override

        public void run() {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {

                if (notice2Pager.getCurrentItem() < notice2ImageList.size() - 1) {

                    notice2Pager.setCurrentItem(notice2Pager.getCurrentItem() + 1);

                } else {

                    notice2Pager.setCurrentItem(0);

                }

            });

        }

    }

    private void animateNavigation(final boolean hide) {
        if (isNavigationHide && hide || !isNavigationHide && !hide) return;
        isNavigationHide = hide;
        int moveY = hide ? (2 * MainActivity.navigation.getHeight()) : 0;
        MainActivity.navigation.animate().translationY(moveY).setStartDelay(100).setDuration(300).start();
    }

    private void loadAnnouncements() {
        Uri.Builder builder = Uri.parse(Constant.ANNOUNCEMENT_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, builder.toString(),null,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try
                {
                    final JSONArray jsonArray = response.getJSONArray("Result");
                    announceText.setText(jsonArray.getJSONObject(0).getString("title"));
                    if (jsonArray.length() == 1){
                        String announcement = jsonArray.getJSONObject(0).getString("title");
                        announceText.setText(announcement);
                    }
                    else if (jsonArray.length() == 2) {
                        try {
                            timer.scheduleAtFixedRate( new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page){
                                        page = 0;
                                    }
                                    else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }
                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000 );
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                    else if (jsonArray.length() == 3) {
                        try {
                            timer.scheduleAtFixedRate( new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page){
                                        page = 0;
                                    }
                                    else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 2) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(2).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }

                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000 );
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                    else if (jsonArray.length() == 4) {
                        try {
                            timer.scheduleAtFixedRate( new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page){
                                        page = 0;
                                    }
                                    else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 2) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(2).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 3) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(3).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }
                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000 );
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                    else if (jsonArray.length() == 5) {
                        try {
                            timer.scheduleAtFixedRate( new TimerTask() {
                                @Override
                                public void run() {
                                    if (jsonArray.length() == page){
                                        page = 0;
                                    }
                                    else {
                                        page++;
                                    }
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() //run on ui thread
                                        {
                                            public void run() {
                                                if (page == 0) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(0).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 1) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(1).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 2) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(2).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 3) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(3).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                } else if (page == 4) {
                                                    String announcement = null;
                                                    try {
                                                        announcement = jsonArray.getJSONObject(4).getString("title");
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                    announceText.setText(announcement);
                                                }
                                            }
                                        });
                                    }
                                }
                            }, 2000, 5000 );
                        }catch (IllegalStateException e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        notificationCard.setVisibility(View.GONE);
                    }
                }
             catch (JSONException e) {
                 notificationCard.setVisibility(View.GONE);
            }
        }
    }, new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (error.getMessage() != null) {
                notificationCard.setVisibility(View.GONE);
            }
        }
    });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonObjectRequest.setShouldCache(false);
        MySingleton.getInstance(getActivity()).addToRequestque(jsonObjectRequest);
    }

    private void initView() {
        this.shimmer_view_container = view.findViewById(R.id.shimmer_view_container);
        this.nestedScrollView = view.findViewById(R.id.nestedScroll);
        this.announceText = view.findViewById(R.id.announceText);
        this.noMatchesLL = view.findViewById(R.id.noMatchesLL);
        this.upcomingLL = view.findViewById(R.id.upcomingLL);
        this.recyclerView = view.findViewById(R.id.recyclerView);
        this.notificationCard = view.findViewById(R.id.notificationCard);
        this.notice2Pager = view.findViewById(R.id.viewPagerForSecondNotice);
    }



    private void loadGames() {
        recyclerView.setVisibility(View.GONE);
        noMatchesLL.setVisibility(View.GONE);
        shimmer_view_container.setVisibility(View.VISIBLE);
        shimmer_view_container.startShimmer();
        Uri.Builder builder = Uri.parse(Constant.LIST_GAME_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        gameJsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        shimmer_view_container.stopShimmer();
                        shimmer_view_container.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        shimmer_view_container.stopShimmer();
                        shimmer_view_container.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noMatchesLL.setVisibility(View.VISIBLE);
                    }
                });
        gameRequestQueue = Volley.newRequestQueue(getActivity());
        gameJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        gameJsonArrayRequest.setShouldCache(false);
        gameRequestQueue.getCache().clear();
        gameRequestQueue.add(gameJsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        gamePojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            GamePojo gamePojo = new GamePojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                gamePojo.setId(json.getString("id"));
                gamePojo.setTitle(json.getString("title"));
                gamePojo.setBanner(json.getString("banner"));
                gamePojo.setUrl(json.getString("url"));
                gamePojo.setType(json.getString("type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            gamePojoList.add(gamePojo);
        }
        if (!gamePojoList.isEmpty()){
            gameAdapter = new GameAdapter(gamePojoList,getActivity());
            gameAdapter.notifyDataSetChanged();
            recyclerView.setAdapter(gameAdapter);
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
            noMatchesLL.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        else {
            shimmer_view_container.stopShimmer();
            shimmer_view_container.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noMatchesLL.setVisibility(View.VISIBLE);
        }
    }


    public void saveCounter() {
        count = 0;

        preferences = getActivity().getSharedPreferences(prefName, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("counter", count);
        editor.apply();
    }


    public void onResume() {
        super.onResume();
        if (new ExtraOperations().haveNetworkConnection(getActivity())) {
            shimmer_view_container.startShimmer();
        }
    }

    public void onPause() {
        shimmer_view_container.stopShimmer();
        super.onPause();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (timer != null) {
                timer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
