package com.edenxpress.mobi.modules;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * Created by HP 15-P038 on Feb 23 17.
 */

public class ImageSlideShow {
    private Context ctx;
    private List<Object> mImages;
    private ViewPager mViewPager;
    private ImagePagerAdapter mAdapter;
    private CirclePageIndicator mPageIndicator;

    public ImageSlideShow(Context ctx, List<Object> image) {
        this.ctx = ctx;
        this.mImages = image;
        this.mAdapter = new ImagePagerAdapter(this.ctx);
        if (mImages != null) {
            this.mAdapter.setImages(this.mImages);
        }
    }

    public ImageSlideShow(Context ctx) {
        this(ctx, null);
    }

    public void setClickListener(View.OnClickListener listener) {
        this.mAdapter.setOnClickListener(listener);
    }

    public ImagePagerAdapter getAdapter() {
        return this.mAdapter;
    }

    public void setImages(List<Object> mImages) {
        this.mImages = mImages;
        this.mAdapter.setImages(mImages);
    }

    public Context getContext() {
        return ctx;
    }

    public ViewPager getViewPager() {
        return mViewPager;
    }

    public void setViewPager(ViewPager mViewPager) {
        this.mViewPager = mViewPager;
        this.mViewPager.setAdapter(this.mAdapter);
        this.mPageIndicator = new CirclePageIndicator(this.ctx);
        this.mPageIndicator.setViewPager(this.mViewPager);
        //this.mViewPager.addOnPageChangeListener(new OnPageChangeListener());

    }

    public CirclePageIndicator getPageIndicator() {
        return this.mPageIndicator;
    }

/*    public class OnPageChangeListener implements ViewPager.OnPageChangeListener{
        private int currentPage;

        public void onPageSelected(int position) {
            this.currentPage = position;
            for (int i = 0; i < ImageSlideShow.this.mImages.length; i++) {
                if (i == position) {
                    ImageSlideShow.this.dotList[i].setBackgroundResource(R.drawable.dot_icon_focused);
                } else {
                    ImageSlideShow.this.dotList[i].setBackgroundResource(R.drawable.dot_icon_unfocused);
                }
            }
        }

        public int getCurrentPage() {
            return this.currentPage;
        }

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float Offset, int positionOffsetPixels) {
            if (Offset <= 0.5f) {
            }
        }

    }*/

}
