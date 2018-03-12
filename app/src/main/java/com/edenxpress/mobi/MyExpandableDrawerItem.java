package com.edenxpress.mobi;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewCompat;
import android.view.View;

import com.mikepenz.fastadapter.utils.ViewHolderFactory;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;
import com.mikepenz.materialdrawer.Drawer.OnDrawerItemClickListener;
import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.icons.MaterialDrawerFont.Icon;
import com.mikepenz.materialdrawer.model.AbstractDrawerItem;
import com.mikepenz.materialdrawer.model.BaseDescribeableDrawerItem;
import com.mikepenz.materialdrawer.model.BaseViewHolder;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.List;

/**
 * Created by ICT on 1/4/2017.
 */


public class MyExpandableDrawerItem extends BaseDescribeableDrawerItem<MyExpandableDrawerItem, MyExpandableDrawerItem.ViewHolder> {

    private ColorHolder arrowColor;

    private OnDrawerItemClickListener mOnDrawerItemClickListener;

    protected int arrowRotationAngleStart = 0;

    protected int arrowRotationAngleEnd = 180;

    public MyExpandableDrawerItem withArrowColor(@ColorInt int arrowColor) {
        this.arrowColor = ColorHolder.fromColor(arrowColor);
        return this;
    }

    public MyExpandableDrawerItem withArrowColorRes(@ColorRes int arrowColorRes) {
        this.arrowColor = ColorHolder.fromColorRes(arrowColorRes);
        return this;
    }

    public MyExpandableDrawerItem withArrowRotationAngleStart(int angle) {
        this.arrowRotationAngleStart = angle;
        return this;
    }

    public MyExpandableDrawerItem withArrowRotationAngleEnd(int angle) {
        this.arrowRotationAngleEnd = angle;
        return this;
    }

    @Override
    public int getType() {
        return R.id.custom_material_drawer_item_expandable;
        //return R.color.background_color_blue;
    }

    @Override
    @LayoutRes
    public int getLayoutRes() {
        return R.layout.custom_material_drawer_item_expandable;
    }

    @Override
    public MyExpandableDrawerItem withOnDrawerItemClickListener(OnDrawerItemClickListener onDrawerItemClickListener) {
        this.mOnDrawerItemClickListener = onDrawerItemClickListener;
        return this;
    }

    @Override
    public OnDrawerItemClickListener getOnDrawerItemClickListener() {
        return this.mOnArrowDrawerItemClickListener;
    }

    /**
     * our internal onDrawerItemClickListener which will handle the arrow animation
     */
    private OnDrawerItemClickListener mOnArrowDrawerItemClickListener = new OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
            if ((drawerItem instanceof AbstractDrawerItem) && drawerItem.isEnabled()) {
                if (((AbstractDrawerItem) drawerItem).getSubItems() != null) {
                    if (((AbstractDrawerItem) drawerItem).isExpanded()) {
                        ViewCompat.animate(view.findViewById(R.id.material_drawer_arrow2)).rotation(arrowRotationAngleEnd).start();
                    } else {
                        ViewCompat.animate(view.findViewById(R.id.material_drawer_arrow2)).rotation(arrowRotationAngleStart).start();
                    }
                }
            }

            return mOnDrawerItemClickListener != null && mOnDrawerItemClickListener.onItemClick(view, position, drawerItem);
        }
    };

    @Override
    public void bindView(ViewHolder viewHolder, List payloads) {
        super.bindView(viewHolder, payloads);
        Context ctx = viewHolder.itemView.getContext();
        //bind the basic view parts
        bindViewHelper(viewHolder);

        //make sure all animations are stopped
        viewHolder.arrow.setColor(this.arrowColor != null ? this.arrowColor.color(ctx) : getIconColor(ctx));
        viewHolder.arrow.clearAnimation();
        if (isExpanded()) {
            ViewCompat.setRotation(viewHolder.arrow, arrowRotationAngleEnd);
        } else {
            ViewCompat.setRotation(viewHolder.arrow, arrowRotationAngleStart);
        }

        //call the onPostBindView method to trigger post bind view actions (like the listener to modify the item if required)
        onPostBindView(this, viewHolder.itemView);
    }

    @Override
    public ViewHolderFactory<ViewHolder> getFactory() {
        return new ItemFactory();
    }

    public static class ItemFactory implements ViewHolderFactory<ViewHolder> {
        public ViewHolder create(View v) {
            return new ViewHolder(v);
        }
    }

    public static class ViewHolder extends BaseViewHolder {
        public IconicsImageView arrow;

        public ViewHolder(View view) {
            super(view);
            this.arrow = (IconicsImageView) view.findViewById(R.id.material_drawer_arrow2);
            this.arrow.setIcon(new IconicsDrawable(view.getContext(), Icon.mdf_expand_more).sizeDp(16).paddingDp(2).color(Color.BLACK));
        }
    }
}