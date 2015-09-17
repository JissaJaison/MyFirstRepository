package com.onbts.ITSMobile.model;

public class AlertSimpleChoiceItem {
    private boolean isChecked;
    private FilterModel filter;

    public AlertSimpleChoiceItem(boolean isChecked, FilterModel filter) {
        this.isChecked = isChecked;
        this.filter = filter;
    }

    public AlertSimpleChoiceItem(FilterModel filter) {
        this.isChecked = false;
        this.filter = filter;
    }

    public AlertSimpleChoiceItem() {
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public FilterModel getFilter() {
        return filter;
    }

    public void setFilter(String title) {
        this.filter = filter;
    }

}
