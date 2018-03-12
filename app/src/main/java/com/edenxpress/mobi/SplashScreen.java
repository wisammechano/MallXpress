package com.edenxpress.mobi;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Display;

import com.edenxpress.mobi.analytics.MallApplication;
import com.edenxpress.mobi.credentials.APICredentials;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class SplashScreen extends Activity {
    static boolean isCalledFromNotification;
    static Intent notificationIntent;
    public static Utils sessionObj;
    SharedPreferences configShared;
    Editor editor;
    Boolean isInternetAvailable = false;
    private MallApplication mMallApplication;
    public String mainResponseAsString;
    private int screen_width;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mMallApplication = (MallApplication) getApplication();
        sessionObj = new Utils(this);
        this.configShared = getSharedPreferences("configureView", 0);
        this.editor = this.configShared.edit();
        this.editor.putBoolean("isMainCreated", false);
        this.editor.commit();
        Locale locale = new Locale(this.configShared.getString("storeCode", "en"));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        requestWindowFeature(1);
        isOnline();
        if (this.isInternetAvailable) {
            setContentView(com.edenxpress.mobi.R.layout.activity_splash_screen);
            this.editor.putString("websiteId", APICredentials.WEBSITE_ID);
            this.editor.putString("NAMESPACE", APICredentials.NAMESPACE);
            this.editor.putString("URL", APICredentials.URL);
            this.editor.putString("soapUserName", APICredentials.SOAP_USER_NAME);
            this.editor.putString("soapPassword", APICredentials.SOAP_PASSWORD);
            this.editor.commit();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            this.screen_width = size.x;
            try {
                JSONObject jo = new JSONObject().put("width", this.screen_width);
                new Connection(this).execute("getHomepage", jo.toString());
                return;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
        OnClickListener dialogClickListener = new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        SplashScreen.this.finish();
                        SplashScreen.this.startActivity(getIntent());
                    default:
                        SplashScreen.this.finish();
                }
            }
        };
        new Builder(this).setMessage(getResources().getString(com.edenxpress.mobi.R.string.internet_unavailable))
                .setPositiveButton(getResources().getString(com.edenxpress.mobi.R.string._retry), dialogClickListener)
                .setNegativeButton(getString(com.edenxpress.mobi.R.string._cancel), dialogClickListener)
                .show();
    }

    public void languageResponse(String backresult) {
    }

    public void getHomepageResponse(String backresult) {
        //Log.d("backresult", backresult + "");
        if (backresult.equalsIgnoreCase("fault")) {
            SplashScreen.this.finish();
            SplashScreen.this.startActivity(getIntent());
            return;
        }
        if (backresult.equalsIgnoreCase("no")) {
            SplashScreen.this.finish();
            return;
        }
        try {
            JSONObject result = new JSONObject(backresult);
            if (result.getInt("error") == 1) {
                OnClickListener dialogClickListener = new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                SplashScreen.this.finish();
                            default:
                                SplashScreen.this.finish();
                        }
                    }
                };
                new Builder(this).setMessage(result.getString("message"))
                        .setNegativeButton(getString(R.string._ok), dialogClickListener)
                        .show();
            } else {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("mainResponseAsString", backresult);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void isOnline() {
        NetworkInfo netInfo = ((ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        this.isInternetAvailable = netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }

    protected void onResume() {
        if (this.mainResponseAsString != null) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("mainResponseAsString", this.mainResponseAsString);
            startActivity(intent);
            finish();
        }
        super.onResume();
    }

}
