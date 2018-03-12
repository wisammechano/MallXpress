package com.edenxpress.mobi;

import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.util.List;

/**
 * Created by ICT on 1/4/2017.
 */


public class CustomPrimaryDrawerItem extends PrimaryDrawerItem {
    private ColorHolder background;

    public CustomPrimaryDrawerItem withBackgroundColor(int backgroundColor) {
        this.background = ColorHolder.fromColor(backgroundColor);
        return this;
    }

    public CustomPrimaryDrawerItem withBackgroundRes(int backgroundRes) {
        this.background = ColorHolder.fromColorRes(backgroundRes);
        return this;
    }

    public void bindView(ViewHolder holder, List payloads) {
        super.bindView(holder, payloads);
        if (this.background != null) {
            this.background.applyToBackground(holder.itemView);
        }
        holder.itemView.findViewById(R.id.material_drawer_name).setPadding(64, 0, 0, 0);
    }
}