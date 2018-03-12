package com.edenxpress.mobi;


import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;


/**
 * Created by ICT on 12/27/2016.
 */

public class BaseNavigationDrawerActivity extends BaseActivity {
    private static final String CHILDREN = "children";
    private static final String ID = "category_id";
    private static final String NAME = "name";
    private HashMap<Integer, List<String>> childActions = new HashMap();
    private HashMap<String, List<DrawerData>> childList;
    HashMap<String, String> childMapToID;
    private Context context;
    private int counterForChildAction;
    private NavigationDrawerAdapter drawerAdapter;
    private int drawerHeaderCounter;
    private DrawerLayout drawerLayout;
    protected ExpandableListView expListView;
    private ArrayList<DrawerData> headerList;
    private int lastExpandedPosition = -1;
    ArrayList<JSONObject> level2List;
    protected ActionBarDrawerToggle mDrawerToggle;
    private List<Element> mainActions = new ArrayList();

    protected OnChildClickListener navigationDrawerChildClickListener = new OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            String pathName = getChildId(groupPosition, childPosition);
            String categoryName = getChildName(groupPosition, childPosition);
            if (getChildType(groupPosition, childPosition).equalsIgnoreCase("categoryType")) {
                Intent intent;
                if (getSharedPreferences("categoryView", MODE_PRIVATE).getBoolean("isGridView", false)) {
                    intent = new Intent(context, mMallApplication.viewCategoryGrid());
                } else {
                    intent = new Intent(context, mMallApplication.viewCategoryList());
                }
                intent.putExtra("ID", pathName);
                intent.putExtra("CATEGORY_NAME", categoryName);
                intent.putExtra("drawerData", CategoryActivity.drawerJsonObject + "");
                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else if (getChildType(groupPosition, childPosition).equalsIgnoreCase("LanguageType")) {
                String selectedLanguage = configShared.getString("storeCode", "en");
                try {
                    String languageToLoad = getChildId(groupPosition, childPosition);
                    Log.d("selectedLanguage", selectedLanguage);
                    Log.d("languageToLoad", languageToLoad);
                    configShared.edit()
                            .putString("storeLanguage", getChildName(groupPosition, childPosition))
                            .putString("storeCode", getChildId(groupPosition, childPosition))
                            .apply();
                    Locale locale = new Locale(languageToLoad);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getResources().updateConfiguration(config, getResources().getDisplayMetrics());
                    JSONObject jo1 = new JSONObject().put("code", languageToLoad);
                    new Connection(context).execute("language", jo1.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                String currency = getChildId(groupPosition, childPosition);
                JSONObject jo1 = new JSONObject();
                try {
                    jo1.put("code", currency);
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
                new Connection(context).execute("currency", jo1.toString());
            }
            drawerLayout.closeDrawer(expListView);
            return false;
        }
    };

    protected OnGroupClickListener navigationDrawerGroupClickListener = new OnGroupClickListener() {
        public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
            List<DrawerData> list = (List) childList.get(((DrawerData) headerList.get(groupPosition)).code);
            if (list == null) {
                return true;
            }
            if (list.size() > 0) {
                return false;
            }
            return false;
        }
    };

    protected OnGroupExpandListener navigationDrawerGroupExpandListener = new OnGroupExpandListener() {
        public void onGroupExpand(int groupPosition) {
            if (!(lastExpandedPosition == -1 || groupPosition == lastExpandedPosition)) {
                expListView.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = groupPosition;
        }
    };
    HashMap<String, String> parentMapToID;
    HashMap<String, HashMap<String, String>> parentToChildMap;
    private JSONArray storeDataArr;
    String storeIdShared;
    private int storePosition;

    protected void setUpDrawer(DrawerLayout mDrawerLayout, ExpandableListView mExpListView, Context context) {
        Log.d("DEBUG", "setUpDrawer called");
        this.expListView = mExpListView;
        this.drawerLayout = mDrawerLayout;
        this.configShared = getSharedPreferences("configureView", MODE_PRIVATE);
        this.context = context;
        this.drawerLayout.setDrawerShadow(new ColorDrawable(Color.parseColor("#80000000")), GravityCompat.END);
        this.drawerLayout.setScrimColor(Color.parseColor("#80000000"));
        this.drawerLayout.setVisibility(View.GONE);
        this.expListView.addHeaderView((ViewGroup) getLayoutInflater().inflate(R.layout.layout_elv_drawer_header, this.expListView, false));
        this.expListView.setIndicatorBounds(getScreenWidth() - dpToPx(50.0f), getScreenWidth() - dpToPx(10.0f));
    }

    protected void createDrawer(JSONObject drawerJsonData) {
        try {
            createDrawerData(drawerJsonData);
            this.drawerAdapter = new NavigationDrawerAdapter(this, this.headerList, this.childList);
            this.expListView.setAdapter(this.drawerAdapter);
            this.expListView.setOnGroupClickListener(this.navigationDrawerGroupClickListener);
            this.expListView.setOnGroupExpandListener(this.navigationDrawerGroupExpandListener);
            this.expListView.setOnChildClickListener(this.navigationDrawerChildClickListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void createDrawerData(JSONObject homeData) {
        this.headerList = new ArrayList();
        this.childList = new HashMap();
        this.drawerHeaderCounter = 0;
        try {
            List<DrawerData> temp;
            int j;
            HashMap hashMap;
            ArrayList arrayList;
            int i;
            JSONArray categoryData = homeData.getJSONArray("categories");
            JSONArray languageData = homeData.getJSONObject("languages").getJSONArray("languages");
            JSONArray currenciesData = homeData.getJSONObject("currencies").getJSONArray("currencies");
            this.headerList.add(new DrawerData(getResources().getString(R.string.category), null, "HeadingType", R.drawable.ic_category + ""));
            this.drawerHeaderCounter++;
            for (int i2 = 0; i2 < categoryData.length(); i2++) {
                JSONObject categoryObject = categoryData.getJSONObject(i2);
                this.headerList.add(new DrawerData(categoryObject.getString("name"), categoryObject.getString("path"), "categoryType", null));
                temp = new ArrayList();
                temp.add(new DrawerData(getResources().getString(R.string.view_all_product) + " " + categoryObject.getString("name"), categoryObject.getString("path"), "categoryType", null));
                for (j = 0; j < categoryObject.getJSONArray("children").length(); j++) {
                    JSONObject categoryDataChild = categoryObject.getJSONArray("children").getJSONObject(j);
                    temp.add(new DrawerData(categoryDataChild.getString("name"), categoryDataChild.getString("path"), "categoryType", null));
                }
                hashMap = this.childList;
                arrayList = this.headerList;
                i = this.drawerHeaderCounter;
                this.drawerHeaderCounter = i + 1;
                hashMap.put(((DrawerData) arrayList.get(i)).code, temp);
            }
            this.headerList.add(new DrawerData(homeData.getJSONObject("languages").getString("text_language"), null, "HeadingType", R.drawable.ic_store + ""));
            this.drawerHeaderCounter++;
            this.headerList.add(new DrawerData(homeData.getJSONObject("languages").getString("text_language"), homeData.getJSONObject("languages").getString("code"), "LanguageType", homeData.getJSONObject("languages").getString("image")));
            temp = new ArrayList();
            for (j = 0; j < languageData.length(); j++) {
                JSONObject languageObject = languageData.getJSONObject(j);
                temp.add(new DrawerData(languageObject.getString("name"), languageObject.getString("code"), "LanguageType", languageObject.getString("image")));
            }
            hashMap = this.childList;
            arrayList = this.headerList;
            i = this.drawerHeaderCounter;
            this.drawerHeaderCounter = i + 1;
            hashMap.put(((DrawerData) arrayList.get(i)).code, temp);
            String currencyData = homeData.getJSONObject("currencies").getString("text_currency");
            this.headerList.add(new DrawerData(currencyData.substring(currencyData.indexOf(" "), currencyData.length()), null, "HeadingType", R.drawable.ic_quick + ""));
            this.drawerHeaderCounter++;
            this.headerList.add(new DrawerData(homeData.getJSONObject("currencies").getString("text_currency"), homeData.getJSONObject("currencies").getString("code"), "currencyType", null));
            temp = new ArrayList();
            for (j = 0; j < currenciesData.length(); j++) {
                JSONObject currenciesObject = currenciesData.getJSONObject(j);
                temp.add(new DrawerData(currenciesObject.getString("title"), currenciesObject.getString("code"), "currencyType", null));
            }
            hashMap = this.childList;
            arrayList = this.headerList;
            i = this.drawerHeaderCounter;
            this.drawerHeaderCounter = i + 1;
            hashMap.put(((DrawerData) arrayList.get(i)).code, temp);
        } catch (JSONException ex) {
            Log.d("CATCH", ex.toString());
        }
    }

    public String getGroupId(int groupPosition) {
        return ((DrawerData) this.headerList.get(groupPosition)).code;
    }

    public String getChildId(int groupPosition, int childPosition) {
        return ((DrawerData) ((List) this.childList.get(((DrawerData) this.headerList.get(groupPosition)).code)).get(childPosition)).code;
    }

    public String getChildName(int groupPosition, int childPosition) {
        return ((DrawerData) ((List) this.childList.get(((DrawerData) this.headerList.get(groupPosition)).code)).get(childPosition)).name;
    }

    public String getChildType(int groupPosition, int childPosition) {
        return ((DrawerData) ((List) this.childList.get(((DrawerData) this.headerList.get(groupPosition)).code)).get(childPosition)).listType;
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (this.isInternetAvailable) {
            this.mDrawerToggle.syncState();
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.mDrawerToggle == null || !this.mDrawerToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
