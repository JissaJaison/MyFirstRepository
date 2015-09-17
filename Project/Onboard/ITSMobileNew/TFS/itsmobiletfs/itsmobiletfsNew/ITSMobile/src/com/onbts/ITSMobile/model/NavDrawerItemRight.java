package com.onbts.ITSMobile.model;

public class NavDrawerItemRight {

	private String filterName;
	private String filterValue;
	public String getFilterName() {
		return filterName;
	}
	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	public NavDrawerItemRight(String filterName, String filterValue) {
		super();
		this.filterName = filterName;
		this.filterValue = filterValue;
	}

}
