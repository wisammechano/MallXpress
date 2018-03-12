package com.edenxpress.mobi.analytics;

import android.app.Application;

import com.edenxpress.mobi.CategoryActivity;
import com.edenxpress.mobi.R;
import com.edenxpress.mobi.ViewProduct;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

//import com.edenxpress.mobi.ViewProductDownloadable;


/**
 * Created by ICT on 12/26/2016.
 */

/**
 * This is a subclass of {@link Application} used to provide shared objects for this app, such as
 * the {@link Tracker}.
 */
public class MallApplication extends Application {
    private Tracker mTracker;

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     */
    public synchronized Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
            mTracker.enableExceptionReporting(true);
        }
        return mTracker;
    }

    public Class viewCategoryList() {
        return CategoryActivity.class;
    }

    public Class viewCategoryGrid() {
        return CategoryActivity.class;
    }

    public Class viewProductSimple() {
        return ViewProduct.class;
    }

    public Class viewProductDownloadable() {
        return this.getClass(); //ViewProductDownloadable.class;
    }

    public Class<?> viewMarketPlaceHome() {
        return null;
    }

    public Class<?> viewSellerDashBoard() {
        return null;
    }

    public Class<?> viewSellerOrderHistory() {
        return null;
    }

    public Class<?> viewSellerProfile() {
        return null;
    }

}
