package com.onbts.ITSMobile.model;


public class SpinnerTransferDepartmentPanel {
	    protected String name;
	    protected int id;

	    public SpinnerTransferDepartmentPanel(int id, String name) {
	        this.name = name;
	        this.id = id;
	    }

	    public String getName() {
	        return name;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public int getId() {
	        return id;
	    }

	    public void setId(int id) {
	        this.id = id;
	    }
}
