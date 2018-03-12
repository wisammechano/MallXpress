package com.edenxpress.mobi;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ViewProductSimple extends BaseActivity {

    private static final int REQUEST_GET_DIR = 2;
    private static final int REQUEST_GET_SINGLE_FILE = 0;
    public static Dialog dialog;
    static int fileBtnId = 0;
    static int fileTvId = 0;
    private static boolean isBottom = true;
    protected static JSONObject mainObject;
    public static int productId;
    public static ProgressDialog progress;
    static ArrayList<ResultData> resultDataList;
    public Handler alertHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    OnClickListener dialogClickListener = new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    ViewProductSimple.this.startActivity(new Intent(ViewProductSimple.this, LoginActivity.class));
                                    return;
                                default:
                            }
                        }
                    };
                    new Builder(ViewProductSimple.this).setMessage(R.string.you_are_not_logged_in_please_login_to_continue).setNegativeButton(ViewProductSimple.this.getResources().getString(R.string._cancel), dialogClickListener).setPositiveButton(ViewProductSimple.this.getResources().getString(R.string.login), dialogClickListener).show();
                    return;
                default:
            }
        }
    };
    int numOfTabs = 3;
    CharSequence[] titles = new CharSequence[]{" DESCRIPTION", " FEATURES", "  REVIEWS"};
    private boolean active = true;
    TabsAdapter adapter;
    Boolean canShowReqDialog;
    ArrayList<CustomOptionData.MultiOptionData> checkBoxList = new ArrayList();
    HashMap<String, String> checkedList = new HashMap();
    private SharedPreferences configShared;
    private String currentRating;
    JSONArray customOptionsArr;
    final ArrayList<CustomOptionData> customOptnList = new ArrayList();
    String[] dateAndTime;
    private ImageView dotImage;
    private ImageView[] dotList;
    private int duration = 0;
    public Editor editor;
    private Bundle extras;
    JSONArray fileJSONArr;
    boolean flag = false;
    int hourCurrent = 0;
    int hourCurrent2 = 0;
    String[] imageUrls;
    JSONObject inputByUser = new JSONObject();
    Boolean isAllReqOptnFilled = true;
    boolean isOkayClicked;
    private int lastExpandedPosition = -1;
    private RelativeLayout layoutContainer;
    ProductDescExpListAdapter listAdapter;
    HashMap<String, List<String>> listDataChild;
    List<String> listDataHeader;
    DetailOnPageChangeListener listener;
    Object mainResponse = null;
    int minuteCurrent = 0;
    int minuteCurrent2 = 0;
    public TextView model;
    public TextView newPrice;
    JSONObject optionsObj = new JSONObject();
    private ViewPager pager;
    NonSwipeableViewPager pager1;
    public TextView price;
    ProductData productData;
    private ImageView productImage;
    JSONArray productImageArr;
    public TextView productName;
    protected int productOptionValueId;
    TextView productPrice;
    private String qtyOfproduct = "1";
    protected int radioProductOptionValueId;
    public RatingBar ratingBar;
    Object response = null;
    protected int screen_width;
    private ArrayList<Integer> spinnerIdList;
    private ArrayList<CustomOptionData.MultiOptionData> spinnerList;
    private ScrollView sv;
    JSONArray tierPrices;
    private Toolbar toolbar;
    private String url;


    public class DetailOnPageChangeListener implements OnPageChangeListener {
        private int currentPage;

        public void onPageSelected(int position) {
            this.currentPage = position;
            for (int i = 0; i < ViewProductSimple.this.imageUrls.length; i++) {
                if (i == position) {
                    ViewProductSimple.this.dotList[i].setBackgroundResource(R.drawable.dot_icon_focused);
                } else {
                    ViewProductSimple.this.dotList[i].setBackgroundResource(R.drawable.dot_icon_unfocused);
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

    private class ImagePagerAdapterSimple extends PagerAdapter {
        private String[] images;
        private LayoutInflater inflater;

        ImagePagerAdapterSimple(String[] images) {
            this.images = images;
            this.inflater = ViewProductSimple.this.getLayoutInflater();
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        public void finishUpdate(View container) {
        }

        public int getCount() {
            return this.images.length;
        }

        public Object instantiateItem(View view, int position) {
            View imageLayout = this.inflater.inflate(R.layout.item_view_pager_banner, null);
            ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
            Log.d("productImageArr", ViewProductSimple.this.productImageArr.toString());
            try {
                if (!ViewProductSimple.this.productImageArr.getJSONObject(0).getString("popup").equalsIgnoreCase("")) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(ViewProductSimple.this.getBaseContext(), ViewPagerExampleActivity.class);
                            try {
                                intent.putExtra("imageUrl", ViewProductSimple.this.productImageArr.getJSONObject(0).getString("popup"));
                                intent.putExtra("productName", ViewProductSimple.mainObject.getString("name"));
                                intent.putExtra("productImageArr", ViewProductSimple.this.productImageArr.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ViewProductSimple.this.startActivity(intent);
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("images", this.images[position] + "------aaaaaa");
            if (this.images[position].equalsIgnoreCase("")) {
                Picasso.with(ViewProductSimple.this).load(R.drawable.placeholder).into(imageView);
            } else {
                Picasso.with(ViewProductSimple.this).load(this.images[position].replace(" ", "%20")).into(imageView);
            }
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

    protected void onResume() {
        if (this.itemCart != null) {
            try {
                Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", 0).getString("cartItems", "0"));
            } catch (Exception e) {
            }
        }
        super.onResume();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == android.R.id.home) {
            this.configShared = getSharedPreferences("configureView", MODE_PRIVATE);
            if (this.configShared.getBoolean("isMainCreated", false)) {
                onBackPressed();
            } else {
                Intent i = new Intent(this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        }
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        if (this.extras.containsKey("isNotification")) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    public void shareProduct(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, this.url);
        sendIntent.setType("text/plain");
        if (VERSION.SDK_INT >= 22) {
            startActivity(Intent.createChooser(sendIntent, "Choose an Action:", null));
        } else {
            startActivity(sendIntent);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.extras = getIntent().getExtras();
        if (this.extras != null) {
            productId = Integer.parseInt(this.extras.getString("idOfProduct"));
            Log.d("title", this.extras.getString("nameOfProduct") + "");
            setTitle(Html.fromHtml(this.extras.getString("nameOfProduct")));
            Log.d("value at on create", "" + productId);
        }
        isOnline();
        if (this.isInternetAvailable) {
            setContentView(R.layout.activity_view_product_simple);
            this.toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(this.toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            this.productPrice = (TextView) findViewById(R.id.product_price);
            this.listDataHeader = new ArrayList();
            this.listDataChild = new HashMap();
            this.dateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime()).split(" ");
            try {
                this.inputByUser.put("options", this.optionsObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            this.listAdapter = new ProductDescExpListAdapter(this, this.listDataHeader, this.listDataChild);
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            this.screen_width = size.x;
            this.fileJSONArr = new JSONArray();
            this.layoutContainer = (RelativeLayout) findViewById(R.id.layoutContainer);
            this.layoutContainer.setVisibility(View.GONE);
            if (!this.flag) {
                JSONObject jo = new JSONObject();
                try {
                    jo.put("width", this.screen_width);
                    jo.put("product_id", productId);
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                new Connection(this).execute("getProduct", jo.toString());
                this.flag = true;
                return;
            }
            return;
        }
        showRetryDialog(this);
    }

    public void changeProductsLargeImage(View v) {
        final View view = (View) v.getParent().getParent();
        try {
            ImageView productImageView = (ImageView) findViewById(R.id.product_image_view);
            Picasso.with(this).load(this.productImageArr.getJSONObject((Integer) view.getTag()).getString("popup")).into(productImageView);
            Log.d("productImageArr", this.productImageArr.toString());
            productImageView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(ViewProductSimple.this.getBaseContext(), ViewPagerExampleActivity.class);
                    try {
                        intent.putExtra("imageUrl", ViewProductSimple.this.productImageArr.getJSONObject((Integer) view.getTag()).getString("popup"));
                        intent.putExtra("productName", ViewProductSimple.mainObject.getString("name"));
                        intent.putExtra("productImageArr", ViewProductSimple.this.productImageArr.toString());
                        intent.putExtra("imageSelection", (Integer) view.getTag());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ViewProductSimple.this.startActivity(intent);
                }
            });
        } catch (JSONException e) {
            Log.d("on click image button", e.getMessage());
            e.printStackTrace();
        }
    }

    public void onClickAddToCartBtn(View v) {
        try {
            this.canShowReqDialog = true;
            this.isAllReqOptnFilled = true;
            this.qtyOfproduct = ((TextView) findViewById(R.id.product_qty)).getText().toString();
            int noOfViews = 0;
            while (noOfViews < this.customOptnList.size()) {
                EditText et;
                Builder dlgAlert;
                if (((CustomOptionData) this.customOptnList.get(noOfViews)).getType().equals("time") || ((CustomOptionData) this.customOptnList.get(noOfViews)).getType().equals("date")) {
                    et = (EditText) findViewById(((CustomOptionData) this.customOptnList.get(noOfViews)).getId());
                    if (et.getText().toString().isEmpty()) {
                        if (((CustomOptionData) this.customOptnList.get(noOfViews)).getIsRequired() == 1) {
                            this.isAllReqOptnFilled = false;
                            if (this.canShowReqDialog) {
                                dlgAlert = new Builder(this);
                                dlgAlert.setMessage(Html.fromHtml(getResources().getString(R.string.field_) + "<b>" + this.customOptnList.get(noOfViews).getTitle() + "</b>" + getResources().getString(R.string._is_not_complete_)));
                                dlgAlert.setPositiveButton(getResources().getString(R.string._ok), null);
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                                this.canShowReqDialog = false;
                            }
                        }
                    } else if (this.customOptnList.get(noOfViews).getType().equals("date")) {
                        this.optionsObj.put("" + this.customOptnList.get(noOfViews).getProductOptionId(), et.getText().toString());
                    } else {
                        this.optionsObj.put("" + this.customOptnList.get(noOfViews).getProductOptionId(), et.getText().toString());
                    }
                }
                if (this.customOptnList.get(noOfViews).getType().equals("datetime")) {
                    EditText etDate = (EditText) findViewById(this.customOptnList.get(noOfViews).getId());
                    EditText etTime = (EditText) findViewById(this.customOptnList.get(noOfViews).getAssociatedId());
                    JSONObject temp = new JSONObject();
                    if (!etDate.getText().toString().isEmpty() && !etTime.getText().toString().isEmpty()) {
                        this.optionsObj.put("" + this.customOptnList.get(noOfViews).getProductOptionId(), etDate.getText().toString() + " " + etTime.getText().toString());
                    } else if (this.customOptnList.get(noOfViews).getIsRequired() == 1) {
                        this.isAllReqOptnFilled = false;
                        if (this.canShowReqDialog) {
                            dlgAlert = new Builder(this);
                            dlgAlert.setMessage(Html.fromHtml(getResources().getString(R.string.field_) + "<b>" + this.customOptnList.get(noOfViews).getTitle() + "</b>" + getResources().getString(R.string._is_not_complete_)));
                            dlgAlert.setPositiveButton(getResources().getString(R.string._ok), null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            this.canShowReqDialog = false;
                        }
                    } else if ((!etDate.getText().toString().isEmpty() && etTime.getText().toString().isEmpty()) || (etDate.getText().toString().isEmpty() && !etTime.getText().toString().isEmpty())) {
                        this.isAllReqOptnFilled = false;
                        if (this.canShowReqDialog) {
                            dlgAlert = new Builder(this);
                            dlgAlert.setMessage(Html.fromHtml(getResources().getString(R.string.field_) + "<b>" + this.customOptnList.get(noOfViews).getTitle() + "</b>" + R.string._is_not_complete_));
                            dlgAlert.setPositiveButton(getResources().getString(R.string._ok), null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            this.canShowReqDialog = false;
                        }
                    }
                }
                if (this.customOptnList.get(noOfViews).getType().equals("textarea") || this.customOptnList.get(noOfViews).getType().equals("text")) {
                    et = (EditText) findViewById(this.customOptnList.get(noOfViews).getId());
                    this.optionsObj.put("" + this.customOptnList.get(noOfViews).getProductOptionId(), et.getText().toString());
                    if (this.customOptnList.get(noOfViews).getIsRequired() == 1 && et.getText().toString().isEmpty()) {
                        this.isAllReqOptnFilled = false;
                        if (this.canShowReqDialog) {
                            dlgAlert = new Builder(this);
                            dlgAlert.setMessage(Html.fromHtml(getResources().getString(R.string.field_) + "<b>" + ((CustomOptionData) this.customOptnList.get(noOfViews)).getTitle() + "</b>" + getResources().getString(R.string._is_not_complete_)));
                            dlgAlert.setPositiveButton(getResources().getString(R.string._ok), null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            this.canShowReqDialog = false;
                        }
                    }
                }
                if (this.customOptnList.get(noOfViews).getType().equals("select")) {
                    if (((Spinner) findViewById(this.customOptnList.get(noOfViews).getId())).getSelectedItemPosition() != 0) {
                        this.optionsObj.put("" + this.customOptnList.get(noOfViews).getProductOptionId(), "" + this.productOptionValueId);
                    } else if (this.customOptnList.get(noOfViews).getIsRequired() == 1) {
                        this.isAllReqOptnFilled = false;
                        if (this.canShowReqDialog) {
                            dlgAlert = new Builder(this);
                            dlgAlert.setMessage(Html.fromHtml(getResources().getString(R.string.field_) + "<b>" + this.customOptnList.get(noOfViews).getTitle() + "</b>" + getResources().getString(R.string._is_not_complete_)));
                            dlgAlert.setPositiveButton(getResources().getString(R.string._ok), null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            this.canShowReqDialog = false;
                        }
                    }
                }
                if (this.customOptnList.get(noOfViews).getType().equals("radio")) {
                    RadioGroup radioGroup = (RadioGroup) findViewById(this.customOptnList.get(noOfViews).getId());
                    if (radioGroup.indexOfChild(findViewById(radioGroup.getCheckedRadioButtonId())) != -1) {
                        this.optionsObj.put("" + this.customOptnList.get(noOfViews).getProductOptionId(), "" + this.radioProductOptionValueId);
                    } else if (this.customOptnList.get(noOfViews).getIsRequired() == 1) {
                        this.isAllReqOptnFilled = false;
                        if (this.canShowReqDialog) {
                            dlgAlert = new Builder(this);
                            dlgAlert.setMessage(Html.fromHtml(getResources().getString(R.string.field_) + "<b>" + this.customOptnList.get(noOfViews).getTitle() + "</b>" + getResources().getString(R.string._is_not_complete_)));
                            dlgAlert.setPositiveButton(getResources().getString(R.string._ok), null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            this.canShowReqDialog = false;
                        }
                    }
                }
                if (this.customOptnList.get(noOfViews).getType().equals("checkbox") || this.customOptnList.get(noOfViews).getType().equals("multiple")) {
                    if (this.customOptnList.get(noOfViews).getIsRequired() == 1) {
                        JSONObject optionIdArray = new JSONObject();
                        if (this.checkedList.size() != 0) {
                            Object[] keys = this.checkedList.keySet().toArray();
                            for (int i = 0; i < keys.length; i++) {
                                optionIdArray.put("" + i, this.checkedList.get(keys[i]));
                            }
                        }
                        this.optionsObj.put("" + this.customOptnList.get(noOfViews).getProductOptionId(), optionIdArray);
                    } else {
                        this.isAllReqOptnFilled = false;
                        if (this.canShowReqDialog) {
                            dlgAlert = new Builder(this);
                            dlgAlert.setMessage(Html.fromHtml(getResources().getString(R.string.field_) + "<b>" + this.customOptnList.get(noOfViews).getTitle() + "</b>" + getResources().getString(R.string._is_not_complete_)));
                            dlgAlert.setPositiveButton(getResources().getString(R.string._ok), null);
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                            this.canShowReqDialog = false;
                        }
                    }
                }
                noOfViews++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("Exception", e.getMessage());
        }
        if (this.isAllReqOptnFilled) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("product_id", productId);
                jo.put("quantity", this.qtyOfproduct);
                jo.put("option", this.optionsObj);
                Log.d("options", this.optionsObj + "");
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
            new Connection(this).execute("addToCart", jo.toString());
            this.flag = true;
            progress = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.processing_request_response), true);
            progress.setCanceledOnTouchOutside(false);
        }
    }

    public void getProductResponse(String backresult) {
        this.spinnerIdList = new ArrayList();
        resultDataList = new ArrayList();
        addSoldByLayout();
        try {
            View textView;
            mainObject = new JSONObject(backresult);
            setCartbadge();
            Log.d("mp_info", mainObject.getString("mp_info") + "");
            if (mainObject.has("mp_info")) {
                addSellerInfo();
            }
            if (mainObject.has("minimum")) {
                ((TextView) findViewById(R.id.product_qty)).setText(mainObject.getString("minimum"));
            }
            this.productImageArr = new JSONArray(mainObject.getJSONArray("images").toString());
            TextView brand = (TextView) findViewById(R.id.brand);
            TextView product_code = (TextView) findViewById(R.id.product_code);
            TextView reward_points = (TextView) findViewById(R.id.reward_points);
            TextView availability = (TextView) findViewById(R.id.product_availability);
            TextView tax = (TextView) findViewById(R.id.tax);
            TextView price_in_reward_points = (TextView) findViewById(R.id.price_in_reward_points);
            ((TextView) findViewById(R.id.product_name)).setText(Html.fromHtml(mainObject.getString("name")));
            if (!mainObject.has("relatedProducts") || mainObject.getString("relatedProducts").equalsIgnoreCase("[]")) {
                findViewById(R.id.related_product_tv).setVisibility(8);
            } else {
                Log.d("DEBUG related products", mainObject.getString("relatedProducts") + "");
                setSimilarProduct(mainObject.getJSONArray("relatedProducts"), (LinearLayout) findViewById(R.id.related_Product_ll));
            }
            if (mainObject.has("href")) {
                this.url = Html.fromHtml(mainObject.getString("href")).toString();
            }
            Log.d("url", this.url + "");
            brand.setTag(mainObject.getString("manufacturer_id"));
            if (mainObject.getString("manufacturer").equalsIgnoreCase("null")) {
                brand.setVisibility(8);
            }
            brand.setText(getResources().getString(R.string.brand_) + mainObject.getString("manufacturer"));
            product_code.setText(getResources().getString(R.string.product_code_) + mainObject.getString("model"));
            if (mainObject.getString("reward").equalsIgnoreCase("null")) {
                reward_points.setVisibility(8);
            }
            reward_points.setText(getResources().getString(R.string.reward_points_) + mainObject.getString("reward"));
            availability.setText(getResources().getString(R.string.availability_) + mainObject.getString("stock"));
            tax.setText(getResources().getString(R.string.ex_tax_) + mainObject.getString("tax"));
            price_in_reward_points.setText(getResources().getString(R.string.price_in_reward_points_) + mainObject.getString("points"));
            if (mainObject.getString("special").equalsIgnoreCase("false")) {
                findViewById(R.id.special_product_price).setVisibility(8);
                findViewById(R.id.special_product_price_save).setVisibility(8);
                this.productPrice.setText(mainObject.getString("price"));
            } else {
                ((TextView) findViewById(R.id.special_product_price)).setText(mainObject.getString("price"));
                ((TextView) findViewById(R.id.special_product_price)).setPaintFlags(((TextView) findViewById(R.id.product_price)).getPaintFlags() | 16);
                findViewById(R.id.special_product_price).setVisibility(0);
                this.productPrice.setText(mainObject.getString("special"));
            }
            ((TextView) findViewById(R.id.next_product)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        JSONObject joNextP = ViewProductSimple.mainObject.getJSONObject("productNext");
                        Intent i = new Intent(ViewProductSimple.this, ViewProductSimple.class);
                        i.putExtra("idOfProduct", joNextP.get("product_id") + "");
                        i.putExtra("nameOfProduct", joNextP.get("name") + "");
                        ViewProductSimple.this.startActivity(i);
                        ViewProductSimple.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            ((TextView) findViewById(R.id.previous_product)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        JSONObject joPrevP = ViewProductSimple.mainObject.getJSONObject("productPrev");
                        Log.d("productPrev=", ViewProductSimple.mainObject.getString("productPrev") + "");
                        Intent i = new Intent(ViewProductSimple.this, ViewProductSimple.class);
                        i.putExtra("idOfProduct", joPrevP.get("product_id") + "");
                        i.putExtra("nameOfProduct", joPrevP.get("name") + "");
                        ViewProductSimple.this.startActivity(i);
                        ViewProductSimple.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            ((TextView) findViewById(R.id.noOfReviewTV)).setText(mainObject.getString("reviews"));
            RatingBar ratingBar = (RatingBar) findViewById(R.id.productRating);
            Log.d("rating", mainObject.get("rating") + "rating");
            if (mainObject.has("rating")) {
                ratingBar.setRating((float) ((Integer) mainObject.get("rating")).intValue());
            } else {
                Log.d("rating", mainObject.get("rating") + "rating");
            }
            ((LayerDrawable) ratingBar.getProgressDrawable()).getDrawable(2).setColorFilter(getResources().getColor(R.color.filter_color_selected), Mode.SRC_ATOP);
            loadBannerImages();
            if (mainObject.has("tierPrices") && mainObject.getJSONArray("tierPrices").length() != 0) {
                findViewById(R.id.tierPricesLinearLayoutStyle).setVisibility(0);
                LinearLayout tierPricesLinearLayout = (LinearLayout) findViewById(R.id.tierPricesLinearLayout);
                if (!mainObject.getJSONArray("tierPrices").toString().equals("[]")) {
                    this.tierPrices = new JSONArray(mainObject.getJSONArray("tierPrices").toString());
                    for (int noOfTierPrices = 0; noOfTierPrices < this.tierPrices.length(); noOfTierPrices++) {
                        Log.d("signal", "" + this.tierPrices.getString(noOfTierPrices));
                        textView = new TextView(this);
                        textView.setText(this.tierPrices.getString(noOfTierPrices));
                        textView.setPadding(5, 5, 5, 5);
                        tierPricesLinearLayout.addView(textView);
                    }
                }
            }
            LinearLayout viewProductLayout = (LinearLayout) findViewById(R.id.customOptionLL);
            if (!mainObject.has("options") || mainObject.getJSONArray("options").length() == 0) {
                findViewById(R.id.requiredFieldTV).setVisibility(8);
                findViewById(R.id.customOptionLLOuter).setVisibility(8);
            } else if (mainObject.getJSONArray("options").length() != 0) {
                Log.d("option", mainObject.getJSONArray("options").toString() + "");
                this.customOptionsArr = new JSONArray(mainObject.getJSONArray("options").toString());
                int noOfCustomOpt = 0;
                while (noOfCustomOpt < this.customOptionsArr.length()) {
                    int id;
                    View child;
                    int i;
                    String optionString;
                    LayoutParams layoutParams;
                    final Calendar myCalendar;
                    final EditText dateEditText1;
                    Button resetBtn;
                    final OnDateSetListener date1;
                    final EditText timeEditText1;
                    Calendar mcurrentTime;
                    this.customOptnList.add(new CustomOptionData(this.customOptionsArr.getJSONObject(noOfCustomOpt).getString("name"), this.customOptionsArr.getJSONObject(noOfCustomOpt).getString("type"), this.customOptionsArr.getJSONObject(noOfCustomOpt).getInt("required"), Double.valueOf(0.0d), "", "", this.customOptionsArr.getJSONObject(noOfCustomOpt).getInt("option_id"), productId, Double.valueOf(0.0d), this.customOptionsArr.getJSONObject(noOfCustomOpt).getInt("product_option_id")));
                    String newCustomOptionLabel = ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getTitle();
                    textView = new TextView(this);
                    textView.setTextSize(18.0f);
                    textView.setPadding(0, 15, 0, 15);
                    Boolean isAffectingPrice = Boolean.valueOf(false);
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getIsRequired() == 1) {
                        newCustomOptionLabel = newCustomOptionLabel + "<font color=\"#FF2107\">*</font></small></i>";
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getUnformatedDefaultPrice().doubleValue() != 0.0d) {
                        isAffectingPrice = Boolean.valueOf(true);
                    }
                    if (isAffectingPrice.booleanValue()) {
                        String formattedDefaultPrice = ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getFormatedDefaultPrice();
                        if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getPriceType().equals("fixed")) {
                            newCustomOptionLabel = newCustomOptionLabel + "  +" + formattedDefaultPrice;
                        } else {
                            Double price = this.productData.getPrice();
                            Double unformatedDefaultPrice = ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getUnformatedDefaultPrice();
                            String pattern = this.productData.getPriceFormat().getPattern();
                            int precision = this.productData.getPriceFormat().getPrecision();
                            price = Double.valueOf((price.doubleValue() * unformatedDefaultPrice.doubleValue()) / 100.0d);
                            String str = pattern;
                            newCustomOptionLabel = newCustomOptionLabel + " +" + str.replaceAll("%s", String.format("%." + precision + "f", new Object[]{price}));
                        }
                    }
                    textView.setText(Html.fromHtml(newCustomOptionLabel));
                    textView.setPadding(0, 10, 0, 10);
                    viewProductLayout.addView(textView);
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("text") || ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("textarea")) {
                        textView = new EditText(this);
                        if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("text")) {
                            textView.setSingleLine();
                        }
                        id = View.generateViewId();
                        textView.setId(id);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setId(id);
                        viewProductLayout.addView(textView);
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("file")) {
                        child = getLayoutInflater().inflate(R.layout.custom_option_file_view, null);
                        int idOfFileNameTV = View.generateViewId();
                        ((TextView) child.findViewById(R.id.fileSelectedTV)).setId(idOfFileNameTV);
                        Button browseButton = (Button) child.findViewById(R.id.browseButton);
                        browseButton.setTag(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getProductOptionId() + "/" + idOfFileNameTV);
                        Log.d("id used", "" + idOfFileNameTV);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setAssociatedId(idOfFileNameTV);
                        browseButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                ViewProductSimple.this.startActivityForResult(new Intent("com.webkul.mobikul.fileselector.single"), 0);
                                String[] optionID_fileTvID = ((String) v.getTag()).split("/");
                                ViewProductSimple.fileBtnId = Integer.parseInt(optionID_fileTvID[0]);
                                ViewProductSimple.fileTvId = Integer.parseInt(optionID_fileTvID[1]);
                            }
                        });
                        viewProductLayout.addView(child);
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("select")) {
                        JSONArray dropDownJSONOpt = this.customOptionsArr.getJSONObject(noOfCustomOpt).getJSONArray("product_option_value");
                        ArrayList<String> SpinnerOptions = new ArrayList();
                        SpinnerOptions.add(getResources().getString(R.string._please_select_));
                        this.spinnerList = new ArrayList();
                        this.spinnerList.add(new MultiOptionData(-1, Double.valueOf(0.0d), "fixed"));
                        for (i = 0; i < dropDownJSONOpt.length(); i++) {
                            if (dropDownJSONOpt.getJSONObject(i).getString("price").equalsIgnoreCase("false")) {
                                optionString = dropDownJSONOpt.getJSONObject(i).getString("name");
                            } else {
                                optionString = dropDownJSONOpt.getJSONObject(i).getString("name") + "(" + dropDownJSONOpt.getJSONObject(i).getString("price") + ")";
                            }
                            this.spinnerList.add(new MultiOptionData(dropDownJSONOpt.getJSONObject(i).getInt("product_option_value_id"), Double.valueOf(0.0d), ""));
                            SpinnerOptions.add(optionString);
                        }
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter(this, 17367049, SpinnerOptions);
                        textView = new Spinner(this);
                        id = View.generateViewId();
                        textView.setId(id);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setId(id);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setMultiOptionListList(this.spinnerList);
                        this.spinnerIdList.add(Integer.valueOf(id));
                        textView.setAdapter(arrayAdapter);
                        textView.setSelection(0, false);
                        textView.setOnItemSelectedListener(new OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> adapterView, View selectedItemView, int position, long id) {
                                ViewProductSimple.this.productOptionValueId = ((MultiOptionData) ViewProductSimple.this.spinnerList.get(position)).optionTypeId;
                            }

                            public void onNothingSelected(AdapterView<?> adapterView) {
                                ViewProductSimple.this.productOptionValueId = ((MultiOptionData) ViewProductSimple.this.spinnerList.get(0)).optionTypeId;
                            }
                        });
                        viewProductLayout.addView(textView);
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("radio")) {
                        layoutParams = new LinearLayout.LayoutParams(-1, -2);
                        layoutParams.topMargin = 3;
                        layoutParams.bottomMargin = 3;
                        JSONArray radioButtonJSONOpt = this.customOptionsArr.getJSONObject(noOfCustomOpt).getJSONArray("product_option_value");
                        textView = new RadioGroup(this);
                        id = View.generateViewId();
                        textView.setId(id);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setId(id);
                        ArrayList<MultiOptionData> radioList = new ArrayList();
                        radioList.add(new MultiOptionData(-1, Double.valueOf(0.0d), "fixed"));
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setMultiOptionListList(radioList);
                        for (i = 0; i < radioButtonJSONOpt.length(); i++) {
                            textView = new RadioButton(this);
                            textView.addView(textView, layoutParams);
                            textView.setId(View.generateViewId());
                            textView.setLayoutParams(layoutParams);
                            textView.setTag(Integer.valueOf(radioButtonJSONOpt.getJSONObject(i).getInt("product_option_value_id")));
                            radioList.add(new MultiOptionData(radioButtonJSONOpt.getJSONObject(i).getInt("product_option_value_id"), Double.valueOf(0.0d), ""));
                            textView.setText(radioButtonJSONOpt.getJSONObject(i).getString("name") + "(+" + radioButtonJSONOpt.getJSONObject(i).getString("price") + ")");
                        }
                        textView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                            public void onCheckedChanged(RadioGroup group, int checkedId) {
                                ViewProductSimple.this.radioProductOptionValueId = ((Integer) ViewProductSimple.this.findViewById(checkedId).getTag()).intValue();
                            }
                        });
                        viewProductLayout.addView(textView, layoutParams);
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("checkbox") || ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("multiple")) {
                        JSONArray checkBoxJSONOpt = this.customOptionsArr.getJSONObject(noOfCustomOpt).getJSONArray("product_option_value");
                        for (i = 0; i < checkBoxJSONOpt.length(); i++) {
                            textView = new CheckBox(this);
                            id = View.generateViewId();
                            textView.setId(id);
                            layoutParams = new LinearLayout.LayoutParams(-1, -2);
                            layoutParams.topMargin = 3;
                            layoutParams.bottomMargin = 3;
                            textView.setTag(checkBoxJSONOpt.getJSONObject(i).getString("product_option_value_id"));
                            optionString = checkBoxJSONOpt.getJSONObject(i).getString("name") + "(+" + checkBoxJSONOpt.getJSONObject(i).getString("price") + ")";
                            this.checkBoxList.add(new MultiOptionData(id, checkBoxJSONOpt.getJSONObject(i).getInt("product_option_value_id"), checkBoxJSONOpt.getJSONObject(i).getInt("product_option_value_id"), Double.valueOf(0.0d), checkBoxJSONOpt.getJSONObject(i).getString("price")));
                            textView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    String id = (String) buttonView.getTag();
                                    if (isChecked) {
                                        ViewProductSimple.this.checkedList.put(id, id);
                                    } else {
                                        ViewProductSimple.this.checkedList.remove(id);
                                    }
                                    Log.d("checked", ViewProductSimple.this.checkedList + "");
                                }
                            });
                            textView.setText(optionString);
                            viewProductLayout.addView(textView, layoutParams);
                        }
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("date")) {
                        myCalendar = Calendar.getInstance();
                        child = getLayoutInflater().inflate(R.layout.custom_option_date_view, null);
                        dateEditText1 = (EditText) child.findViewById(R.id.dateET);
                        id = View.generateViewId();
                        dateEditText1.setId(id);
                        dateEditText1.setText(this.dateAndTime[0]);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setId(id);
                        dateEditText1.setTag(this.customOptnList.get(noOfCustomOpt));
                        resetBtn = (Button) child.findViewById(R.id.resetBtn);
                        resetBtn.setId(View.generateViewId());
                        resetBtn.setTag(Integer.valueOf(id));
                        resetBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                ((EditText) ViewProductSimple.this.findViewById(((Integer) v.getTag()).intValue())).setText("");
                            }
                        });
                        date1 = new OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(1, year);
                                myCalendar.set(2, monthOfYear);
                                myCalendar.set(5, dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                if (ViewProductSimple.this.isOkayClicked) {
                                    dateEditText1.setText(sdf.format(myCalendar.getTime()));
                                }
                                ViewProductSimple.this.isOkayClicked = false;
                            }
                        };
                        dateEditText1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                final DatePickerDialog mDate = new DatePickerDialog(ViewProductSimple.this, null, myCalendar.get(1), myCalendar.get(2), myCalendar.get(5));
                                mDate.setCancelable(true);
                                mDate.setCanceledOnTouchOutside(true);
                                mDate.setButton(-1, "OK", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -1) {
                                            ViewProductSimple.this.isOkayClicked = true;
                                            DatePicker datePicker = mDate.getDatePicker();
                                            date1.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                        }
                                    }
                                });
                                mDate.setButton(-2, "Cancel", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -2) {
                                            dialog.cancel();
                                            ViewProductSimple.this.isOkayClicked = false;
                                        }
                                    }
                                });
                                mDate.show();
                            }
                        });
                        viewProductLayout.addView(child);
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("datetime")) {
                        float px = 120.0f * getResources().getDisplayMetrics().density;
                        textView = new LinearLayout(this);
                        textView.setOrientation(0);
                        layoutParams = new android.app.ActionBar.LayoutParams(-1, -1);
                        textView.setLayoutParams(layoutParams);
                        myCalendar = Calendar.getInstance();
                        textView = new TextView(this);
                        textView.setText(R.string.date_);
                        textView.setPadding(5, 0, 0, 0);
                        dateEditText1 = new EditText(this);
                        dateEditText1.setSingleLine();
                        dateEditText1.setWidth((int) px);
                        dateEditText1.setInputType(0);
                        dateEditText1.setFocusable(false);
                        dateEditText1.setText(this.dateAndTime[0]);
                        textView.addView(textView);
                        textView.addView(dateEditText1);
                        id = View.generateViewId();
                        int id2 = View.generateViewId();
                        dateEditText1.setId(id);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setId(id);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setAssociatedId(id2);
                        viewProductLayout.addView(textView);
                        child = getLayoutInflater().inflate(R.layout.custom_option_time_view, null);
                        textView = new LinearLayout(this);
                        textView.setOrientation(0);
                        textView.setLayoutParams(layoutParams);
                        timeEditText1 = (EditText) child.findViewById(R.id.timeET);
                        timeEditText1.setId(id2);
                        timeEditText1.setText(this.dateAndTime[1]);
                        Button resetDateTimeBtn = (Button) child.findViewById(R.id.resetBtn);
                        resetDateTimeBtn.setId(View.generateViewId());
                        resetDateTimeBtn.setText(R.string.reset);
                        resetDateTimeBtn.setTag(id + "/" + id2);
                        mcurrentTime = Calendar.getInstance();
                        this.hourCurrent2 = mcurrentTime.get(11);
                        this.minuteCurrent2 = mcurrentTime.get(12);
                        date1 = new OnDateSetListener() {
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(1, year);
                                myCalendar.set(2, monthOfYear);
                                myCalendar.set(5, dayOfMonth);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                                if (ViewProductSimple.this.isOkayClicked) {
                                    dateEditText1.setText(sdf.format(myCalendar.getTime()));
                                }
                                ViewProductSimple.this.isOkayClicked = false;
                                if (timeEditText1.getText().toString().equals("")) {
                                    int hour = ViewProductSimple.this.hourCurrent2;
                                    int minute = ViewProductSimple.this.minuteCurrent2;
                                    final TimePickerDialog mTimePicker = new TimePickerDialog(ViewProductSimple.this, new OnTimeSetListener() {
                                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                            if (ViewProductSimple.this.isOkayClicked) {
                                                timeEditText1.setText(selectedHour + ":" + selectedMinute);
                                            }
                                            ViewProductSimple.this.isOkayClicked = false;
                                            ViewProductSimple.this.hourCurrent2 = selectedHour;
                                            ViewProductSimple.this.minuteCurrent2 = selectedMinute;
                                        }
                                    }, hour, minute, false);
                                    mTimePicker.setTitle(R.string.select_time);
                                    mTimePicker.setButton(-1, "OK", new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == -1) {
                                                ViewProductSimple.this.isOkayClicked = true;
                                                mTimePicker.onClick(dialog, which);
                                            }
                                        }
                                    });
                                    mTimePicker.setButton(-2, "Cancel", new OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (which == -2) {
                                                ViewProductSimple.this.isOkayClicked = false;
                                                dialog.cancel();
                                            }
                                        }
                                    });
                                    mTimePicker.show();
                                }
                            }
                        };
                        dateEditText1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                final DatePickerDialog mDate = new DatePickerDialog(ViewProductSimple.this, null, myCalendar.get(1), myCalendar.get(2), myCalendar.get(5));
                                mDate.setCanceledOnTouchOutside(true);
                                mDate.setButton(-1, "OK", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -1) {
                                            ViewProductSimple.this.isOkayClicked = true;
                                            DatePicker datePicker = mDate.getDatePicker();
                                            date1.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                        }
                                    }
                                });
                                mDate.setButton(-2, "Cancel", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -2) {
                                            dialog.cancel();
                                            ViewProductSimple.this.isOkayClicked = false;
                                        }
                                    }
                                });
                                mDate.show();
                            }
                        });
                        resetDateTimeBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String[] ids = ((String) v.getTag()).split("/");
                                EditText resetDate = (EditText) ViewProductSimple.this.findViewById(Integer.parseInt(ids[0]));
                                resetDate.setText("");
                                resetDate.setError(null);
                                EditText resetTime = (EditText) ViewProductSimple.this.findViewById(Integer.parseInt(ids[1]));
                                resetTime.setText("");
                                resetTime.setError(null);
                                Calendar mcurrentTime = Calendar.getInstance();
                                ViewProductSimple.this.hourCurrent2 = mcurrentTime.get(11);
                                ViewProductSimple.this.minuteCurrent2 = mcurrentTime.get(12);
                            }
                        });
                        timeEditText1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                int hour = ViewProductSimple.this.hourCurrent2;
                                int minute = ViewProductSimple.this.minuteCurrent2;
                                final TimePickerDialog mTimePicker = new TimePickerDialog(ViewProductSimple.this, new OnTimeSetListener() {
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        if (ViewProductSimple.this.isOkayClicked) {
                                            timeEditText1.setText(selectedHour + ":" + selectedMinute);
                                            ViewProductSimple.this.isOkayClicked = false;
                                            ViewProductSimple.this.hourCurrent2 = selectedHour;
                                            ViewProductSimple.this.minuteCurrent2 = selectedMinute;
                                            if (dateEditText1.getText().toString().equals("")) {
                                                final DatePickerDialog mDate = new DatePickerDialog(ViewProductSimple.this, null, myCalendar.get(1), myCalendar.get(2), myCalendar.get(5));
                                                mDate.setCanceledOnTouchOutside(true);
                                                mDate.setButton(-1, "OK", new OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == -1) {
                                                            ViewProductSimple.this.isOkayClicked = true;
                                                            DatePicker datePicker = mDate.getDatePicker();
                                                            date1.onDateSet(datePicker, datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                                        }
                                                    }
                                                });
                                                mDate.setButton(-2, "Cancel", new OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == -2) {
                                                            dialog.cancel();
                                                            ViewProductSimple.this.isOkayClicked = false;
                                                        }
                                                    }
                                                });
                                                mDate.show();
                                            }
                                        }
                                    }
                                }, hour, minute, false);
                                mTimePicker.setTitle(R.string.select_time);
                                mTimePicker.setButton(-1, "OK", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -1) {
                                            ViewProductSimple.this.isOkayClicked = true;
                                            mTimePicker.onClick(dialog, which);
                                        }
                                    }
                                });
                                mTimePicker.setButton(-2, "Cancel", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -2) {
                                            ViewProductSimple.this.isOkayClicked = false;
                                            dialog.cancel();
                                        }
                                    }
                                });
                                mTimePicker.show();
                            }
                        });
                        viewProductLayout.addView(textView);
                        viewProductLayout.addView(child);
                    }
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("time")) {
                        child = getLayoutInflater().inflate(R.layout.custom_option_time_view, null);
                        timeEditText1 = (EditText) child.findViewById(R.id.timeET);
                        timeEditText1.setText(this.dateAndTime[1]);
                        id = View.generateViewId();
                        timeEditText1.setId(id);
                        ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).setId(id);
                        resetBtn = (Button) child.findViewById(R.id.resetBtn);
                        resetBtn.setTag(Integer.valueOf(id));
                        resetBtn.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                ((EditText) ViewProductSimple.this.findViewById(((Integer) v.getTag()).intValue())).setText("");
                                Calendar mcurrentTime = Calendar.getInstance();
                                ViewProductSimple.this.hourCurrent = mcurrentTime.get(11);
                                ViewProductSimple.this.minuteCurrent = mcurrentTime.get(12);
                            }
                        });
                        mcurrentTime = Calendar.getInstance();
                        this.hourCurrent = mcurrentTime.get(11);
                        this.minuteCurrent = mcurrentTime.get(12);
                        timeEditText1.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                int hour = ViewProductSimple.this.hourCurrent;
                                int minute = ViewProductSimple.this.minuteCurrent;
                                final TimePickerDialog mTimePicker = new TimePickerDialog(ViewProductSimple.this, new OnTimeSetListener() {
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        ViewProductSimple.this.hourCurrent = selectedHour;
                                        ViewProductSimple.this.minuteCurrent = selectedMinute;
                                        if (ViewProductSimple.this.isOkayClicked) {
                                            timeEditText1.setText(selectedHour + ":" + selectedMinute);
                                        }
                                        ViewProductSimple.this.isOkayClicked = false;
                                    }
                                }, hour, minute, false);
                                mTimePicker.setButton(-1, "OK", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -1) {
                                            ViewProductSimple.this.isOkayClicked = true;
                                            mTimePicker.onClick(dialog, which);
                                        }
                                    }
                                });
                                mTimePicker.setButton(-2, "Cancel", new OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == -2) {
                                            ViewProductSimple.this.isOkayClicked = false;
                                            dialog.cancel();
                                        }
                                    }
                                });
                                mTimePicker.show();
                            }
                        });
                        viewProductLayout.addView(child);
                    }
                    noOfCustomOpt++;
                }
            }
            List<String> descriptionList = new ArrayList();
            List<String> reviewsList = new ArrayList();
            descriptionList.add(mainObject.getString("description"));
            reviewsList.add(mainObject.getJSONObject("reviewData").getJSONArray("reviews").toString());
            this.pager1 = () findViewById(R.id.pager);
            TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
            tabLayout.setTabGravity(0);
            tabLayout.setTabMode(1);
            this.adapter = new TabsAdapter(getSupportFragmentManager(), this, this.Titles, this.Numboftabs, (String) descriptionList.get(0), mainObject.getJSONArray("attribute_groups"), (String) reviewsList.get(0), mainObject, this.productImageArr);
            this.pager1.setAdapter(this.adapter);
            tabLayout.setupWithViewPager(this.pager1);
            ((ProgressBar) findViewById(R.id.view_product_progress_bar)).setVisibility(8);
            this.layoutContainer.setVisibility(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSimilarProduct(JSONArray productArray, LinearLayout parentView) {
        try {
            int gape = (int) (10.0f * getResources().getDisplayMetrics().density);
            int padding = (int) (5.0f * getResources().getDisplayMetrics().density);
            if (productArray.length() != 0) {
                for (int i = 0; i < productArray.length(); i++) {
                    View child = getLayoutInflater().inflate(R.layout.item_product_grid_view, null);
                    RelativeLayout productImageLayout = (RelativeLayout) child.findViewById(R.id.productImageLayout);
                    RelativeLayout.LayoutParams productImageLayoutparams = (RelativeLayout.LayoutParams) productImageLayout.getLayoutParams();
                    productImageLayoutparams.width = (int) (((double) this.screen_width) / 2.3d);
                    productImageLayoutparams.height = (int) (((double) this.screen_width) / 2.3d);
                    productImageLayout.setLayoutParams(productImageLayoutparams);
                    child.setTag(Integer.valueOf(i));
                    parentView.addView(child);
                    if (i < productArray.length() - 1) {
                        View view = new View(this);
                        view.setLayoutParams(new LinearLayout.LayoutParams(gape, -1));
                        view.setBackgroundColor(0);
                        parentView.addView(view);
                    }
                    ((RelativeLayout) child.findViewById(R.id.relative)).setPadding(padding, 0, padding, padding);
                    JSONObject eachProduct = productArray.getJSONObject(i);
                    Button addToCart = (Button) child.findViewById(R.id.addToCart);
                    ImageView wishlist = (ImageView) child.findViewById(R.id.wishlist);
                    if (eachProduct.has("hasOption")) {
                        if ("true".equalsIgnoreCase(eachProduct.getString("hasOption"))) {
                            addToCart.setTag(i + "/" + 1);
                            addToCart.setText("View Product");
                        } else {
                            addToCart.setTag(i + "/" + 0);
                            addToCart.setText("Add To Cart");
                        }
                    }
                    Log.d("DEBUG", "Kam aaya");
                    final JSONArray jSONArray = productArray;
                    addToCart.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Exception e;
                            String[] indexFlagPair = ((String) v.getTag()).split("/");
                            int position = Integer.parseInt(indexFlagPair[0]);
                            if (indexFlagPair[1].equals("0")) {
                                ViewProductSimple.progress = ProgressDialog.show(ViewProductSimple.this, ViewProductSimple.this.getResources().getString(R.string.please_wait), ViewProductSimple.this.getResources().getString(R.string.processing_request_response), true);
                                ViewProductSimple.progress.setCanceledOnTouchOutside(false);
                                JSONObject jo = new JSONObject();
                                try {
                                    jo.put("product_id", jSONArray.getJSONObject(position).getString("product_id"));
                                    jo.put("quantity", "1");
                                } catch (JSONException e2) {
                                    e2.printStackTrace();
                                }
                                new MobikulMakeConnection(ViewProductSimple.this).execute(new String[]{"addToCart", jo.toString()});
                                return;
                            }
                            Intent intent = new Intent(ViewProductSimple.this, ViewProductSimple.this.mMobikulApplication.ViewProduct());
                            try {
                                intent.putExtra("idOfProduct", jSONArray.getJSONObject(position).getString("product_id"));
                                intent.putExtra("nameOfProduct", jSONArray.getJSONObject(position).getString("name"));
                            } catch (NumberFormatException e3) {
                                e = e3;
                                e.printStackTrace();
                                ViewProductSimple.this.startActivity(intent);
                            } catch (JSONException e4) {
                                e = e4;
                                e.printStackTrace();
                                ViewProductSimple.this.startActivity(intent);
                            }
                            ViewProductSimple.this.startActivity(intent);
                        }
                    });
                    wishlist.setTag(eachProduct.getString("product_id"));
                    wishlist.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            String productId = (String) v.getTag();
                            if (Boolean.valueOf(ViewProductSimple.this.getSharedPreferences("customerData", 0).getBoolean("isLoggedIn", false)).booleanValue()) {
                                ViewProductSimple.progress = ProgressDialog.show(ViewProductSimple.this, ViewProductSimple.this.getResources().getString(R.string.please_wait), ViewProductSimple.this.getResources().getString(R.string.processing_request_response), true);
                                ViewProductSimple.progress.setCanceledOnTouchOutside(false);
                                try {
                                    new JSONObject().put("product_id", productId);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                new MobikulMakeConnection(ViewProductSimple.this).execute(new String[]{"addToWishlist", jo.toString()});
                                return;
                            }
                            Builder alert = new Builder(ViewProductSimple.this);
                            alert.setNegativeButton(ViewProductSimple.this.getResources().getString(17039370), new OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            alert.setMessage(Html.fromHtml(ViewProductSimple.this.getResources().getString(R.string.wishlist_msg))).show();
                        }
                    });
                    if (eachProduct.has("stock") && eachProduct.getString("stock").equalsIgnoreCase("In Stock")) {
                        ((TextView) child.findViewById(R.id.outOfStock)).setVisibility(8);
                    }
                    this.productImage = (ImageView) child.findViewById(R.id.productImage);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (((double) this.screen_width) / 2.3d), (int) (((double) this.screen_width) / 2.3d));
                    params.addRule(13, -1);
                    this.productImage.setLayoutParams(params);
                    String tagString = i + "/" + 0;
                    this.productImage.setTag(tagString);
                    jSONArray = productArray;
                    this.productImage.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Exception e;
                            String[] indexFlagPair = ((String) v.getTag()).split("/");
                            Intent intent = new Intent(ViewProductSimple.this, ViewProductSimple.this.mMobikulApplication.ViewProduct());
                            try {
                                intent.putExtra("idOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("product_id"));
                                intent.putExtra("nameOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("name"));
                            } catch (NumberFormatException e2) {
                                e = e2;
                                e.printStackTrace();
                                ViewProductSimple.this.startActivity(intent);
                            } catch (JSONException e3) {
                                e = e3;
                                e.printStackTrace();
                                ViewProductSimple.this.startActivity(intent);
                            }
                            ViewProductSimple.this.startActivity(intent);
                        }
                    });
                    this.productName = (TextView) child.findViewById(R.id.productName);
                    this.productName.setTag(tagString);
                    jSONArray = productArray;
                    this.productName.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Exception e;
                            String[] indexFlagPair = ((String) v.getTag()).split("/");
                            Intent intent = new Intent(ViewProductSimple.this, ViewProductSimple.this.mMobikulApplication.ViewProduct());
                            try {
                                intent.putExtra("idOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("product_id"));
                                intent.putExtra("nameOfProduct", jSONArray.getJSONObject(Integer.parseInt(indexFlagPair[0])).getString("name"));
                            } catch (NumberFormatException e2) {
                                e = e2;
                                e.printStackTrace();
                                ViewProductSimple.this.startActivity(intent);
                            } catch (JSONException e3) {
                                e = e3;
                                e.printStackTrace();
                                ViewProductSimple.this.startActivity(intent);
                            }
                            ViewProductSimple.this.startActivity(intent);
                        }
                    });
                    this.model = (TextView) child.findViewById(R.id.model);
                    if (eachProduct.has("model")) {
                        this.model.setText(eachProduct.getString("model"));
                    } else {
                        this.model.setVisibility(8);
                    }
                    this.newPrice = (TextView) child.findViewById(R.id.newPrice);
                    this.price = (TextView) child.findViewById(R.id.price);
                    this.price.setVisibility(8);
                    this.ratingBar = (RatingBar) child.findViewById(R.id.ratingBar);
                    this.newPrice.setSingleLine(false);
                    this.productName.setText(Html.fromHtml(eachProduct.getString("name")));
                    if (eachProduct.getString("special").equalsIgnoreCase("false")) {
                        this.newPrice.setText(eachProduct.getString("price"));
                        child.findViewById(R.id.sale).setVisibility(8);
                    } else {
                        this.newPrice.setText(Html.fromHtml("<u>" + eachProduct.getString("special") + "</u>"));
                        this.newPrice.setTextColor(ContextCompat.getColor(this, R.color.special_price_color));
                        this.price.setText(eachProduct.getString("price"));
                        this.price.setPaintFlags(this.price.getPaintFlags() | 16);
                        this.price.setTextSize(11.5f);
                        this.price.setVisibility(0);
                        child.findViewById(R.id.sale).setVisibility(0);
                        if (eachProduct.has("discount")) {
                            ((TextView) child.findViewById(R.id.sale)).setText(eachProduct.getString("discount"));
                        }
                    }
                    Log.d("featured_name", eachProduct.getString("name") + "");
                    Log.d("featured_image", eachProduct.getString("thumb") + "");
                    Picasso.with(this).load(eachProduct.getString("thumb")).placeholder(R.drawable.placeholder).into(this.productImage);
                }
                return;
            }
            findViewById(R.id.related_Product_hsv).setVisibility(8);
            findViewById(R.id.related_product_tv).setVisibility(8);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void updatePrice() {
        try {
            String pattern = this.productData.getPriceFormat().getPattern();
            String precisionFormat = "%." + this.productData.getPriceFormat().getPrecision() + "f";
            Double actualPriceOfProduct = this.productData.getPrice();
            Double price = this.productData.getCurrentPrice();
            for (int noOfCheckBox = 0; noOfCheckBox < this.checkBoxList.size(); noOfCheckBox++) {
                MultiOptionData tempCheckData = (MultiOptionData) this.checkBoxList.get(noOfCheckBox);
                if (((CheckBox) findViewById(tempCheckData.getId())).isChecked()) {
                    if (tempCheckData.getPriceType().equals("fixed")) {
                        price = Double.valueOf(price.doubleValue() + tempCheckData.getDefaultPrice().doubleValue());
                    } else {
                        price = Double.valueOf(price.doubleValue() + ((tempCheckData.getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                    }
                }
            }
            int noOfCustomOpt = 0;
            while (noOfCustomOpt < this.customOptionsArr.length()) {
                View v;
                if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("time") && !((EditText) findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getId())).getText().toString().isEmpty()) {
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getPriceType().equals("fixed")) {
                        price = Double.valueOf(price.doubleValue() + ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue());
                    } else {
                        price = Double.valueOf(price.doubleValue() + ((((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                    }
                }
                if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("field") || ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("area")) {
                    v = findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getId());
                    Log.d("signal", "inside field/area");
                    if (!((EditText) v).getText().toString().matches("")) {
                        if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getPriceType().equals("fixed")) {
                            price = Double.valueOf(price.doubleValue() + ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue());
                        } else {
                            price = Double.valueOf(price.doubleValue() + ((((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                        }
                    }
                }
                if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("date_time")) {
                    Log.d("date+time", "inside");
                    v = findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getId());
                    if (!((EditText) v).getText().toString().matches("")) {
                        price = Double.valueOf(price.doubleValue() + ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue());
                    }
                    if (((EditText) v).getText().toString().matches("") && !((EditText) findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getAssociatedId())).getText().toString().matches("")) {
                        if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getPriceType().equals("fixed")) {
                            price = Double.valueOf(price.doubleValue() + ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue());
                        } else {
                            price = Double.valueOf(price.doubleValue() + ((((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                        }
                    }
                }
                if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("radio")) {
                    RadioGroup radioGroup = (RadioGroup) findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getId());
                    if (radioGroup.getCheckedRadioButtonId() != -1) {
                        int positionOfRadioItem = radioGroup.indexOfChild((RadioButton) findViewById(radioGroup.getCheckedRadioButtonId()));
                        if (((MultiOptionData) ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getMultiOptionListList().get(positionOfRadioItem)).getPriceType().equals("fixed")) {
                            price = Double.valueOf(price.doubleValue() + ((MultiOptionData) ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getMultiOptionListList().get(positionOfRadioItem)).getDefaultPrice().doubleValue());
                        } else {
                            price = Double.valueOf(price.doubleValue() + ((((MultiOptionData) ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getMultiOptionListList().get(positionOfRadioItem)).getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                        }
                    }
                }
                if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("drop_down")) {
                    int positionOfSpinnerItem = ((Spinner) findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getId())).getSelectedItemPosition();
                    if (((MultiOptionData) ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getMultiOptionListList().get(positionOfSpinnerItem)).getPriceType().equals("fixed")) {
                        price = Double.valueOf(price.doubleValue() + ((MultiOptionData) ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getMultiOptionListList().get(positionOfSpinnerItem)).getDefaultPrice().doubleValue());
                    } else {
                        price = Double.valueOf(price.doubleValue() + ((((MultiOptionData) ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getMultiOptionListList().get(positionOfSpinnerItem)).getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                    }
                }
                if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("date") && !((EditText) findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getId())).getText().toString().matches("")) {
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getPriceType().equals("fixed")) {
                        price = Double.valueOf(price.doubleValue() + ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue());
                    } else {
                        price = Double.valueOf(price.doubleValue() + ((((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                    }
                }
                if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getType().equals("file") && !((TextView) findViewById(((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getAssociatedId())).getText().toString().equals(getResources().getString(R.string.no_file_selected))) {
                    if (((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getPriceType().equals("fixed")) {
                        price = Double.valueOf(price.doubleValue() + ((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue());
                    } else {
                        price = Double.valueOf(price.doubleValue() + ((((CustomOptionData) this.customOptnList.get(noOfCustomOpt)).getDefaultPrice().doubleValue() * actualPriceOfProduct.doubleValue()) / 100.0d));
                    }
                }
                noOfCustomOpt++;
            }
            this.productPrice.setText(pattern.replaceAll("%s", String.format(precisionFormat, new Object[]{price})));
        } catch (Exception e) {
            Log.d("upadate price Exceptiom", e.getMessage());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == -1) {
                    File file = new File(((Uri) data.getParcelableExtra("file")).getPath());
                    byte[] bytes = new byte[((int) file.length())];
                    try {
                        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                        buf.read(bytes, 0, bytes.length);
                        buf.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    String fileName = file.getName();
                    String mimeType = "";
                    try {
                        mimeType = file.toURL().openConnection().getContentType();
                    } catch (MalformedURLException e3) {
                        e3.printStackTrace();
                    } catch (IOException e22) {
                        e22.printStackTrace();
                    }
                    String encodedString = Base64.encodeToString(bytes, 0);
                    JSONObject fileJSONResponse = new JSONObject();
                    try {
                        if (!fileName.equals("")) {
                            fileJSONResponse.put("name", fileName);
                            fileJSONResponse.put("type", mimeType);
                            this.inputByUser.getJSONObject("options").put("" + fileBtnId, fileJSONResponse);
                        }
                        fileJSONResponse.put("encodeImage", encodedString);
                    } catch (JSONException e4) {
                        e4.printStackTrace();
                    }
                    ((TextView) findViewById(fileTvId)).setText(fileName);
                    return;
                }
                return;
            case 2:
                if (resultCode == -1) {
                    StringBuilder sb = new StringBuilder();
                    Iterator it = data.getParcelableArrayListExtra("files").iterator();
                    while (it.hasNext()) {
                        sb.append(((Uri) it.next()).getPath()).append("\r\n");
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void onClickAddToWishListTV(View v) {
        if (Boolean.valueOf(getSharedPreferences("customerData", 0).getBoolean("isLoggedIn", false)).booleanValue()) {
            progress = ProgressDialog.show(this, getResources().getString(R.string.please_wait), getResources().getString(R.string.processing_request_response), true);
            progress.setCanceledOnTouchOutside(false);
            try {
                new JSONObject().put("product_id", productId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new MobikulMakeConnection(this).execute(new String[]{"addToWishlist", jo.toString()});
            return;
        }
        Builder alert = new Builder(this);
        alert.setNegativeButton(getResources().getString(17039370), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.setMessage(Html.fromHtml(getResources().getString(R.string.wishlist_msg))).show();
    }

    public void addToWishlistResponse(String backresult) {
        try {
            JSONObject mainObject = new JSONObject(backresult);
            progress.dismiss();
            Builder alert = new Builder(this);
            if (mainObject.getString("error").equalsIgnoreCase("0")) {
                alert.setTitle(getResources().getString(R.string.Success));
            } else {
                alert.setTitle(getResources().getString(R.string.Error));
            }
            alert.setNegativeButton(getResources().getString(17039370), new OnClickListener() {
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

    public void addToCartResponse(String backresult) {
        try {
            JSONObject CheckoutAddtoCartobject = new JSONObject(backresult);
            Log.d("data", CheckoutAddtoCartobject + "");
            progress.dismiss();
            if (CheckoutAddtoCartobject.getString("error").equalsIgnoreCase("0")) {
                Toast.makeText(getApplicationContext(), Html.fromHtml(CheckoutAddtoCartobject.getString("message")), 0).show();
                String totalCount = CheckoutAddtoCartobject.getString("total");
                totalCount = totalCount.substring(0, totalCount.indexOf(" "));
                Editor editor = getSharedPreferences("customerData", 0).edit();
                editor.putString("cartItems", totalCount);
                editor.commit();
                Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", 0).getString("cartItems", "0"));
                return;
            }
            Toast.makeText(getApplicationContext(), CheckoutAddtoCartobject.getJSONArray("message").getString(0), 1).show();
        } catch (JSONException e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }

    public void onStop() {
        super.onStop();
        this.active = false;
    }

    private void loadBannerImages() {
        try {
            if (this.productImageArr.length() != 0) {
                this.imageUrls = new String[this.productImageArr.length()];
                Log.d("Array size  ", "" + this.productImageArr.getJSONObject(0).getString("popup"));
                for (int noOfBannerUrls = 0; noOfBannerUrls < this.productImageArr.length(); noOfBannerUrls++) {
                    this.imageUrls[noOfBannerUrls] = this.productImageArr.getJSONObject(noOfBannerUrls).getString("popup");
                }
                this.pager = (ViewPager) findViewById(R.id.banner_pager);
                this.pager.setAdapter(new ImagePagerAdapterSimple(this.imageUrls));
                this.pager.setLayoutParams(new LinearLayout.LayoutParams(this.screen_width, this.screen_width));
                this.listener = new DetailOnPageChangeListener();
                this.pager.setOnPageChangeListener(this.listener);
                ViewGroup group = (ViewGroup) findViewById(R.id.dot_group);
                this.dotList = new ImageView[this.imageUrls.length];
                Log.d("DEBUG  here", "dotList" + this.dotList);
                for (int i = 0; i < this.imageUrls.length; i++) {
                    this.dotImage = new ImageView(this);
                    this.dotList[i] = this.dotImage;
                    if (i == 0) {
                        this.dotList[i].setBackgroundResource(R.drawable.dot_icon_focused);
                    } else {
                        this.dotList[i].setBackgroundResource(R.drawable.dot_icon_unfocused);
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
                    params.setMargins(10, 0, 10, 0);
                    group.addView(this.dotImage, params);
                }
                return;
            }
            findViewById(R.id.pagerLayout).setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openReview(View v) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(1);
        dialog.setContentView(R.layout.custom_review_dialog);
        dialog.show();
        RatingBar ratePoints = (RatingBar) dialog.findViewById(R.id.review_rating_bar);
        ratePoints.setStepSize(1.0f);
        ratePoints.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ViewProductSimple.this.currentRating = rating + "";
            }
        });
        TextView cancel = (TextView) dialog.findViewById(R.id.btn_cancel);
        dialog.setTitle(null);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ViewProductSimple.dialog.dismiss();
            }
        });
        ((TextView) dialog.findViewById(R.id.btn_apply)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ViewProductSimple.this.onReviewSubmit(ViewProductSimple.dialog);
            }
        });
    }

    public void onReviewSubmit(Dialog dialog) {
        EditText userNameET = (EditText) dialog.findViewById(R.id.user_name);
        String userName = userNameET.getText().toString().trim();
        EditText cmtET = (EditText) dialog.findViewById(R.id.cmt);
        String cmt = cmtET.getText().toString().trim();
        boolean isFormValidated = true;
        if (userName.matches("")) {
            userNameET.setError("Your Name " + getResources().getString(R.string.is_require_text));
            isFormValidated = false;
        }
        if (cmt.matches("")) {
            cmtET.setError("Your Comment " + getResources().getString(R.string.is_require_text));
            isFormValidated = false;
        }
        if (isFormValidated) {
            progress = ProgressDialog.show(this, null, getResources().getString(R.string.loading), true);
            progress.setCanceledOnTouchOutside(false);
            JSONObject jo = new JSONObject();
            try {
                jo.put("name", userName);
                jo.put("text", cmt);
                jo.put("rating", this.currentRating);
                jo.put("product_id", productId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new MobikulMakeConnection(this).execute(new String[]{"writeProductReview", jo.toString()});
        }
    }

    public void writeProductReviewResponse(String backresult) {
        try {
            progress.dismiss();
            this.responseObject = new JSONObject(backresult);
            if (this.responseObject.has("message")) {
                Toast.makeText(getApplicationContext(), this.responseObject.getString("message") + "", 1).show();
                dialog.dismiss();
                return;
            }
            Toast.makeText(getApplicationContext(), this.responseObject.getString("error") + "", 1).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addSoldByLayout() {
    }

    protected void addSellerInfo() {
    }

    public void setCartbadge() {
        try {
            if (mainObject.has("cartCount")) {
                Log.d("DEBUG", mainObject.getString("cartCount"));
                Editor editor = getSharedPreferences("customerData", MODE_PRIVATE).edit();
                editor.putString("cartItems", mainObject.getString("cartCount"));
                editor.commit();
                if (this.itemCart != null) {
                    Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", 0).getString("cartItems", "0"));
                    return;
                }
                return;
            }
            Log.d("DEBUG", "noValue for cartCount");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ResultData {

        int optionId;
        int productId;
        String valueInput;

        public ResultData(int optionId, int productId, String valueInput) {
            this.optionId = optionId;
            this.productId = productId;
            this.valueInput = valueInput;
        }

        public int getOptionId() {
            return this.optionId;
        }

        public void setOptionId(int optionId) {
            this.optionId = optionId;
        }

        public int getProductId() {
            return this.productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getValueInput() {
            return this.valueInput;
        }

        public void setValueInput(String valueInput) {
            this.valueInput = valueInput;
        }
    }


    private class TabsAdapter extends FragmentStatePagerAdapter {
        int NumbOfTabs;
        CharSequence[] Titles;
        JSONArray arr;
        Context context;
        String description;
        JSONArray feature;
        JSONObject mo;
        String review;

        public TabsAdapter(FragmentManager fm, Context context, CharSequence[] mTitles, int mNumbOfTabsumb, String description, JSONArray feature, String review, JSONObject mo, JSONArray arr) {
            super(fm);
            this.context = context;
            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;
            this.feature = feature;
            this.description = description;
            this.review = review;
            this.mo = mo;
            this.arr = arr;
        }

        public Fragment getItem(int position) {
            if (position == 0) {
                return new DescriptionTab(this.description);
            }
            if (position == 1) {
                return new FeaturedTab(this.feature);
            }
            return new ReviewsTab(this.review, this.mo, this.arr);
        }

        public CharSequence getPageTitle(int position) {
            return this.Titles[position];
        }

        public int getCount() {
            return this.NumbOfTabs;
        }

        @SuppressLint({"ValidFragment"})
        private class DescriptionTab extends Fragment {
            ViewGroup container;
            String description;
            LayoutInflater inflater;

            @SuppressLint({"ValidFragment"})
            public DescriptionTab(String description) {
                this.description = description;
            }

            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View v = inflater.inflate(R.layout.activity_description_tab, container, false);
                ((TextView) v.findViewById(R.id.tv_description)).setText(this.description);
                WebView myWebView = new WebView(getActivity());
                try {
                    myWebView.setFocusable(true);
                    myWebView.loadDataWithBaseURL(null, this.description, "text/html", "utf-8", null);
                    ((RelativeLayout) v.findViewById(R.id.descLayout)).addView(myWebView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return v;
            }

        }

        @SuppressLint({"ValidFragment"})
        private class FeaturedTab extends Fragment {
            JSONArray feature;

            @SuppressLint({"ValidFragment"})
            public FeaturedTab(JSONArray feature) {
                this.feature = feature;
            }

            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View v = null;
                try {
                    v = inflater.inflate(R.layout.activity_featured_tab, container, false);
                    LinearLayout specification_Values = (LinearLayout) v.findViewById(R.id.featureContentLayout);
                    if (this.feature.length() > 0) {
                        for (int i = 0; i < this.feature.length(); i++) {
                            try {
                                JSONObject specificationData = this.feature.getJSONObject(i);
                                TextView specificationHeading = new TextView(getActivity());
                                specificationHeading.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                specificationHeading.setBackgroundResource(R.drawable.rect_shape);
                                specificationHeading.setText(specificationData.getString("name"));
                                specificationHeading.setTextSize(18.0f);
                                specificationHeading.setTypeface(null, 1);
                                specificationHeading.setPadding(20, 5, 5, 10);
                                LinearLayout attributesLayout = new LinearLayout(getActivity());
                                attributesLayout.setOrientation(LinearLayout.HORIZONTAL);
                                attributesLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                for (int j = 0; j < specificationData.getJSONArray("attribute").length(); j++) {
                                    JSONObject specificationDataAttributes = specificationData.getJSONArray("attribute").getJSONObject(j);
                                    TextView attributeLabel = new TextView(getActivity());
                                    attributeLabel.setText(specificationDataAttributes.getString("name"));
                                    attributeLabel.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                                    attributeLabel.setBackgroundResource(R.drawable.rect_shape);
                                    attributeLabel.setPadding(20, 5, 5, 10);
                                    TextView attributeValue = new TextView(getActivity());
                                    attributeValue.setText(specificationDataAttributes.getString("text"));
                                    attributeValue.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                                    attributeValue.setBackgroundResource(R.drawable.rect_shape);
                                    attributeValue.setPadding(20, 5, 5, 10);
                                    attributesLayout.addView(attributeLabel);
                                    attributesLayout.addView(attributeValue);
                                }
                                specification_Values.addView(specificationHeading);
                                specification_Values.addView(attributesLayout);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                new BaseActivity().trackException(e, getActivity());
                            }
                        }
                        return v;
                    }
                    v.findViewById(R.id.no_featured_list).setVisibility(View.VISIBLE);
                    return v;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                return v;
            }

        }

        @SuppressLint({"ValidFragment"})
        private class ReviewsTab extends Fragment {
            JSONArray arr;
            private String currentRating;
            JSONArray feature;
            JSONObject mo;
            private JSONObject responseObject;
            String review;
            private LinearLayout reviewLayout;
            private JSONArray reviewList;

            @SuppressLint({"ValidFragment"})
            public ReviewsTab(String review, JSONObject mo, JSONArray arr) {
                this.review = review;
                this.mo = mo;
                this.arr = arr;
            }

            public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View convertView = null;
                try {
                    this.reviewList = new JSONArray(this.review);
                } catch (JSONException e) {
                    Log.d("Exception", "Review product ELV");
                    e.printStackTrace();
                }
                try {
                    if (this.review.equals("[]")) {
                        convertView = inflater.inflate(R.layout.item_category_drawer_list, null);
                        TextView txtListChild = (TextView) convertView.findViewById(R.id.category_item_list);
                        txtListChild.setTag("first_review");
                        txtListChild.setGravity(Gravity.CENTER);
                        txtListChild.setText(R.string.be_the_first_to_review_this_product);
                        txtListChild.setClickable(true);
                        txtListChild.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ReviewsTab.this.openReview(view);
                            }
                        });
                        txtListChild.setTextColor(Color.parseColor("#3399cc"));
                    } else {
                        convertView = inflater.inflate(R.layout.activity_reviews_tab, null);
                        convertView.setTag("review_in_product_desc_elv");
                        this.reviewLayout = (LinearLayout) convertView.findViewById(R.id.reviewLayout);
                        TextView firstReview = (TextView) convertView.findViewById(R.id.firstReview);
                        if (this.reviewList.length() != 0) {
                            createReview(this.reviewList.length());
                        } else {
                            this.reviewLayout.setVisibility(View.GONE);
                            firstReview.setVisibility(View.VISIBLE);
                            ((TextView) convertView.findViewById(R.id.totalReviews)).setVisibility(View.GONE);
                            ((TextView) convertView.findViewById(R.id.ownReview)).setTextSize(13.0f);
                            firstReview.setTextColor(Color.parseColor("3399cc"));
                        }
                        TextView Total = (TextView) convertView.findViewById(R.id.totalReviews);
                        if (this.reviewList.length() > 0) {
                            Total.setText(getActivity().getResources().getString(R.string.total) + this.reviewList.length() + getResources().getString(R.string._customer_reviews));
                        }
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                return convertView;
            }

            public void openReview(View v) {
                ViewProductSimple.dialog = new Dialog(getActivity());
                ViewProductSimple.dialog.requestWindowFeature(1);
                ViewProductSimple.dialog.setContentView(R.layout.custom_review_dialog);
                ViewProductSimple.dialog.show();
                RatingBar ratePoints = (RatingBar) ViewProductSimple.dialog.findViewById(R.id.review_rating_bar);
                ratePoints.setStepSize(1.0f);
                ratePoints.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        ReviewsTab.this.currentRating = rating + "";
                    }
                });
                TextView cancel = (TextView) ViewProductSimple.dialog.findViewById(R.id.btn_cancel);
                ViewProductSimple.dialog.setTitle(null);
                cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ViewProductSimple.dialog.dismiss();
                    }
                });
                ((TextView) ViewProductSimple.dialog.findViewById(R.id.btn_apply)).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        ReviewsTab.this.onReviewSubmit(ViewProductSimple.dialog);
                    }
                });
            }

            public void onReviewSubmit(Dialog dialog) {
                EditText userNameET = (EditText) ViewProductSimple.dialog.findViewById(R.id.user_name);
                String userName = userNameET.getText().toString().trim();
                EditText cmtET = (EditText) ViewProductSimple.dialog.findViewById(R.id.cmt);
                String cmt = cmtET.getText().toString().trim();
                boolean isFormValidated = true;
                if (userName.matches("")) {
                    userNameET.setError("Your Name " + getResources().getString(R.string.is_require_text));
                    isFormValidated = false;
                }
                if (cmt.matches("")) {
                    cmtET.setError("Your Comment " + getResources().getString(R.string.is_require_text));
                    isFormValidated = false;
                }
                if (isFormValidated) {
                    ViewProductSimple.progress = ProgressDialog.show(getActivity(), null, getResources().getString(R.string.loading), true);
                    ViewProductSimple.progress.setCanceledOnTouchOutside(false);
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("name", userName);
                        jo.put("text", cmt);
                        jo.put("rating", this.currentRating);
                        jo.put("product_id", ViewProductSimple.productId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Connection(getActivity()).execute("writeProductReview", jo.toString());
                }
            }

            public void writeProductReviewResponse(String backresult) {
                try {
                    ViewProductSimple.progress.dismiss();
                    this.responseObject = new JSONObject(backresult);
                    if (this.responseObject.has("message")) {
                        Toast.makeText(getActivity(), this.responseObject.getString("message") + "", Toast.LENGTH_LONG).show();
                        ViewProductSimple.dialog.dismiss();
                        return;
                    }
                    Toast.makeText(getActivity(), this.responseObject.getString("error") + "", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void createReview(int reviewLength) {
                LinearLayout linearLayout;
                Exception e;
                int i = 0;
                LinearLayout linearLayout2 = null;
                while (i < reviewLength) {
                    try {
                        JSONObject reviewListAttributes = this.reviewList.getJSONObject(i);
                        TextView details = new TextView(getActivity());
                        TextView reviewBy = new TextView(getActivity());
                        TextView reviewOn = new TextView(getActivity());
                        RatingBar ratePoints1 = new RatingBar(getActivity(), null, 16842877);
                        ((LayerDrawable) ratePoints1.getProgressDrawable()).getDrawable(2).setColorFilter(getResources().getColor(R.color.filter_color_selected), Mode.SRC_ATOP);
                        linearLayout = new LinearLayout(getActivity());
                        try {
                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                            linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                            linearLayout.setPadding(2, 2, 2, 2);
                            reviewBy.setText(reviewListAttributes.getString("author"));
                            reviewBy.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                            reviewBy.setTextSize(14.0f);
                            reviewBy.setTypeface(null, 1);
                            reviewBy.setBackgroundResource(R.drawable.rect_shape);
                            reviewBy.setPadding(20, 5, 5, 10);
                            reviewOn.setText(reviewListAttributes.getString("date_added"));
                            reviewOn.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                            reviewOn.setTypeface(null, 2);
                            reviewOn.setGravity(Gravity.END);
                            reviewOn.setBackgroundResource(R.drawable.rect_shape);
                            reviewOn.setPadding(5, 5, 20, 10);
                            linearLayout.addView(reviewBy);
                            linearLayout.addView(reviewOn);
                            details.setText(Html.fromHtml(reviewListAttributes.getString("text")));
                            ratePoints1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                            ratePoints1.setRating(Float.parseFloat(reviewListAttributes.getString("rating")));
                            this.reviewLayout.addView(linearLayout);
                            this.reviewLayout.addView(details);
                            this.reviewLayout.addView(ratePoints1);
                            this.reviewLayout.addView(line());
                            i++;
                            linearLayout2 = linearLayout;
                        } catch (Exception e2) {
                            e = e2;
                            Log.d("Error", e.toString());

                        }
                    } catch (Exception e3) {
                        e = e3;
                        Log.d("Error", e.toString());
                        linearLayout = linearLayout2;
                    }
                }
                linearLayout = linearLayout2;
                return;
            }

            public View line() {
                View v = new View(getActivity());
                LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2);
                lineParams.setMargins(20, 20, 20, 20);
                v.setLayoutParams(lineParams);
                v.setBackgroundColor(Color.parseColor("#9b9b9b"));
                v.setPadding(10, 5, 10, 5);
                return v;
            }
        }
    }

    private class CustomOptionData {
        int associatedId;
        Double defaultPrice;
        String formatedDefaultPrice;
        int id;
        Boolean isAffectingPrice;
        Boolean isChanged;
        int isRequired;
        Boolean isValueAddedToPrice = false;
        ArrayList<MultiOptionData> multiOptionList;
        int optionId;
        String priceType;
        int productId;
        int productOptionId;
        String title;
        String type;
        Double unformatedDefaultPrice;

        public int getAssociatedId() {
            return this.associatedId;
        }

        public void setAssociatedId(int associatedId) {
            this.associatedId = associatedId;
        }

        public int getId() {
            return this.id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public ArrayList<MultiOptionData> getMultiOptionListList() {
            return this.multiOptionList;
        }

        public void setMultiOptionListList(ArrayList<MultiOptionData> multiOptionList) {
            this.multiOptionList = multiOptionList;
        }

        public Boolean getIsValueAddedToPrice() {
            return this.isValueAddedToPrice;
        }

        public void setIsValueAddedToPrice(Boolean isValueAddedToPrice) {
            this.isValueAddedToPrice = isValueAddedToPrice;
        }

        public Boolean getIsChanged() {
            return this.isChanged;
        }

        public void setIsChanged(Boolean isChanged) {
            this.isChanged = isChanged;
        }

        public Boolean getIsAffectingPrice() {
            return this.isAffectingPrice;
        }

        public void setIsAffectingPrice(Boolean isAffectingPrice) {
            this.isAffectingPrice = isAffectingPrice;
        }

        public Double getDefaultPrice() {
            return this.defaultPrice;
        }

        public void setDefaultPrice(Double defaultPrice) {
            this.defaultPrice = defaultPrice;
        }

        public int getOptionId() {
            return this.optionId;
        }

        public void setOptionId(int optionId) {
            this.optionId = optionId;
        }

        public int getProductOptionId() {
            return this.productOptionId;
        }

        public void setProductOptionId(int productOptionId) {
            this.productOptionId = productOptionId;
        }

        public CustomOptionData(String title, String type, int isRequired, Double unformatedDefaultPrice, String formatedDefaultPrice, String priceType, int optionId, int productId, Double defaultPrice, int productOptionId) {
            this.title = title;
            this.type = type;
            this.isRequired = isRequired;
            this.unformatedDefaultPrice = unformatedDefaultPrice;
            this.formatedDefaultPrice = formatedDefaultPrice;
            this.priceType = priceType;
            this.optionId = optionId;
            this.productOptionId = productOptionId;
            this.productId = productId;
            this.defaultPrice = defaultPrice;
        }

        public int getProductId() {
            return this.productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getPriceType() {
            return this.priceType;
        }

        public void setPriceType(String priceType) {
            this.priceType = priceType;
        }

        public String getFormatedDefaultPrice() {
            return this.formatedDefaultPrice;
        }

        public void setFormatedDefaultPrice(String formatedDefaultPrice) {
            this.formatedDefaultPrice = formatedDefaultPrice;
        }

        public Double getUnformatedDefaultPrice() {
            return this.unformatedDefaultPrice;
        }

        public void setUnformatedDefaultPrice(Double unformatedDefaultPrice) {
            this.unformatedDefaultPrice = unformatedDefaultPrice;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getIsRequired() {
            return this.isRequired;
        }

        public void setIsRequired(int isRequired) {
            this.isRequired = isRequired;
        }

        class MultiOptionData {
            Double defaultPrice;
            int id;
            int optionTypeId;
            int option_id;
            String priceType;

            public MultiOptionData(int id, int option_id, int optionTypeId, Double defaultPrice, String priceType) {
                this.id = id;
                this.option_id = option_id;
                this.optionTypeId = optionTypeId;
                this.defaultPrice = defaultPrice;
                this.priceType = priceType;
            }

            public int getOption_id() {
                return this.option_id;
            }

            public void setOption_id(int option_id) {
                this.option_id = option_id;
            }

            public String getPriceType() {
                return this.priceType;
            }

            public void setPriceType(String priceType) {
                this.priceType = priceType;
            }

            public int getId() {
                return this.id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getOptionTypeId() {
                return this.optionTypeId;
            }

            public void setOptionTypeId(int optionTypeId) {
                this.optionTypeId = optionTypeId;
            }

            public Double getDefaultPrice() {
                return this.defaultPrice;
            }

            public void setDefaultPrice(Double defaultPrice) {
                this.defaultPrice = defaultPrice;
            }

            public int getOptionId() {
                return this.optionTypeId;
            }

            public void setOptionId(int optionTypeId) {
                this.optionTypeId = optionTypeId;
            }

            public MultiOptionData(int optionTypeId, Double defaultPrice, String priceType) {
                this.optionTypeId = optionTypeId;
                this.defaultPrice = defaultPrice;
                this.priceType = priceType;
            }

            public MultiOptionData(int id, int optionTypeId, Double defaultPrice, String priceType) {
                this.id = id;
                this.optionTypeId = optionTypeId;
                this.defaultPrice = defaultPrice;
                this.priceType = priceType;
            }

        }

    }

    @SuppressLint({"InflateParams"})
    private class ProductDescExpListAdapter extends BaseExpandableListAdapter {
        private Context _context;
        private HashMap<String, List<String>> _listDataChild;
        private List<String> _listDataHeader;
        LinearLayout reviewLayout;
        JSONArray reviewList = null;
        private JSONArray specificationList;

        public ProductDescExpListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        public Object getChild(int groupPosition, int childPosititon) {
            return ((List) this._listDataChild.get(this._listDataHeader.get(groupPosition))).get(childPosititon);
        }

        public long getChildId(int groupPosition, int childPosition) {
            return (long) childPosition;
        }

        @SuppressLint({"InflateParams"})
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            String childText = (String) getChild(groupPosition, childPosition);
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(LAYOUT_INFLATER_SERVICE);
            if (groupPosition == 0) {
                WebView myWebView = new WebView(this._context);
                String html = childText;
                try {
                    myWebView.setFocusable(true);
                    myWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
                    return myWebView;
                } catch (Exception e) {
                    e.printStackTrace();
                    return myWebView;
                }
            }
            if ((this._listDataHeader.get(groupPosition)).equals("Specification")) {
                convertView = infalInflater.inflate(R.layout.layout_additional_information_product, null);
                convertView.setTag("additional_information_product");
                try {
                    this.specificationList = new JSONArray(childText);
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                LinearLayout specification_Values = (LinearLayout) convertView.findViewById(R.id.specification_Value_Layout);
                for (int i = 0; i < this.specificationList.length(); i++) {
                    try {
                        JSONObject specificationData = this.specificationList.getJSONObject(i);
                        TextView textView = new TextView(this._context);
                        textView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        textView.setBackgroundResource(R.drawable.rect_shape);
                        textView.setText(specificationData.getString("name"));
                        textView.setTypeface(null, 1);
                        textView.setPadding(20, 5, 5, 10);
                        LinearLayout attributesLayout = new LinearLayout(this._context);
                        attributesLayout.setOrientation(LinearLayout.HORIZONTAL);
                        attributesLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                        int j = 0;
                        while (true) {
                            if (j >= specificationData.getJSONArray("attribute").length()) {
                                break;
                            }
                            JSONObject specificationDataAttributes = specificationData.getJSONArray("attribute").getJSONObject(j);
                            TextView attributeLabel = new TextView(this._context);
                            attributeLabel.setText(specificationDataAttributes.getString("name"));
                            attributeLabel.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0.7f));
                            attributeLabel.setBackgroundResource(R.drawable.rect_shape);
                            attributeLabel.setPadding(20, 5, 5, 10);
                            TextView attributeValue = new TextView(this._context);
                            attributeValue.setText(specificationDataAttributes.getString("text"));
                            attributeValue.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                            attributeValue.setBackgroundResource(R.drawable.rect_shape);
                            attributeValue.setPadding(20, 5, 5, 10);
                            attributesLayout.addView(attributeLabel);
                            attributesLayout.addView(attributeValue);
                            j++;
                        }
                        specification_Values.addView(textView);
                        specification_Values.addView(attributesLayout);
                    } catch (JSONException e3) {
                        e3.printStackTrace();
                    }
                }
            } else {
                try {
                    this.reviewList = new JSONArray(childText);
                } catch (JSONException e32) {
                    Log.d("Exception@getChildView", "Review product ELV");
                    e32.printStackTrace();
                }
                try {
                    if (childText.equals("[]")) {
                        convertView = infalInflater.inflate(R.layout.item_category_drawer_list, null);
                        TextView txtListChild = (TextView) convertView.findViewById(R.id.category_item_list);
                        txtListChild.setTag("first_review");
                        txtListChild.setText(R.string.be_the_first_to_review_this_product);
                        txtListChild.setTextColor(Color.parseColor("#3399cc"));
                    } else {
                        convertView = infalInflater.inflate(R.layout.layout_review_in_product_desc_elv, null);
                        convertView.setTag("review_in_product_desc_elv");
                        this.reviewLayout = (LinearLayout) convertView.findViewById(R.id.reviewLayout);
                        TextView firstReview = (TextView) convertView.findViewById(R.id.firstReview);
                        if (this.reviewList.length() != 0) {
                            createReview(this.reviewList.length());
                        } else {
                            this.reviewLayout.setVisibility(View.GONE);
                            firstReview.setVisibility(View.VISIBLE);
                            (convertView.findViewById(R.id.totalReviews)).setVisibility(View.GONE);
                            ((TextView) convertView.findViewById(R.id.ownReview)).setTextSize(13.0f);
                            firstReview.setTextColor(Color.parseColor("3399cc"));
                        }
                        ((TextView) convertView.findViewById(R.id.totalReviews)).setText(this._context.getResources().getString(R.string.total) + " " + this.reviewList.length() + this._context.getResources().getString(R.string._customer_reviews));
                    }
                } catch (Exception e22) {
                    Log.d("review in product elv", e22.getMessage());
                }
            }
            return convertView;
        }

        public int getChildrenCount(int groupPosition) {
            return ((List) this._listDataChild.get(this._listDataHeader.get(groupPosition))).size();
        }

        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        public long getGroupId(int groupPosition) {
            return (long) groupPosition;
        }

        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                convertView = ((LayoutInflater) this._context.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_category_drawer_group, null);
            }
            TextView lblListHeader = (TextView) convertView.findViewById(R.id.category_item_group);
            lblListHeader.setTypeface(null, 1);
            lblListHeader.setText(headerTitle);
            if (getChildrenCount(groupPosition) != 0) {
                ImageView childLogo = (ImageView) convertView.findViewById(R.id.childOpenCloseLogo);
                if (isExpanded) {
                    childLogo.setImageResource(R.drawable.ic_sub);
                } else {
                    childLogo.setImageResource(R.drawable.ic_add);
                }
            }
            return convertView;
        }

        public boolean hasStableIds() {
            return false;
        }

        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public void createReview(int reviewLength) {
            LinearLayout linearLayout;
            Exception e;
            int i = 0;
            LinearLayout linearLayout2 = null;
            while (i < reviewLength) {
                try {
                    JSONObject reviewListAttributes = this.reviewList.getJSONObject(i);
                    TextView details = new TextView(this._context);
                    TextView reviewBy = new TextView(this._context);
                    TextView reviewOn = new TextView(this._context);
                    RatingBar ratePoints1 = new RatingBar(this._context, null, 16842877);
                    linearLayout = new LinearLayout(this._context);
                    try {
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(-1, -2));
                        linearLayout.setPadding(2, 2, 2, 2);
                        reviewBy.setText(reviewListAttributes.getString("author"));
                        reviewBy.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                        reviewBy.setTextSize(14.0f);
                        reviewBy.setTypeface(null, 1);
                        reviewBy.setBackgroundResource(R.drawable.rect_shape);
                        reviewBy.setPadding(20, 5, 5, 10);
                        reviewOn.setText(reviewListAttributes.getString("date_added"));
                        reviewOn.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f));
                        reviewOn.setTypeface(null, 2);
                        reviewOn.setGravity(8388613);
                        reviewOn.setBackgroundResource(R.drawable.rect_shape);
                        reviewOn.setPadding(5, 5, 20, 10);
                        linearLayout.addView(reviewBy);
                        linearLayout.addView(reviewOn);
                        details.setText(Html.fromHtml(reviewListAttributes.getString("text")));
                        ratePoints1.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        ratePoints1.setRating(Float.parseFloat(reviewListAttributes.getString("rating")));
                        this.reviewLayout.addView(linearLayout);
                        this.reviewLayout.addView(details);
                        this.reviewLayout.addView(ratePoints1);
                        this.reviewLayout.addView(line());
                        i++;
                        linearLayout2 = linearLayout;
                    } catch (Exception e2) {
                        e = e2;
                        Log.d("Error", e.toString());

                    }
                } catch (Exception e3) {
                    e = e3;
                    Log.d("Error", e.toString());

                    linearLayout = linearLayout2;
                }
            }
            linearLayout = linearLayout2;
            return;
        }

        public View line() {
            View v = new View(this._context);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 2);
            lineParams.setMargins(0, 5, 0, 5);
            v.setLayoutParams(lineParams);
            v.setBackgroundColor(Color.parseColor("#9b9b9b"));
            return v;
        }
    }

    private class ProductData {
        String availability;
        Double currentPrice;
        String description;
        String formatedPrice;
        int isInRange;
        int msrpDisplayActualPriceType;
        int msrpEnabled;
        Double price;
        PriceFormat priceFormat;
        String productName;
        String shortDescription;
        String specialPrice;

        public Double getCurrentPrice() {
            return this.currentPrice;
        }

        public void setCurrentPrice(Double currentPrice) {
            this.currentPrice = currentPrice;
        }

        public ProductData(String productName, String description, String shortDescription, String availability, Double price, int msrpEnabled, int isInRange, PriceFormat priceFormat, int msrpDisplayActualPriceType, String formatedPrice, String specialPrice) {
            this.productName = productName;
            this.description = description;
            this.shortDescription = shortDescription;
            this.availability = availability;
            this.price = price;
            this.msrpEnabled = msrpEnabled;
            this.isInRange = isInRange;
            this.priceFormat = priceFormat;
            this.msrpDisplayActualPriceType = msrpDisplayActualPriceType;
            this.formatedPrice = formatedPrice;
            this.specialPrice = specialPrice;
        }

        public String getFormatedPrice() {
            return this.formatedPrice;
        }

        public void setFormatedPrice(String formatedPrice) {
            this.formatedPrice = formatedPrice;
        }

        public int getMsrpDisplayActualPriceType() {
            return this.msrpDisplayActualPriceType;
        }

        public void setMsrpDisplayActualPriceType(int msrpDisplayActualPriceType) {
            this.msrpDisplayActualPriceType = msrpDisplayActualPriceType;
        }

        public PriceFormat getPriceFormat() {
            return this.priceFormat;
        }

        public void setPriceFormat(PriceFormat priceFormat) {
            this.priceFormat = priceFormat;
        }

        public int getIsInRange() {
            return this.isInRange;
        }

        public void setIsInRange(int isInRange) {
            this.isInRange = isInRange;
        }

        public String getProductName() {
            return this.productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getShortDescription() {
            return this.shortDescription;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public String getAvailability() {
            return this.availability;
        }

        public void setAvailability(String availability) {
            this.availability = availability;
        }

        public Double getPrice() {
            return this.price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public int getMsrpEnabled() {
            return this.msrpEnabled;
        }

        public void setMsrpEnabled(int msrpEnabled) {
            this.msrpEnabled = msrpEnabled;
        }

        class PriceFormat {
            String pattern;
            int precision;

            public String getPattern() {
                return this.pattern;
            }

            public void setPattern(String pattern) {
                this.pattern = pattern;
            }

            public int getPrecision() {
                return this.precision;
            }

            public void setPrecision(int precision) {
                this.precision = precision;
            }

            public PriceFormat(String pattern, int precision) {
                this.pattern = pattern;
                this.precision = precision;
            }
        }
    }

    private class NonSwipeableViewPager extends ViewPager {

        public NonSwipeableViewPager(Context context) {
            super(context);
        }

        public NonSwipeableViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int height = 0;
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if (h > height) {
                    height = h;
                }
            }
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }
}
