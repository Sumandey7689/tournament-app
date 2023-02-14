package com.omsidh.huntsmanwar.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.adapter.ResultAdapter;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;
import com.omsidh.huntsmanwar.model.ResultPojo;
import com.omsidh.huntsmanwar.session.SessionManager;
import com.omsidh.huntsmanwar.utils.ExtraOperations;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultFragment extends androidx.fragment.app.Fragment {

    private View view;
    private boolean isLoaded =false,isVisibleToUser;

    ShimmerFrameLayout mShimmerViewContainer;
    LinearLayout noResults;
    
    String id;
    String username;
    String token;

    private Bundle bundle;
    private String strId,strTitle;

    private RecyclerView recyclerView;
    private LinearLayout upcomingLinearLayout;
    private RecyclerView.Adapter adapter;

    private ArrayList<ResultPojo> resultPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;
    
    private SessionManager session;
    private NestedScrollView nestedScrollView;

    public ResultFragment() {
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
        view = inflater.inflate(R.layout.fragment_result, container, false);

        initView();
        initSession();
        loadBundle();

        resultPojoList = new ArrayList<>();

        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setNestedScrollingEnabled(false);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser=isVisibleToUser;
        if(isVisibleToUser && isAdded() ){
            if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                loadMatch();
            }else {
                Toast.makeText(getActivity(), "No internet connection", Toast.LENGTH_SHORT).show();
            }
            isLoaded =true;
        }
    }

    private void initView() {
        this.mShimmerViewContainer = (ShimmerFrameLayout) view.findViewById(R.id.shimmer_view_container);
        this.recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        this.upcomingLinearLayout = (LinearLayout) view.findViewById(R.id.upcomingLL);
        this.noResults = (LinearLayout) view.findViewById(R.id.noResultsLL);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
    }

    private void initSession() {
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        id = user.get(SessionManager.KEY_ID);
        username= user.get(SessionManager.KEY_USERNAME);
        token = user.get(SessionManager.KEY_PASSWORD);
    }

    private void loadMatch() {
        recyclerView.setVisibility(View.GONE);
        noResults.setVisibility(View.GONE);
        mShimmerViewContainer.setVisibility(View.VISIBLE);
        mShimmerViewContainer.startShimmer();

        Uri.Builder builder = Uri.parse(Constant.RESULT_MATCH_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        builder.appendQueryParameter("user_id",id);
        builder.appendQueryParameter("game_id",strId);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        noResults.setVisibility(View.VISIBLE);
                    }
                });
        requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_MATCH(JSONArray response) {
        resultPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            ResultPojo resultPojo = new ResultPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                resultPojo.setId(json.getString("id"));
                resultPojo.setTitle(json.getString("title"));
                resultPojo.setTime(json.getString("time"));
                resultPojo.setPrize_pool(json.getInt("prize_pool"));
                //resultPojo.setImage(json.getString("image"));
                resultPojo.setPer_kill(json.getInt("per_kill"));
                resultPojo.setEntry_fee(json.getInt("entry_fee"));
                resultPojo.setEntry_type(json.getString("entry_type"));
                resultPojo.setVersion(json.getString("version"));
                resultPojo.setMap(json.getString("map"));
                resultPojo.setIs_private(json.getString("is_private"));
                resultPojo.setMatch_type(json.getString("match_type"));
                resultPojo.setSponsored_by(json.getString("sponsored_by"));
                resultPojo.setSpectate_url(json.getString("spectate_url"));
                resultPojo.setMatch_notes(json.getString("match_notes"));
                resultPojo.setMatch_desc(json.getString("match_desc"));
                resultPojo.setMatch_status(json.getString("match_status"));
                resultPojo.setTotal_joined(json.getInt("total_joined"));
                resultPojo.setJoined_status(json.getString("joined_status"));
                resultPojo.setPlatform(json.getString("platform"));
                resultPojo.setPool_type(json.getString("pool_type"));
                resultPojo.setAdmin_share(json.getInt("admin_share"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resultPojoList.add(resultPojo);
        }
        if (!resultPojoList.isEmpty()){
            adapter = new ResultAdapter(resultPojoList,getActivity());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            noResults.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

        }
        else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }

    private void loadBundle() {
        bundle = new Bundle();
        bundle = this.getArguments();
        if (bundle != null){
            strId = bundle.getString("ID_KEY");
            strTitle = bundle.getString("TITLE_KEY");
        }
    }

}
