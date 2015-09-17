package com.onbts.ITSMobile.model;

public class AlertPriorityChoiceItem {
	private boolean isChecked;
	private FilterModel filter;
	public AlertPriorityChoiceItem(boolean isChecked, FilterModel filter) {
		super();
		this.isChecked = isChecked;
		this.filter = filter;
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

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }
}