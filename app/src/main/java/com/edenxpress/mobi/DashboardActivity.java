package com.edenxpress.mobi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONObject;

/**
 * Created by ICT on 1/3/2017.
 */

public class DashboardActivity extends BaseActivity {

    String billingAddressId;
    SharedPreferences configShared;
    public Editor editor;
    private boolean isInternetAvailable;
    JSONObject mainObject;
    String soapPassword;
    String soapUserName;
    Integer websiteId = 1;

    public void isOnline() {
        NetworkInfo netInfo = ((ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        this.isInternetAvailable = netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOnline();
        if (this.isInternetAvailable) {
            setContentView(R.layout.activity_dashboard);
            this.toolbar = (Toolbar) findViewById(R.id.toolbar);
            this.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.heading_color));
            setSupportActionBar(this.toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle(this.shared.getString("customerName", ""));
            ((AppBarLayout) findViewById(R.id.appbar)).addOnOffsetChangedListener(new OnOffsetChangedListener() {
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (verticalOffset <= -216) {
                        DashboardActivity.this.toolbar.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, R.color.primary_color));
                    } else {
                        DashboardActivity.this.toolbar.setBackgroundColor(ContextCompat.getColor(DashboardActivity.this, R.color.heading_color));
                    }
                }
            });
            if (!this.shared.getBoolean("isLoggedIn", false)) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return;
            }
            return;
        } else {
            showRetryDialog(this);
        }
    }

    public void redirectToAccountInfo(View v) {
        /*Intent intent = new Intent(this, AccountinfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("changeAccountInfo", "1");
        startActivity(intent);*/
    }

    public void changePassword(View v) {
        /*Intent intent = new Intent(this, AccountinfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("changePassword", "1");
        startActivity(intent);*/
    }

    public void viewWishlist(View v) {
        /*Intent intent = new Intent(this, MyWishlistActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    public void viewDownloadableProduct(View v) {
        /*Intent intent = new Intent(this, MyDownloadsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    public void viewAllOrders(View v) {
        /*Intent intent = new Intent(this, MyorderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    public void viewPoints(View v) {
        /*Intent intent = new Intent(this, PointsAndTransactions.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Points", "1");
        startActivity(intent);*/
    }

    public void viewTransactions(View v) {
        /*Intent intent = new Intent(this, PointsAndTransactions.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Transactions", "1");
        startActivity(intent);*/
    }

    public void viewReturnData(View v) {
        /*Intent intent = new Intent(this, ReturnData.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    public void manageAddress(View v) {
        /*Intent intent = new Intent(this, AddrBookActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }

    public void viewNewsletter(View v) {
        /*Intent intent = new Intent(this, NewsLetterSubsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);*/
    }
}
