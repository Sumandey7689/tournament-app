package com.omsidh.huntsmanwar.activity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.omsidh.huntsmanwar.R;

public class UpdateAppActivity extends AppCompatActivity {

    private String updateURL;
    private String updatedOn;
    private String whatsNewData;
    private String latestVersion;


    private TextView updateDate;
    private TextView whatsNew;
    private TextView webupdate;
    private TextView newVersion;

    private ProgressDialog progressDialog;
    private String[] str = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_SMS", "android.permission.RECEIVE_SMS"};
    private int code = 1;
    public static String TAG = "UpdateApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_app);

        this.updateDate = (TextView) findViewById(R.id.date);
        this.newVersion = (TextView) findViewById(R.id.version);
        this.whatsNew = (TextView) findViewById(R.id.whatsnew);
        this.webupdate = (TextView) findViewById(R.id.webupdate);



        this.updateURL = getIntent().getStringExtra("updateURL");
        this.updatedOn = getIntent().getStringExtra("updateDate");
        this.whatsNewData = getIntent().getStringExtra("whatsNew");
        this.latestVersion = getIntent().getStringExtra("latestVersionName");

        this.updateDate.setText("Updated On: "+this.updatedOn);
        this.newVersion.setText("New Version: v"+ latestVersion);

        if (whatsNewData != null){
            this.whatsNew.setText(whatsNewData);
        }

        webupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage(updateURL);
            }
        });

    }

    public void openWebPage(String url) {
        try {
            Uri webpage = Uri.parse(url);
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                webpage = Uri.parse("http://" + url);
            }
            Intent myIntent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(myIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install verifyDownload web browser or check your URL.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

}
