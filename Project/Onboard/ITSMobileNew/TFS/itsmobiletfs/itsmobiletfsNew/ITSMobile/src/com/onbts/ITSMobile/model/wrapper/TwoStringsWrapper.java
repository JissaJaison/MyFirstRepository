package com.onbts.ITSMobile.model.wrapper;

import android.view.View;

/**
 * Created by JLAB on 28.04.2014.
 * Used for store two strings from expanded list row. For example "Creator Page(str1) : Smells(str2)
 */

public class TwoStringsWrapper {
    private String label;
    private String value;
    private View layout;

    public TwoStringsWrapper(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public TwoStringsWrapper() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public void setLayout(View layout) {
        this.layout = layout;
    }

    public View getLayout() {
        return layout;
    }
}