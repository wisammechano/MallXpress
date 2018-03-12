package com.edenxpress.mobi;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.edenxpress.mobi.analytics.MallApplication;

import org.json.JSONException;

import java.util.List;


/**
 * Created by ICT on 1/5/2017.
 */

public class SubCategoryExpandableRecyclerAdapter extends ExpandableRecyclerAdapter<SubCategoryExpandableRecyclerAdapter.CrimeParentViewHolder, SubCategoryExpandableRecyclerAdapter.CrimeChildViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private MallApplication mMallApplication = null;
    private int mPositionOfFragment;
    public TextView oldLblListHeader;

    public class CrimeChildViewHolder extends ChildViewHolder {
        public TextView txtListChild;

        public CrimeChildViewHolder(View itemView) {
            super(itemView);
            this.txtListChild = (TextView) itemView.findViewById(R.id.lblListItem);
        }
    }

    public class CrimeParentViewHolder extends ParentViewHolder {
        public TextView lblListHeader;

        public CrimeParentViewHolder(View itemView) {
            super(itemView);
            this.lblListHeader = (TextView) itemView.findViewById(R.id.lblListHeader);
            this.lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_green_arrow, 0);
        }

        public void onClick(View v) {
            if (isExpanded()) {
                this.lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_green_arrow, 0);
                collapseView();
                return;
            }
            this.lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_green_arrow, 0);
            expandView();
        }
    }


    public SubCategoryExpandableRecyclerAdapter(Context context, List<ParentListItem> itemList, int positionOfFragment) {
        super(itemList);
        this.mContext = context;
        this.mMallApplication = ((Subcategory) context).mMallApplication;
        this.mInflater = LayoutInflater.from(context);
        this.mPositionOfFragment = positionOfFragment;
    }

    public CrimeParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        return new CrimeParentViewHolder(this.mInflater.inflate(R.layout.item_subcategory_fragment_elv_group, viewGroup, false));
    }

    public CrimeChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        return new CrimeChildViewHolder(this.mInflater.inflate(R.layout.item_subcategory_fragment_elv_child, viewGroup, false));
    }

    public void onBindParentViewHolder(final CrimeParentViewHolder parentViewHolder, final int position, ParentListItem parentListItem) {
        final SubcategoryParentListItem subcategoryParentListItem = (SubcategoryParentListItem) parentListItem;
        parentViewHolder.lblListHeader.setText(Html.fromHtml(subcategoryParentListItem.getmTitle()));
        if (position == 0 || (subcategoryParentListItem.getChildItemList() != null && subcategoryParentListItem.getChildItemList().size() == 1)) {
            parentViewHolder.lblListHeader.setCompoundDrawables(null, null, null, null);
        }
        parentViewHolder.lblListHeader.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(SubCategoryExpandableRecyclerAdapter.this.mContext, CategoryActivity.class);
                if (position == 0) {
                    try {
                        intent.putExtra("ID", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getString("path"));
                        intent.putExtra("CATEGORY_NAME", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getString("name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SubCategoryExpandableRecyclerAdapter.this.mContext.startActivity(intent);
                } else if (subcategoryParentListItem.getChildItemList() == null || subcategoryParentListItem.getChildItemList().size() != 1) {
                    if (SubCategoryExpandableRecyclerAdapter.this.oldLblListHeader != null) {
                        SubCategoryExpandableRecyclerAdapter.this.oldLblListHeader.setBackgroundResource(R.color.background_color);
                        SubCategoryExpandableRecyclerAdapter.this.oldLblListHeader.setTextColor(ContextCompat.getColor(SubCategoryExpandableRecyclerAdapter.this.mContext, R.color.secondary_text_color));
                    }
                    parentViewHolder.lblListHeader.setBackgroundResource(R.color.view_switcher_color);
                    parentViewHolder.lblListHeader.setTextColor(ContextCompat.getColor(SubCategoryExpandableRecyclerAdapter.this.mContext, R.color.white));
                    SubCategoryExpandableRecyclerAdapter.this.oldLblListHeader = parentViewHolder.lblListHeader;
                    parentViewHolder.onClick(v);
                } else {
                    try {
                        intent.putExtra("ID", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getJSONArray("children").getJSONObject(position - 1).getString("path"));
                        intent.putExtra("CATEGORY_NAME", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getJSONArray("children").getJSONObject(position - 1).getString("name"));
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                    }
                    SubCategoryExpandableRecyclerAdapter.this.mContext.startActivity(intent);
                }
            }
        });
    }

    public void onBindChildViewHolder(CrimeChildViewHolder childViewHolder, int position, Object childListItem) {
        SubcategoryChildListItem subcategoryChildListItem = (SubcategoryChildListItem) childListItem;
        final int parentPosition = subcategoryChildListItem.getParentPosition();
        final int childPosition = subcategoryChildListItem.getChildPosition();
        childViewHolder.txtListChild.setText(Html.fromHtml(subcategoryChildListItem.getTitle()));
        childViewHolder.txtListChild.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(SubCategoryExpandableRecyclerAdapter.this.mContext, SubCategoryExpandableRecyclerAdapter.this.mMallApplication.viewCategoryGrid());
                    if (childPosition == 0) {
                        intent.putExtra("ID", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getJSONArray("children").getJSONObject(parentPosition - 1).getString("path"));
                        intent.putExtra("CATEGORY_NAME", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getJSONArray("children").getJSONObject(parentPosition - 1).getString("name"));
                    } else {
                        intent.putExtra("ID", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getJSONArray("children").getJSONObject(parentPosition - 1).getJSONArray("children").getJSONObject(childPosition - 1).getString("path"));
                        intent.putExtra("CATEGORY_NAME", MainActivity.categoriesJSONObject.getJSONObject(SubCategoryExpandableRecyclerAdapter.this.mPositionOfFragment).getJSONArray("children").getJSONObject(parentPosition - 1).getJSONArray("children").getJSONObject(childPosition - 1).getString("name"));
                    }
                    SubCategoryExpandableRecyclerAdapter.this.mContext.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
