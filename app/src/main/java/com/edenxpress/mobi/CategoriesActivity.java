package com.edenxpress.mobi;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CatAdapter adapter;
    private List<Category> catList;
    private JSONArray catJson;
    private JSONObject backdrop;
    private boolean backDropEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        catJson = MainActivity.categoriesJSONObject;
        if (MainActivity.catHasBD) {
            backDropEnabled = true;
            backdrop = MainActivity.catBD;
        } else backDropEnabled = false;
        initCollapsingToolbar();

        recyclerView = (RecyclerView) findViewById(R.id.cat_recycler_view);


        catList = new ArrayList<>();
        adapter = new CatAdapter(this, catList);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Category currentCat = catList.get(position);
                if (currentCat.getParent() == 0) {
                    return 2;
                } else
                    return 1;
            }
        });
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(0), true));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareCategories();

        if (backDropEnabled) {
            try {
                Picasso.with(this).load(backdrop.getString("backdrop")).into((ImageView) findViewById(R.id.backdrop));
                TextView title = (TextView) findViewById(R.id.backdrop_title);
                title.setText(backdrop.getString("title"));
                title = (TextView) findViewById(R.id.backdrop_subtitle);
                title.setText(backdrop.getString("subtitle"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        LinearLayout alterLayout = (LinearLayout) findViewById(R.id.noBackdropLayout);


        AppBarLayout.OnOffsetChangedListener onOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Log.d("vertical offset", verticalOffset + "");
                int totalScrollRange = appBarLayout.getTotalScrollRange();

                if (verticalOffset + totalScrollRange == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                } else {
                    collapsingToolbar.setTitle("");
                }
                int primary_color = ContextCompat.getColor(CategoriesActivity.this, R.color.primary_color);
                int opacity = 0x00;
                int final_opacity = Color.alpha(primary_color);
                int transparent = Color.argb(opacity, Color.red(primary_color), Color.green(primary_color), Color.blue(primary_color));
                int max_offset = (totalScrollRange - 200) * -1;
                if (verticalOffset <= max_offset) {
                    int offset_percentage = ((-1 * verticalOffset) + max_offset) * 100 / (totalScrollRange + max_offset);
                    int alpha = (final_opacity - opacity) * offset_percentage / 100 + opacity;
                    int transition_color = Color.argb(alpha, Color.red(primary_color), Color.green(primary_color), Color.blue(primary_color));

                    toolbar.setBackgroundColor(transition_color);
                    return;
                }
                toolbar.setBackgroundColor(transparent);
            }
        };

        if (backDropEnabled) {
            setSupportActionBar(toolbar);
            collapsingToolbar.setScrimVisibleHeightTrigger(collapsingToolbar.getScrimVisibleHeightTrigger() + 1);
            collapsingToolbar.setExpandedTitleColor(getColor(R.color.transparent));
            collapsingToolbar.setTitle("");
            appBarLayout.setExpanded(true);

            // hiding & showing the title when toolbar expanded & collapsed
            appBarLayout.addOnOffsetChangedListener(onOffsetChangedListener);
        } else {
            appBarLayout.removeView(collapsingToolbar);
            collapsingToolbar.removeView(toolbar);
            alterLayout.addView(toolbar);
            setSupportActionBar(toolbar);
            alterLayout.setVisibility(View.VISIBLE);
            setTitle(getString(R.string.app_name));
        }
    }

    /**
     * Adding few albums for testing
     */
    private void prepareCategories() {
        int i = 0;
        for (; i < catJson.length(); i++) {
            try {
                JSONObject cat = catJson.getJSONObject(i);
                Category c = new Category(cat.getString("name"), cat.getString("image"), cat.getInt("id"), cat.getInt("parent"));
                c.setPath(cat.getString("path"));
                c.setIcon(cat.getString("icon"));
                catList.add(c);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}

class Category {
    private String name;
    private Uri thumbnail;
    private Uri icon;
    private int identifier;
    private int parent;
    private String path;

    public Category() {
    }

    public Category(String name, String thumbnail, int id) {
        this.name = name;
        this.thumbnail = Uri.parse(thumbnail);
        this.identifier = id;
        this.parent = 0;
    }

    public Category(String name, String thumbnail, int id, int parent) {
        this.name = name;
        this.thumbnail = Uri.parse(thumbnail);
        this.identifier = id;
        this.parent = parent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getIdentifier() {
        return identifier;
    }

    public Uri getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = Uri.parse(icon);
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = Uri.parse(thumbnail);
    }
}

class CatAdapter extends RecyclerView.Adapter<CatAdapter.MyViewHolder> {

    private Context mContext;
    private List<Category> catList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.cat_title);
            thumbnail = (ImageView) view.findViewById(R.id.cat_thumbnail);
        }
    }


    public CatAdapter(Context mContext, List<Category> catList) {
        this.mContext = mContext;
        this.catList = catList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_card, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Category cat = catList.get(position);
        holder.title.setText(cat.getName());

        // loading album cover using Glide library
        Picasso.with(mContext).load(cat.getThumbnail()).into(holder.thumbnail);

    }


    @Override
    public int getItemCount() {
        return catList.size();
    }
}
