package com.edenxpress.mobi;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.edenxpress.mobi.credentials.APICredentials;

import org.json.JSONObject;
import org.ksoap2.HeaderProperty;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HP 15-P038 on Dec 26 16.
 */

public class Connection extends AsyncTask<String, String, Object> {

    SharedPreferences configShared;
    public Object customerLoginResponse;
    public Editor editor;
    Context mContext;
    JSONObject mainObject;
    String returnFunctionName;

    private final HttpTransportSE getHttpTransportSE() {
        String URL = APICredentials.URL + this.configShared.getString("TOKEN", "Session_Not_Loggin");
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL, 60000);
        Log.d("url", URL);
        httpTransportSE.debug = true;
        httpTransportSE.setXmlVersionTag("<!--?xml version=\"1.0\" encoding= \"UTF-8\" ?-->");
        return httpTransportSE;
    }

    private final SoapSerializationEnvelope getSoapSerializationEnvelope(SoapObject soapObject) {
        SoapSerializationEnvelope soapSerializationEnvelope = new SoapSerializationEnvelope(110);
        soapSerializationEnvelope.dotNet = false;
        soapSerializationEnvelope.xsd = "http://www.w3.org/2001/XMLSchema";
        soapSerializationEnvelope.enc = "http://schemas.xmlsoap.org/soap/encoding/";
        soapSerializationEnvelope.setOutputSoapObject(soapObject);
        return soapSerializationEnvelope;
    }

    public Connection(final Context context) {
        this.mContext = context;
    }

    @Override
    protected Object doInBackground(String... args) {
        String apiFunctionName = args[0];
        String jsonString = args[1];
        this.returnFunctionName = apiFunctionName + "Response";
        try {
            if (this.configShared.getString("SESSION_ID", "Session_Not_Loggin").equalsIgnoreCase("Session_Not_Loggin")) {
                JSONObject sess_response = new JSONObject(SplashScreen.sessionObj.getSessionId());
                if (sess_response.getInt("error") == 1) {
                    return sess_response.toString();
                }
                this.configShared.edit()
                        .putString("SESSION_ID", sess_response.getString("session_id"))
                        .putString("TOKEN", sess_response.getString("token"))
                        .apply();
                Log.d("session", "login");
            }
            List<HeaderProperty> headers = new ArrayList<>();
            headers.add(new HeaderProperty("Cookie", this.configShared.getString("SESSION_ID", "")));
            headers.add(new HeaderProperty("Connection", "close"));
            HttpTransportSE ht = getHttpTransportSE();
            SoapObject customerLoginRequest = new SoapObject(APICredentials.NAMESPACE, apiFunctionName);
            SoapSerializationEnvelope requestEnvelop = getSoapSerializationEnvelope(customerLoginRequest);
            JSONObject jo = new JSONObject(jsonString);
            if (jo.has("page") && !jo.getString("page").equalsIgnoreCase("1")) {
                this.returnFunctionName += "LazyLoad";
            }
            //jo.put("session_id", this.configShared.getString("SESSION_ID", "Session_Not_Loggin"));
            jsonString = jo.toString();
            PropertyInfo stringArrayPropertyInfo = new PropertyInfo();
            stringArrayPropertyInfo.setName("attributes");
            stringArrayPropertyInfo.setValue(jsonString);
            stringArrayPropertyInfo.setType(jsonString.getClass());
            Log.d("Name", apiFunctionName + "");
            Log.d("JSONData---->", jsonString + "");
            customerLoginRequest.addProperty(stringArrayPropertyInfo);
            try {
                ht.call(APICredentials.NAMESPACE, requestEnvelop, headers);
                this.customerLoginResponse = requestEnvelop.getResponse();
                this.mainObject = new JSONObject(this.customerLoginResponse.toString());
                Log.d("Response in Connection", apiFunctionName + "-->" + this.mainObject.toString(4));
                if (!this.mainObject.has("fault")) {
                    return this.mainObject.toString();
                }
                Log.d("fault", "fault");
                this.configShared.edit()
                        .putString("SESSION_ID", "Session_Not_Loggin")
                        .apply();
                //cancel(true);
                //Connection(this.mContext).execute(new String[]{apiFunctionName, jsonString.toString()});
                List<String> retry = new ArrayList<>();
                retry.add(apiFunctionName);
                retry.add(jsonString);
                return retry;
            } catch (SocketException e) {
                Log.d(this.mContext.getClass().getName(), "Io exception bufferedIOStream closed" + e);
                return "no";
            } catch (IOException ex) {
                Log.d(this.mContext.getClass().getName(), "Io exception bufferedIOStream closed" + ex);
                //cancel(true);
                //new Connection(this.mContext).execute(new String[]{apiFunctionName, jsonString.toString()});
                ex.printStackTrace();
                return "no";
            }
        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
            return "no";
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.configShared = this.mContext.getSharedPreferences("customerData", Context.MODE_PRIVATE);
    }

    @Override
    protected void onPostExecute(Object response) {
        if (response instanceof ArrayList) {
            Log.d("debug", "session expired. re-issuing session id.");
            new Connection(this.mContext).execute((String) ((ArrayList) response).get(0), (String) ((ArrayList) response).get(1));
            return;
        } else if (response.toString().equalsIgnoreCase("no")) {

        }
        Exception e;
        try {
            this.mContext.getClass().getDeclaredMethod(this.returnFunctionName, new Class[]{String.class}).invoke(this.mContext, new Object[]{(String) response});
            return;
        } catch (IllegalAccessException e2) {
            e = e2;
        } catch (IllegalArgumentException e3) {
            e = e3;
        } catch (InvocationTargetException e4) {
            e = e4;
        } catch (NoSuchMethodException e5) {
            e5.printStackTrace();
            return;
        }
        e.printStackTrace();
    }
}