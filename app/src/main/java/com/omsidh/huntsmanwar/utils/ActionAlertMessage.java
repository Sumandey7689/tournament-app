package com.omsidh.huntsmanwar.utils;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.textfield.TextInputEditText;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import com.omsidh.huntsmanwar.R;
import com.omsidh.huntsmanwar.activity.JoiningMatchActivity;
import com.omsidh.huntsmanwar.activity.MainActivity;
import com.omsidh.huntsmanwar.common.Config;
import com.omsidh.huntsmanwar.common.Constant;

public class ActionAlertMessage {
    private String accessKey;
    private String encodeGameUserID;

    public ActionAlertMessage() {
    }

    public void showJoinMatchAlert(final JoiningMatchActivity joiningMatchActivity, final String id, final String username, final String name, final String matchID, final String entryType, final String matchType, final String privateStatus, final int entryFee) {
        final Dialog dialog = new Dialog(joiningMatchActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_joinprompt);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final TextInputLayout accessCodeView = (TextInputLayout) dialog.findViewById(R.id.accessCodeView);
        final TextInputEditText accessCode = (TextInputEditText) dialog.findViewById(R.id.accessCode);
        final TextInputEditText gameID = (TextInputEditText) dialog.findViewById(R.id.gameID);

        Button button = (Button) dialog.findViewById(R.id.next);
        Button button2 = (Button) dialog.findViewById(R.id.cancel);

        final TextView textError = (TextView) dialog.findViewById(R.id.textError);
        TextView accessCodeInfoText = (TextView) dialog.findViewById(R.id.accessCodeInfoText);

        if (privateStatus.equals("yes")) {
            accessCodeView.setVisibility(View.VISIBLE);
            accessCodeInfoText.setVisibility(View.VISIBLE);
        }
        else {
            accessCode.setVisibility(View.GONE);
            accessCodeInfoText.setVisibility(View.GONE);
        }

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                accessKey = accessCode.getText().toString().trim();
                encodeGameUserID = gameID.getText().toString();

                if (privateStatus.equals("yes") && accessKey.isEmpty() || accessKey.contains(" ")) {
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Invalid Access Code. Retry.");
                    JoiningMatchActivity.progressBar.setVisibility(View.GONE);
                }
                if (!encodeGameUserID.isEmpty()) {
                    joinMatch(joiningMatchActivity,id,username,accessKey,encodeGameUserID,name,matchID,entryType,matchType,privateStatus,entryFee);
                    JoiningMatchActivity.progressBar.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
                else {
                    textError.setVisibility(View.VISIBLE);
                    textError.setText("Invalid PUBG Username. Retry.");
                    JoiningMatchActivity.progressBar.setVisibility(View.GONE);
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void joinMatch(final JoiningMatchActivity joiningMatchActivity, final String id, final String username, final String accessKey, final String encodeGameUserID, final String name, final String matchID, final String entryType, final String matchType, final String privateStatus, final int entryFee) {
        if (new ExtraOperations().haveNetworkConnection(joiningMatchActivity)) {
            Uri.Builder builder = Uri.parse(Constant.JOIN_MATCH_URL).buildUpon();
            builder.appendQueryParameter("access_key", Config.PURCHASE_CODE);
            builder.appendQueryParameter("match_id", matchID);
            builder.appendQueryParameter("user_id", id);
            builder.appendQueryParameter("username", username);
            builder.appendQueryParameter("name", name);
            builder.appendQueryParameter("privateStatus", privateStatus);
            builder.appendQueryParameter("accessKey", accessKey);
            builder.appendQueryParameter("pubg_id", encodeGameUserID);
            builder.appendQueryParameter("entryType", entryType);
            builder.appendQueryParameter("matchType", matchType);
            builder.appendQueryParameter("entryFee", String.valueOf(entryFee));
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
                            JoiningMatchActivity.progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("1")) {
                            JoiningMatchActivity.progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        } else if (success.equals("2")) {
                            JoiningMatchActivity.progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                            try {
                                Intent intent = new Intent(joiningMatchActivity, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                joiningMatchActivity.startActivity(intent);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        } else if (success.equals("3")) {
                            JoiningMatchActivity.progressBar.setVisibility(View.GONE);
                            Toast.makeText(joiningMatchActivity, msg + "", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        JoiningMatchActivity.progressBar.setVisibility(View.GONE);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    JoiningMatchActivity.progressBar.setVisibility(View.GONE);
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
            MySingleton.getInstance(joiningMatchActivity).addToRequestque(request);
        } else {
            Toast.makeText(joiningMatchActivity, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }


}
