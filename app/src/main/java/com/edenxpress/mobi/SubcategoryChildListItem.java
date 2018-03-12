package com.edenxpress.mobi;

/**
 * Created by ICT on 1/5/2017.
 */

public class SubcategoryChildListItem {
    private int mChildPosition;
    private int mParentPosition;
    private String mTitle;

    public SubcategoryChildListItem(String mTitle, int parentPosition, int childPosition) {
        this.mTitle = mTitle;
        this.mParentPosition = parentPosition;
        this.mChildPosition = childPosition;
    }

    public int getChildPosition() {
        return this.mChildPosition;
    }

    public void setChildPosition(int mChildPosition) {
        this.mChildPosition = mChildPosition;
    }

    public int getmParentPosition() {
        return this.mParentPosition;
    }

    public void setmParentPosition(int mParentPosition) {
        this.mParentPosition = mParentPosition;
    }

    public int getParentPosition() {
        return this.mParentPosition;
    }

    public void setParentPosition(int mParentPosition) {
        this.mParentPosition = mParentPosition;
    }

    public String getmTitle() {
        return this.mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
