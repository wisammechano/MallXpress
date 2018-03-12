package com.edenxpress.mobi;

public class Singleton {
    private static Singleton mInstance = null;
    public static String sessionId;
    private Object adapter = null;
    private Object childActions;
    private Object level2list = null;
    private Object mainActions;
    private Object parentMapToID = null;
    private Object parentTOChildMap = null;
    private Object storeDataArr = null;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        sessionId = sessionId;
    }

    public Object getStoreDataArr() {
        return this.storeDataArr;
    }

    public void setStoreDataArr(Object storeDataArr) {
        this.storeDataArr = storeDataArr;
    }

    public Object getMainActions() {
        return this.mainActions;
    }

    public void setMainActions(Object mainActions) {
        this.mainActions = mainActions;
    }

    public Object getLevel2list() {
        return this.level2list;
    }

    public Object getChildActions() {
        return this.childActions;
    }

    public void setChildActions(Object childActions) {
        this.childActions = childActions;
    }

    public void setLevel2list(Object level2list) {
        this.level2list = level2list;
    }

    private Singleton() {
    }

    public Object getParentMapToID() {
        return this.parentMapToID;
    }

    public void setParentMapToID(Object parentMapToID) {
        this.parentMapToID = parentMapToID;
    }

    public Object getParentTOChildMap() {
        return this.parentTOChildMap;
    }

    public void setParentTOChildMap(Object parentTOChildMap) {
        this.parentTOChildMap = parentTOChildMap;
    }

    public static Singleton getInstance() {
        if (mInstance == null) {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    public Object getAdapter() {
        return this.adapter;
    }

    public void setAdapter(Object value) {
        this.adapter = value;
    }
}