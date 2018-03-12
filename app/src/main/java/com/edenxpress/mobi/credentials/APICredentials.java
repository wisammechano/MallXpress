package com.edenxpress.mobi.credentials;

/**
 * Created by HP 15-P038 on Dec 26 16.
 */

public class APICredentials {
    public static String NAMESPACE;
    public static String SOAP_PASSWORD;
    public static String SOAP_USER_NAME;
    public static String URL;
    public static String WEBSITE_ID;

    static {
        APICredentials.WEBSITE_ID = "1";
        APICredentials.NAMESPACE = "urn:opencart";
        //APICredentials.URL = "http://192.168.1.102/store/appserver?token=";
        APICredentials.URL = "http://10.0.2.1/store/index.php?route=api/mobile&token=";
        //APICredentials.URL = "http://10.0.3.2/store/index.php?route=api/mobile&token=";
        //APICredentials.URL = "http://edenxpress.rf.gd/appserver";
        //APICredentials.URL = "http://oc.webkul.com/mobikul/api/appserver.php";
        APICredentials.SOAP_USER_NAME = "test";
        APICredentials.SOAP_PASSWORD = "tester";
    }
}
