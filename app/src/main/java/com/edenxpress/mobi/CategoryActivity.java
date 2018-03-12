package com.edenxpress.mobi;

import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.edenxpress.mobi.analytics.MallApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by HP 15-P038 on Jan 7 17.
 */

public class CategoryActivity extends BaseNavigationDrawerActivity {
    static final int INTENT_LAYERED_NAVIGATION_REQUEST = 1;
    static final int INTENT_SORT_BY_REQUEST = 2;
    public static int LAYOUT_ITEM_GRID = 0;
    public static int LAYOUT_ITEM_LIST = 1;
    static JSONObject drawerJsonObject;
    static HashMap<String, String> filterDataHashMap;
    private String categoryId;
    private String categoryName;
    private SharedPreferences categoryViewShared;
    private String customId;
    public Editor editor;
    private ExpandableListView expListViewForSub;
    private Bundle extras;
    private String filterString;
    private boolean loading = false;
    private ProgressDialog mCategoryActivityConnectionProgressBar;
    private DrawerLayout mDrawerLayout;
    private LinearLayoutManager mLayoutManager;
    private MallApplication mMallApplication;
    protected Toast mToast;
    public static JSONObject mainObject;
    private String manufacturerId;
    private String manufacturerTitle;
    public MyAdapter myAdapter;
    protected int pageNumber = 1;
    private List<ParentListItem> parentListItems;
    int productTotal;
    private String queryStringJSON;
    private RecyclerView recyclerView;
    private int screen_width;
    private String searchQuery = "";
    private SearchView searchView;
    String[] sortData = new String[]{"", ""};
    private NavigationDrawerAdapter subCategoryAdapter;
    private RecyclerView subCategoryRecyclerView;
    private List<ParentListItem> subchildList;
    private List<ParentListItem> subheaderList;
    private Toolbar toolbar;
    public int totalItems;
    private ImageView viewSwticherImageView;

    protected OnChildClickListener ChildClickListenerForSubCategory = new OnChildClickListener() {
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            Intent intent;
            if (getSharedPreferences("categoryView", MODE_PRIVATE).getBoolean("isGridView", false)) {
                intent = new Intent(CategoryActivity.this, mMallApplication.viewCategoryGrid());
            } else {
                intent = new Intent(CategoryActivity.this, mMallApplication.viewCategoryList());
            }
            intent.putExtra("CATEGORY_NAME", categoryName);
            intent.putExtra("drawerData", drawerJsonObject + "");
            startActivity(intent);
            return false;
        }
    };

    private OnScrollListener ScrollListener = new OnScrollListener() {
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            int lastCompletelyVisibleItemPosition;
            super.onScrolled(recyclerView, dx, dy);
            if (categoryViewShared.getBoolean("isGridView", false)) {
                try {
                    lastCompletelyVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                } catch (Exception e) {
                    Log.d("DEBUG", "lastCompletelyVisibleItemPosition");
                    lastCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                }
            } else {
                try {
                    lastCompletelyVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                } catch (Exception e2) {
                    lastCompletelyVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                }
            }
            try {
                if (mToast != null) {
                    mToast.setText((lastCompletelyVisibleItemPosition + 1) + CategoryActivity.this.getResources().getString(R.string.of_toast_for_no_of_item) + CategoryActivity.this.productTotal);
                } else {
                    mToast = Toast.makeText(CategoryActivity.this, (lastCompletelyVisibleItemPosition + 1) + CategoryActivity.this.getResources().getString(R.string.of_toast_for_no_of_item) + CategoryActivity.this.productTotal, Toast.LENGTH_SHORT);
                }
            } catch (NotFoundException e1) {
                e1.printStackTrace();
            }
            if (dy > 5) {
                mToast.show();
            }
            if (dy < -80 || dy > 80) {
                findViewById(R.id.notificationLayout).setVisibility(View.GONE);
            }
            try {
                if (lastCompletelyVisibleItemPosition == totalItems - 1 && totalItems < productTotal && !loading) {
                    findViewById(R.id.listcategoryRequestBar).setVisibility(View.VISIBLE);
                    loading = true;
                    CategoryActivity categoryActivity = CategoryActivity.this;
                    categoryActivity.pageNumber++;
                    if (!CategoryActivity.this.searchQuery.isEmpty()) {
                        new Connection(CategoryActivity.this).execute("productSearch", getJSONData().toString());
                    }
                    if (CategoryActivity.this.extras.containsKey("manufacturer_id")) {
                        new Connection(CategoryActivity.this).execute("manufacturerInfo", getJSONData().toString());
                        return;
                    }
                    new Connection(CategoryActivity.this).execute("productCategory", getJSONData().toString());
                }
            } catch (NumberFormatException e3) {
                e3.printStackTrace();
            }
        }
    };

    OnClickListener viewSwitcherListener = new OnClickListener() {
        public void onClick(View v) {
            try {
                Editor editor = categoryViewShared.edit();
                if (myAdapter.getViewType() == LAYOUT_ITEM_GRID) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(CategoryActivity.this));
                    myAdapter.setType(LAYOUT_ITEM_LIST);
                    viewSwticherImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_grid, null));
                    editor.putBoolean("isGridView", false);
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(CategoryActivity.this, 2));
                    myAdapter.setType(LAYOUT_ITEM_GRID);
                    viewSwticherImageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_list, null));
                    editor.putBoolean("isGridView", true);
                }
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void onBackPressed() {
        super.onBackPressed();
        if (this.extras.containsKey("isNotification")) {
            Intent i = new Intent(this, MainActivity.class);
            i.setFlags(FLAG_ACTIVITY_SINGLE_TOP | FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_navigation_drawer);
        this.mMallApplication = (MallApplication) getApplication();
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.base_content_frame);
        View activity_category = getLayoutInflater().inflate(R.layout.activity_category, null);
        contentFrame.addView(activity_category);
        this.recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        filterDataHashMap = new HashMap();
        this.extras = getIntent().getExtras();
        if (this.extras != null) {
            if ("android.intent.action.SEARCH".equals(getIntent().getAction())) {
                this.searchQuery = getIntent().getStringExtra("query");
                findViewById(R.id.shop_by_button_layout).setVisibility(View.GONE);
            } else if (this.extras.containsKey("searchTerm")) {
                this.searchQuery = this.extras.getString("searchTerm");
                findViewById(R.id.shop_by_button_layout).setVisibility(View.GONE);
                setTitle(this.searchQuery);
            } else if (this.extras.containsKey("queryStringJSON")) {
                this.queryStringJSON = this.extras.getString("queryStringJSON");
                findViewById(R.id.shop_by_button_layout).setVisibility(View.GONE);
            } else if (this.extras.containsKey("type") && this.extras.get("type").toString().equalsIgnoreCase("custom")) {
                this.customId = this.extras.getString("id");
            } else {
                if (this.extras.containsKey("manufacturer_id")) {
                    this.manufacturerId = this.extras.getString("manufacturer_id");
                    this.manufacturerTitle = this.extras.getString("imageTitle");
                    setTitle(Html.fromHtml(this.manufacturerTitle));
                    findViewById(R.id.shop_by_button_layout).setVisibility(View.GONE);
                } else {
                    this.categoryId = this.extras.getString("ID");
                    this.categoryName = this.extras.getString("CATEGORY_NAME");
                    setTitle(Html.fromHtml(this.categoryName));
                }
                try {
                    if (this.extras.containsKey("drawerData")) {
                        drawerJsonObject = new JSONObject(this.extras.getString("drawerData"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                this.subCategoryRecyclerView = (RecyclerView) findViewById(R.id.subCategoryRecyclerView);
            }
        }
        this.screen_width = getScreenWidth();
        this.toolbar = (Toolbar) activity_category.findViewById(R.id.toolbar);
        setSupportActionBar(this.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.viewSwticherImageView = (ImageView) findViewById(R.id.viewSwitcher);
        this.viewSwticherImageView.setOnClickListener(this.viewSwitcherListener);
        findViewById(R.id.view1).setOnClickListener(this.viewSwitcherListener);
        findViewById(R.id.view2).setOnClickListener(this.viewSwitcherListener);
        this.categoryViewShared = getSharedPreferences("categoryView", MODE_PRIVATE);
        if (this.categoryViewShared.getBoolean("isGridView", false)) {
            this.mLayoutManager = new GridLayoutManager(this, 2);
            this.viewSwticherImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
        } else {
            this.mLayoutManager = new LinearLayoutManager(this);
            this.viewSwticherImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
        }
        this.recyclerView.setLayoutManager(this.mLayoutManager);
        this.mCategoryActivityConnectionProgressBar = ProgressDialog.show(this, null, "Loading", true);
        try {
            JSONObject jo = new JSONObject();
            jo.put("page", this.pageNumber);
            jo.put("width", this.screen_width);
            jo.put("limit", "20");
            if (!this.searchQuery.isEmpty()) {
                jo.put("search", this.searchQuery);
                new Connection(this).execute("productSearch", jo.toString());
            } else if (this.extras.containsKey("manufacturer_id")) {
                jo.put("manufacturer_id", this.manufacturerId);
                new Connection(this).execute("manufacturerInfo", jo.toString());
            } else if (this.extras.containsKey("type") && this.extras.get("type").toString().equalsIgnoreCase("custom")) {
                jo.put("id", this.customId);
                new Connection(this).execute("customCollection", jo.toString());
            } else {
                jo.put("path", this.categoryId);
                new Connection(this).execute("productCategory", jo.toString());
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    private void prepareListData(JSONArray subCategoryData) {
        try {
            this.parentListItems = new ArrayList();
            List<SubcategoryParentListItem> subcategoryParentListItems = new ArrayList();
            subcategoryParentListItems.add(new SubcategoryParentListItem("View"));
            this.subchildList = new ArrayList();
            SubcategoryParentListItem subcategoryParentListItem = subcategoryParentListItems.get(0);
            List<SubcategoryChildListItem> childItemList = new ArrayList();
            for (int j = 0; j < subCategoryData.length(); j++) {
                childItemList.add(new SubcategoryChildListItem(subCategoryData.getJSONObject(j).getString("name"), 0, j + 1));
            }
            subcategoryParentListItem.setChildItemList(childItemList);
            this.parentListItems.add(subcategoryParentListItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    List<Product> items(JSONArray categoryData) {
        List<Product> products = new ArrayList();
        int i = 0;
        while (i < categoryData.length()) {
            try {
                JSONObject eachProduct = categoryData.getJSONObject(i);
                Log.d("image--->", eachProduct.getString("thumb"));
                Log.d("eachProduct--->", eachProduct.toString(4));
                String discount = getString(R.string.sale);
                String model = "";
                String instock = "";
                if (eachProduct.has("discount")) {
                    discount = eachProduct.getString("discount");
                }
                if (eachProduct.has("model")) {
                    model = eachProduct.getString("model");
                }
                if (eachProduct.has("instock")) {
                    instock = eachProduct.getString("instock");
                }
                List<Product> list = products;
                list.add(new Product(eachProduct.getString("name"), eachProduct.getString("thumb"), eachProduct.getString("price"), eachProduct.getString("description"), eachProduct.getString("rating"), "ADD TO CART", "Add to Wishlist", discount, "0", "0", "3", eachProduct.getString("special"), "0", model, eachProduct.getString("product_id"), eachProduct.getString("hasOption"), instock));
                addSellerData(eachProduct, products, i);
                i++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return products;
    }

    protected void addSellerData(JSONObject eachProduct, List<Product> list, int i) {
    }

    public void viewSwitcher(View v) {
        Editor editor = this.categoryViewShared.edit();
        if (this.myAdapter.getViewType() == MyAdapter.LAYOUT_ITEM_GRID) {
            this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
            this.viewSwticherImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_grid));
            this.myAdapter.setType(MyAdapter.LAYOUT_ITEM_LIST);
            editor.putBoolean("isGridView", false);
        } else {
            this.recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            this.myAdapter.setType(MyAdapter.LAYOUT_ITEM_GRID);
            this.viewSwticherImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_list));
            editor.putBoolean("isGridView", true);
        }
        editor.apply();
    }

    public void shopBy(View v) {
        try {
            if (mainObject.getJSONObject("moduleData").getJSONArray("filter_groups").length() != 0) {
                /*Intent filterIntent = new Intent(getApplicationContext(), LayeredNavigation.class);
                try {
                    filterIntent.putExtra("product", mainObject.getJSONObject("moduleData").getJSONArray("filter_groups").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivityForResult(filterIntent, 1);
                return;*/
            }
            Toast.makeText(getApplicationContext(), R.string.filter_options_are_not_available, Toast.LENGTH_SHORT).show();
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public void sortBy(View v) {
        Intent sortIntent = new Intent(getApplicationContext(), Sorter.class);
        try {
            Log.d("mainObject", mainObject + "");
            Log.d("searchQuery", this.searchQuery + "");
            if (!this.searchQuery.equalsIgnoreCase("")) {
                sortIntent.putExtra("sortingLabels", mainObject.getJSONArray("sorts").toString());
            } else if (this.extras.containsKey("manufacturer_id")) {
                sortIntent.putExtra("sortingLabels", mainObject.getJSONObject("manufacturers").getJSONArray("sorts").toString());
            } else {
                sortIntent.putExtra("sortingLabels", mainObject.getJSONObject("categoryData").getJSONArray("sorts").toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        startActivityForResult(sortIntent, 2);
    }

    public void home(View v) {
        Intent homeIntent = new Intent(this, MainActivity.class);
        homeIntent.addFlags(FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == -1) {
            this.pageNumber = 1;
            this.totalItems = 0;
            this.mCategoryActivityConnectionProgressBar = ProgressDialog.show(this, null, "Loading", true);
            findViewById(R.id.notificationLayout).setVisibility(View.VISIBLE);
            new Connection(this).execute("productCategory", getJSONData().toString());
        }
        if (requestCode == 2 && resultCode == -1) {
            this.pageNumber = 1;
            this.totalItems = 0;
            if (data != null) {
                this.sortData = data.getExtras().getStringArray("sortData");
            }
            this.mCategoryActivityConnectionProgressBar = ProgressDialog.show(this, null, "Loading", true);
            findViewById(R.id.notificationLayout).setVisibility(View.VISIBLE);
            if (!this.searchQuery.isEmpty()) {
                new Connection(this).execute("productSearch", getJSONData().toString());
            } else if (this.extras.containsKey("manufacturer_id")) {
                new Connection(this).execute("manufacturerInfo", getJSONData().toString());
            } else {
                new Connection(this).execute("productCategory", getJSONData().toString());
            }
        }
    }

    private JSONObject getJSONData() {
        JSONObject jo = new JSONObject();
        try {
            if (!this.searchQuery.isEmpty()) {
                jo.put("search", this.searchQuery);
            }
            if (this.extras.containsKey("manufacturer_id")) {
                jo.put("manufacturer_id", this.manufacturerId);
            } else {
                jo.put("path", this.categoryId);
            }
            jo.put("limit", "20");
            jo.put("page", this.pageNumber);
            jo.put("width", this.screen_width);
            jo.put("sort", this.sortData[0]);
            jo.put("order", this.sortData[1]);
            if (getFilterString() != null) {
                jo.put("filter", getFilterString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    private String getFilterString() {
        if (filterDataHashMap.isEmpty()) {
            return null;
        }
        Object[] keys = filterDataHashMap.keySet().toArray();
        this.filterString = "";
        for (int i = 0; i < keys.length; i++) {
            if (i == 0) {
                this.filterString = (String) filterDataHashMap.get(keys[i]);
            } else {
                this.filterString = this.filterString.concat("," + ((String) filterDataHashMap.get(keys[i])));
            }
        }
        return this.filterString;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (!"android.intent.action.SEARCH".equals(getIntent().getAction()) && !getIntent().getExtras().containsKey("searchTerm")) {
            return super.onCreateOptionsMenu(menu);
        }
        getMenuInflater().inflate(R.menu.search_results, menu);
        this.menu = menu;
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        this.searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        this.searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        this.searchView.setQuery(this.searchQuery, false);
        this.searchView.clearFocus();
        this.searchView.setIconifiedByDefault(false);
        SharedPreferences shared = getSharedPreferences("customerData", MODE_PRIVATE);
        Boolean isLoggedIn = shared.getBoolean("isLoggedIn", false);
        String isSeller = shared.getString("isSeller", "");
        MenuItem loginMenuItem = menu.findItem(R.id.login);
        MenuItem signupMenuItem = menu.findItem(R.id.signup);
        menu.findItem(R.id.marketPlace).setVisible(true);
        if (isLoggedIn) {
            loginMenuItem.setTitle(getResources().getString(R.string.logout_title));
            signupMenuItem.setVisible(false);
            if (isSeller.equalsIgnoreCase("1")) {
                menu.findItem(R.id.sellerDashboard).setVisible(true);
                menu.findItem(R.id.sellerOrder).setVisible(true);
            }
        }
        return true;
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void onResume() {
        super.onResume();
        if (this.itemCart != null) {
            Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", MODE_PRIVATE).getString("cartItems", "0"));
        }
    }


    //API Responses
    public void customCollectionResponse(String backResult) {
        try {
            JSONObject jo = new JSONObject(backResult);
            JSONArray jsonArray = jo.getJSONArray("products");
            this.productTotal = jo.getInt("product_total");
            ((TextView) findViewById(R.id.notificationMessage)).setVisibility(View.GONE);
            this.myAdapter = new MyAdapter(this, items(jsonArray), this.mMallApplication);
            this.totalItems += jsonArray.length();
            this.recyclerView.setAdapter(this.myAdapter);
            this.recyclerView.addOnScrollListener(this.ScrollListener);
            this.mCategoryActivityConnectionProgressBar.dismiss();
            findViewById(R.id.listCategoryContainer).setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addToWishlistResponse(String backresult) {
        try {
            JSONObject mainObject = new JSONObject(backresult);
            MyAdapter.progressdialog.dismiss();
            Builder alert = new Builder(this);
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

    public void productCategoryResponse(String backresult) {
        try {
            mainObject = new JSONObject(backresult);
            this.productTotal = mainObject.getJSONObject("categoryData").getInt("product_total");
            if (mainObject.getJSONObject("categoryData").has("categories") && mainObject.getJSONObject("categoryData").getJSONArray("categories").length() != 0) {
                findViewById(R.id.subCategoryRecyclerView).setVisibility(View.VISIBLE);
                Log.d("DEBUG", "productCategoryResponse: lvExpSubLayout VISIBLE");
                prepareListData(mainObject.getJSONObject("categoryData").getJSONArray("categories"));
                this.subCategoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
                this.subCategoryRecyclerView.setAdapter(new CategoryExpandableRecyclerAdapter(this, this.parentListItems));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView categoryTitle = (TextView) findViewById(R.id.notificationMessage);
        this.myAdapter = null;
        try {
            if (mainObject.getJSONObject("categoryData").getJSONArray("products").length() != 0 || mainObject.getJSONObject("categoryData").getJSONArray("categories").length() == 0) {
                categoryTitle.setText(getResources().getString(R.string.total) + " " + this.productTotal + " " + getResources().getString(R.string.items_found));
            } else {
                categoryTitle.setVisibility(View.GONE);
            }
            this.myAdapter = new MyAdapter(this, items(mainObject.getJSONObject("categoryData").getJSONArray("products")), this.mMallApplication);
            this.totalItems += mainObject.getJSONObject("categoryData").getJSONArray("products").length();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.recyclerView.setAdapter(this.myAdapter);
        this.recyclerView.addOnScrollListener(this.ScrollListener);
        this.mCategoryActivityConnectionProgressBar.dismiss();
        findViewById(R.id.listCategoryContainer).setVisibility(View.VISIBLE);
        this.mDrawerLayout.setVisibility(View.VISIBLE);
    }

    public void productCategoryResponseLazyLoad(String backresult) {
        try {
            JSONArray json_array;
            mainObject = new JSONObject(backresult);
            findViewById(R.id.listcategoryRequestBar).setVisibility(View.GONE);
            if (this.searchQuery.isEmpty() && this.queryStringJSON == null) {
                json_array = mainObject.getJSONObject("categoryData").getJSONArray("products");
            } else {
                json_array = mainObject.getJSONArray("productCollection");
            }
            this.totalItems += json_array.length();
            this.myAdapter.addAll(items(json_array));
            this.myAdapter.notifyDataSetChanged();
            this.loading = false;
        } catch (JSONException e) {
            Log.d("onPostLazyLoad", "" + e);
            e.printStackTrace();
        }
    }

    public void addToCartResponse(String backresult) {
        try {
            JSONObject CheckoutAddtoCartobject = new JSONObject(backresult);
            MyAdapter.progressdialog.dismiss();
            if (CheckoutAddtoCartobject.getString("error").equalsIgnoreCase("0")) {
                Toast.makeText(getApplicationContext(), Html.fromHtml(CheckoutAddtoCartobject.getString("message")), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Can't be added", Toast.LENGTH_SHORT).show();
            }
            String totalCount = CheckoutAddtoCartobject.getString("total");
            totalCount = totalCount.substring(0, totalCount.indexOf(" "));
            getSharedPreferences("customerData", MODE_PRIVATE).edit()
                    .putString("cartItems", totalCount).apply();
            Utils.setBadgeCount(this, (LayerDrawable) this.itemCart.getIcon(), getSharedPreferences("customerData", MODE_PRIVATE).getString("cartItems", "0"));
        } catch (JSONException e) {
            Log.d("Exception add to cart", e.toString());
            e.printStackTrace();
        }
    }

    public void productSearchResponse(String backresult) {
        try {
            mainObject = new JSONObject(backresult);
            this.productTotal = mainObject.getInt("product_total");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        TextView categoryTitle = (TextView) findViewById(R.id.notificationMessage);
        this.myAdapter = null;
        try {
            categoryTitle.setText("Total " + this.productTotal + " " + getResources().getString(R.string.items_found));
            this.myAdapter = new MyAdapter(this, items(mainObject.getJSONArray("products")), this.mMallApplication);
            this.totalItems += mainObject.getJSONArray("products").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.recyclerView.setAdapter(this.myAdapter);
        this.recyclerView.addOnScrollListener(this.ScrollListener);
        this.mCategoryActivityConnectionProgressBar.dismiss();
        findViewById(R.id.listCategoryContainer).setVisibility(View.VISIBLE);
    }

    public void productSearchResponseLazyLoad(String backresult) {
        try {
            mainObject = new JSONObject(backresult);
            findViewById(R.id.listcategoryRequestBar).setVisibility(View.GONE);
            JSONArray json_array = mainObject.getJSONArray("products");
            this.totalItems += json_array.length();
            this.myAdapter.addAll(items(json_array));
            this.myAdapter.notifyDataSetChanged();
            this.loading = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void manufacturerInfoResponse(String backresult) {
        try {
            mainObject = new JSONObject(backresult);
            this.productTotal = mainObject.getJSONObject("manufacturers").getInt("product_total");
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        TextView categoryTitle = (TextView) findViewById(R.id.notificationMessage);
        this.myAdapter = null;
        try {
            categoryTitle.setText("Total " + this.productTotal + " " + getResources().getString(R.string.items_found));
            this.myAdapter = new MyAdapter(this, items(mainObject.getJSONObject("manufacturers").getJSONArray("products")), this.mMallApplication);
            this.totalItems += mainObject.getJSONObject("manufacturers").getJSONArray("products").length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.recyclerView.setAdapter(this.myAdapter);
        this.recyclerView.addOnScrollListener(this.ScrollListener);
        this.mCategoryActivityConnectionProgressBar.dismiss();
        findViewById(R.id.listCategoryContainer).setVisibility(View.VISIBLE);
        this.mDrawerLayout.setVisibility(View.VISIBLE);
    }

    public void manufacturerInfoResponseLazyLoad(String backresult) {
        try {
            mainObject = new JSONObject(backresult);
            findViewById(R.id.listcategoryRequestBar).setVisibility(View.GONE);
            JSONArray json_array = mainObject.getJSONObject("manufacturers").getJSONArray("products");
            this.totalItems += json_array.length();
            this.myAdapter.addAll(items(json_array));
            this.myAdapter.notifyDataSetChanged();
            this.loading = false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void languageResponse(String backresult) {
        try {
            Intent intent = new Intent(getBaseContext(), SplashScreen.class);
            intent.putExtra("CreateNewCategoryList", "");
            finish();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void currencyResponse(String backresult) {
        try {
            Intent intent = new Intent(getBaseContext(), SplashScreen.class);
            intent.putExtra("CreateNewCategoryList", "");
            finish();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
