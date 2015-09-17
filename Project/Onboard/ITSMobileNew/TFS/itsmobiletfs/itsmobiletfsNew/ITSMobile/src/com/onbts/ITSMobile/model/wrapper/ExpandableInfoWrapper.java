package com.onbts.ITSMobile.model.wrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JLAB on 22.04.2014.
 */
public class ExpandableInfoWrapper {
    //title of expandable element
    private String headTitle;
    private String headValue;
    //items in expandable element
    private List<TwoStringsWrapper> childsList;
    //flag for hiding history items in listview
    private boolean isHistory = false;

    public ExpandableInfoWrapper(String headTitle, String headValue, List<TwoStringsWrapper> childsList) {
        this.headTitle = headTitle;
        this.headValue = headValue;
        this.childsList = new ArrayList<>();
//        this.childsList.clear();
        //check null, because History element will come with null list
        if (childsList != null) {
            this.childsList.addAll(childsList);
        }
    }

    public ExpandableInfoWrapper(String headTitle, String headValue, List<TwoStringsWrapper> childsList, boolean isHistory) {
        this.headTitle = headTitle;
        this.headValue = headValue;
        this.childsList = new ArrayList<>();
//        this.childsList.clear();
        //check null, because History element will come with null list
        if (childsList != null) {
            this.childsList.addAll(childsList);
        }
        this.isHistory = isHistory;
    }

    public String getHeadValue() {
        return headValue;
    }

    public void setHeadValue(String headValue) {
        this.headValue = headValue;
    }

    public List<TwoStringsWrapper> getChildsList() {
        return childsList;
    }

    public void setChildsList(List<TwoStringsWrapper> childsList) {
        this.childsList = childsList;
    }

    public String getHeadTitle() {
        return headTitle;
    }

    public void setHeadTitle(String headTitle) {
        this.headTitle = headTitle;
    }

    public boolean isHistory() {
        return isHistory;
    }

    public void setHistory(boolean isHistory) {
        this.isHistory = isHistory;
    }


}
