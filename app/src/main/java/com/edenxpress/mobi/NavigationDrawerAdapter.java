package com.edenxpress.mobi;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by HP 15-P038 on Jan 7 17.
 */

public class NavigationDrawerAdapter extends BaseExpandableListAdapter {
    private HashMap<String, List<DrawerData>> childElements;
    private Context context;
    private LayoutInflater inflator;
    private List<DrawerData> mainElements;

    public NavigationDrawerAdapter(Context context, List<DrawerData> mainElements, HashMap<String, List<DrawerData>> childElements) {
        this.context = context;
        this.mainElements = mainElements;
        this.childElements = childElements;
        this.inflator = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    public int getGroupCount() {
        return this.mainElements.size();
    }

    public int getChildrenCount(int groupPosition) {
        if (this.childElements.get(((DrawerData) this.mainElements.get(groupPosition)).code) == null) {
            return 0;
        }
        return ((List) this.childElements.get(((DrawerData) this.mainElements.get(groupPosition)).code)).size();
    }

    public Object getGroup(int groupPosition) {
        return this.mainElements.get(groupPosition);
    }

    public Object getChild(int groupPosition, int childPosition) {
        return ((List) this.childElements.get(((DrawerData) this.mainElements.get(groupPosition)).code)).get(childPosition);
    }

    public long getGroupId(int groupPosition) {
        return (long) groupPosition;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long) childPosition;
    }

    public boolean hasStableIds() {
        return false;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if (this.mainElements.get(groupPosition).listType.equalsIgnoreCase("HeadingType")) {
            view = this.inflator.inflate(R.layout.layout_elv_drawer_header, parent, false);
            view.setOnClickListener(null);
            view.setOnLongClickListener(null);
            view.setLongClickable(false);
            TextView sectionView = (TextView) view.findViewById(R.id.header_text_view);
            ImageView headerLogo = (ImageView) view.findViewById(R.id.headerlogo);
            sectionView.setTextColor(Color.parseColor("#ffffff"));
            Toast.makeText(this.context, isExpanded + "", Toast.LENGTH_LONG).show();
            sectionView.setText(Html.fromHtml(this.mainElements.get(groupPosition).name));
            headerLogo.setImageResource(Integer.parseInt(this.mainElements.get(groupPosition).imgURL));
            return view;
        } else if (this.mainElements.get(groupPosition).listType.equalsIgnoreCase("LanguageType")) {
            view = this.inflator.inflate(R.layout.layout_elv_drawer_language, parent, false);
            Log.d("LangFlag---->", "" + this.mainElements.get(groupPosition).imgURL);
            Picasso.with(this.context).load(this.mainElements.get(groupPosition).imgURL).into((ImageView) view.findViewById(R.id.languageFlag));
            TextView langtitle = (TextView) view.findViewById(R.id.language_text_view);
            if (langtitle != null) {
                langtitle.setText(Html.fromHtml(this.mainElements.get(groupPosition).name));
            }
            if (getChildrenCount(groupPosition) == 0) {
                return view;
            }
            ImageView childLogo = (ImageView) view.findViewById(R.id.childOpenCloseLogo);
            if (isExpanded) {
                childLogo.setImageResource(R.drawable.ic_sub);
                return view;
            }
            childLogo.setImageResource(R.drawable.ic_add);
            return view;
        } else {
            view = this.inflator.inflate(R.layout.item_category_drawer_group, parent, false);
            Toast.makeText(this.context, isExpanded + "", Toast.LENGTH_LONG).show();
            TextView title = (TextView) view.findViewById(R.id.category_item_group);
            if (title != null) {
                title.setText(Html.fromHtml(this.mainElements.get(groupPosition).name));
            }
            if (getChildrenCount(groupPosition) == 0) {
                return view;
            }
            ImageView childLogo = (ImageView) view.findViewById(R.id.childOpenCloseLogo);
            if (isExpanded) {
                childLogo.setImageResource(R.drawable.ic_sub);
                return view;
            }
            childLogo.setImageResource(R.drawable.ic_add);
            return view;
        }
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = ((DrawerData) getChild(groupPosition, childPosition)).name;
        convertView = this.inflator.inflate(R.layout.item_category_drawer_list, parent, false);
        TextView txtListChild = (TextView) convertView.findViewById(R.id.category_item_list);
        txtListChild.setText(Html.fromHtml(childText));
        txtListChild.setTextColor(Color.BLACK);
        txtListChild.setBackgroundColor(Color.WHITE);
        if (((DrawerData) this.mainElements.get(groupPosition)).listType.equalsIgnoreCase("LanguageType")) {
            convertView.findViewById(R.id.languageFlag).setVisibility(View.VISIBLE);
            convertView.findViewById(R.id.languageFlag).setPadding(25, 5, 5, 5);
            Picasso.with(this.context).load(((DrawerData) getChild(groupPosition, childPosition)).imgURL).into((ImageView) convertView.findViewById(R.id.languageFlag));
            txtListChild.setPadding(5, 5, 5, 5);
        }
        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
