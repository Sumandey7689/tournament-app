package com.omsidh.huntsmanwar.common;

import com.omsidh.huntsmanwar.BuildConfig;

public class Config {


    // put your admin panel url here
    public static String ADMIN_PANEL_URL = BuildConfig.My_api;
    public static String FILE_PATH_URL = BuildConfig.My_file;
    public static String PAYTM_URL = BuildConfig.My_paytm;
    public static String PAYPAL_URL = BuildConfig.My_paypal;
    public static String PURCHASE_CODE = BuildConfig.My_code;

    // Paytm Production API Details
    public static final String M_ID = "nulled";   //Paytm Merchand Id we got it in button_paytm credentials
    public static final String CHANNEL_ID = "WAP";              //Paytm Channel Id, got it in button_paytm credentials
    public static final String INDUSTRY_TYPE_ID = "Retail";     //Paytm industry type got it in button_paytm credential
    public static final String WEBSITE = "DEFAULT";
    public static final String CALLBACK_URL = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

    // UPI Payment Id
    public static final String UPI_ID = "Q094471264@ybl";

    // How To Join Room YouTube Link
    public static final String YOUTUBE_CHANNEL_ID = "https://www.youtube.com";
    public static final String HOW_TO_JOIN_URL = "https://www.youtube.com";

    // Discord Channel Link
    public static final String DISCORD_CHANNEL_ID = "https://huntsmanwar.ecoshirt.xyz";

    // UPI Payment Id
    public static final String WEB_URL = "https://huntsmanwar.ecoshirt.xyz";

    // Refer & Reward Offer
    //set true to enable or set false to disable
    public static final boolean REFER_EARN = true;
    public static final boolean WATCH_EARN = false;

    // AdMob Keys
    //set admob app id and ad unit id
    public static final String AD_APP_ID = "ca-app-pub-4502551436957929~3260952619";
    public static final String AD_REWARDED_ID = "ca-app-pub-4502551436957929/9939532294";

    // Reward Ads Setup
    //set next reward time, watch video, pay rewars
    public static final String WATCH_COUNT = "3";       // Set minimum watch video
    public static final String PAY_REWARD = "1";        // Set amount after rewarded

    // Customer Support Details
    //set true to enable or set false to disable
    public static final boolean ENABLE_EMAIL_SUPPORT = true;
    public static final boolean ENABLE_PHONE_SUPPORT = true;
    public static final boolean ENABLE_WHATSAPP_SUPPORT = true;
    public static final boolean ENABLE_MESSENGER_SUPPORT = true;
    public static final boolean ENABLE_DISCORD_SUPPORT = false;

    // Follow Us Link
    //set true to enable or set false to disable
    public static final boolean ENABLE_TWITTER_FOLLOW = true;
    public static final boolean ENABLE_YOUTUBE_FOLLOW = true;
    public static final boolean ENABLE_FACEBOOK_FOLLOW = true;
    public static final boolean ENABLE_INSTAGRAM_FOLLOW = true;

}
