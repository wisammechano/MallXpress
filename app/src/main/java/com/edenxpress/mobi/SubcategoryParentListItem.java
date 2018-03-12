package com.edenxpress.mobi;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by ICT on 1/5/2017.
 */
public class SubcategoryParentListItem implements ParentListItem {
    private List<SubcategoryChildListItem> mChildItemList;
    private String mTitle;

    public SubcategoryParentListItem(String mTitle) {
        this.mTitle = mTitle;
    }

    public List<SubcategoryChildListItem> getChildItemList() {
        return this.mChildItemList;
    }

    public void setChildItemList(List<SubcategoryChildListItem> list) {
        this.mChildItemList = list;
    }

    public boolean isInitiallyExpanded() {
        return false;
    }

    public String getmTitle() {
        return this.mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
