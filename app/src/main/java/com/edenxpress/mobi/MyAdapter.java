package com.edenxpress.mobi;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.edenxpress.mobi.analytics.MallApplication;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.List;

import static android.graphics.Paint.STRIKE_THRU_TEXT_FLAG;

/**
 * Created by HP 15-P038 on Jan 7 17.
 */


public class MyAdapter extends Adapter<MyAdapter.ViewHolder> {
    public static int LAYOUT_ITEM_GRID = 0;
    public static int LAYOUT_ITEM_LIST = 1;
    public static Dialog progressdialog;

    OnClickListener addToWishlistListener = new OnClickListener() {
        public void onClick(View v) {
            String productId = (String) v.getTag();
            if (MyAdapter.this.mContext.getSharedPreferences("customerData", Context.MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
                MyAdapter.progressdialog = ProgressDialog.show(MyAdapter.this.mContext, MyAdapter.this.mContext.getResources().getString(R.string.please_wait), MyAdapter.this.mContext.getResources().getString(R.string.processing_request_response), true);
                MyAdapter.progressdialog.setCanceledOnTouchOutside(false);
                JSONObject jo = new JSONObject();
                try {
                    jo.put("product_id", productId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new Connection(MyAdapter.this.mContext).execute("addToWishlist", jo.toString());
                return;
            }
            Builder alert = new Builder(MyAdapter.this.mContext);
            alert.setNegativeButton(MyAdapter.this.mContext.getResources().getString(R.string._ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alert.setMessage(Html.fromHtml(MyAdapter.this.mContext.getResources().getString(R.string.wishlist_msg))).show();
        }
    };
    private SharedPreferences configShared;
    private Editor editor;
    private Context mContext;
    private List<Product> mDataset;
    private MallApplication mMallApplication;
    int screen_width;
    private int viewType;

    private class CheckoutAddtoCartConnection extends AsyncTask<String, String, Object> {
        private Object CheckoutAddtoCartResponse;
        private JSONObject CheckoutAddtoCartResponseObj;

        private CheckoutAddtoCartConnection() {
        }

        protected String doInBackground(String... arguments) {
            try {
                HttpTransportSE ht = MyAdapter.this.getHttpTransportSE();
                if (MyAdapter.this.configShared.getString("SESSION_ID", "5").equalsIgnoreCase("Session_Not_Loggin")) {
                    Singleton.getInstance().setSessionId(SplashScreen.sessionObj.getSessionId());
                    MyAdapter.this.editor.putString("SESSION_ID", "noSessionId");
                    MyAdapter.this.editor.commit();
                }
                SoapObject mainRequest = new SoapObject(MyAdapter.this.configShared.getString("NAMESPACE", ""), "CheckoutAddtoCart");
                SoapSerializationEnvelope requestEnvelop = MyAdapter.this.getSoapSerializationEnvelope(mainRequest);
                mainRequest.addProperty("sessionId", Singleton.sessionId);
                JSONObject jo = new JSONObject();
                jo.put("storeId", MyAdapter.this.configShared.getString("storeId", "1"));
                jo.put("customerId", MyAdapter.this.mContext.getSharedPreferences("customerData", Context.MODE_PRIVATE).getString("customerId", ""));
                jo.put("productId", Integer.parseInt(arguments[0]));
                String jsonAsString = jo.toString();
                PropertyInfo stringArrayPropertyInfo = new PropertyInfo();
                stringArrayPropertyInfo.setName("attributes");
                stringArrayPropertyInfo.setValue(jsonAsString);
                stringArrayPropertyInfo.setType(jsonAsString.getClass());
                mainRequest.addProperty(stringArrayPropertyInfo);
                try {
                    ht.call(MyAdapter.this.configShared.getString("NAMESPACE", ""), requestEnvelop);
                } catch (IOException ex) {
                    Log.d("create account ", "Io exception bufferedIOStream closed" + ex);
                }

                this.CheckoutAddtoCartResponse = requestEnvelop.getResponse();
                String mainResponseAsString = this.CheckoutAddtoCartResponse.toString();
                this.CheckoutAddtoCartResponseObj = new JSONObject(mainResponseAsString);
                Log.d("add to wishlist resp", mainResponseAsString);
                return "yes";
            } catch (Exception e) {
                e.printStackTrace();
                return "no";
            }
        }

        protected void onPostExecute(Object backresult) {
            super.onPostExecute(backresult);
            MyAdapter.progressdialog.dismiss();
            try {
                Toast.makeText(MyAdapter.this.mContext, this.CheckoutAddtoCartResponseObj.getString("message"), Toast.LENGTH_SHORT).show();
                Log.d("COAddtoCartResponseObj", this.CheckoutAddtoCartResponseObj.toString());
                if (this.CheckoutAddtoCartResponseObj.getInt("error") == 0) {
                    //MyAdapter.this.mContext.startActivity(new Intent(MyAdapter.this.mContext, Cart.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        public View addToCart;
        public ImageView addToWishlist;
        public TextView model;
        public TextView newPrice;
        public TextView outOfStock;
        public TextView price;
        public ImageView productImage;
        public RelativeLayout productImageLayout;
        public TextView productName;
        public RatingBar ratingBar;
        public TextView sale;
        public TextView sellerStringTV;
        public TextView shortDescription;

        public ViewHolder(View convertView) {
            super(convertView);
            this.productName = (TextView) convertView.findViewById(R.id.productName);
            this.shortDescription = (TextView) convertView.findViewById(R.id.shortDescription);
            this.productImage = (ImageView) convertView.findViewById(R.id.productImage);
            this.productImageLayout = (RelativeLayout) convertView.findViewById(R.id.productImageLayout);
            LayoutParams productImageLayoutparams = (LayoutParams) this.productImageLayout.getLayoutParams();
            productImageLayoutparams.width = (int) (((double) MyAdapter.this.screen_width) / 2.5d);
            productImageLayoutparams.height = (int) (((double) MyAdapter.this.screen_width) / 2.5d);
            this.productImageLayout.setLayoutParams(productImageLayoutparams);
            this.newPrice = (TextView) convertView.findViewById(R.id.newPrice);
            this.price = (TextView) convertView.findViewById(R.id.price);
            this.price.setVisibility(View.GONE);
            this.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            ((LayerDrawable) this.ratingBar.getProgressDrawable()).getDrawable(2).setColorFilter(MyAdapter.this.mContext.getResources().getColor(R.color.filter_color_selected), Mode.SRC_ATOP);
            this.sellerStringTV = (TextView) convertView.findViewById(R.id.sellerStringTV);
            this.addToCart = convertView.findViewById(R.id.addToCart);
            this.addToWishlist = (ImageView) convertView.findViewById(R.id.wishlist);
            this.outOfStock = (TextView) convertView.findViewById(R.id.outOfStock);
            this.sale = (TextView) convertView.findViewById(R.id.sale);
            this.model = (TextView) convertView.findViewById(R.id.model);
        }
    }

    public int getViewType() {
        return this.viewType;
    }

    public void setType(int viewType) {
        this.viewType = viewType;
    }

    public int getItemViewType(int position) {
        return this.viewType;
    }

    public MyAdapter(Context context, List<Product> list, MallApplication mMallApplication) {
        this.mDataset = list;
        this.mContext = context;
        this.screen_width = getScreenWidth();
        this.configShared = this.mContext.getSharedPreferences("configureView", Context.MODE_PRIVATE);
        this.mMallApplication = mMallApplication;
        if (this.mContext.getSharedPreferences("categoryView", Context.MODE_PRIVATE).getBoolean("isGridView", false)) {
            this.viewType = LAYOUT_ITEM_GRID;
        } else {
            this.viewType = LAYOUT_ITEM_LIST;
        }
    }

    private int getScreenWidth() {
        Display display = ((Activity) this.mContext).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        if (viewType == LAYOUT_ITEM_GRID) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_grid_view, parent, false);
        }
        if (viewType == LAYOUT_ITEM_LIST) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list_view, parent, false);
        }
        return new ViewHolder(v);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        Product perProduct = this.mDataset.get(position);
        Picasso.with(this.mContext).load(this.mDataset.get(position).img_url.replace(" ", "%20")).placeholder(R.drawable.placeholder).into(holder.productImage);
        if (perProduct.hasOptions.equalsIgnoreCase("1")) {
            holder.productImage.setTag(position + "/" + 0);
        } else {
            holder.productImage.setTag(position + "/" + 1);
        }
        holder.productImage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MyAdapter.this.onClickProductImage(v);
            }
        });
        holder.productImage.setBackgroundResource(R.color.background_color);
        holder.productName.setClickable(true);
        if (perProduct.hasOptions.equalsIgnoreCase("1")) {
            holder.productName.setTag(position + "/" + 0);
        } else {
            holder.productName.setTag(position + "/" + 1);
        }
        if ("false".equalsIgnoreCase(perProduct.inStock)) {
            holder.outOfStock.setVisibility(View.VISIBLE);
        } else {
            holder.outOfStock.setVisibility(View.GONE);
        }
        holder.productName.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                MyAdapter.this.onCLickProductName(v);
            }
        });
        holder.shortDescription.setText(Html.fromHtml(perProduct.shortDescription));
        holder.productName.setText(Html.fromHtml(perProduct.getProductName()));
        holder.addToWishlist.setTag(perProduct.getProductId());
        holder.addToWishlist.setOnClickListener(this.addToWishlistListener);
        holder.model.setText(perProduct.model);
        if (perProduct.formatedSpecialPrice.equalsIgnoreCase("false")) {
            holder.newPrice.setText(perProduct.price);
            holder.price.setVisibility(View.GONE);
            holder.sale.setVisibility(View.GONE);
            Log.d("---" + perProduct.getProductName() + "---->", "" + perProduct.formatedSpecialPrice);
        } else {
            holder.newPrice.setText(perProduct.formatedSpecialPrice);
            holder.newPrice.setTextColor(ContextCompat.getColor(this.mContext, R.color.special_price_color));
            holder.price.setText(perProduct.price);
            holder.price.setPaintFlags(holder.newPrice.getPaintFlags() | STRIKE_THRU_TEXT_FLAG);
            holder.price.setVisibility(View.VISIBLE);
            if (!perProduct.discount.equalsIgnoreCase("")) {
                holder.sale.setVisibility(View.VISIBLE);
                holder.sale.setText(perProduct.discount);
            }
            Log.d(perProduct.getProductName() + "---->", "" + perProduct.formatedSpecialPrice);
        }
        loadAvailableSellerDetail(perProduct, holder.sellerStringTV);
        if (perProduct.hasOptions.equalsIgnoreCase("true")) {
            holder.addToCart.setTag(position + "/" + 1);
            ((Button) holder.addToCart).setText("View Product");
        } else {
            holder.addToCart.setTag(position + "/" + 0);
            ((Button) holder.addToCart).setText("Add To Cart");
        }
        if (perProduct.rating.equals("0") || perProduct.rating.equals("false")) {
            holder.ratingBar.setVisibility(View.INVISIBLE);
        } else {
            try {
                holder.ratingBar.setRating(Float.parseFloat(perProduct.rating));
                holder.ratingBar.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        holder.addToCart.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String[] indexFlagPair = ((String) v.getTag()).split("/");
                int position = Integer.parseInt(indexFlagPair[0]);
                if (indexFlagPair[1].equals("0")) {
                    MyAdapter.progressdialog = ProgressDialog.show(MyAdapter.this.mContext, MyAdapter.this.mContext.getResources().getString(R.string.please_wait), MyAdapter.this.mContext.getResources().getString(R.string.processing_request_response), true);
                    MyAdapter.progressdialog.setCanceledOnTouchOutside(false);
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("product_id", ((Product) MyAdapter.this.mDataset.get(position)).getProductId());
                        jo.put("quantity", "1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new Connection(MyAdapter.this.mContext).execute("addToCart", jo.toString());
                    return;
                }
                Intent intent = new Intent(MyAdapter.this.mContext, MyAdapter.this.mMallApplication.viewProductSimple());
                intent.putExtra("idOfProduct", ((Product) MyAdapter.this.mDataset.get(position)).getProductId());
                intent.putExtra("nameOfProduct", ((Product) MyAdapter.this.mDataset.get(position)).getProductName());
                MyAdapter.this.mContext.startActivity(intent);
            }
        });
    }

    protected void loadAvailableSellerDetail(Product perProduct, TextView sellerStringTV) {
        if (!perProduct.getSellerString().equals("")) {
            sellerStringTV.setVisibility(View.VISIBLE);
            sellerStringTV.setText(perProduct.getSellerString());
        }
    }

    public int getItemCount() {
        return this.mDataset.size();
    }

    protected void onCLickProductName(View v) {
        String[] indexFlagPair = ((String) ((TextView) v).getTag()).split("/");
        Intent intent = new Intent(this.mContext, this.mMallApplication.viewProductSimple());
        intent.putExtra("idOfProduct", ((Product) this.mDataset.get(Integer.parseInt(indexFlagPair[0]))).getProductId());
        intent.putExtra("nameOfProduct", ((Product) this.mDataset.get(Integer.parseInt(indexFlagPair[0]))).getProductName());
        this.mContext.startActivity(intent);
    }

    protected void onClickProductImage(View v) {
        String[] indexFlagPair = ((String) ((ImageView) v).getTag()).split("/");
        Intent intent = new Intent(this.mContext, this.mMallApplication.viewProductSimple());
        intent.putExtra("idOfProduct", ((Product) this.mDataset.get(Integer.parseInt(indexFlagPair[0]))).getProductId());
        intent.putExtra("nameOfProduct", ((Product) this.mDataset.get(Integer.parseInt(indexFlagPair[0]))).getProductName());
        this.mContext.startActivity(intent);
    }

    protected void onClickAddToCart(View v) {
        String[] indexFlagPair = ((String) ((ImageView) v).getTag()).split("/");
        if (indexFlagPair[1].equals("1")) {
            connectionToAddToCart(indexFlagPair);
        }
    }

    protected void connectionToAddToCart(String[] indexFlagPair) {
        if (this.mContext.getSharedPreferences("customerData", Context.MODE_PRIVATE).getBoolean("isLoggedIn", false)) {
            progressdialog = ProgressDialog.show(this.mContext, this.mContext.getResources().getString(R.string.please_wait), this.mContext.getResources().getString(R.string.processing_request_response), true);
            progressdialog.setCanceledOnTouchOutside(false);
            new CheckoutAddtoCartConnection().execute("" + ((Product) this.mDataset.get(Integer.parseInt(indexFlagPair[0]))).getProductId());
            return;
        }
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        MyAdapter.this.mContext.startActivity(new Intent(MyAdapter.this.mContext, LoginActivity.class));
                        return;
                    default:
                        return;
                }
            }
        };
        new Builder(this.mContext).setMessage(R.string.you_are_not_logged_in_please_login_to_continue).setNegativeButton(this.mContext.getResources().getString(R.string._cancel), dialogClickListener).setPositiveButton(this.mContext.getResources().getString(R.string.login_action_title), dialogClickListener).show();
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(110);
        envelope.dotNet = false;
        envelope.xsd = "http://www.w3.org/2001/XMLSchema";
        envelope.enc = "http://schemas.xmlsoap.org/soap/encoding/";
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE ht = new HttpTransportSE(this.configShared.getString("URL", ""), 60000);
        ht.debug = true;
        ht.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return ht;
    }

    public void addAll(List<Product> items) {
        this.mDataset.addAll(items);
    }
}
