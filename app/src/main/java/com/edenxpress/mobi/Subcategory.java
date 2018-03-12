package com.edenxpress.mobi;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.AppBarLayout.OnOffsetChangedListener;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ICT on 1/5/2017.
 */

public class Subcategory extends BaseActivity {
    private ActionBar actionBar;
    private Adapter adapter;
    private ViewPager bannerPager;
    private TabLayout tabs;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    int menuId;
    private int selectedTab;
    OnPageChangeListener subcategoryViewPagerOnPageChangeListener = new OnPageChangeListener() {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            bannerPager.setCurrentItem(position);
            if (collapsingToolbarLayout.getTitle() != "")
                collapsingToolbarLayout.setTitle(adapter.getPageTitle(position));
            selectedTab = position;
        }

        public void onPageScrollStateChanged(int state) {
        }
    };
    private Toolbar toolbar;
    private ViewPager viewPager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOnline();
        if (isInternetAvailable) {
            setContentView(R.layout.activity_subcategory);
            Log.d("DEBUG", "Creating Subcategory Activity");
            try {
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    menuId = extras.getInt("mDrawerIdentifier");
                }
            } catch (Exception e) {
                e.printStackTrace();
                trackException(e, this);
            }
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
            collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.toolbar_title_color));
            collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this, R.color.transparent));
            collapsingToolbarLayout.setScrimAnimationDuration(0);
            try {
                setTitle(MainActivity.categoriesJSONObject.getJSONObject(menuId).getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bannerPager = (ViewPager) findViewById(R.id.banner_pager);
            bannerPager.setAdapter(new bannerImagePagerAdapter(this));
            bannerPager.addOnPageChangeListener(new OnPageChangeListener() {
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                public void onPageSelected(int position) {
                    viewPager.setCurrentItem(position);
                }

                public void onPageScrollStateChanged(int state) {
                }
            });
            bannerPager.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    viewPager.onTouchEvent(event);
                    return false;
                }
            });
            adapter = new Adapter(getSupportFragmentManager());
            initViewPager();
            ((TabLayout) findViewById(R.id.tabs)).setupWithViewPager(viewPager);
            ((AppBarLayout) findViewById(R.id.appbar)).addOnOffsetChangedListener(new OnOffsetChangedListener() {
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    //Log.d("vertical offset", verticalOffset + "");
                    int totalScrollRange = appBarLayout.getTotalScrollRange();
                    int primary_color = ContextCompat.getColor(Subcategory.this, R.color.primary_color);
                    int opacity = 0x50;
                    int final_opacity = Color.alpha(primary_color);
                    int transparent = Color.argb(opacity, Color.red(primary_color), Color.green(primary_color), Color.blue(primary_color));
                    int max_offset = (totalScrollRange - 200) * -1;
                    if (verticalOffset <= max_offset) {
                        int offset_percentage = ((-1 * verticalOffset) + max_offset) * 100 / (totalScrollRange + max_offset);
                        int alpha = (final_opacity - opacity) * offset_percentage / 100 + opacity;
                        int transition_color = Color.argb(alpha, Color.red(primary_color), Color.green(primary_color), Color.blue(primary_color));

                        toolbar.setBackgroundColor(transition_color);
                        collapsingToolbarLayout.setTitle(adapter.getPageTitle(selectedTab));
                        menu.findItem(R.id.action_bell).setVisible(false);
                        menu.findItem(R.id.action_cart).setVisible(false);
                        return;
                    }
                    collapsingToolbarLayout.setTitle("");
                    toolbar.setBackgroundColor(transparent);

                    if (menu != null) {
                        menu.findItem(R.id.action_bell).setVisible(true);
                        menu.findItem(R.id.action_cart).setVisible(true);
                    }
                }
            });
        } else {
            showRetryDialog(this);
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.content_viewpager);
        if (viewPager != null) {
            //Log.d("DEBUG", MainActivity.categoriesJSONObject.toString());
            for (int i = 0; i < MainActivity.categoriesJSONObject.length(); i++) {
                setupViewPager(i);
            }
        }
        int i = 0;
        while (i < MainActivity.categoriesJSONObject.length() && menuId != i) {
            selectedTab++;
            i++;
        }
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(subcategoryViewPagerOnPageChangeListener);
        viewPager.setCurrentItem(selectedTab);
    }

    private void setupViewPager(int index) {
        try {
            adapter.addFragment(SubcategoryFragment.newInstance(index), MainActivity.categoriesJSONObject.getJSONObject(index).getString("name"));
        } catch (JSONException e) {
            e.printStackTrace();
            trackException(e, this);
        }
    }

    public class bannerImagePagerAdapter extends PagerAdapter {
        private Context mContext;

        public bannerImagePagerAdapter(Context context) {
            mContext = context;
        }

        public Object instantiateItem(ViewGroup collection, int position) {
            ImageView bannerImage = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.item_subcategory_banner_pager_image, collection, false);
            try {
                Log.d("category image", MainActivity.categoriesJSONObject.getJSONObject(position) + "");
                if (MainActivity.categoriesJSONObject.getJSONObject(position).getString("image").equalsIgnoreCase("null")) {
                    Picasso.with(Subcategory.this).load(R.drawable.placeholder).into(bannerImage);
                } else {
                    Picasso.with(Subcategory.this).load(MainActivity.categoriesJSONObject.getJSONObject(position).getString("image")).into(bannerImage);
                }
                bannerImage.setScaleType(ScaleType.MATRIX);
            } catch (Exception e) {
                e.printStackTrace();
            }
            collection.addView(bannerImage);
            return bannerImage;
        }

        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        public int getCount() {
            return MainActivity.categoriesJSONObject.length();
        }

        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    public class Adapter extends FragmentPagerAdapter {
        private final List<String> mFragmentTitles = new ArrayList();
        private final List<Fragment> mFragments = new ArrayList();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(Html.fromHtml(title).toString());
        }

        public Fragment getItem(int position) {
            return (Fragment) this.mFragments.get(position);
        }

        public int getCount() {
            return mFragments.size();
        }

        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

}

