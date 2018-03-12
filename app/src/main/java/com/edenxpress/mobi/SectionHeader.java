package com.edenxpress.mobi;

/**
 * Created by HP 15-P038 on Jan 7 17.
 */

public class SectionHeader implements Element {
    private final int img;
    private final String title;

    public SectionHeader(String title, int img) {
        this.title = title;
        this.img = img;
    }

    public String getTitle() {
        return this.title;
    }

    public int getImage() {
        return this.img;
    }

    public boolean isGroupSection() {
        return true;
    }

    public boolean isAction() {
        return false;
    }
}