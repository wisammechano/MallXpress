package com.edenxpress.mobi;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ICT on 1/5/2017.
 */


public class SubcategoryFragment extends Fragment {
    private Subcategory mContext;
    private int mPositionOfFragment;
    private List<ParentListItem> parentListItems;
    private RecyclerView recyclerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.recyclerView = (RecyclerView) inflater.inflate(R.layout.subcategory_fragment_list, container, false);
        return this.recyclerView;
    }

    public static SubcategoryFragment newInstance(int index) {
        SubcategoryFragment myFragment = new SubcategoryFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        myFragment.setArguments(args);
        return myFragment;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.mPositionOfFragment = getArguments().getInt("index");
        this.mContext = (Subcategory) getActivity();
        prepareDataForSubcategory();
        this.recyclerView.setAdapter(new SubCategoryExpandableRecyclerAdapter(this.mContext, this.parentListItems, this.mPositionOfFragment));
        this.recyclerView.setSelected(true);
    }

    private void prepareDataForSubcategory() {
        this.parentListItems = new ArrayList();
        List<SubcategoryParentListItem> subcategoryParentListItems = new ArrayList();
        try {
            SubcategoryParentListItem viewAllSubcategoryParentListItem = new SubcategoryParentListItem(this.mContext.getResources().getString(R.string.view_all_) + " " + MainActivity.categoriesJSONObject.getJSONObject(this.mPositionOfFragment).getString("name"));
            subcategoryParentListItems.add(viewAllSubcategoryParentListItem);
            this.parentListItems.add(viewAllSubcategoryParentListItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            int i;
            //Log.d("categoriesJSONObject", MainActivity.categoriesJSONObject + "");
            JSONArray defaultCategoryChildrenArray = MainActivity.categoriesJSONObject.getJSONObject(this.mPositionOfFragment).getJSONArray("children");
            for (i = 0; i < defaultCategoryChildrenArray.length(); i++) {
                subcategoryParentListItems.add(new SubcategoryParentListItem(defaultCategoryChildrenArray.getJSONObject(i).getString("name")));
            }
            Log.d("DEBUG", subcategoryParentListItems.size() + "");
            for (i = 1; i < subcategoryParentListItems.size(); i++) {
                SubcategoryParentListItem subcategoryParentListItem = subcategoryParentListItems.get(i);
                List<SubcategoryChildListItem> childItemList = new ArrayList();
                childItemList.add(new SubcategoryChildListItem(this.mContext.getResources().getString(R.string.view_all_) + " " + defaultCategoryChildrenArray.getJSONObject(i - 1).getString("name"), i, 0));
                if (defaultCategoryChildrenArray.getJSONObject(i - 1).has("children")) {
                    for (int j = 0; j < defaultCategoryChildrenArray.getJSONObject(i - 1).getJSONArray("children").length(); j++) {
                        childItemList.add(new SubcategoryChildListItem(defaultCategoryChildrenArray.getJSONObject(i - 1).getJSONArray("children").getJSONObject(j).getString("name"), i, j + 1));
                    }
                }
                subcategoryParentListItem.setChildItemList(childItemList);
                this.parentListItems.add(subcategoryParentListItem);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }
}
