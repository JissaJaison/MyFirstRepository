package com.onbts.ITSMobile.UI.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.activities.base.ImageGalleryActivity;
import com.onbts.ITSMobile.UI.dialogs.action.ActionDialog;
import com.onbts.ITSMobile.UI.fragments.base.BaseFragment;
import com.onbts.ITSMobile.adapters.ExpIssueAdapter;
import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.model.ActionIssue;
import com.onbts.ITSMobile.model.DetailedIssue;
import com.onbts.ITSMobile.model.FileModel;
import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.base.Model;
import com.onbts.ITSMobile.model.wrapper.ExpandableInfoWrapper;
import com.onbts.ITSMobile.model.wrapper.TwoStringsWrapper;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.DbService;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import util.GetColorByPriority;
import util.SqliteImageDownloader;

/**
 * Created by JLAB on 12.03.14.
 */
public class DetailedPage extends BaseFragment implements ActionDialog.ActionDialogListener, View.OnClickListener {
    ArrayList<ExpandableInfoWrapper> mData = new ArrayList<>();
    OnNavigationChange navigationListener;
    TextView errorView;
    //store id's of images from db
//    private String[] urls;
    //store file names and send them to gallery(display in overlay text view)
//    private String[] filenames;
    private ActionDialog dialog;
    private ExpIssueAdapter listAdapter;
    private int mPosition = -1;
    private DetailedIssue details;
    private View.OnClickListener actionClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d("toast", "clicked" + view.getId());
            //Toast.makeText(getActivity(), ((Button) view).getText(), Toast.LENGTH_SHORT).show();
            ActionIssue action = (ActionIssue) view.getTag();
            if (details.getActionIssues() != null)
                for (ActionIssue issue : details.getActionIssues()) {
                    if (action.getId() == issue.getId() || (issue.getId() == 22
                            && action.getId() == R.id.item_play)) {
                        dialog = new ActionDialog();
                        dialog.setActionDialogListener(DetailedPage.this);
                        dialog.setIssue(issue);
                        dialog.setUser(navigationListener.onGetUser());
                        dialog.setDetails(details);
                        dialog.show(getActivity().getSupportFragmentManager(), "dialog_action");
                    }
                }
        }
    };
    private List<String> listDataHeader;
    private HashMap<String, List<TwoStringsWrapper>> listDataChild;
    private long issueId;
    private LayoutInflater inflater;
    private View priorityView, progressView;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions options;
    private View mainView;
    private int screenWidth;
    //actions expand
    private LinearLayout actionsTableLayout;
    private BroadcastReceiver dbListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                DBRequest request = intent
                        .getParcelableExtra(ServiceDataBase.KEY_REQUEST);
                if (request != null) {
                    request.setModels(intent.<Model>getParcelableArrayListExtra(ServiceDataBase.KEY_REQUEST_MODELS));
                    onHandelDBMessage(request);
                }
            } else {
            }


        }
    };
    private View header;

    @Deprecated
    public static DetailedPage newInstance(int pos, DetailedIssue details) {
        DetailedPage f = new DetailedPage();
        Bundle args = new Bundle();
        args.putInt("id", pos);
        args.putLong("issueid", details.getId());
        args.putParcelable("details", details);
        f.setArguments(args);
        return f;
    }

    public static DetailedPage newInstance(int pos, long issueId) {
        DetailedPage f = new DetailedPage();
        Bundle args = new Bundle();
        args.putInt("id", pos);
        args.putLong("issueid", issueId);
        f.setArguments(args);
        Log.i("frag", "new instance");

        return f;
    }

    public DetailedIssue getDetails() {
        return details;
    }

    public long getIssueId() {
        return issueId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments() != null ? getArguments().getInt("id") : 1;
        //setRetainInstance(true);
        setHasOptionsMenu(false);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        mData = new ArrayList<>();

        Log.i("expand", "oncreate DetailedPage");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnNavigationChange) {
            navigationListener = (OnNavigationChange) activity;
        } else {
            Log.d("cast error", "cannot cast activity to ActionDialog.ActionDialogListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.item_pager_detailed, container, false);
        this.inflater = inflater;
        progressView = mainView.findViewById(R.id.view_progress_bar);
        priorityView = mainView.findViewById(R.id.vPriorDetailed);
        errorView = (TextView) mainView.findViewById(R.id.view_error);
        Log.i("frag", "oncreate view detailed page" + mPosition);
        if (getArguments() != null) {
            issueId = getArguments().getLong("issueid");
        }
        return mainView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mNavigator.onLoadDetaileIssue(issueId);
        errorView.setVisibility(View.GONE);
        IntentFilter filterDB = new IntentFilter(ServiceDataBase.BROADCAST_ACTION);
        getActivity().registerReceiver(dbListener, filterDB);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(dbListener);
    }

    private void setHeaderData(TextView title, View viewHeader, View divider, String text) {
        if (text != null && text.length() > 0) {
            viewHeader.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            title.setText(text);
        } else {
//            viewHeader.setVisibility(View.GONE);
//            divider.setVisibility(View.GONE);
            //TODO: for test
//            title.setText("нету :)");
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void updateView(final DetailedIssue details, View view) {
        if (getView() == null || details == null)
            return;
        this.details = details;
        final ExpandableListView lvIssueDetail = (ExpandableListView) view.findViewById(R.id.lvIssueDetailed);
        if (header == null) {
            header = inflater.inflate(R.layout.header_issue_detail, null);
            lvIssueDetail.addFooterView(header);
        }

        String notes = details.getNotes();
        setHeaderData((TextView) header.findViewById(R.id.tvNotesValue),
                header.findViewById(R.id.tvNotesLabel), header.findViewById(R.id.tvNotesLabelDivider), notes);

        //If we have any attachments to issue, display them and set listener to launch new activity with gallery
        if (details.getFileList() != null && details.getFileList().size() > 0) {
            TableLayout attachmentTableLayout = (TableLayout) header.findViewById(R.id.tlAttachments);
            attachmentTableLayout.removeAllViews();
            final ArrayList<FileModel> fileModels = new ArrayList<>();

            options = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.ic_empty)
                    .showImageOnFail(R.drawable.ic_error).resetViewBeforeLoading(true)
                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).bitmapConfig(Bitmap.Config.RGB_565)
                    .considerExifParams(true).build();
            for (int i = 0; i < details.getFileList().size(); i++) {
                final FileModel model = details.getFileList().get(i);
                model.setPath("db://" + model.getId());
                TableRow row = (TableRow) inflater.inflate(R.layout.item_table_row_attach, attachmentTableLayout, false);

                String nameWithSize = details.getFileList().get(i).getFilename()
                        + "(" + details.getFileList().get(i).getFilesize() + " bytes )";
                TextView content = (TextView) row.findViewById(R.id.tvAttachmentName);
                content.setText(nameWithSize);

                ImageView contentImage = (ImageView) row.findViewById(R.id.imgAttachmentIcon);
                attachmentTableLayout.addView(row);
                //Using custom scheme db://, logic of scheme parsing in SqliteImageDownloader.java class

                if (model.isImage()) {
                    //is image
                    imageLoader.displayImage(model.getPath(), contentImage, options);
                    fileModels.add(model);
                    row.setTag(fileModels.size() - 1);
                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ImageGalleryActivity.class);
                            intent.putParcelableArrayListExtra("images", fileModels);
                            Object o = v.getTag();
                            int p = 0;
                            if (o != null) {
                                try {
                                    p = (int) o;
                                } catch (Exception e) {
                                }
                            }
                            intent.putExtra("currentPosition", p);
                            startActivity(intent);
                        }
                    });
                } else {
                    // not image
                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNavigator.onOpenFile(model.getId());
                        }
                    });
                }
            }

        } else {
            //We do not have any attachments
            ((TextView) header.findViewById(R.id.tvAttachValue)).setText("No attachment");
        }

        Button actionFirst = (Button) header.findViewById(R.id.action_first);
        Button actionSecond = (Button) header.findViewById(R.id.action_second);
        Button actionMore = (Button) header.findViewById(R.id.action_more);
        actionFirst.setOnClickListener(this);
        actionSecond.setOnClickListener(this);
        actionMore.setOnClickListener(this);
        int count = details.getActionIssues() != null ? details.getActionIssues().size() : 0;
        switch (count) {
            case 0:
                header.findViewById(R.id.actions_more_group).setVisibility(View.GONE);
                header.findViewById(R.id.tvActionsLabelDivider).setVisibility(View.GONE);
                actionFirst.setVisibility(View.GONE);
                actionSecond.setVisibility(View.GONE);
                break;
            case 1:
                header.findViewById(R.id.actions_more_group).setVisibility(View.GONE);
                header.findViewById(R.id.tvActionsLabelDivider).setVisibility(View.GONE);
                actionFirst.setVisibility(View.VISIBLE);
                actionFirst.setText(details.getActionIssues().get(0).getName());
                actionSecond.setVisibility(View.GONE);
                break;
            case 2:
                header.findViewById(R.id.actions_more_group).setVisibility(View.GONE);
                header.findViewById(R.id.tvActionsLabelDivider).setVisibility(View.GONE);
                actionFirst.setVisibility(View.VISIBLE);
                actionFirst.setText(details.getActionIssues().get(0).getName());
                actionSecond.setText(details.getActionIssues().get(1).getName());
                actionSecond.setVisibility(View.VISIBLE);
                break;
            default:
                header.findViewById(R.id.actions_more_group).setVisibility(View.VISIBLE);
                header.findViewById(R.id.tvActionsLabelDivider).setVisibility(View.VISIBLE);
                actionFirst.setVisibility(View.VISIBLE);
                actionSecond.setVisibility(View.VISIBLE);
                actionMore.setVisibility(View.VISIBLE);
                actionFirst.setText(details.getActionIssues().get(0).getName());
                actionSecond.setText(details.getActionIssues().get(1).getName());
                break;
        }
        if (count > 2) {
            header.findViewById(R.id.actions_more_group).setVisibility(View.VISIBLE);
            header.findViewById(R.id.tvActionsLabelDivider).setVisibility(View.VISIBLE);
        } else {
            header.findViewById(R.id.actions_more_group).setVisibility(View.GONE);
            header.findViewById(R.id.tvActionsLabelDivider).setVisibility(View.GONE);
        }
        //Set upper strip color(priority color)

        //Add actions to header
        //TODO: removed

        Button historyMore = (Button) header.findViewById(R.id.history_more);
        historyMore.setOnClickListener(this);
        if (details.getHistoryList() != null && details.getHistoryList().size() > 0) {
            header.findViewById(R.id.history_more_group).setVisibility(View.VISIBLE);
        } else {
            header.findViewById(R.id.history_more_group).setVisibility(View.GONE);
        }

        priorityView.setBackgroundColor(GetColorByPriority.getColor(details.getPriorId(),
                getActivity()));




        pr();


        if (listAdapter == null) {
            listAdapter = new ExpIssueAdapter(getActivity(), mData);
            lvIssueDetail.setAdapter(listAdapter);
        } else {
            listAdapter.setData(mData);
        }

        lvIssueDetail.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String title = listAdapter.getGroupTitle(groupPosition);
                switch (title) {
                    case "History":
                        mNavigator.onShowHistory(details.getId());
                        return true;
                    case "Details":
                        mNavigator.onShowDetails(details.getId());
                        return true;
                    default:
                        return false;
                }
                /*if (groupPosition == listAdapter.getHistoryPosition()) {
                    mNavigator.onShowHistory(details.getId());
                    return true;
                } else
                    return false;*/
            }
        });


    }


    public String getFormattedTime(String timestamp) {
        // the format of ur date
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, H:mm", Locale.US);
        long time = Long.parseLong(timestamp);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time); // *1000 is to convert seconds to
        // milliseconds
        Date date = cal.getTime();
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public void pr() {
        listDataHeader.clear();
        listDataChild.clear();
        mData.clear();

        ArrayList<TwoStringsWrapper> creationDetails = new ArrayList<>();
        addDetailsItem(creationDetails, "Entered by: ", details.getEnteredBy());
        addDetailsItem(creationDetails, "Reported by Crew: ", details.getReportedByCrew());
        addDetailsItem(creationDetails, "Crew Department: ", details.getCrewDepartment());
        addDetailsItem(creationDetails, "Crew Position: ", details.getCrewPosition());
        addDetailsItem(creationDetails, "On behalf of User: ", details.getOnbehalfofuser());
        // NOT COLUMN addOrNotToAdd("On behalf of group: " +
        // details.getOnbehalfofgroup());
        addDetailsItem(creationDetails, "Office Phone Number: ",
                details.getCreatorOfficePhoneNumber());
        addDetailsItem(creationDetails, "Extension: ", details.getCreatorExtension());
        addDetailsItem(creationDetails, "Mobile: ", details.getCreatorMobile());
        addDetailsItem(creationDetails, "Pager: ", details.getCreatorPager());


//
//        ArrayList<TwoStringsWrapper> amosDetails = new ArrayList<>();


        ArrayList<TwoStringsWrapper> issueDetails = new ArrayList<>();
        addDetailsItem(issueDetails, "Status: ", details.getStatus());
        addDetailsItem(issueDetails, "Defect: ", details.getDefect());
        addDetailsItem(issueDetails, "Comp name: ", details.getCompName());
        addDetailsItem(issueDetails, "Func no: ", details.getFuncNo());
        addDetailsItem(issueDetails, "Func desc: ", details.getFuncDescr());
        addDetailsItem(issueDetails, "Serial no: ", details.getSerialNo());

        ArrayList<TwoStringsWrapper> locationDetails = new ArrayList<>();
        addDetailsItem(locationDetails, "Deck: ", details.getDeckDesc());
        addDetailsItem(locationDetails, "Transverse: ", details.getTransverse());
        addDetailsItem(locationDetails, "Section: ", details.getSection());
        addDetailsItem(locationDetails, "Fire zone: ", details.getFireZone());
        addDetailsItem(locationDetails, "Location Owner: ", details.getLocationOwner());

        ArrayList<TwoStringsWrapper> guestDetails = new ArrayList<>();
        addDetailsItem(guestDetails, "Guest Service Issue: ", details.getGuestServiceIssue());
        addDetailsItem(guestDetails, "Require Guest Call Back: ", details.getRequireGuestCallBack());
        addDetailsItem(guestDetails, "Guest FirstName: ", details.getGuestFirstName());
        addDetailsItem(guestDetails, "Guest LastName: ", details.getGuestLastName());
        if (details.getDateGuestExp() != null)
            addDetailsItem(guestDetails, "Date Guest Experienced Issue: ", getFormattedTime(details.getDateGuestExp()));
        addDetailsItem(guestDetails, "Cabin Number: ", details.getCabinNumber());
        addDetailsItem(guestDetails, "Reservation Number: ", details.getReservationNumber());
        addDetailsItem(guestDetails, "Disembark Date: ", details.getDisembarkDate());
        addDetailsItem(guestDetails, "Severity: ", details.getSeverity());
        Log.d("creation details + ", "" + creationDetails.size());
        Log.d("issueDetails details + ", "" + issueDetails.size());
        Log.d("guestDetails details + ", "" + guestDetails.size());

        addDetailsHeader(listDataChild, "Issue Type: ", details.getIssueType(), issueDetails);
        addDetailsHeader(listDataChild, "Location: ", details.getLocationDesc(), locationDetails);
        addDetailsHeader(listDataChild, "Creation: ", getFormattedTime(details.getCreatedDate()), creationDetails);
        addDetailsHeader(listDataChild, "Guest Details: ", details.getGuestServiceIssue(), guestDetails);

//        addDetailsHeader(listDataChild, "AMOS details", amosDetails);
   /*     listDataHeader.add("Actions");
        List<TwoStringsWrapper> ls = new ArrayList<>();
        TwoStringsWrapper tw = new TwoStringsWrapper("q", "q");
        tw.setLayout(actionsTableLayout);
        ls.add(tw);
        listDataChild.put("Actions", ls);
        //mData.add(new ExpandableInfoWrapper("Actions", ls));
        listDataHeader.add("History");
        listDataChild.put("History", null);*/
//        mData.add(new ExpandableInfoWrapper("Details", null));


//        mData.add(new ExpandableInfoWrapper("History", null));


        //int count = details.getHistoryList().size();
      /*  int count = 0; //NO HISTORY HERE, NOW ITS LIKE AN ACTION
        for (int i = count - 1; i >= 0; i--) {
            Log.d("history", "Hist");
            ArrayList<TwoStringsWrapper> h = new ArrayList<>();
            HistoryModel model = details.getHistoryList().get(i);
//            History - Labels should be "Priority", "Assigned Department", "Assigned User", Time Since Last Update", "Update By"
            addDetailsItem(h, "Priority: ", model.getPriorityDesc());
            addDetailsItem(h, "Assigned Department: ", model.getDepartmentDesc());
            addDetailsItem(h, "Assigned User: ", model.getAssignedUser());
            if (i > 0) {
                long prevStamp = details.getHistoryList().get(i - 1).getLastUpdateDate();
                long nowStamp = model.getLastUpdateDate();
                addDetailsItem(h, "Time Since Last Update: ", differenceBetweenStamps(nowStamp - prevStamp));

            }
//            addDetailsItem(h, "Update Date: ", timestampToDate(model.getLastUpdateDate()));
            addDetailsItem(h, "Update By: ", model.getUpdatedByUser());
            addDetailsItem(h, "Notes: ", model.getNotes());

            addDetailsHeader(listDataChild, model.getActionDesc(), h, true);

        }*/

    }

    public void addDetailsItem(ArrayList<TwoStringsWrapper> list, String title, String value) {
        if (value != null && value.length() > 0) {
            list.add(new TwoStringsWrapper(title, value));
        }
    }

    public void addDetailsHeader(HashMap<String, List<TwoStringsWrapper>> map, String key, String value, List<TwoStringsWrapper> list) {
        if (list.size() > 0) {
            listDataHeader.add(key);
            map.put(key, list);
            mData.add(new ExpandableInfoWrapper(key, value, list));
        }
    }


    public String timestampToDate(long timestamp) {
        long time = timestamp;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss", Locale.US); // the format of your date
        cal.setTimeInMillis(time); // *1000 is to convert seconds to milliseconds
        Date date = cal.getTime();
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public String differenceBetweenStamps(long timestamp) {
        long time = timestamp;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.US); // the format of your date
        cal.setTimeInMillis(time); // *1000 is to convert seconds to milliseconds
        Date date = cal.getTime();
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    @Override
    public void onHandelDBMessage(DBRequest request) {
        if (request.getType() == DBRequest.DBRequestType.DETAILS_ISSUE) {
            DetailedIssue issue = (DetailedIssue) request.getModel();

            if (issue != null && issue.getId() == issueId) {
                updateView(issue, getView());
                if (progressView != null)
                    progressView.setVisibility(View.GONE);
            } else {
                if (request.getId() == issueId) {
                    if (errorView != null) {
                        errorView.setVisibility(View.VISIBLE);
                        errorView.setText(String.format("Error loading issue: %d", issueId));
                    }
                    if (progressView != null)
                        progressView.setVisibility(View.GONE);

                }
            }
        }
    }

    @Override
    public void onActionDataConfirm(ArrayList<ReturnDateWithActionDialog> data, long idAction, String actionCode, long prevActionID, long nextActionID, boolean keep) {
        if (data == null || data.size() == 0 || details == null) {
            Toast.makeText(getActivity(), "Something is wrong!", Toast.LENGTH_LONG).show();
            return;
        }
        onAddActions(details, data, idAction, actionCode, prevActionID, nextActionID, keep);
    }

    @Override
    public void onClose() {

    }

    public void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction,
                             String actionCode, long prevActionID,
                             long nextActionID, boolean keep) {
        Intent intent = navigationListener.onGetDB();
        intent.putExtra("user", navigationListener.onGetUser());
        intent.putExtra("issue", details);
        intent.putParcelableArrayListExtra("data", data);
        intent.putExtra("idAction", idAction);
        intent.putExtra("ActionCode", actionCode);
        intent.putExtra("nextActionID", nextActionID);
        intent.putExtra("prevActionID", prevActionID);
        intent.putExtra("keep", keep);
        intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.INSERT_ISSUE_TRACK));
        navigationListener.onSendDBRequest(intent);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_more:
                mNavigator.onActionMoreClock(details);
                break;
            case R.id.history_more:
                mNavigator.onShowHistory(details.getId());
                break;
            case R.id.action_first:
                if (details.getActionIssues() != null && details.getActionIssues().size() > 0) {
                    ActionIssue action = details.getActionIssues().get(0);
                    dialog = new ActionDialog();
                    dialog.setActionDialogListener(DetailedPage.this);
                    dialog.setIssue(action);
                    dialog.setUser(navigationListener.onGetUser());
                    dialog.setDetails(details);
                    dialog.show(getActivity().getSupportFragmentManager(), "dialog_action");
                }
                break;
            case R.id.action_second:
                if (details.getActionIssues() != null && details.getActionIssues().size() > 1) {
                    ActionIssue action = details.getActionIssues().get(1);
                    dialog = new ActionDialog();
                    dialog.setActionDialogListener(DetailedPage.this);
                    dialog.setIssue(action);
                    dialog.setUser(navigationListener.onGetUser());
                    dialog.setDetails(details);
                    dialog.show(getActivity().getSupportFragmentManager(), "dialog_action");
                }

                break;
        }
    }
}
