package com.edenxpress.mobi;

import android.app.ActivityManager;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.edenxpress.mobi.analytics.MallApplication;
import com.edenxpress.mobi.credentials.APICredentials;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashSet;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by ICT on 12/27/2016.
 */

public class BaseActivity extends AppCompatActivity implements AsyncResponse {
    static String NAMESPACE;
    static String URL;
    public static Context ctx;
    private static boolean loadMenu = true;
    private static Tracker mTracker;
    Connection asyncTaskObj = new Connection(this);
    public SharedPreferences configShared;
    SharedPreferences.Editor editor;
    protected boolean isInternetAvailable;
    protected MenuItem itemBell;
    protected MenuItem itemCart;
    //protected JSONObject jo = new JSONObject();
    public MallApplication mMallApplication;
    protected Menu menu;
    protected Intent menuIntent;
    SharedPreferences notificationShared;
    protected Object response;
    protected JSONObject responseObject;
    private SearchView searchView;
    public SharedPreferences shared;
    String soapPassword;
    String soapUserName;
    public Toolbar toolbar;
    private HashSet<String> unreadNotifications;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configShared = getSharedPreferences("configureView", 0);
        NAMESPACE = APICredentials.NAMESPACE;
        URL = APICredentials.URL;
        this.soapUserName = APICredentials.SOAP_USER_NAME;
        this.soapPassword = APICredentials.SOAP_PASSWORD;
        this.shared = getSharedPreferences("customerData", 0);
        Log.d("session_id", this.shared.getString("SESSION_ID", "Session_Not_Loggin") + "");
        this.mMallApplication = (MallApplication) getApplication();
        if (mTracker == null) {
            mTracker = this.mMallApplication.getDefaultTracker();
        }
        mTracker.send(new HitBuilders.ExceptionBuilder().build());
        mTracker.setScreenName("" + getClass().getName());
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        ctx = this;
    }

    public void trackException(Exception e, Context ctx) {
        if (e != null) {
            mTracker.send(new HitBuilders.ExceptionBuilder().setDescription(new StandardExceptionParser(ctx, null).getDescription(Thread.currentThread().getStackTrace().toString(), e)).setFatal(false).build());
        }
    }


    public void getActivityInStack() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int sizeStack = am.getRunningTasks(5).size();
        for (int i = 0; i < sizeStack; i++) {
            ComponentName cn = am.getRunningTasks(2).get(i).topActivity;
            ComponentName cn2 = am.getRunningTasks(2).get(i).baseActivity;
            Log.d("stack " + am.getRunningTasks(2).get(i).numActivities, cn.getClassName() + "-----" + cn2.getClassName());
        }
    }

    protected void onResume() {
        if (this.itemCart != null) {
            Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", MODE_PRIVATE).getString("cartItems", "0"));
        }
        super.onResume();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        if (id == android.R.id.home) {
            onBackPressed();
            finish();
            return true;
        } else if (id == R.id.marketPlace) {
            if (this.mMallApplication.viewMarketPlaceHome() == null) {
                return true;
            }
            intent = new Intent(this, this.mMallApplication.viewMarketPlaceHome());
            intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.sellerDashboard) {
            if (this.mMallApplication.viewSellerDashBoard() == null) {
                return true;
            }
            intent = new Intent(this, this.mMallApplication.viewSellerDashBoard());
            intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } else if (id != R.id.sellerOrder) {
            SharedPreferences shared = getSharedPreferences("customerData", 0);
            Boolean isLoggedIn = shared.getBoolean("isLoggedIn", false);
            String isSeller = shared.getString("isSeller", "");
            if (id == R.id.action_settings) {
                MenuItem loginMenuItem = this.menu.findItem(R.id.login);
                MenuItem signupMenuItem = this.menu.findItem(R.id.signup);
                if (isLoggedIn) {
                    loginMenuItem.setTitle(getResources().getString(R.string.logout_title));
                    signupMenuItem.setVisible(false);
                    if (isSeller.equalsIgnoreCase("1")) {
                        this.menu.findItem(R.id.sellerDashboard).setVisible(true);
                        this.menu.findItem(R.id.sellerOrder).setVisible(true);
                    }
                } else {
                    loginMenuItem.setTitle(getResources().getString(R.string.login_title));
                    signupMenuItem.setVisible(true);
                    this.menu.findItem(R.id.sellerDashboard).setVisible(false);
                    this.menu.findItem(R.id.sellerOrder).setVisible(false);
                }
            }
            if (!(isLoggedIn || id == R.id.signup || id == R.id.action_settings || id == R.id.action_bell || id == R.id.search)) {
                id = R.id.login;
            }
            if (id == R.id.login && item.getTitle().equals(getResources().getString(R.string.logout_title))) {
                shared.edit().clear().apply();
                JSONObject jo = new JSONObject();
                new Connection(this).execute("customerLogout", jo.toString());
                intent = new Intent(this, MainActivity.class);
                intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.login) {
                if (isLoggedIn) {
                    intent = new Intent(this, DashboardActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                intent = new Intent(this, LoginActivity.class);
                intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.signup) {
                if (isLoggedIn) {
                    intent = new Intent(this, DashboardActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
                /*intent = new Intent(this, CreateAccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                return true;
            } else if (id == R.id.dashboard) {
                intent = new Intent(this, DashboardActivity.class);
                intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            } else if (id == R.id.accountinfo) {
                /*intent = new Intent(this, AccountinfoActivity.class);
                intent.putExtra("changeAccountInfo", "1");
                startActivity(intent);*/
                return true;
            } else if (id == R.id.addrbook) {
                /*intent = new Intent(this, AddrBookActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                return true;
            } else if (id == R.id.myorders) {
                /*intent = new Intent(this, MyorderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                return true;
            } else if (id == R.id.mywishlist) {
                /*intent = new Intent(this, MyWishlistActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                return true;
            } else if (id == R.id.newslettersubs) {
                /*intent = new Intent(this, NewsLetterSubsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                return true;
            } else if (id != R.id.mydownloads) {
                return super.onOptionsItemSelected(item);
            } else {
                /*intent = new Intent(this, MyDownloadsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                return true;
            }
        } else if (this.mMallApplication.viewSellerOrderHistory() == null) {
            return true;
        } else {
            /*intent = new Intent(this, this.mMallApplication.viewSellerOrderHistory());
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);*/
            return true;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        this.searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        this.searchView.setSearchableInfo(((SearchManager) getSystemService(SEARCH_SERVICE)).getSearchableInfo(getComponentName()));
        menu.findItem(R.id.myproreview).setVisible(false);
        if (this.mMallApplication.viewMarketPlaceHome() != null) {
            menu.findItem(R.id.marketPlace).setVisible(true);
        }
        this.itemCart = menu.findItem(R.id.action_cart);
        SharedPreferences shared = getSharedPreferences("customerData", MODE_PRIVATE);
        Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), shared.getString("cartItems", "0"));
        this.itemCart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                //BaseActivity.this.startActivity(new Intent(BaseActivity.this, Cart.class));
                return true;
            }
        });
        this.itemBell = menu.findItem(R.id.action_bell);
        this.notificationShared = getSharedPreferences("com.mallxpress.mobi.notification", 4);
        LayerDrawable icon = (LayerDrawable) this.itemBell.getIcon();
        this.unreadNotifications = (HashSet) this.notificationShared.getStringSet("unreadNotifications", new HashSet());
        Utils.setBadgeCount(this, icon, this.unreadNotifications.size() + "");
        this.itemBell.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                /*BaseActivity.this.menuIntent = new Intent(BaseActivity.this, NotificationActivity.class);
                BaseActivity.this.startActivity(BaseActivity.this.menuIntent);*/
                return true;
            }
        });
        Boolean isLoggedIn = shared.getBoolean("isLoggedIn", false);
        String isSeller = shared.getString("isSeller", "");
        MenuItem loginMenuItem = menu.findItem(R.id.login);
        MenuItem signupMenuItem = menu.findItem(R.id.signup);
        if (isLoggedIn) {
            loginMenuItem.setTitle(R.string.logout_title);
            signupMenuItem.setVisible(false);
            if (isSeller.equalsIgnoreCase("1")) {
                menu.findItem(R.id.sellerDashboard).setVisible(true);
                menu.findItem(R.id.sellerOrder).setVisible(true);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void isOnline() {
        NetworkInfo netInfo = ((ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        this.isInternetAvailable = netInfo != null && netInfo.isConnected() && netInfo.isAvailable();
    }

    protected void showRetryDialog(Context mContext) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        BaseActivity.this.finish();
                        BaseActivity.this.startActivity(getIntent());
                    default:
                        BaseActivity.this.finish();
                }
            }
        };
        new AlertDialog.Builder(mContext).setMessage(getResources().getString(R.string.internet_unavailable))
                .setPositiveButton(getResources().getString(R.string._retry), dialogClickListener)
                .setNegativeButton(getString(R.string._cancel), dialogClickListener)
                .show();
    }

    public final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(110);
        envelope.dotNet = false;
        envelope.xsd = "http://www.w3.org/2001/XMLSchema";
        ;
        envelope.enc = "http://schemas.xmlsoap.org/soap/encoding/";
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    public final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(URL, 60000);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    protected int getScreenWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    protected int dpToPx(float dips) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        //int ret = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, metrics)); //For future reference
        return Math.round((dips * metrics.density));
    }

    protected int pxToDp(float pixels) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return Math.round(pixels / metrics.density);
    }

    public void processFinish(String output) {
    }

    public void onUserInteraction() {
        if (this.itemBell != null) {
            this.notificationShared = getSharedPreferences("com.mallxpress.mobi.notification", 4);
            Utils.setBadgeCount(this, (LayerDrawable) this.itemBell.getIcon(), ((HashSet) this.notificationShared.getStringSet("unreadNotifications", new HashSet())).size() + "");
        }
    }

    public View line(int color) {
        View v = new View(this);
        v.setBackgroundColor(color);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        layoutParams.setMargins(0, 5, 0, 5);
        v.setLayoutParams(layoutParams);
        return v;
    }

    public void showToast(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
    }


}
