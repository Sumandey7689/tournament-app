package com.omsidh.huntsmanwar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.adapter.NotificationAdapter;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;
import com.omsidh.huntsmanwar.model.NotificationPojo;
import com.omsidh.huntsmanwar.utils.ExtraOperations;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<NotificationPojo> notificationPojoList;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        notificationPojoList = new ArrayList<>();

        initToolbar();

        this.recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadNotification();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle((CharSequence) "Important Updates");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadNotification() {
        Uri.Builder builder = Uri.parse(Constant.NOTIFICATION_URL).buildUpon();
        builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
        jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //progressBar.setVisibility(View.GONE);
                        JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        jsonArrayRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(jsonArrayRequest);
    }

    private void JSON_PARSE_DATA_AFTER_WEBCALL_TOP_PLAYERS(JSONArray response) {
        notificationPojoList.clear();
        for(int i = 0; i<response.length(); i++) {
            NotificationPojo notificationPojo = new NotificationPojo();
            JSONObject json = null;
            try {
                json = response.getJSONObject(i);
                notificationPojo.setId(json.getString("id"));
                notificationPojo.setTitle(json.getString("title"));
                notificationPojo.setMessage(json.getString("message"));
                notificationPojo.setImage(json.getString("image"));
                notificationPojo.setUrl(json.getString("url"));
                notificationPojo.setCreated(json.getString("created"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            notificationPojoList.add(notificationPojo);
        }
        if (!notificationPojoList.isEmpty()){
            adapter = new NotificationAdapter(notificationPojoList,getApplicationContext());
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
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
        if (new ExtraOperations().haveNetworkConnection(getApplicationContext())) {
            loadNotification();
        }else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }
}
