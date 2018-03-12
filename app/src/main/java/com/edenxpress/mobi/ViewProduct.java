package com.edenxpress.mobi;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.edenxpress.mobi.modules.CirclePageIndicator;
import com.edenxpress.mobi.modules.ImagePagerAdapter;
import com.edenxpress.mobi.modules.ImageSlideShow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewProduct extends BaseActivity {
    private static boolean running = false;
    private int product_id;
    private Toolbar toolbar;
    private String product_name;
    private ViewPager images_pager;
    private int screen_width;
    private ImagePagerAdapter images_adapter;
    private ImageSlideShow images_slide_show;
    private LinearLayout pagerContainer;
    private CirclePageIndicator pageIndicator;
    private JSONObject jsonResponse;

    @Override
    public void onStart() {
        super.onStart();
        running = true;
    }

    @Override
    public void onDestroy() {
        super.onStop();
        running = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product_id = Integer.parseInt(getIntent().getExtras().getString("idOfProduct"));
        product_name = getIntent().getExtras().getString("nameOfProduct");
        setContentView(R.layout.activity_view_product);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        screen_width = Utils.getScreenWidth(this);
        fetchProductData();
        images_pager = (ViewPager) findViewById(R.id.images_pager);
        pagerContainer = (LinearLayout) images_pager.getParent();

    }

    private void fetchProductData() {
        isOnline();
        if (this.isInternetAvailable) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("width", screen_width);
                jo.put("product_id", product_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new Connection(this).execute("getProduct", jo.toString());
        } else showRetryDialog(this);
    }

    //api responses
    public void getProductResponse(String backresult) {
        List<Object> imagesURL = new ArrayList<>();
        try {
            jsonResponse = new JSONObject(backresult);
            JSONArray images = jsonResponse.getJSONArray("images");
            imagesURL.add(jsonResponse.getString("popup"));
            for (int i = 0; i < images.length(); i++) {
                imagesURL.add(images.getJSONObject(i).getString("popup"));
            }
            images_slide_show = new ImageSlideShow(this, imagesURL);
            images_slide_show.setViewPager(images_pager);
            images_pager.setVisibility(View.GONE);
            pageIndicator = images_slide_show.getPageIndicator();
            pageIndicator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            pagerContainer.addView(pageIndicator, 0);
            pagerContainer.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class Product {

    }
}
