package com.onbts.ITSMobile.UI.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.UI.fragments.base.BaseFragment;
import com.onbts.ITSMobile.adapters.ExpIssueAdapter;
import com.onbts.ITSMobile.adapters.IssuePagerAdapter;
import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.model.issue.IssueModel;
import com.onbts.ITSMobile.model.wrapper.ExpandableInfoWrapper;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class IssueDetailFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<List<ActionIssue>> {
    MenuItem menuItem;
    ArrayList<ExpandableInfoWrapper> mData = new ArrayList<>();
    private Context mContext;
    private IssuePagerAdapter pagerAdapter;
    private ViewPager mPager;
    private ExpIssueAdapter listAdapter;
    private List<String> listDataHeader;
    // save old title when onbackpressed setup
    private HashMap<String, List<String>> listDataChild;
    private View view;
    private ExpandableListView lvIssueDetail;
    private View header;
    private DetailedIssue details;
    private TextView tvIssueType, tvLocation, tvEnteredBy, tvCreated, tvStatus, tvGuestService,
            tvNotes, tvAttach;
    private int selectedPosition;
    private int query;
    private Cursor mCursor;
    private int selectedPage;
    private int index = 0;
    private ViewPager.OnPageChangeListener pageListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int i2) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedPage = position;
            index = position - 1;
            if (position == 0) {
                index = position;
                return;
            } else if (position == pagerAdapter.getCount() - 1) {
                index = 0;
            }
            mNavigator.onTitleChange("ISSUE " + pagerAdapter.getIdByPosition(index));
            updateUI(index);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int trueIndex = mPager.getCurrentItem();

            if (state == ViewPager.SCROLL_STATE_IDLE) {
                int curr = mPager.getCurrentItem();
                int lastReal = mPager.getAdapter().getCount() - 2;

                Log.v("scroll", "curr: " + curr + "lastReal: " + lastReal + "TRUE INDEX  = " + trueIndex);
                if (curr == 0) {
                    trueIndex = lastReal - 1;
                    mPager.setCurrentItem(lastReal, false);
                } else if (curr == lastReal + 1) {
                    trueIndex = 1;
                    mPager.setCurrentItem(1, false);
                }
            }

        }
    };
    private ActionDialog dialog;
    private int pageCount;
    private Menu menu;
    private List<ActionIssue> issues;
    private List<IssueModel> models;

    public void setListIssues(List<IssueModel> models, int position) {
        this.models = models;
        this.selectedPosition = position;
    }

    public Cursor getCursor() {
        return mCursor;
    }

    public void setCursor(Cursor cursor) {
        if (mCursor != null) {
            pagerAdapter.swapCursor(cursor);
            mPager.setAdapter(pagerAdapter);
        }
        this.mCursor = cursor;
    }


    @TargetApi (Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (details == null && savedInstanceState != null)
            details = savedInstanceState.getParcelable("details");
//        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        view = inflater.inflate(R.layout.frag_issue_detail, null);
        mPager = (ViewPager) view.findViewById(R.id.vpDetailedIssue);
        header = inflater.inflate(R.layout.header_issue_detail, null);
        mPager.setAdapter(pagerAdapter);
        mPager.setOffscreenPageLimit(7);
//        mPager.setId(0x7F04FFF1);
        pageCount = pagerAdapter.getCount();
        Log.d("priwlo", "" + selectedPosition);
        mPager.setCurrentItem(++selectedPosition, false);
        mPager.setOnPageChangeListener(pageListener);
        Log.v("gest", "listener setted");

        mNavigator.onTitleChange("ISSUE " + pagerAdapter.getIdByPosition(countIndex(mPager.getCurrentItem())));
//        ((TextView) header.findViewById(R.id.tvIssueTypeValue)).setText(details.getIssueType());
//        ((TextView) header.findViewById(R.id.tvLocationValue)).setText(details.getLocationDesc());
//        ((TextView) header.findViewById(R.id.tvEnteredByValue)).setText(details.getEnteredBy());
//        ((TextView) header.findViewById(R.id.tvStatusValue)).setText(details.getStatus());
//        ((TextView) header.findViewById(R.id.tvGuestServiceValue)).setText(details.getGuestServiceIssue());
        ((TextView) header.findViewById(R.id.tvNotesValue)).setText(details.getNotes());
        ((TextView) header.findViewById(R.id.tvAttachValue)).setText("No attachments");

        tvAttach = (TextView) header.findViewById(R.id.tvAttachValue);


        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        listAdapter = new ExpIssueAdapter(getActivity(), mData);
        //pr();
        if (menuItem != null)
            updateUI(countIndex(mPager.getCurrentItem()));
        return view;
    }

    public int countIndex(int position) {
        index = position - 1;
        if (position == 0) {
            index = position;
        } else if (position == pagerAdapter.getCount() - 1) {
            index = 0;
        }
        return index;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null && details != null)
            outState.putParcelable("details", details);
        super.onSaveInstanceState(outState);
    }

    public void pr() {
        listDataHeader.clear();
        listDataChild.clear();
        mData.clear();
        ArrayList<String> creationDetails = new ArrayList<String>();
        addDetailsItem(creationDetails, "Reported by crew: ", details.getReportedByCrew());
        addDetailsItem(creationDetails, "Crew Department: ", details.getCrewDepartment());
        addDetailsItem(creationDetails, "Crew Position: ", details.getCrewPosition());
        addDetailsItem(creationDetails, "On behalf of user: ", details.getOnbehalfofuser());
        // NOT COLUMN addOrNotToAdd("On behalf of group: " +
        // details.getOnbehalfofgroup());
        addDetailsItem(creationDetails, "Creator Office Phone Number: ",
                       details.getCreatorOfficePhoneNumber());
        addDetailsItem(creationDetails, "Creator Extension: ", details.getCreatorExtension());
        addDetailsItem(creationDetails, "Creator Mobile: ", details.getCreatorMobile());
        addDetailsItem(creationDetails, "Creator Pager: ", details.getCreatorPager());

        ArrayList<String> qq = new ArrayList<String>();
        addDetailsItem(qq, "qReported by crew: ", details.getReportedByCrew());
        addDetailsItem(qq, "qCrew Department: ", details.getCrewDepartment());
        addDetailsItem(qq, "qCrew Position: ", details.getCrewPosition());
        addDetailsItem(qq, "qOn behalf of user: ", details.getOnbehalfofuser());

        /*
        ArrayList<String> issueDetails = new ArrayList<String>();
        addDetailsItem(issueDetails, "Defect: ", details.getDefect());

        ArrayList<String> locationDetails = new ArrayList<String>();
        addDetailsItem(locationDetails, "Deck: ", details.getDeckDesc());
        addDetailsItem(locationDetails, "Transverse: ", details.getTransverse());
        addDetailsItem(locationDetails, "Section: ", details.getSection());
        addDetailsItem(locationDetails, "Fire zone: ", details.getFireZone());
        addDetailsItem(locationDetails, "Location Owner: ", details.getLocationOwner());

        ArrayList<String> guestDetails = new ArrayList<String>();
        addDetailsItem(guestDetails, "Guest Service Issue: ", details.getGuestServiceIssue());
        addDetailsItem(guestDetails, "Require Guest Call Back: ", details.getRequireGuestCallBack());
        addDetailsItem(guestDetails, "Guest FirstName: ", details.getGuestFirstName());
        addDetailsItem(guestDetails, "Guest LastName: ", details.getGuestLastName());
        addDetailsItem(guestDetails, "Date Guest Experienced Issue: ", details.getDateGuestExp());
        addDetailsItem(guestDetails, "Cabin Number: ", details.getCabinNumber());
        addDetailsItem(guestDetails, "Reservation Number: ", details.getReservationNumber());
        addDetailsItem(guestDetails, "Disembark Date: ", details.getDisembarkDate());
        addDetailsItem(guestDetails, "Severity: ", details.getSeverity());
        Log.d("creation details + ", "" + creationDetails.size());
        Log.d("issueDetails details + ", "" + issueDetails.size());
        Log.d("locationDetails details + ", "" + locationDetails.size());
        Log.d("guestDetails details + ", "" + guestDetails.size());
        */

        // listDataHeader.add("Issue Details");
        // listDataHeader.add("Location Details");
        // listDataHeader.add("AMOS Details");
        // listDataHeader.add("Guest Details");
        // listDataHeader.add("History");
        // listDataHeader.add("Issue Created");

        addDetailsHeader(listDataChild, "Creation Details", creationDetails);
        //addDetailsHeader(listDataChild, "Issue Details", issueDetails);
        // addDetailsHeader(listDataChild, "Location Details", locationDetails);
        //addDetailsHeader(listDataChild, "Guest Details", guestDetails);
        addDetailsHeader(listDataChild, "Guest Details123", qq);

        ArrayList<String> hist = new ArrayList<String>();
        addDetailsItem(hist, "Hist: ", "hiiiii");
        addDetailsItem(hist, "Hist22: ", "hiiiii");
        addDetailsHeader(listDataChild, "Guest Details", hist);
        // listDataChild.put(listDataHeader.get(0), creationDetails);
        // listDataChild.put(listDataHeader.get(1), issueDetails);
        // listDataChild.put(listDataHeader.get(2), locationDetails);
        // listDataChild.put(listDataHeader.get(3), guestDetails);
        // listDataChild.put(listDataHeader.get(4), null);
        // listDataChild.put(listDataHeader.get(5), null);

        for (Map.Entry<String, List<String>> entry : listDataChild.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            //mData.add(new ExpandableInfoWrapper(key, value));
            // do what you have to do here
            // In your case, an other loop.
        }

        listAdapter.setData(mData);
    }

    private void updateUI(int pos) {
        if (menu != null && menuItem != null) {
            menu.removeGroup(R.id.item_overflow);
            menu.findItem(R.id.item_play).setVisible(false);
            UserModel user = mNavigator.onGetUser();
//            887565
            Bundle bundle = new Bundle();
            bundle.putLong("status", pagerAdapter.getStatusIdByPosition(pos));
            bundle.putString("issue", pagerAdapter.getIdByPosition(pos));
            bundle.putParcelable("user", user);
            Loader loader = getLoaderManager().restartLoader(887565, bundle, this);
            if (loader != null)
                loader.forceLoad();

            /*issues = DBRequest.getActionForIssue(user, pagerAdapter.getIdByPosition(pos), pagerAdapter.getStatusIdByPosition(pos), true, DbService.getInstance(mContext).getIssutraxdb());
            menu.removeGroup(R.id.item_overflow);
            if (issues != null) {
                boolean hasStart = false;
                for (ActionIssue issue : issues) {
//                add(int groupId, int itemId, int order, int titleRes)
                    if (issue.getId() == 22) {
                        hasStart = true;
                    } else
                        menu.add(R.id.item_overflow, (int) issue.getId(), 0, issue.getName()).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }
                menu.findItem(R.id.item_play).setVisible(hasStart);
            }*/
            if (DbService.getInstance(mContext).isFavorite(pagerAdapter.getIdByPosition(pos))) {
                //DbService.getInstance(mContext).setFavorite(id, true);
                menuItem.setIcon(R.drawable.ic_star_actionbar_pressed);
            } else {
                //DbService.getInstance(mContext).setFavorite(id, false);
                menuItem.setIcon(R.drawable.ic_star_actionbar);
            }
        } else {
            issues = null;
        }
    }

    public void addDetailsItem(ArrayList<String> list, String title, String value) {
        if (value != null && value.length() > 0) {
            list.add(title + "" + value);
        }
    }

    public void addDetailsHeader(HashMap<String, List<String>> map, String key, List<String> list) {
        if (list.size() > 0) {
            listDataHeader.add(key);
            map.put(key, list);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mContext = getActivity();
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        Log.d("expand", "oncreate expndbl");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.d("life", "onattach created issuesfragment");
        pagerAdapter = new IssuePagerAdapter(getChildFragmentManager(), mCursor, mContext);

    }

    @Override
    public void onHandelDBMessage(DBRequest request) {

    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public DetailedIssue getDetails() {
        return details;
    }

    public void setDetails(DetailedIssue details, int selectedPosition) {
        Log.d("frag", "setDetails");
        this.details = details;
        this.selectedPosition = selectedPosition;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            //case R.id.
            default: {
                if (issues != null)
                    for (ActionIssue issue : issues) {
                        if (item.getItemId() == issue.getId()) {
                            dialog = new ActionDialog();

                            dialog.setIssue(issue);
                            dialog.show(getChildFragmentManager(), "dialog_action");

                            return true;
                        }
                    }
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (dialog != null) {
            dialog.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detailed, menu);
        this.menu = menu;
        menuItem = menu.findItem(R.id.item_star);
        if (mPager != null)
            updateUI(countIndex(mPager.getCurrentItem()));
    }

    public int getQuery() {
        return query;
    }

    public void setQuery(int query) {
        this.query = query;
    }

    @Override
    public Loader<List<ActionIssue>> onCreateLoader(int id, Bundle args) {
        if (id == 887565) {
            UserModel user = args.getParcelable("user");
            String issueId = args.getString("issue");
            long statusId = args.getLong("status");
//            return new ActionLoader(getActivity(), user, issueId, statusId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<List<ActionIssue>> loader, List<ActionIssue> data) {
        Log.i("frag", "size " + data.size());
        issues = data;
        if (issues != null) {
            boolean hasStart = false;
            for (ActionIssue issue : issues) {
//                add(int groupId, int itemId, int order, int titleRes)
                if (issue.getId() == 22) {
                    hasStart = true;
                } else
                    menu.add(R.id.item_overflow, (int) issue.getId(), 0, issue.getName()).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            }
            menu.findItem(R.id.item_play).setVisible(hasStart);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<ActionIssue>> loader) {

    }
}
