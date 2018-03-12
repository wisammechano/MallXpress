package com.edenxpress.mobi;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
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
 * Created by HP 15-P038 on Jan 7 17.
 */

class CategoryExpandableRecyclerAdapter extends ExpandableRecyclerAdapter<CategoryExpandableRecyclerAdapter.CrimeParentViewHolder, CategoryExpandableRecyclerAdapter.CrimeChildViewHolder> {
    Context ctx;
    LayoutInflater mInflater;
    MallApplication mMallApplication;

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
            this.lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_green_arrow, 0);
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

    public CategoryExpandableRecyclerAdapter(Context ctx, List<ParentListItem> parentItemList) {
        super(parentItemList);
        this.ctx = ctx;
        this.mInflater = LayoutInflater.from(ctx);
    }

    public CrimeParentViewHolder onCreateParentViewHolder(ViewGroup parentViewGroup) {
        return new CrimeParentViewHolder(this.mInflater.inflate(R.layout.item_subcategory_fragment_elv_group, parentViewGroup, false));
    }

    public CrimeChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup) {
        return new CrimeChildViewHolder(this.mInflater.inflate(R.layout.item_subcategory_fragment_elv_child, childViewGroup, false));
    }

    public void onBindParentViewHolder(CrimeParentViewHolder parentViewHolder, int position, ParentListItem parentListItem) {
        parentViewHolder.lblListHeader.setText(Html.fromHtml(((SubcategoryParentListItem) parentListItem).getmTitle()));
        if (position == 0) {
            parentViewHolder.lblListHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_green_arrow, 0);
        }
        try {
            if (CategoryActivity.mainObject.getJSONObject("categoryData").getJSONArray("products").length() == 0 && CategoryActivity.mainObject.getJSONObject("categoryData").getJSONArray("categories").length() != 0) {
                parentViewHolder.setExpanded(true);
                parentViewHolder.onExpansionToggled(false);
                if (parentViewHolder.getParentListItemExpandCollapseListener() != null) {
                    parentViewHolder.getParentListItemExpandCollapseListener().onParentListItemExpanded(parentViewHolder.getAdapterPosition());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onBindChildViewHolder(CrimeChildViewHolder childViewHolder, final int position, Object childListItem) {
        childViewHolder.txtListChild.setText(Html.fromHtml(((SubcategoryChildListItem) childListItem).getTitle()));
        childViewHolder.txtListChild.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(ctx, mMallApplication.viewCategoryGrid());
                    intent.putExtra("ID", CategoryActivity.mainObject.getJSONObject("categoryData").getJSONArray("categories").getJSONObject(position - 1).getString("path"));
                    intent.putExtra("CATEGORY_NAME", CategoryActivity.mainObject.getJSONObject("categoryData").getJSONArray("categories").getJSONObject(position - 1).getString("name"));
                    ctx.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}