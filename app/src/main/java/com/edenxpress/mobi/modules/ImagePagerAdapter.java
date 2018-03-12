package com.edenxpress.mobi.modules;

import android.content.Context;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.edenxpress.mobi.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by HP 15-P038 on Feb 23 17.
 */

public class ImagePagerAdapter extends PagerAdapter {
    private Context context;
    private List<Object> images;
    private View.OnClickListener onClickListener;
    private LayoutInflater inflater;

    public ImagePagerAdapter(Context ctx) {
        this.context = ctx;
    }

    public ImagePagerAdapter(Context ctx, List<Object> mImages) {
        this.context = ctx;
        this.images = mImages;
    }

    public void setImages(List<Object> mImages) {
        this.images = mImages;
        notifyDataSetChanged();
    }

    public ImagePagerAdapter setOnClickListener(View.OnClickListener listener) {
        this.onClickListener = listener;
        return this;
    }

    public void addImage(Object image) {
        images.add(image);
        notifyDataSetChanged();
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void removeImage(int position) {
        images.remove(position);
        notifyDataSetChanged();
    }

    public void removeImage(Object item) {
        images.remove(item);
        notifyDataSetChanged();
    }

    public void finishUpdate(ViewGroup container) {
    }

    public int getCount() {
        return this.images.size();
    }

    public Object instantiateItem(ViewGroup container, final int position) {
        View imageLayout = LayoutInflater.from(context).inflate(R.layout.item_view_pager_banner, null);
        ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
        if (this.onClickListener != null) {
            imageView.setOnClickListener(this.onClickListener);
        }
        Object item = this.images.get(position);
        switch (item.getClass().getSimpleName()) {
            case "String":
                try {
                    Picasso.with(context).load(Integer.parseInt((String) item)).into(imageView); //String is resource id
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                    Picasso.with(context).load((String) item).into(imageView); //string is url
                }
                break;
            case "Uri":
                Picasso.with(context).load((Uri) item).into(imageView);
                break;
            default:
                //Picasso.with(context).load((int) item).into(imageView);
                break;
        }

        container.addView(imageLayout);
        return imageLayout;
    }

    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public Parcelable saveState() {
        return null;
    }

    public void startUpdate(ViewGroup container) {
    }
}
