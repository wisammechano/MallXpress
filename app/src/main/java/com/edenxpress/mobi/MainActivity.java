package com.edenxpress.mobi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.edenxpress.mobi.analytics.MallApplication;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.utils.RecyclerViewCacheUtil;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeader.OnAccountHeaderListener;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.view.BezelImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class MainActivity extends BaseActivity {
    public static final int DRAWER_CATEGORY_IDENTIFIER_START = 1000;
    public static final int DRAWER_CURRENCY_IDENTIFIER = 1200;
    public static final int DRAWER_CUSTOMER_ACCOUNT_INFO_IDENTIFIER = 1124;
    public static final int DRAWER_CUSTOMER_ADDRESS_BOOK_IDENTIFIER = 1125;
    public static final int DRAWER_CUSTOMER_DASHBOARD_IDENTIFIER = 1121;
    public static final int DRAWER_CUSTOMER_DOWNLOADS_IDENTIFIER = 1126;
    public static final int DRAWER_CUSTOMER_HOME_IDENTIFIER = 1120;
    public static final int DRAWER_CUSTOMER_NEWSLETTER_IDENTIFIER = 1127;
    public static final int DRAWER_CUSTOMER_ORDER_IDENTIFIER = 1122;
    public static final int DRAWER_LANG_IDENTIFIER = 1110;
    private static final int INTENT_LOGIN = 1;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    public static boolean catHasBD;
    public static JSONObject catBD;
    public static JSONArray categoriesJSONObject;
    private static boolean isBottom = true;
    public static Dialog progress;
    static int screen_width;
    public Utils sessionObj;
    private long backPressedTime = 0;
    String[] bannerId;
    String[] bannerType;
    private int carousalLayoutWidth;
    HashMap<Integer, List<String>> childActions;
    int counterForChildAction;
    private JSONArray currenciesJSONObject;
    private ProfileDrawerItem currentProfile;
    ImageView dotImage;
    ImageView[] dotList;
    Editor editor;
    private LinearLayout featuredProductLayout;
    JSONArray featuredProductsArr;
    boolean firstTime;
    String[] imageUrls;
    private JSONArray languagesJSONObject;
    private LinearLayout latestProductLayout;
    HashMap<String, List<String>> listDataChild;
    List<String> listDataHeader;
    DetailOnPageChangeListener listener;
    private AccountHeader mAccountHeader;
    BezelImageView mAccountHeaderCurrentProfileImage;
    private OnClickListener mAccountHeaderTextSectionOnClickLister = new OnClickListener() {
        public void onClick(View v) {
            if (!MainActivity.this.shared.getBoolean("isLoggedIn", false)) {
                if (MainActivity.this.mDrawer != null && MainActivity.this.mDrawer.isDrawerOpen()) {
                    MainActivity.this.mDrawer.closeDrawer();
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
            } else if (MainActivity.this.mAccountSwitcherArrow.getVisibility() == View.VISIBLE && MainActivity.this.mDrawer != null) {
                if (MainActivity.this.mDrawer.switchedDrawerContent()) {
                    Log.d("DEBUG", "onClick: true");
                    MainActivity.this.mDrawer.resetDrawerContent();
                    MainActivity.this.mAccountSwitcherArrow.clearAnimation();
                    ViewCompat.animate(MainActivity.this.mAccountSwitcherArrow).rotation(0.0f).start();
                    return;
                }
                Log.d("DEBUG", "onClick: false");
                int position = 0;
                ArrayList<IDrawerItem> profileDrawerItems = new ArrayList();
                if (MainActivity.this.mAccountHeader.getProfiles() != null) {
                    for (IProfile profile : MainActivity.this.mAccountHeader.getProfiles()) {
                        if (profile instanceof ProfileSettingDrawerItem) {
                            ((IDrawerItem) profile).withSetSelected(false);
                            profileDrawerItems.add((IDrawerItem) profile);
                        }
                        position++;
                    }
                }
                MainActivity.this.mDrawer.switchDrawerContent(MainActivity.this.mySecondaryOnDrawerItemClickListener, null, profileDrawerItems, 0);
                MainActivity.this.mAccountSwitcherArrow.clearAnimation();
                ViewCompat.animate(MainActivity.this.mAccountSwitcherArrow).rotation(180.0f).start();
            }
        }
    };
    private ImageView mAccountSwitcherArrow;
    private Drawer mDrawer;
    ItemAdapter<IDrawerItem> mDrawerItemAdapter;
    private IProfile[] mIProfiles;
    private MallApplication mMallApplication;
    List<Element> mainActions;
    JSONObject mainObject;
    ProgressBar mainProgressBar;
    Object mainResponse = null;
    String mainResponseAsString;
    private TextView model;
    protected OnDrawerItemClickListener myPrimaryOnDrawerItemClickListener = new OnDrawerItemClickListener() {
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            drawerItem.withSetSelected(true);
            long mDrawerIdentifier = drawerItem.getIdentifier();
            if (mDrawerIdentifier >= DRAWER_CATEGORY_IDENTIFIER_START && mDrawerIdentifier <= ((long) (MainActivity.this.noOfCategories + DRAWER_CATEGORY_IDENTIFIER_START))) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
                intent.putExtra("mDrawerIdentifier", ((int) mDrawerIdentifier) % DRAWER_CATEGORY_IDENTIFIER_START);
                MainActivity.this.startActivity(intent);
            } else if (mDrawerIdentifier >= DRAWER_LANG_IDENTIFIER && mDrawerIdentifier <= ((long) (MainActivity.this.languagesJSONObject.length() + DRAWER_LANG_IDENTIFIER))) {
                try {
                    String languageToLoad = MainActivity.this.languagesJSONObject.getJSONObject(((int) mDrawerIdentifier) % DRAWER_LANG_IDENTIFIER).getString("code");
                    String localeLang = languageToLoad.split("-")[0];
                    MainActivity.this.configShared.edit()
                            .putString("storeLanguage", MainActivity.this.languagesJSONObject.getJSONObject(((int) mDrawerIdentifier) % DRAWER_LANG_IDENTIFIER).getString("name"))
                            .putString("storeCode", localeLang)
                            .apply();
                    Locale locale = new Locale(localeLang);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    //config.locale = locale;
                    config.setLocale(locale);
                    MainActivity.this.getResources().updateConfiguration(config, MainActivity.this.getResources().getDisplayMetrics());
                    JSONObject jo1 = new JSONObject();
                    try {
                        jo1.put("code", languageToLoad);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Connection(MainActivity.this).execute("language", jo1.toString());
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            } else if (mDrawerIdentifier >= DRAWER_CURRENCY_IDENTIFIER && mDrawerIdentifier <= ((long) (MainActivity.this.currenciesJSONObject.length() + DRAWER_CURRENCY_IDENTIFIER))) {
                String currency = null;
                try {
                    currency = MainActivity.this.currenciesJSONObject.getJSONObject(((int) mDrawerIdentifier) % DRAWER_CURRENCY_IDENTIFIER).getString("code");
                } catch (JSONException e22) {
                    e22.printStackTrace();
                }
                MainActivity.this.configShared.edit()
                        .putString("currencyCode", currency)
                        .apply();
                JSONObject jo1 = new JSONObject();
                try {
                    jo1.put("code", currency);
                } catch (JSONException e222) {
                    e222.printStackTrace();
                }
                new Connection(MainActivity.this).execute("currency", jo1.toString());
            }
            return false;
        }
    };
    private OnDrawerItemClickListener mySecondaryOnDrawerItemClickListener = new OnDrawerItemClickListener() {
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if (drawerItem != null) {
                Intent intent;
                switch ((int) drawerItem.getIdentifier()) {
                    case DRAWER_CUSTOMER_HOME_IDENTIFIER:
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                        break;
                    case DRAWER_CUSTOMER_DASHBOARD_IDENTIFIER:
                        intent = new Intent(MainActivity.this, DashboardActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                        break;
                    case DRAWER_CUSTOMER_ORDER_IDENTIFIER:
                        /*intent = new Intent(MainActivity.this, MyorderActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                        break;*/
                    case DRAWER_CUSTOMER_ACCOUNT_INFO_IDENTIFIER:
                        /*intent = new Intent(MainActivity.this, AccountinfoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("changeAccountInfo", "1");
                        MainActivity.this.startActivity(intent);
                        break;*/
                    case DRAWER_CUSTOMER_ADDRESS_BOOK_IDENTIFIER:
                        /*intent = new Intent(MainActivity.this, AddrBookActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                        break;*/
                    case DRAWER_CUSTOMER_DOWNLOADS_IDENTIFIER:
                        /*intent = new Intent(MainActivity.this, MyDownloadsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                        break;*/
                    case DRAWER_CUSTOMER_NEWSLETTER_IDENTIFIER:
                        /*intent = new Intent(MainActivity.this, NewsLetterSubsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                        break;*/
                    default:
                        intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
                        MainActivity.this.startActivity(intent);
                        break;
                }
            }
            return false;
        }
    };
    String[] nameOfBanner;
    public TextView newPrice;
    private int noOfCategories;
    protected SharedPreferences notificationShared;
    int page = 0;
    private ViewPager pager;
    public TextView price;
    public ImageView productImage;
    public TextView productName;
    protected ProgressDialog progressdialog;
    public RatingBar ratingBar;
    Object response = null;
    RemindTask swipeAtInterval;
    RemindTask1 swipeAtInterval1;
    Timer timer;
    Timer timer1;
    private Toolbar toolbar;
    private int x;
    private int y;

    class RemindTask extends TimerTask {
        int noOfBanners;

        RemindTask(int noOfBanners) {
            this.noOfBanners = noOfBanners;
        }

        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (MainActivity.this.page > noOfBanners) {
                        MainActivity.this.timer.cancel();
                    } else if (MainActivity.this.page == noOfBanners - 1) {
                        MainActivity.this.pager.setCurrentItem(0);
                        MainActivity.this.page = 0;
                    } else {
                        MainActivity.this.page++;
                        MainActivity.this.pager.setCurrentItem(MainActivity.this.page);
                    }
                }
            });
        }
    }

    class RemindTask1 extends TimerTask {
        int noOfBanners;

        RemindTask1(int noOfBanners) {
            this.noOfBanners = noOfBanners;
        }

        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    MainActivity.this.carousalLayoutWidth = MainActivity.this.findViewById(R.id.carouselScroller).getWidth();
                    if (MainActivity.this.y > MainActivity.this.carousalLayoutWidth + MainActivity.screen_width) {
                        ((HorizontalScrollView) MainActivity.this.findViewById(R.id.carouselScrollerView)).fullScroll(View.FOCUS_LEFT);
                        MainActivity.this.x = 0;
                        MainActivity.this.y = MainActivity.screen_width + MainActivity.this.x;
                        return;
                    }
                    ((HorizontalScrollView) MainActivity.this.findViewById(R.id.carouselScrollerView)).smoothScrollTo(MainActivity.this.x, MainActivity.this.y);
                    MainActivity.this.x = MainActivity.this.y;
                    MainActivity.this.y = MainActivity.screen_width + MainActivity.this.x;
                }
            });
        }
    }

    public class DetailOnPageChangeListener implements OnPageChangeListener {
        private int currentPage;

        public void onPageSelected(int position) {
            this.currentPage = position;
            for (int i = 0; i < MainActivity.this.imageUrls.length; i++) {
                if (i == position) {
                    MainActivity.this.dotList[i].setBackgroundResource(R.drawable.dot_icon_focused);
                } else {
                    MainActivity.this.dotList[i].setBackgroundResource(R.drawable.dot_icon_unfocused);
                }
            }
        }

        public int getCurrentPage() {
            return this.currentPage;
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float Offset, int positionOffsetPixels) {
            if (Offset <= 0.5f) {
            }
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {
        private String[] images;
        private LayoutInflater inflater;

        ImagePagerAdapter(String[] images) {
            this.images = images;
            this.inflater = MainActivity.this.getLayoutInflater();
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        public void finishUpdate(View container) {
        }

        public int getCount() {
            return this.images.length;
        }

        public Object instantiateItem(View view, final int position) {
            View imageLayout = this.inflater.inflate(R.layout.item_view_pager_banner, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            imageView.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = null;
                    if (MainActivity.this.bannerType[position].equals("product")) {
                        intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewProductSimple());
                        intent.putExtra("idOfProduct", MainActivity.this.bannerId[position]);
                        intent.putExtra("nameOfProduct", MainActivity.this.nameOfBanner[position]);
                    }
                    if (MainActivity.this.bannerType[position].equals("category")) {
                        if (MainActivity.this.getSharedPreferences("categoryView", MODE_PRIVATE).getBoolean("isGridView", false)) {
                            intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewCategoryGrid());
                        } else {
                            intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewCategoryList());
                        }
                        intent.putExtra("ID", MainActivity.this.bannerId[position]);
                        intent.putExtra("CATEGORY_NAME", MainActivity.this.nameOfBanner[position]);
                        intent.putExtra("drawerData", MainActivity.this.mainObject + "");
                    }
                    if (MainActivity.this.bannerType[position].equals("manufacturer")) {
                        //@FIIIIIIIIIIIIIIIIIIX THIS
                        //intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewProductSimple());
                        //intent.putExtra("idOfProduct", MainActivity.this.bannerId[position]);
                        //intent.putExtra("nameOfProduct", MainActivity.this.nameOfBanner[position]);
                    }
                    MainActivity.this.startActivity(intent);
                }
            });
            Picasso.with(MainActivity.this).load(this.images[position]).into(imageView);
            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View container) {
        }
    }

    public void onBackPressed() {
        long t = System.currentTimeMillis();
        if (t - this.backPressedTime > 2000) {
            this.backPressedTime = t;
            Toast.makeText(this, getResources().getString(R.string.press_back_to_exit), Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Current Locale is" + getClass().getSimpleName(), getResources().getConfiguration().locale.getLanguage());
        super.onCreate(savedInstanceState);
        isOnline();
        if (this.isInternetAvailable) {
            setContentView(R.layout.activity_main);
            if (VERSION.SDK_INT > 9) {
                StrictMode.setThreadPolicy(new Builder().permitAll().build());
            }
            this.mMallApplication = (MallApplication) getApplication();
            setTitle(getResources().getString(R.string.app_name));
            setDataFromExtras();
            initializeConfiguration(savedInstanceState);
            Log.d("GCM", this.configShared.getBoolean("isUserRegisteredForGCM", true) + "");
        } else {
            showRetryDialog(this);
        }
        if (this.mainObject == null) {
            getCatalogDataFromApi();
        } else {
            loadMainUI();
        }
    }

    private void addingCustomChangesToRemoveEmailListFromDrawer(Bundle savedInstanceState) {
        Log.d("DEBUG", "addingCustomChangesToRemoveEmailListFromDrawer: ");
        try {
            View mMaterialDrawerHeaderView = this.mAccountHeader.getView();
            View mAccountHeaderTextSection = mMaterialDrawerHeaderView.findViewById(R.id.material_drawer_account_header_text_section);
            this.mAccountHeaderCurrentProfileImage = (BezelImageView) mMaterialDrawerHeaderView.findViewById(R.id.material_drawer_account_header_current);
            this.mAccountHeaderCurrentProfileImage.setVisibility(View.VISIBLE);
            mAccountHeaderTextSection.setVisibility(View.VISIBLE);
            this.mAccountSwitcherArrow = (ImageView) mMaterialDrawerHeaderView.findViewById(R.id.material_drawer_account_header_text_switcher);
            this.mAccountSwitcherArrow.setVisibility(View.VISIBLE);
            mAccountHeaderTextSection.setOnClickListener(this.mAccountHeaderTextSectionOnClickLister);
            if (this.shared.getBoolean("isLoggedIn", false)) {
                Picasso.with(this).load(this.shared.getString("customerPic", "")).placeholder(R.drawable.placeholder).into(this.mAccountHeaderCurrentProfileImage);
            } else {
                this.mAccountSwitcherArrow.setVisibility(View.GONE);
            }
            new RecyclerViewCacheUtil().withCacheSize(2).apply(this.mDrawer.getRecyclerView(), this.mDrawer.getDrawerItems());
            Log.d("DEBUG", "addingCustomChangesToRemoveEmailListFromDrawer: RecyclerViewCacheUtil");
            if (savedInstanceState == null) {
                this.mAccountHeader.setActiveProfile(this.currentProfile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeConfiguration(Bundle savedInstanceState) {
        try {
            this.configShared = getSharedPreferences("configureView", MODE_PRIVATE);
            this.toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(this.toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            initDrawerImageLoader();
            initAccountHeader(savedInstanceState);
            initDrawer(savedInstanceState);
            addingCustomChangesToRemoveEmailListFromDrawer(savedInstanceState);
            getWindowManager().getDefaultDisplay().getMetrics(new DisplayMetrics());
            Display display = getWindowManager().getDefaultDisplay();
            this.mainProgressBar = (ProgressBar) findViewById(R.id.main_progress_bar);
            this.mainProgressBar.setVisibility(View.VISIBLE);
            this.listDataHeader = new ArrayList();
            this.listDataChild = new HashMap();
            Point size = new Point();
            display.getSize(size);
            screen_width = size.x;
            this.featuredProductLayout = (LinearLayout) findViewById(R.id.featured_Product_Layout);
            this.latestProductLayout = (LinearLayout) findViewById(R.id.latest_Product_Layout);
            this.mainActions = new ArrayList();
            this.childActions = new HashMap();
            resetData();
        } catch (Exception e) {
            trackException(e, this);
            e.printStackTrace();
        }
    }

    private void initDrawerImageLoader() {
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(R.drawable.placeholder).into(imageView);
            }

            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });
    }

    public void initAccountHeader(Bundle savedInstanceState) {
        try {
            this.mAccountHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withTranslucentStatusBar(true)
                    .withHeaderBackground(R.drawable.navigation_drawer_header_bg)
                    .withSelectionListEnabled(false)
                    .withAccountHeader(R.layout.material_drawer_header)
                    .addProfiles(getIProfile())
                    .withOnAccountHeaderListener(new OnAccountHeaderListener() {

                        public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                            if (!MainActivity.this.shared.getBoolean("isLoggedIn", false)) {
                                if (MainActivity.this.mDrawer != null && MainActivity.this.mDrawer.isDrawerOpen()) {
                                    MainActivity.this.mDrawer.closeDrawer();
                                }
                                MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            }
                            return true;
                        }
                    }).withSavedInstance(savedInstanceState).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IProfile[] getIProfile() {
        Log.d("DEBUG", "getIProfile: isLoggedIn" + this.shared.getBoolean("isLoggedIn", false));
        try {
            if (this.shared.getBoolean("isLoggedIn", false)) {
                this.mIProfiles = new IProfile[8];
                this.currentProfile = (ProfileDrawerItem) new ProfileDrawerItem().withName(this.shared.getString("customerName", "")).withEmail(this.shared.getString("customerEmail", "")).withIdentifier(101);
                this.mIProfiles[0] = this.currentProfile;
                this.mIProfiles[1] = (IProfile) new ProfileSettingDrawerItem().withName(getString(R.string.home)).withIcon(R.drawable.icon_home).withIdentifier(DRAWER_CUSTOMER_HOME_IDENTIFIER);
                this.mIProfiles[2] = (IProfile) new ProfileSettingDrawerItem().withName(getString(R.string.dashboard_action_title)).withIcon(R.drawable.icon_dashboard).withIdentifier(DRAWER_CUSTOMER_DASHBOARD_IDENTIFIER);
                this.mIProfiles[3] = (IProfile) new ProfileSettingDrawerItem().withName(getString(R.string.myorder_action_title)).withIcon(R.drawable.icon_my_order).withIdentifier(DRAWER_CUSTOMER_ORDER_IDENTIFIER);
                this.mIProfiles[4] = (IProfile) new ProfileSettingDrawerItem().withName(getString(R.string.accountinfo_action_title)).withIcon(R.drawable.icon_information).withIdentifier(DRAWER_CUSTOMER_ACCOUNT_INFO_IDENTIFIER);
                this.mIProfiles[5] = (IProfile) new ProfileSettingDrawerItem().withName(getString(R.string.addrbook_action_title)).withIcon(R.drawable.icon_contact).withIdentifier(DRAWER_CUSTOMER_ADDRESS_BOOK_IDENTIFIER);
                this.mIProfiles[6] = (IProfile) new ProfileSettingDrawerItem().withName(getString(R.string.mydownloads_action_title)).withIcon(R.drawable.icon_download).withIdentifier(DRAWER_CUSTOMER_DOWNLOADS_IDENTIFIER);
                this.mIProfiles[7] = (IProfile) new ProfileSettingDrawerItem().withName(getString(R.string.newslettersubs_action_title)).withIcon(R.drawable.icon_newsletter).withIdentifier(DRAWER_CUSTOMER_NEWSLETTER_IDENTIFIER);
            } else {
                this.mIProfiles = new IProfile[1];
                this.currentProfile = (ProfileDrawerItem) new ProfileDrawerItem().withName("").withEmail(getString(R.string.login_or_register)).withIdentifier(101);
                this.mIProfiles[0] = this.currentProfile;
            }
            Log.d("DEBUG", "getIProfile:length " + this.mIProfiles.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.mIProfiles;
    }

    private void initDrawer(Bundle savedInstanceState) {
        try {
            if (mainObject.has("catBackdrop")) {
                catHasBD = true;
                catBD = mainObject.getJSONObject("catBackdrop");
            }
            categoriesJSONObject = this.mainObject.getJSONArray("categories");
            this.languagesJSONObject = this.mainObject.getJSONObject("languages").getJSONArray("languages");
            this.currenciesJSONObject = this.mainObject.getJSONObject("currencies").getJSONArray("currencies");

            //Initiate categories array
            IDrawerItem[] mCategoriesItem = new ProfileDrawerItem[0];
            if (!categoriesJSONObject.toString().equals("[]")) {
                noOfCategories = 0;
                for (int i = 0; i < categoriesJSONObject.length(); i++) {
                    if (categoriesJSONObject.getJSONObject(i).getInt("parent") == 0) {
                        noOfCategories++;
                    }
                }
                mCategoriesItem = new ProfileDrawerItem[this.noOfCategories];
                int k = 0;
                for (int i = 0; i < categoriesJSONObject.length(); i++) {
                    if (categoriesJSONObject.getJSONObject(i).getInt("parent") != 0)
                        continue;
                    String categoryThumbnailUrl = null;
                    if (!categoriesJSONObject.getJSONObject(i).getString("icon").equals("")) {
                        categoryThumbnailUrl = categoriesJSONObject.getJSONObject(i).getString("icon");
                    }

                    //If category has Icon set it, otherwise put the placeholder
                    if (categoryThumbnailUrl != null) {
                        mCategoriesItem[k] = (IDrawerItem) new ProfileDrawerItem()
                                .withName(Html.fromHtml(categoriesJSONObject.getJSONObject(i).getString("name")).toString())
                                .withIcon(categoryThumbnailUrl)
                                .withIdentifier((long) (i + DRAWER_CATEGORY_IDENTIFIER_START))
                                .withSelectable(false);
                    } else {
                        mCategoriesItem[k] = (IDrawerItem) new ProfileDrawerItem()
                                .withName(Html.fromHtml(categoriesJSONObject.getJSONObject(i).getString("name")).toString())
                                .withIcon(R.drawable.placeholder)
                                .withIdentifier((long) (i + DRAWER_CATEGORY_IDENTIFIER_START))
                                .withSelectable(false);
                    }
                    k++;
                } //end categories iterator
            } //End categories initiator

            this.mDrawer = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(this.toolbar)
                    .withHasStableIds(true)
                    .withItemAnimator(new AlphaCrossFadeAnimator())
                    .withAccountHeader(this.mAccountHeader)
                    .addDrawerItems(mCategoriesItem)
                    .withOnDrawerItemClickListener(this.myPrimaryOnDrawerItemClickListener)
                    .withSavedInstance(savedInstanceState)
                    .build();

            //Initiate Language and currency expandable drawer
            this.mDrawerItemAdapter = this.mDrawer.getItemAdapter();

            //Languages array
            CustomPrimaryDrawerItem[] mLanguageItem = new CustomPrimaryDrawerItem[this.languagesJSONObject.length()];
            for (int i = 0; i < this.languagesJSONObject.length(); i++) {
                mLanguageItem[i] = (CustomPrimaryDrawerItem) ((PrimaryDrawerItem) new CustomPrimaryDrawerItem()
                        .withName(this.languagesJSONObject.getJSONObject(i).getString("name"))
                        .withIdentifier((long) (i + DRAWER_LANG_IDENTIFIER))
                        .withTag(this.languagesJSONObject.getJSONObject(i).getString("code"))
                        .withSetSelected(false));
                if (this.languagesJSONObject.getJSONObject(i).getString("code").equals(this.configShared.getString("storeCode", ""))) {
                    mLanguageItem[i].withBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_gray_color1));
                }
            }

            //Currency array
            CustomPrimaryDrawerItem[] mCurrencyItem = new CustomPrimaryDrawerItem[this.currenciesJSONObject.length()];
            for (int i = 0; i < this.currenciesJSONObject.length(); i++) {
                mCurrencyItem[i] = (CustomPrimaryDrawerItem) ((PrimaryDrawerItem) ((PrimaryDrawerItem) ((PrimaryDrawerItem) new CustomPrimaryDrawerItem()
                        .withName(this.currenciesJSONObject.getJSONObject(i).getString("title")))
                        .withIdentifier((long) (i + DRAWER_CURRENCY_IDENTIFIER)))
                        .withTag(this.currenciesJSONObject.getJSONObject(i).getString("code")))
                        .withSetSelected(false);
                if (this.currenciesJSONObject.getJSONObject(i).getString("code").equals(this.configShared.getString("currencyCode", ""))) {
                    mCurrencyItem[i].withBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.light_gray_color1));
                }
            }
            this.mDrawerItemAdapter.add(
                    (IDrawerItem) new MyExpandableDrawerItem()
                            .withName(R.string.language)
                            .withIcon(new ImageHolder(R.drawable.ic_language))
                            .withSubItems((IDrawerItem[]) mLanguageItem)
            );
            this.mDrawerItemAdapter.add(
                    (IDrawerItem) new MyExpandableDrawerItem()
                            .withName(R.string.currency)
                            .withIcon(new ImageHolder(R.drawable.ic_currency))
                            .withSubItems((IDrawerItem[]) mCurrencyItem)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetData() {
        try {
            this.counterForChildAction = 0;
            this.listDataHeader.clear();
            this.listDataChild.clear();
            this.mainActions.clear();
            this.childActions.clear();
            this.featuredProductLayout.removeAllViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode == 0) {
            return true;
        }
        if (apiAvailability.isUserResolvableError(resultCode)) {
            apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST, new OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case AlertDialog.BUTTON_NEGATIVE:
                                    MainActivity.this.configShared.edit()
                                            .putBoolean("isUserRegisteredForGCM", false)
                                            .apply();
                                case AlertDialog.BUTTON_POSITIVE:
                                    dialog.dismiss();
                                default:
                            }
                        }
                    };
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Do you want to see previous warning message again!")
                            .setNegativeButton("Never See", dialogClickListener)
                            .setPositiveButton("Want to See", dialogClickListener)
                            .show();
                }
            }).show();
        } else {
            Log.i("MainActivity", "This device is not supported.");
            finish();
        }
        return false;
    }

    private void setDataFromExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("mainResponseAsString")) {
            this.mainResponseAsString = extras.getString("mainResponseAsString");
            try {
                this.mainObject = new JSONObject(this.mainResponseAsString);
                if (this.mainObject.has("cart")) {
                    getSharedPreferences("customerData", MODE_PRIVATE).edit()
                            .putString("cartItems", this.mainObject.getString("cart"))
                            .apply();
                    if (this.itemCart != null) {
                        Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", MODE_PRIVATE).getString("cartItems", "0"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getCatalogDataFromApi() {
        Log.d("getCatalogDataFromApi", "called");
        JSONObject jo;
        try {
            jo = new JSONObject().put("width", screen_width);
            new Connection(this).execute("getHomepage", jo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void home(View v) {
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    public void pageSwitcher(int seconds, int length) {
        this.swipeAtInterval = new RemindTask(length);
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(this.swipeAtInterval, 0, (long) (seconds * 1000));
    }

    public void pageSwitcher1(int seconds, int length) {
        this.swipeAtInterval1 = new RemindTask1(length);
        this.timer1 = new Timer();
        this.timer1.scheduleAtFixedRate(this.swipeAtInterval1, 0, (long) (seconds * 1000));
    }

    public int GetPixelFromDips(float pixels) {
        return (int) ((pixels * getResources().getDisplayMetrics().density) + 0.5f);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        SharedPreferences shared = getSharedPreferences("customerData", MODE_PRIVATE);
        Boolean isLoggedIn = shared.getBoolean("isLoggedIn", false);
        String isSeller = shared.getString("isSeller", "");
        MenuItem loginMenuItem = this.menu.findItem(R.id.login);
        MenuItem signupMenuItem = this.menu.findItem(R.id.signup);
        if (id != R.id.action_settings) {
            return super.onOptionsItemSelected(item);
        }
        if (isLoggedIn) {
            loginMenuItem.setTitle(getResources().getString(R.string.logout_title));
            signupMenuItem.setVisible(false);
            if (!isSeller.equalsIgnoreCase("1")) {
                return true;
            }
            this.menu.findItem(R.id.sellerDashboard).setVisible(true);
            this.menu.findItem(R.id.sellerOrder).setVisible(true);
            return true;
        }
        loginMenuItem.setTitle(getResources().getString(R.string.login_title));
        signupMenuItem.setVisible(true);
        this.menu.findItem(R.id.sellerDashboard).setVisible(false);
        this.menu.findItem(R.id.sellerOrder).setVisible(false);
        return true;
    }

    public void onLoginClick(View v) {
        startActivityForResult(new Intent(this, LoginActivity.class), 1);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    protected void loadMainUI() {
        try {
            categoriesJSONObject = this.mainObject.getJSONArray("categories");
            this.languagesJSONObject = this.mainObject.getJSONObject("languages").getJSONArray("languages");
            this.currenciesJSONObject = this.mainObject.getJSONObject("currencies").getJSONArray("currencies");
            Log.d("DEBUG", categoriesJSONObject.toString(4));
            //Log.d("DEBUG", categoriesJSONObject.getJSONObject(0).getJSONArray("children").length() + "");
            try {
                Log.d("mainObject", this.mainObject.toString(4) + "");
                if (!this.mainObject.getString("banner_status").equalsIgnoreCase("0")) {
                    loadBannerImages();
                }
                if (this.mainObject.getString("featured_status").equalsIgnoreCase("0")) {
                    findViewById(R.id.featured_product).setVisibility(View.GONE);
                } else {
                    loadFeaturedProduct();
                }
                if (this.mainObject.getString("latest_status").equalsIgnoreCase("0")) {
                    findViewById(R.id.latest_product).setVisibility(View.GONE);
                } else {
                    loadLatestProduct();
                }
                Log.d("modules", this.mainObject.getJSONObject("modules").toString(4) + "");
                if (this.mainObject.getJSONObject("modules").getJSONArray("carousel").length() == 0 || this.mainObject.getString("carousel_status").equalsIgnoreCase("0")) {
                    findViewById(R.id.browseByBrandsLayout).setVisibility(View.GONE);
                }
                loadCarouselImage(this.mainObject.getJSONObject("modules").getJSONArray("carousel"));
                this.configShared = getSharedPreferences("configureView", MODE_PRIVATE);
                this.configShared.edit()
                        .putBoolean("isMainCreated", true)
                        .apply();
                Log.d("howMany", getSharedPreferences("com.mallxpress.mobi", MODE_PRIVATE).getAll().size() + "");
                this.mainProgressBar.setVisibility(View.GONE);
                onTrimMemory(TRIM_MEMORY_UI_HIDDEN);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            trackException(e2, this);
        }
    }

    private void loadCarouselImage(JSONArray carouselImagesArray) {
        try {
            String[] imageUrls = new String[carouselImagesArray.length()];
            for (int i = 0; i < carouselImagesArray.length(); i++) {
                JSONObject carouselImagesObject = carouselImagesArray.getJSONObject(i);
                imageUrls[i] = carouselImagesObject.getString("image");
                ImageView im = new ImageView(this);
                im.setTag(carouselImagesObject.getString("link") + "#" + carouselImagesObject.getString("title"));
                im.setBackgroundResource(R.drawable.rect_shape);
                LayoutParams params = new LayoutParams(-2, -2);
                params.setMargins(5, 5, 5, 5);
                im.setLayoutParams(params);
                Picasso.with(this).load(imageUrls[i]).into(im);
                im.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        Intent intent;
                        String[] data = v.getTag().toString().split("#");
                        if (MainActivity.this.getSharedPreferences("categoryView", MODE_PRIVATE).getBoolean("isGridView", false)) {
                            intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewCategoryGrid());
                        } else {
                            intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewCategoryList());
                        }
                        intent.putExtra("manufacturer_id", data[0]);
                        intent.putExtra("imageTitle", data[1]);
                        intent.putExtra("drawerData", MainActivity.this.mainObject + "");
                        MainActivity.this.startActivity(intent);
                    }
                });
                ((LinearLayout) findViewById(R.id.carouselScroller)).addView(im);
            }
            ((HorizontalScrollView) findViewById(R.id.carouselScrollerView)).fullScroll(View.FOCUS_RIGHT);
            this.x = 0;
            this.y = screen_width + this.x;
            pageSwitcher1(4, imageUrls.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFeaturedProduct() {
        try {
            this.featuredProductsArr = new JSONArray(this.mainObject.getJSONObject("modules").getJSONArray("featured").toString());
            if (this.mainObject.getJSONObject("modules").has("featured")) {
                Log.d("loadFeaturedProduct", this.mainObject.getJSONObject("modules") + "");
            }
            if (this.featuredProductsArr.length() != 0) {
                this.featuredProductLayout.setTag("featuredProducts");
                setProducts(this.featuredProductsArr, this.featuredProductLayout);
            } else {
                findViewById(R.id.featured_product).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLatestProduct() {
        try {
            JSONArray latestProductsArr = this.mainObject.getJSONObject("latestProducts").getJSONArray("products");
            if (this.mainObject.has("latestProducts")) {
                Log.d("latestProducts-->", this.mainObject.getJSONObject("latestProducts").toString(4));
            }
            if (latestProductsArr.length() != 0) {
                this.latestProductLayout.setTag("latestProducts");
                setProducts(latestProductsArr, this.latestProductLayout);
            } else {
                findViewById(R.id.latest_product).setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBannerImages() {
        try {
            if (!this.mainObject.has("banners") || this.mainObject.getJSONArray("banners").length() == 0) {
                findViewById(R.id.pagerLayout).setVisibility(View.GONE);
                return;
            }
            JSONArray bannerImagesArr = this.mainObject.getJSONArray("banners");
            this.imageUrls = new String[bannerImagesArr.length()];
            this.bannerType = new String[bannerImagesArr.length()];
            this.nameOfBanner = new String[bannerImagesArr.length()];
            this.bannerId = new String[bannerImagesArr.length()];
            for (int i = 0; i < bannerImagesArr.length(); i++) {
                this.imageUrls[i] = bannerImagesArr.getJSONObject(i).getString("image");
                this.bannerType[i] = bannerImagesArr.getJSONObject(i).getString("type");
                this.nameOfBanner[i] = bannerImagesArr.getJSONObject(i).getString("title");
                this.bannerId[i] = bannerImagesArr.getJSONObject(i).getString("link");
            }
            this.pager = (ViewPager) findViewById(R.id.banner_pager);
            this.pager.setAdapter(new ImagePagerAdapter(this.imageUrls));
            this.pager.setLayoutParams(new LayoutParams(screen_width, screen_width / 2));
            this.listener = new DetailOnPageChangeListener();
            this.pager.setOnPageChangeListener(this.listener);
            ViewGroup group = (ViewGroup) findViewById(R.id.dot_group);
            this.dotList = new ImageView[this.imageUrls.length];
            this.firstTime = true;
            pageSwitcher(5, bannerImagesArr.length());
            for (int i = 0; i < this.imageUrls.length; i++) {
                this.dotImage = new ImageView(this);
                this.dotList[i] = this.dotImage;
                if (i == 0) {
                    this.dotList[i].setBackgroundResource(R.drawable.dot_icon_focused);
                } else {
                    this.dotList[i].setBackgroundResource(R.drawable.dot_icon_unfocused);
                }
                LayoutParams params = new LayoutParams(10, 10);
                params.setMargins(10, 0, 10, 0);
                group.addView(this.dotImage, params);
            }
        } catch (Exception e) {
            Log.d("Exception in banner", e.getMessage());
            e.printStackTrace();
        }
    }

    public void SlideToAbove(View v) {
        final View v1 = v;
        this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).setVisibility(View.VISIBLE);
        Animation slide = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 5.2f, 1, 0.0f);
        slide.setDuration(400);
        slide.setFillEnabled(true);
        slide.setFillAfter(true);
        this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).startAnimation(slide);
        slide.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                MainActivity.this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).clearAnimation();
                MainActivity.this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).setVisibility(View.VISIBLE);
            }
        });
    }

    public void SlideToDown(View v) {
        final View v1 = v;
        this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).setVisibility(View.VISIBLE);
        Animation slide = new TranslateAnimation(1, 0.0f, 1, 0.0f, 1, 0.0f, 1, 5.2f);
        slide.setDuration(400);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).startAnimation(slide);
        slide.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                MainActivity.this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).clearAnimation();
                MainActivity.this.featuredProductLayout.findViewWithTag(v1.getTag()).findViewById(R.id.trans).setVisibility(View.GONE);
            }
        });
    }

    public void onUserInteraction() {
        if (this.itemBell != null) {
            this.notificationShared = getSharedPreferences("com.mallxpress.mobi.notification", MODE_MULTI_PROCESS);
            Utils.setBadgeCount(this, (LayerDrawable) this.itemBell.getIcon(), ((HashSet) this.notificationShared.getStringSet("unreadNotifications", new HashSet())).size() + "");
        }
    }

    public void onResume() {
        if (this.itemBell != null) {
            this.notificationShared = getSharedPreferences("com.mallxpress.mobi.notification", MODE_MULTI_PROCESS);
            Utils.setBadgeCount(this, (LayerDrawable) this.itemBell.getIcon(), ((HashSet) this.notificationShared.getStringSet("unreadNotifications", new HashSet())).size() + "" + "");
        }
        if (this.itemCart != null) {
            Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", 0).getString("cartItems", "0"));
        }
        if (this.mDrawer != null) {
            this.mAccountHeader.clear();
            this.mAccountHeader.addProfiles(getIProfile());
            addingCustomChangesToRemoveEmailListFromDrawer(null);
        }
        this.configShared = getSharedPreferences("configureView", MODE_PRIVATE);
        this.configShared.edit().putBoolean("isMainCreated", true).apply();
        super.onResume();
    }

    private void setProducts(JSONArray productArray, LinearLayout parentView) {
        try {
            int gape = (int) (10.0f * getResources().getDisplayMetrics().density);
            int padding = (int) (5.0f * getResources().getDisplayMetrics().density);
            if (productArray.length() != 0) {
                for (int i = 0; i < productArray.length(); i++) {
                    String tagString;
                    View child = getLayoutInflater().inflate(R.layout.gridhome, null);
                    RelativeLayout productImageLayout = (RelativeLayout) child.findViewById(R.id.productImageLayout);
                    RelativeLayout.LayoutParams productImageLayoutparams = (RelativeLayout.LayoutParams) productImageLayout.getLayoutParams();
                    productImageLayoutparams.width = (int) (((double) screen_width) / 2.3d);
                    productImageLayoutparams.height = (int) (((double) screen_width) / 2.3d);
                    productImageLayout.setLayoutParams(productImageLayoutparams);
                    child.setTag(i);
                    parentView.addView(child);
                    if (i < productArray.length() - 1) {
                        View view = new View(this);
                        view.setLayoutParams(new LayoutParams(gape, -1));
                        view.setBackgroundColor(0);
                        parentView.addView(view);
                    }
                    ((RelativeLayout) child.findViewById(R.id.relative)).setPadding(padding, 0, padding, padding);
                    ImageView addToCart = (ImageView) child.findViewById(R.id.addToCart);
                    ImageView wishlist = (ImageView) child.findViewById(R.id.wishlist);
                    JSONObject eachProduct = productArray.getJSONObject(i);
                    if (eachProduct.has("hasOption")) {
                        if ("true".equalsIgnoreCase(eachProduct.getString("hasOption"))) {
                            tagString = i + "/" + 1;
                            addToCart.setImageResource(R.drawable.home_view_product);
                            addToCart.setTag(tagString);
                        } else {
                            addToCart.setTag(i + "/" + 0);
                            addToCart.setImageResource(R.drawable.home_add_to_cart);
                        }
                    }
                    final JSONArray jSONArray = productArray;
                    addToCart.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            String[] indexFlagPair = ((String) v.getTag()).split("/");
                            int position = Integer.parseInt(indexFlagPair[0]);
                            if (indexFlagPair[1].equals("0")) {
                                MainActivity.this.progressdialog = ProgressDialog.show(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_wait), MainActivity.this.getResources().getString(R.string.processing_request_response), true);
                                MainActivity.this.progressdialog.setCanceledOnTouchOutside(false);
                                JSONObject jo = new JSONObject();
                                try {
                                    jo.put("product_id", jSONArray.getJSONObject(position).getString("product_id"));
                                    jo.put("quantity", "1");
                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                                new Connection(MainActivity.this).execute("addToCart", jo.toString());
                                return;
                            }
                            Intent intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewProductSimple());
                            try {
                                intent.putExtra("idOfProduct", jSONArray.getJSONObject(position).getString("product_id"));
                                intent.putExtra("nameOfProduct", jSONArray.getJSONObject(position).getString("name"));
                            } catch (NumberFormatException | JSONException e3) {
                                e3.printStackTrace();
                                MainActivity.this.startActivity(intent);
                            }
                            MainActivity.this.startActivity(intent);
                        }
                    });
                    wishlist.setTag(eachProduct.getString("product_id"));
                    wishlist.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            String productId = (String) v.getTag();
                            if (MainActivity.this.getSharedPreferences("customerData", MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
                                MainActivity.progress = ProgressDialog.show(MainActivity.this, MainActivity.this.getResources().getString(R.string.please_wait), MainActivity.this.getResources().getString(R.string.processing_request_response), true);
                                MainActivity.progress.setCanceledOnTouchOutside(false);
                                JSONObject jo = new JSONObject();
                                try {
                                    jo.put("product_id", productId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new Connection(MainActivity.this).execute("addToWishlist", jo.toString());
                                return;
                            }
                            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                            alert.setNegativeButton(MainActivity.this.getResources().getString(R.string._ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setMessage(MainActivity.this.getResources().getString(R.string.wishlist_msg)).show();
                        }
                    });
                    child.findViewById(R.id.btnLayout).setOnClickListener(new OnClickListener() {
                        public void onClick(View view) {
                            if (MainActivity.isBottom) {
                                MainActivity.this.SlideToAbove(view);
                                MainActivity.isBottom = false;
                                return;
                            }
                            MainActivity.this.SlideToDown(view);
                            MainActivity.isBottom = true;
                        }
                    });
                    this.productImage = (ImageView) child.findViewById(R.id.productImage);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (((double) screen_width) / 2.3d), (int) (((double) screen_width) / 2.3d));
                    params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    this.productImage.setLayoutParams(params);
                    tagString = i + "/" + 0;
                    this.productImage.setTag(tagString);
                    this.productImage.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            String[] indexFlagPair = ((String) v.getTag()).split("/");
                            Intent intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewProductSimple());
                            try {
                                intent.putExtra("idOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("product_id"));
                                intent.putExtra("nameOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("name"));
                            } catch (NumberFormatException | JSONException e2) {
                                e2.printStackTrace();
                                MainActivity.this.startActivity(intent);
                            }
                            MainActivity.this.startActivity(intent);
                        }
                    });
                    this.productName = (TextView) child.findViewById(R.id.productName);
                    this.productName.setTag(tagString);
                    this.productName.setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            String[] indexFlagPair = ((String) v.getTag()).split("/");
                            Intent intent = new Intent(MainActivity.this, MainActivity.this.mMallApplication.viewProductSimple());
                            try {
                                intent.putExtra("idOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("product_id"));
                                intent.putExtra("nameOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("name"));
                            } catch (NumberFormatException | JSONException e2) {
                                e2.printStackTrace();
                                MainActivity.this.startActivity(intent);
                            }
                            MainActivity.this.startActivity(intent);
                        }
                    });
                    this.model = (TextView) child.findViewById(R.id.model);
                    if (eachProduct.has("model")) {
                        this.model.setText(eachProduct.getString("model"));
                    } else {
                        this.model.setVisibility(View.GONE);
                    }
                    this.newPrice = (TextView) child.findViewById(R.id.newPrice);
                    this.price = (TextView) child.findViewById(R.id.price);
                    this.price.setVisibility(View.GONE);
                    this.ratingBar = (RatingBar) child.findViewById(R.id.ratingBar);
                    this.newPrice.setSingleLine(false);
                    this.productName.setText(Html.fromHtml(eachProduct.getString("name")));
                    if (eachProduct.getString("special").equalsIgnoreCase("false")) {
                        this.newPrice.setText(eachProduct.getString("price"));
                        child.findViewById(R.id.sale).setVisibility(View.GONE);
                    } else {
                        this.newPrice.setText(Html.fromHtml("<u>" + eachProduct.getString("special") + "</u>"));
                        this.newPrice.setTextColor(ContextCompat.getColor(this, R.color.special_price_color));
                        this.price.setText(eachProduct.getString("price"));
                        this.price.setPaintFlags(this.price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        this.price.setTextSize(11.5f);
                        this.price.setVisibility(View.VISIBLE);
                        child.findViewById(R.id.sale).setVisibility(View.VISIBLE);
                        if (eachProduct.has("discount")) {

                            //Implement the discount tag! in php
                            ((TextView) child.findViewById(R.id.sale)).setText(eachProduct.getString("discount"));
                        }
                    }
                    Log.d("featured_name", eachProduct.getString("name") + "");
                    Log.d("featured_image", eachProduct.getString("thumb") + "");
                    Picasso.with(this).load(eachProduct.getString("thumb")).placeholder(R.drawable.placeholder).into(this.productImage);
                }
                return;
            }
            findViewById(R.id.featured_product).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //API RESPONSES METHODS

    public void getHomepageResponse(String backresult) {
        try {
            this.mainObject = new JSONObject(backresult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadMainUI();
    }

    public void addToCartResponse(String backresult) {
        try {
            JSONObject CheckoutAddtoCartobject = new JSONObject(backresult);
            this.progressdialog.dismiss();
            if (CheckoutAddtoCartobject.getString("error").equalsIgnoreCase("0")) {
                Toast.makeText(getApplicationContext(), Html.fromHtml(CheckoutAddtoCartobject.getString("message")), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Can't be added", Toast.LENGTH_SHORT).show();
            }
            String totalCount = CheckoutAddtoCartobject.getString("total");
            totalCount = totalCount.substring(0, totalCount.indexOf(" "));
            getSharedPreferences("customerData", MODE_PRIVATE).edit().putString("cartItems", totalCount).apply();
            Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), totalCount);
        } catch (JSONException e) {
            Log.d("Exception add to cart", e.toString());
            e.printStackTrace();
        }
    }

    public void addToWishlistResponse(String backresult) {
        try {
            JSONObject mainObject = new JSONObject(backresult);
            progress.dismiss();
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            if (mainObject.getString("error").equalsIgnoreCase("0")) {
                alert.setTitle(getResources().getString(R.string.Success));
            } else {
                alert.setTitle(getResources().getString(R.string.Error));
            }
            alert.setNegativeButton(getResources().getString(R.string._ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            if (mainObject.has("info")) {
                alert.setMessage(Html.fromHtml(mainObject.getString("info"))).show();
            } else {
                alert.setMessage(Html.fromHtml(mainObject.getString("message"))).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void languageResponse(String backresult) {
        JSONObject response;
        try {
            response = new JSONObject(backresult);
            if (response.getInt("error") == 0) {
                try {
                    Intent intent = new Intent(getBaseContext(), SplashScreen.class);
                    //intent.putExtra("CreateNewCategoryList", "");
                    finish();
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void currencyResponse(String backresult) {
        JSONObject response;
        try {
            response = new JSONObject(backresult);
            if (response.getInt("error") == 0) {
                try {
                    Intent intent = new Intent(getBaseContext(), SplashScreen.class);
                    //intent.putExtra("CreateNewCategoryList", "");
                    finish();
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
