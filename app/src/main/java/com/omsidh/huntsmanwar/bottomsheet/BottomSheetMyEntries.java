package com.omsidh.huntsmanwar.bottomsheet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.omsidh.huntsmanwar.activity.MainActivity;
import com.omsidh.huntsmanwar.utils.ExtraOperations;
import com.omsidh.huntsmanwar.utils.MySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;
import com.omsidh.huntsmanwar.model.MyEntriesPojo;
import com.google.android.material.textfield.TextInputEditText;

@SuppressLint({"ValidFragment"})
public class BottomSheetMyEntries extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private TextView cancelButton;
    private NestedScrollView scroll;

    private TextView noEntriesText;
    private TextView matchID;
    private ImageView closeBtn;

    private ArrayList<MyEntriesPojo> myEntriesPojoList;
    private RecyclerView.Adapter adapter;
    private RequestQueue requestQueue;
    private JsonArrayRequest jsonArrayRequest ;

    private String matchTitle;
    private String userName;
    private String password;
    private String roomID;
    private String isCanceled;


    public BottomSheetMyEntries() {
    }

    public BottomSheetMyEntries(String str, String str2, String str3, String str4, String str5) {
        this.matchTitle = str;
        this.userName = str2;
        this.password = str3;
        this.roomID = str4;
        this.isCanceled = str5;
    }


    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.bottomsheet_my_entries, viewGroup, false);
        
        this.recyclerView = (RecyclerView) inflate.findViewById(R.id.recyclerView);
        this.cancelButton = (TextView) inflate.findViewById(R.id.cancelButton);
        this.closeBtn = (ImageView) inflate.findViewById(R.id.closeBtn);
        this.noEntriesText = (TextView) inflate.findViewById(R.id.noEntriesText);
        this.matchID = (TextView) inflate.findViewById(R.id.matchID);
        this.scroll = (NestedScrollView) inflate.findViewById(R.id.scroll);

        if (!(roomID.equals("null") || roomID.isEmpty()) || !isCanceled.equals("0")) {
            cancelButton.setVisibility(View.GONE);
        }
        else {
            cancelButton.setVisibility(View.VISIBLE);
        }

        this.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (new ExtraOperations().haveNetworkConnection(getActivity())) {
                    Uri.Builder builder = Uri.parse(Constant.CANCEL_MY_ENTRIES_URL).buildUpon();
                    builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                    builder.appendQueryParameter("user_id", userName);
                    builder.appendQueryParameter("match_id", matchTitle);
                    StringRequest request = new StringRequest(Request.Method.POST, builder.toString(), new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                JSONArray jsonArray=jsonObject.getJSONArray("result");
                                JSONObject jsonObject1=jsonArray.getJSONObject(0);

                                String success = jsonObject1.getString("success");

                                if (success.equals("1")) {
                                    Intent i = new Intent(getActivity(), MainActivity.class);
                                    startActivity(i);
                                    getActivity().finish();
                                    Toast.makeText(getActivity(),"Successfully Cancel All Entry", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    BottomSheetMyEntries.this.dismiss();
                                    Toast.makeText(getActivity(),"Something went wrong", Toast.LENGTH_LONG).show();
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
        });


        this.closeBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                BottomSheetMyEntries.this.dismiss();
            }
        });

        
        myEntriesPojoList = new ArrayList();
        TextView textView = this.matchID;
        StringBuilder sb = new StringBuilder();
        sb.append("Match #");
        sb.append(this.matchTitle);
        textView.setText(sb.toString());
        
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new MyEntriesList().execute();
        
        return inflate;
    }

    private class MyEntriesList {

        public MyEntriesList() {
        }

        public void execute() {
            noEntriesText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);

            Uri.Builder builder = Uri.parse(Constant.MY_ENTRIES_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("match_id", matchTitle);
            builder.appendQueryParameter("user_id", userName);
            jsonArrayRequest = new JsonArrayRequest(builder.toString(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            myEntriesPojoList.clear();
                            for(int i = 0; i<response.length(); i++) {
                                MyEntriesPojo myEntriesPojo = new MyEntriesPojo();
                                JSONObject json = null;
                                try {
                                    json = response.getJSONObject(i);
                                    myEntriesPojo.setId(json.getString("id"));
                                    myEntriesPojo.setUser_id(json.getString("user_id"));
                                    myEntriesPojo.setMatch_id(json.getString("match_id"));
                                    myEntriesPojo.setPubg_id(json.getString("pubg_id"));
                                    myEntriesPojo.setSlot(json.getString("slot"));
                                    myEntriesPojo.setIs_canceled(json.getString("is_canceled"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                myEntriesPojoList.add(myEntriesPojo);
                            }
                            if (!myEntriesPojoList.isEmpty()){
                                adapter = new MyEntriesAdapter(myEntriesPojoList,getActivity());
                                adapter.notifyDataSetChanged();
                                recyclerView.setAdapter(adapter);

                                noEntriesText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                cancelButton.setVisibility(View.VISIBLE);
                            }
                            else {
                                noEntriesText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                cancelButton.setVisibility(View.GONE);
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
            requestQueue = Volley.newRequestQueue(getActivity());
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            jsonArrayRequest.setShouldCache(false);
            requestQueue.getCache().clear();
            requestQueue.add(jsonArrayRequest);
        }

    }

    public class MyEntriesAdapter extends RecyclerView.Adapter<MyEntriesAdapter.ViewHolder> {

        private Context context;
        private List<MyEntriesPojo> myEntriesPojoList;
        private String encodeGameUserID1;

        public MyEntriesAdapter(List<MyEntriesPojo> myEntriesPojoList, Context context){
            super();
            this.myEntriesPojoList = myEntriesPojoList;
            this.context = context;
        }

        @Override
        public MyEntriesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_my_entries, parent, false);
            MyEntriesAdapter.ViewHolder viewHolder = new MyEntriesAdapter.ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final MyEntriesAdapter.ViewHolder holder, int position) {
            final MyEntriesPojo myEntriesPojo =  myEntriesPojoList.get(position);

            TextView textView = holder.ingamename;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("•     #");
            stringBuilder.append(myEntriesPojo.getSlot());
            stringBuilder.append("     ");
            stringBuilder.append(myEntriesPojo.getPubg_id());
            textView.setText(stringBuilder.toString());

            if (!(roomID.equals("null") || roomID.isEmpty()) || !isCanceled.equals("0")) {
                holder.editButton.setVisibility(View.GONE);
            }
            else {
                holder.editButton.setVisibility(View.VISIBLE);
            }

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomSheetMyEntries.this.dismiss();
                    openDialog(myEntriesPojo.getId(),myEntriesPojo.getUser_id(),myEntriesPojo.getPubg_id(),myEntriesPojo.getMatch_id());
                }
            });
        }

        private void openDialog(final String id, final String user_id, String pubg_id, final String match_id) {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_edit_user);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            final TextInputEditText gameID = (TextInputEditText) dialog.findViewById(R.id.gameID);
            gameID.setText(pubg_id);

            Button button = (Button) dialog.findViewById(R.id.next);
            Button button2 = (Button) dialog.findViewById(R.id.cancel);

            final TextView textError = (TextView) dialog.findViewById(R.id.textError);

            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });


            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    encodeGameUserID1 = gameID.getText().toString().trim();
                    if (!encodeGameUserID1.isEmpty()) {
                        EditUsername(id,user_id,match_id);
                        dialog.dismiss();
                    }
                    else {
                        textError.setVisibility(View.VISIBLE);
                        textError.setText("Invalid Game Username. Retry.");
                    }
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

        private void EditUsername(String id, String user_id, String match_id) {
            if (new ExtraOperations().haveNetworkConnection(context)) {
                Uri.Builder builder = Uri.parse(Constant.UPDATE_MY_ENTRIES_URL).buildUpon();
                builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
                builder.appendQueryParameter("id", id);
                builder.appendQueryParameter("match_id", match_id);
                builder.appendQueryParameter("user_id", user_id);
                builder.appendQueryParameter("pubg_id", encodeGameUserID1);
                StringRequest request = new StringRequest(Request.Method.GET, builder.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                            String success = jsonObject1.getString("success");
                            String msg = jsonObject1.getString("msg");

                            if (success.equals("0")) {
                                Toast.makeText(context, msg + "", Toast.LENGTH_LONG).show();
                            } else if (success.equals("1")) {
                                Toast.makeText(context, msg + "", Toast.LENGTH_LONG).show();
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
                request.setRetryPolicy(new DefaultRetryPolicy(60000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                request.setShouldCache(false);
                MySingleton.getInstance(context).addToRequestque(request);
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }


        @Override
        public int getItemCount() {
            return myEntriesPojoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView ingamename;
            ImageView editButton;

            public ViewHolder(View itemView) {
                super(itemView);
                this.ingamename = (TextView) itemView.findViewById(R.id.ingamename);
                this.editButton = (ImageView) itemView.findViewById(R.id.editButton);
            }

        }
    }
}
