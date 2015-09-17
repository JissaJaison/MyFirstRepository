package com.onbts.ITSMobile.interfaces;

import com.onbts.ITSMobile.model.FilterModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.util.ArrayList;

/**
 * Created by JLAB on 17.04.2014.
 */
public interface OnFiltered {
    public void setFilter(FilterModel priority, FilterModel status,
                          FilterModel type, FilterModel section, FilterModel deck, FilterModel department, FilterModel firezone, ArrayList<FilterModel> locationIDs, FilterModel alertFilter);

    public ArrayList<FilterModel> getFilters(DBRequest request);

    public void setSort(FilterModel sort);

    public FilterModel getSort();

    public void clearAllFilters();

    public ArrayList<FilterModel> getListOfActualFilters(String filter);

}