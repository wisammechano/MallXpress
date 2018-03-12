package com.edenxpress.mobi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by HP 15-P038 on Jan 7 17.
 */

public class Sorter extends Activity {
    static String code;
    static int directionId = -1;
    static String sortingType = "0";
    int screen_width;
    private JSONArray sortArray;
    String[] sortCode;
    ImageView[] sortDirection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.edenxpress.mobi.R.layout.activity_sorter);
        setTitle(com.edenxpress.mobi.R.string.sort);
        setTitleColor(getResources().getColor(com.edenxpress.mobi.R.color.image_border_center));
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        this.screen_width = size.x;
        getWindow().setLayout((int) (((double) this.screen_width) * 0.7d), -2);
        findViewById(getResources().getIdentifier("android:id/titleDivider", null, null)).setBackgroundColor(getResources().getColor(com.edenxpress.mobi.R.color.light_gray_color1));
        LinearLayout layout = (LinearLayout) findViewById(com.edenxpress.mobi.R.id.layout);
        try {
            this.sortArray = new JSONArray(getIntent().getStringExtra("sortingLabels"));
            TextView[] sortText = new TextView[this.sortArray.length()];
            if (directionId == -1) {
                directionId = 0;
                code = this.sortArray.getJSONObject(0).getString("value");
            }
            for (int i = 0; i < this.sortArray.length(); i++) {
                JSONObject sortLabel = this.sortArray.getJSONObject(i);
                LinearLayout itemLayout = new LinearLayout(this);
                itemLayout.setOrientation(LinearLayout.HORIZONTAL);
                sortText[i] = new TextView(this);
                itemLayout.addView(sortText[i]);
                itemLayout.setTag(i);
                sortText[i].setSingleLine(false);
                sortText[i].setText(Html.fromHtml(" " + sortLabel.getString("text") + " "));
                sortText[i].setTextSize(18.0f);
                sortText[i].setTextColor(getResources().getColor(com.edenxpress.mobi.R.color.secondary_text_color));
                new View(this).setLayoutParams(new LayoutParams(0, -1, 0.25f));
                sortText[i].setPadding(10, 10, 10, 10);
                this.sortCode = new String[2];
                View v = new View(this);
                v.setLayoutParams(new LayoutParams(-1, 1));
                v.setBackgroundResource(com.edenxpress.mobi.R.color.light_gray_color1);
                layout.addView(itemLayout);
                layout.addView(v);
                itemLayout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        int index = (Integer) v.getTag();
                        try {
                            Sorter.this.sortCode[0] = Sorter.this.sortArray.getJSONObject(index).getString("value");
                            Sorter.this.sortCode[1] = Sorter.this.sortArray.getJSONObject(index).getString("order");
                        } catch (JSONException e) {
                            Log.d("Exception", e.toString());
                        }
                        Sorter.this.createSortArray();
                    }
                });
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }

    void createSortArray() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("sortData", this.sortCode);
        setResult(-1, returnIntent);
        finish();
    }
}

