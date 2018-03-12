package com.edenxpress.mobi;

/**
 * Created by HP 15-P038 on Jan 7 17.
 */

public class DrawerData {

    String code;
    String imgURL;
    String listType;
    String name;

    public DrawerData(String name, String value, String type, String imgURL) {
        this.name = name;
        this.code = value;
        this.listType = type;
        this.imgURL = imgURL;
    }


}
