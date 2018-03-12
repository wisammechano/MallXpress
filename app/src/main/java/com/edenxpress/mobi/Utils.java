package com.edenxpress.mobi;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.edenxpress.mobi.credentials.APICredentials;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.edenxpress.mobi.credentials.APICredentials.URL;

public class Utils {
    private Context context;

    Utils(Context c) {
        this.context = c;
    }

    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {
        BadgeDrawable badge;
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse == null || !(reuse instanceof BadgeDrawable)) {
            badge = new BadgeDrawable(context);
        } else {
            badge = (BadgeDrawable) reuse;
        }
        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    public static final String md5(String s) {
        String MD5 = "MD5";
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(aMessageDigest & 0xff);
                while (h.length() < 2) {
                    h = "0" + h;
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String initCapitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public String getSessionId() {
        try {
            HttpTransportSE ht = getHttpTransportSE();
            SoapObject soapLoginRequest = new SoapObject(APICredentials.NAMESPACE, "apiLogin");
            SoapSerializationEnvelope envelope = getSoapSerializationEnvelope(soapLoginRequest);
            JSONObject jo = new JSONObject();
            jo.put("apiKey", APICredentials.SOAP_USER_NAME);
            jo.put("apiPassword", md5(APICredentials.SOAP_PASSWORD));
            SharedPreferences shared = this.context.getSharedPreferences("customerData", 0);
            SharedPreferences configShared = this.context.getSharedPreferences("configureView", 0);
            if (shared.getBoolean("isLoggedIn", false)) {
                jo.put("customer_id", shared.getString("customerId", ""));
            }
            jo.put("language", configShared.getString("storeCode", ""));
            jo.put("currency", configShared.getString("currencyCode", ""));
            Log.d("Jo", jo + "");
            soapLoginRequest.addProperty("attributes", jo + "");
            try {
                ht.call(APICredentials.NAMESPACE, envelope);
            } catch (IOException ex) {
                Log.d("create account ", "Io exception bufferedIOStream closed" + ex);
            }
            JSONObject response;
            response = new JSONObject((String) envelope.getResponse());
            if (response.has("user_expired")) {
                shared.edit().putBoolean("isLoggedIn", false).apply();
            }
            Log.d(getClass().getName() + "debug", response.toString() + "");
            return response.toString();

        } catch (Exception ex2) {
            Log.d("Utils Exception", "In generating Session Id" + ex2);
            return "no";
        }
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject request) {
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(110);
        envelope.dotNet = false;
        envelope.xsd = "http://www.w3.org/2001/XMLSchema";
        ;
        envelope.enc = "http://schemas.xmlsoap.org/soap/encoding/";
        envelope.setOutputSoapObject(request);
        return envelope;
    }

    private final HttpTransportSE getHttpTransportSE() {
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL, 60000);
        Log.d("url", URL);
        httpTransportSE.debug = true;
        httpTransportSE.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return httpTransportSE;
    }

    public static boolean isPackageExisted(String targetPackage, Context context) {
        try {
            context.getPackageManager().getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
            return true;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static float dpToPx(float dips, DisplayMetrics metrics) {
        //int ret = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dips, metrics)); //For future reference
        return (dips * metrics.density);
    }

    public static float pxToDp(float pixels, DisplayMetrics metrics) {
        return pixels / metrics.density;
    }

    public static int getScreenWidth(Context ctx) {
        Display display = ((Activity) ctx).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

}
