package com.onbts.ITSMobile.UI.fragments.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.adapters.NavDrawerRightAdapter;
import com.onbts.ITSMobile.adapters.PriorityAlertListAdapter;
import com.onbts.ITSMobile.adapters.SimpleAlertListAdapter;
import com.onbts.ITSMobile.interfaces.OnFiltered;
import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.model.AlertPriorityChoiceItem;
import com.onbts.ITSMobile.model.AlertSimpleChoiceItem;
import com.onbts.ITSMobile.model.FilterModel;
import com.onbts.ITSMobile.model.NavDrawerItemRight;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.util.ArrayList;
import java.util.HashMap;

import util.SPHelper;

/**
 * Created by tigre on 16.04.14.
 */
public class RightMenuFragment extends BaseMenuFragment implements AdapterView.OnItemClickListener {
    private NavDrawerRightAdapter adapter;
    private String[] navMenuRightTitles;
    private ArrayList<NavDrawerItemRight> rightNavDrawerItems;
    private OnFiltered filterListener;
    //    private OnDrawerMove drawerMove;
    private AlertDialog.Builder builder;
    private PriorityAlertListAdapter alertPriorityAdapter;
    private SimpleAlertListAdapter alertSimpleAdapter;
    private HashMap<String, FilterModel> filterModels;
    private OnNavigationChange mNavigator;
    private FilterModel priorityFilter;
    private FilterModel statusFilter;
    private FilterModel typeFilter;
    private FilterModel sectionFilter;
    private FilterModel deckFilter;
    private FilterModel departmentFilter;
    private FilterModel firezoneFilter;
    private FilterModel alertFilter;
    private FilterModel locationGroupFilter;

    //Here we store LocationID and LocationDesc of selected LocationGroup
    private ArrayList<FilterModel> locationIDFilter;
    private ArrayList<AlertPriorityChoiceItem> itemPriorityList;
    private ArrayList<AlertSimpleChoiceItem> itemChoiceList;

    private AdapterView.OnItemClickListener mOnPriorityItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            alertPriorityAdapter.changeRadio(position);
        }
    };
    private AdapterView.OnItemClickListener mOnSimpleItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            alertSimpleAdapter.changeRadio(position);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnFiltered) {
            filterListener = (OnFiltered) activity;
            mNavigator = (OnNavigationChange) activity;
        } else {
            throw new ClassCastException("Activity must implements fragment's OnFiltered");
        }
    }

    public void onGetFilter() {
        if (filterListener != null) {
            filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
        }
    }

    public void setFilters(FilterModel priority, FilterModel status, FilterModel type,
                           FilterModel section, FilterModel deck, FilterModel department, FilterModel firezone,  ArrayList<FilterModel> locationIDs, FilterModel alertFilter) {
        priorityFilter = priority;
        statusFilter = status;
        typeFilter = type;
        sectionFilter = section;
        deckFilter = deck;
        departmentFilter = department;
        firezoneFilter = firezone;
        this.locationIDFilter = locationIDs;
        this.alertFilter = alertFilter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View v = inflater.inflate(R.layout.menu_right_fragment, container, false);
        ListView list = (ListView) v.findViewById(R.id.list);
        alertPriorityAdapter = new PriorityAlertListAdapter();
        navMenuRightTitles = getResources().getStringArray(R.array.nav_drawer_right_titles);
        rightNavDrawerItems = new ArrayList<>();
        adapter = new NavDrawerRightAdapter(inflater, rightNavDrawerItems);
        list.setAdapter(adapter);
        list.setOnItemClickListener(this);
        fillRightMenu();
        return v;
    }

    public void fillRightMenu() {
        rightNavDrawerItems.clear();
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[0], SPHelper
                .getPriority(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[1], SPHelper
                .getStatus(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[2], SPHelper
                .getType(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[3], SPHelper
                .getSection(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[4], SPHelper
                .getDeck(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[5], SPHelper
                .getDepartment(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[6], SPHelper
                .getFirezone(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[7], SPHelper
                .getLocationGroup(getActivity())));
        rightNavDrawerItems.add(new NavDrawerItemRight(navMenuRightTitles[8], SPHelper
                .getAlert(getActivity())));
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                ArrayList<FilterModel> priorities = filterListener.getFilters(new DBRequest(DBRequest.DBRequestType.GET_FILTER_PRIORITIES));
                if (priorities == null) {
                    priorities = new ArrayList<>();
                    priorities.add(new FilterModel(-1, "OFF"));
                }
                builder = new AlertDialog.Builder(getActivity());
                View customHeaderView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.item_alert_header, null, false);
                ((TextView) customHeaderView.findViewById(R.id.tvAlertTitle))
                        .setText("FILTER BY PRIORITY");
                builder.setCustomTitle(customHeaderView);
                View customBodyView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.alert_list_radio, null, false);
                ListView listView = (ListView) customBodyView.findViewById(R.id.lvAlertList);
                itemPriorityList = new ArrayList<>();
                for (FilterModel priority : priorities) {
                    itemPriorityList.add(new AlertPriorityChoiceItem(false, priority));
                }
                alertPriorityAdapter = new PriorityAlertListAdapter(itemPriorityList, getActivity());
                listView.setAdapter(alertPriorityAdapter);
                listView.setOnItemClickListener(mOnPriorityItemClick);
                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                alertPriorityAdapter.getPositionByName(SPHelper.getPriority(getActivity()));
                builder.setView(customBodyView);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (alertPriorityAdapter.isAnySelected()) {
                            SPHelper.setPriority(getActivity(),
                                    alertPriorityAdapter.getCheckedTitle());
                        }
                        fillRightMenu();
                        // filter off value
                        priorityFilter = alertPriorityAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                });
                builder.show();
                break;
            case 1:
                ArrayList<FilterModel> statuses = filterListener.getFilters(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_STATUSES));
                if (statuses == null) {
                    statuses = new ArrayList<>();
                    statuses.add(new FilterModel(-1, "OFF"));
                }
                itemChoiceList = new ArrayList<>();
                for (FilterModel status : statuses) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(status));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getStatus(getActivity()));
                DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPHelper.setStatus(getActivity(), alertSimpleAdapter.getCheckedTitle(alertSimpleAdapter.isAnySelected()));
                        fillRightMenu();
                        // filter off value
                        statusFilter = alertSimpleAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY STATUS", mOnSimpleItemClick,
                        okListener);
                break;

            case 2:
                ArrayList<FilterModel> types = filterListener.getListOfActualFilters("type");
                if (types == null) {
                    types = new ArrayList<>();
                    types.add(new FilterModel(-1, "OFF"));
                }
                itemChoiceList = new ArrayList<>();
                for (FilterModel type : types) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(type));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getType(getActivity()));
                DialogInterface.OnClickListener okListenerType = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pos = alertSimpleAdapter.isAnySelected();
                        SPHelper.setType(getActivity(), alertSimpleAdapter.getCheckedTitle(pos));
                        fillRightMenu();
                        // filter off value
                        typeFilter = alertSimpleAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY TYPE", mOnSimpleItemClick,
                        okListenerType);
                break;
            case 3:
                ArrayList<FilterModel> sections = filterListener.getFilters(new DBRequest(DBRequest.DBRequestType
                        .GET_FILTER_LIST_SECTIONS));
                if (sections == null) {
                    sections = new ArrayList<>();
                    sections.add(new FilterModel(-1, "OFF"));
                }
                itemChoiceList = new ArrayList<>();
                for (FilterModel section : sections) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(section));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getSection(getActivity()));
                DialogInterface.OnClickListener okListenerSec = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPHelper.setSection(getActivity(), alertSimpleAdapter.getCheckedTitle(alertSimpleAdapter.isAnySelected()));
                        fillRightMenu();
                        // filter off value
                        sectionFilter = alertSimpleAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY SECTION", mOnSimpleItemClick,
                        okListenerSec);
                break;
            case 4:
                ArrayList<FilterModel> decks = filterListener.getFilters(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_DECKS));
                if (decks == null) {
                    decks = new ArrayList<>();
                    decks.add(new FilterModel(-1, "OFF"));
                }
                itemChoiceList = new ArrayList<>();
                for (FilterModel deck : decks) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(deck));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getDeck(getActivity()));

                DialogInterface.OnClickListener okListenerDeck = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPHelper.setDeck(getActivity(), alertSimpleAdapter.getCheckedTitle(alertSimpleAdapter.isAnySelected()));
                        fillRightMenu();
                        // filter off value
                        deckFilter = alertSimpleAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY DECK", mOnSimpleItemClick,
                        okListenerDeck);
                break;
            case 5:
                ArrayList<FilterModel> departments = filterListener.getFilters(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_DEPARTMENTS));
                if (departments == null) {
                    departments = new ArrayList<>();
                    departments.add(new FilterModel(-1, "OFF"));
                }
                itemChoiceList = new ArrayList<>();
                for (FilterModel department : departments) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(department));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getDepartment(getActivity()));
                DialogInterface.OnClickListener okListenerDep = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPHelper.setDepartment(getActivity(),
                                alertSimpleAdapter.getCheckedTitle(alertSimpleAdapter.isAnySelected()));
                        fillRightMenu();
                        // filter off value
                        departmentFilter = alertSimpleAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY DEPARTMENT", mOnSimpleItemClick,
                        okListenerDep);
                break;
            case 6:
                ArrayList<FilterModel> firezones = filterListener.getFilters(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_FIREZONES));
                if (firezones == null) {
                    firezones = new ArrayList<>();
                    firezones.add(new FilterModel(-1, "OFF"));
                }
                itemChoiceList = new ArrayList<>();
                for (FilterModel firezone : firezones) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(firezone));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getFirezone(getActivity()));
                DialogInterface.OnClickListener okListenerFire = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPHelper.setFirezone(getActivity(),
                                alertSimpleAdapter.getCheckedTitle(alertSimpleAdapter.isAnySelected()));
                        fillRightMenu();
                        // filter off value
                        firezoneFilter = alertSimpleAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY FIREZONE", mOnSimpleItemClick,
                        okListenerFire);
                break;
            case 7:
                ArrayList<FilterModel> locationGroups = filterListener.getFilters(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_LOCATIONGROUPS));
                if (locationGroups == null) {
                    locationGroups = new ArrayList<>();
                    locationGroups.add(new FilterModel(-1, "OFF"));
                }
                itemChoiceList = new ArrayList<>();
                for (FilterModel locationGroup : locationGroups) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(locationGroup));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getLocationGroup(getActivity()));
                DialogInterface.OnClickListener okListenerLoc = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPHelper.setLocationGroup(getActivity(),
                                alertSimpleAdapter.getCheckedTitle(alertSimpleAdapter.isAnySelected()));
                        fillRightMenu();
                        // filter off value
                        locationGroupFilter = alertSimpleAdapter.getCheckedFilter();
                        sendRequestToDB(new DBRequest(DBRequest.DBRequestType.GET_FILTER_LIST_LOCATION_ID));
                        //Send request here, sort in setLocationIDFilter method
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY LOCATION GROUP", mOnSimpleItemClick,
                        okListenerLoc);
                break;
            case 8:
                ArrayList<FilterModel> alerts = new ArrayList<>();
                alerts.add(new FilterModel(-1, "OFF"));
                alerts.add(new FilterModel(1, "Pre-Alert"));
                alerts.add(new FilterModel(2, "Alert"));
                itemChoiceList = new ArrayList<>();
                for (FilterModel alert : alerts) {
                    itemChoiceList.add(new AlertSimpleChoiceItem(alert));
                }
                alertSimpleAdapter = new SimpleAlertListAdapter(itemChoiceList, getActivity());
                alertSimpleAdapter.getPositionByName(SPHelper.getAlert(getActivity()));
                DialogInterface.OnClickListener okListenerAlert = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPHelper.setAlert(getActivity(),
                                alertSimpleAdapter.getCheckedTitle(alertSimpleAdapter.isAnySelected()));
                        fillRightMenu();
                        // filter off value
                        alertFilter = alertSimpleAdapter.getCheckedFilter();
                        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
                    }
                };
                buildCustomDialog(alertSimpleAdapter, "FILTER BY ALERT", mOnSimpleItemClick,
                        okListenerAlert);
                break;
            default:
                break;
        }
    }

    public void buildCustomDialog(BaseAdapter adapter, String headerTitle,
                                  AdapterView.OnItemClickListener listener, DialogInterface.OnClickListener okListener) {
        View header = LayoutInflater.from(getActivity()).inflate(R.layout.item_alert_header, null, false);
        View body = LayoutInflater.from(getActivity()).inflate(R.layout.alert_list_radio, null, false);
        builder = new AlertDialog.Builder(getActivity());
        ((TextView) header.findViewById(R.id.tvAlertTitle)).setText(headerTitle);
        builder.setCustomTitle(header);
        ListView lv = (ListView) body.findViewById(R.id.lvAlertList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(listener);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        builder.setView(body);
        builder.setPositiveButton("OK", okListener);
        builder.show();
    }

    public void setFilterLocationFilter(FilterModel model) {
        locationGroupFilter = model;
        fillRightMenu();
    }

    public void sendRequestToDB(DBRequest request) {
        mNavigator.onSendDBRequest(mNavigator.onGetDB().putExtra(ServiceDataBase.KEY_REQUEST, request).putExtra("locationGroupID", locationGroupFilter.id));
    }

    public void setLocationIDFilter(ArrayList<FilterModel> models) {
        this.locationIDFilter = models;
        filterListener.setFilter(priorityFilter, statusFilter, typeFilter, sectionFilter, deckFilter, departmentFilter, firezoneFilter, locationIDFilter, alertFilter);
    }

}
