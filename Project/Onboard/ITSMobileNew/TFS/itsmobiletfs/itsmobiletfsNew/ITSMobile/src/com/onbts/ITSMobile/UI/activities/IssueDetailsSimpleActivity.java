package com.onbts.ITSMobile.UI.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.activities.base.BaseIssueActivity;
import com.onbts.ITSMobile.UI.fragments.ActionsFragment;
import com.onbts.ITSMobile.UI.fragments.HistoryFragment;
import com.onbts.ITSMobile.model.DetailedIssue;

import com.onbts.ITSMobile.model.ReturnDateWithActionDialog;
import com.onbts.ITSMobile.model.UserModel;
import com.onbts.ITSMobile.services.DBRequest;
import com.onbts.ITSMobile.services.ServiceDataBase;

import java.util.ArrayList;

/**
 * Created by tigre on 01.06.14.
 */
public class IssueDetailsSimpleActivity extends BaseIssueActivity {

    @Override
    protected void onHandelDBMessage(DBRequest request) {
        if (request.getType() == DBRequest.DBRequestType.INSERT_ISSUE_TRACK)
            finish();
        super.onHandelDBMessage(request);
    }
    SimpleGestureFilter mDetector;
    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_main);
        mDetector = new SimpleGestureFilter(this,new SimpleGestureListener() {
            @Override
            public void onSwipe(int direction) {
                Log.i("IssueDetailsSimpleActivity", "swipe = " + direction);
                switch (direction) {

                    case SimpleGestureFilter.SWIPE_RIGHT :
                        onBackPressed();
                        break;
/*
            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
                break;
*/
                }
            }

            @Override
            public void onDoubleTap() {

            }
        });

        mDetector.setSwipeMaxDistance((int) (getResources().getDisplayMetrics().density*320));
        mDetector.setSwipeMinDistance((int) (getResources().getDisplayMetrics().density*120));
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (action == null) {
                finish();
                return;
            } else {
                switch (action) {
                    case "history": {
                        actionBar.setTitle("History");
                        HistoryFragment fragment = new HistoryFragment();
                        fragment.setIssueId(intent.getLongExtra("issueID", 0));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        break;
                    }
                    case "actionsMore": {
                        actionBar.setTitle("Actions more");
                        ActionsFragment fragment = new ActionsFragment();
                        fragment.setDetailed(intent.<DetailedIssue>getParcelableExtra("details"));
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        break;
                    }
                    case "details": {
                       /* actionBar.setTitle("Details");
                        IssueDetailsPageFragment fragment = new IssueDetailsPageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putLong("issueid", intent.getLongExtra("issueID", 0));
                        fragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();*/
                        break;
                    }
                }
            }
        } else {
            actionBar.setTitle(savedInstanceState.getString("title"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (!getSupportFragmentManager().popBackStackImmediate()) {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("title", (String) getActionBar().getTitle());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onUserUpdate() {

    }

    @Override
    public void onOpenFile(long id) {

    }

    @Override
    public void onStartIssueList() {

    }

    @Override
    public void onTitleChange(String title) {

    }

    @Override
    public void onShowActionBar() {

    }

    @Override
    public void onHideActionBar() {

    }

    @Override
    public void onSetUser(UserModel user) {

    }

    @Override
    public UserModel onGetUser() {
        return user;
    }

    @Override
    public void onNeedSync() {

    }

    @Override
    public void onAddActions(DetailedIssue details, ArrayList<ReturnDateWithActionDialog> data, long idAction, long prevActionID, long nextActionID, boolean keep) {

    }

    @Override
    public void onLoadDetaileIssue(long issueId) {
        Intent intent = getDB();
        intent.putExtra(ServiceDataBase.KEY_REQUEST, new DBRequest(DBRequest.DBRequestType.DETAILS_ISSUE));
        intent.putExtra("issueID", issueId);
        intent.putExtra("user", user);
        sendDBRequest(intent);

    }

    @Override
    public void updateIssue(long issueId, boolean open, boolean favorite) {

    }

    @Override
    public Intent onGetDB() {
        return getDB();
    }

    @Override
    public void onSendDBRequest(Intent i) {
        sendDBRequest(i);
    }

    @Override
    public void onShowHistory(long id) {

    }

    @Override
    public void onActionMoreClock(DetailedIssue details) {

    }



    @Override
    public void onShowDetails(long id) {

    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        this.mDetector.onTouchEvent(event);
//        // Be sure to call the superclass implementation
//        return super.onTouchEvent(event);
//    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent me){
        // Call onTouchEvent of SimpleGestureFilter class
        this.mDetector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

/*    @Override
    public void onSwipe(int direction) {
        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT :
                onBackPressed();
                break;
*//*
            case SimpleGestureFilter.SWIPE_LEFT :  str = "Swipe Left";
                break;
            case SimpleGestureFilter.SWIPE_DOWN :  str = "Swipe Down";
                break;
            case SimpleGestureFilter.SWIPE_UP :    str = "Swipe Up";
                break;
*//*
        }
    }*/



    private class SimpleGestureFilter extends GestureDetector.SimpleOnGestureListener {

        public final static int SWIPE_UP    = 1;
        public final static int SWIPE_DOWN  = 2;
        public final static int SWIPE_LEFT  = 3;
        public final static int SWIPE_RIGHT = 4;

        public final static int MODE_TRANSPARENT = 0;
        public final static int MODE_SOLID       = 1;
        public final static int MODE_DYNAMIC     = 2;

        private final static int ACTION_FAKE = -13; //just an unlikely number
        private int swipe_Min_Distance = 100;
        private int swipe_Max_Distance = 350;
        private int swipe_Min_Velocity = 100;

        private int mode             = MODE_DYNAMIC;
        private boolean running      = true;
        private boolean tapIndicator = false;

        private Activity context;
        private GestureDetector detector;
        private SimpleGestureListener listener;

        public SimpleGestureFilter(Activity context,SimpleGestureListener sgl) {

            this.context = context;
            this.detector = new GestureDetector(context, this);
            this.listener = sgl;
        }

        public void onTouchEvent(MotionEvent event){

            if(!this.running)
                return;

            boolean result = this.detector.onTouchEvent(event);

            if(this.mode == MODE_SOLID)
                event.setAction(MotionEvent.ACTION_CANCEL);
            else if (this.mode == MODE_DYNAMIC) {

                if(event.getAction() == ACTION_FAKE)
                    event.setAction(MotionEvent.ACTION_UP);
                else if (result)
                    event.setAction(MotionEvent.ACTION_CANCEL);
                else if(this.tapIndicator){
                    event.setAction(MotionEvent.ACTION_DOWN);
                    this.tapIndicator = false;
                }

            }
            //else just do nothing, it's Transparent
        }

        public void setMode(int m){
            this.mode = m;
        }

        public int getMode(){
            return this.mode;
        }

        public void setEnabled(boolean status){
            this.running = status;
        }

        public void setSwipeMaxDistance(int distance){
            this.swipe_Max_Distance = distance;
        }

        public void setSwipeMinDistance(int distance){
            this.swipe_Min_Distance = distance;
        }

        public void setSwipeMinVelocity(int distance){
            this.swipe_Min_Velocity = distance;
        }

        public int getSwipeMaxDistance(){
            return this.swipe_Max_Distance;
        }

        public int getSwipeMinDistance(){
            return this.swipe_Min_Distance;
        }

        public int getSwipeMinVelocity(){
            return this.swipe_Min_Velocity;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            final float xDistance = Math.abs(e1.getX() - e2.getX());
            final float yDistance = Math.abs(e1.getY() - e2.getY());

            if(xDistance > this.swipe_Max_Distance || yDistance > this.swipe_Max_Distance)
                return false;

            velocityX = Math.abs(velocityX);
            velocityY = Math.abs(velocityY);
            boolean result = false;

            if(velocityX > this.swipe_Min_Velocity && xDistance > this.swipe_Min_Distance){
                if(e1.getX() > e2.getX()) // right to left
                    this.listener.onSwipe(SWIPE_LEFT);
                else
                    this.listener.onSwipe(SWIPE_RIGHT);

                result = true;
            }
           /* else if(velocityY > this.swipe_Min_Velocity && yDistance > this.swipe_Min_Distance){
                if(e1.getY() > e2.getY()) // bottom to up
                    this.listener.onSwipe(SWIPE_UP);
                else
                    this.listener.onSwipe(SWIPE_DOWN);

                result = true;
            }*/

            return result;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            this.tapIndicator = true;
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent arg) {
            this.listener.onDoubleTap();;
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent arg) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent arg) {

            if(this.mode == MODE_DYNAMIC){        // we owe an ACTION_UP, so we fake an
                arg.setAction(ACTION_FAKE);      //action which will be converted to an ACTION_UP later.
                this.context.dispatchTouchEvent(arg);
            }

            return false;
        }



    }
    private interface SimpleGestureListener{
        void onSwipe(int direction);
        void onDoubleTap();
    }
}
