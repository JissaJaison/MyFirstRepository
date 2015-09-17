package com.onbts.ITSMobile.UI.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.services.SyncService;
import com.onbts.ITSMobile.util.Settings;

@Deprecated
public abstract class BaseActivity extends Activity {
	protected  SyncService.SyncTask syncTask = SyncService.SyncTask.MANUAL_SYNC_TASK;
	protected  static View rootView = null;
	
	protected static MenuItem curSyncButton = null;
	protected static MenuItem curSyncDatails = null;
	protected static MenuItem prevSyncDatails = null;
	protected static MenuItem prevSyncButton = null;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        rootView = getWindow().getDecorView().getRootView();
        // Override the default landscape orientation to
        // portrait if the screen size is below 9 inches
        Settings.getInstance(this).setProperOrientation(this);
    }
    @Override
    protected void onStop() {
        super.onStop();  // Always call the superclass method first

        
    }
    
    @Override
    protected void onStart() {
        super.onStart();  // Always call the superclass method first
 
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first

    }
    public static Handler syncServiceHandler = new Handler() {
    	  @Override
    	  public void handleMessage(Message msg) {
    		  TextView theView = null;
    		  if (curSyncDatails != null)
				 {
					  theView = (TextView)curSyncDatails.getActionView().findViewById(R.id.tvw_sync_details);
				 }
    		  switch (msg.what)
    		  {
    			  case SyncService.MSG_BACKGROUND_SYNC_WAITING:

    				     if (theView != null)
    				     {
    				    	 theView.setText(msg.obj.toString());
    				     }
    				 
    				  break;
    			 case SyncService.MSG_SYNC_IN_PROGRESS:
    			 case SyncService.MSG_HARD_SYNC_IN_PROGRESS:
    				 if (curSyncButton != null && curSyncButton.getActionView() == null)
    				 {
    					 //orignialView = syncIndicator.;
    					 
    					 curSyncButton.setActionView(R.layout.actionbar_sync);
    					 curSyncButton.expandActionView();

    				 }

				     if (theView != null)
				     {
				    	 if (msg.obj.getClass().equals(String.class))
				    	{	 
				    		 theView.setText(msg.obj.toString());
				    	}
				     }
   				  break;
    			 case SyncService.MSG_SYNC_COMPLETED:
    				 curSyncButton.collapseActionView();
    				 curSyncButton.setActionView(null);
    				 
    				

    				 if (theView != null)
				     {
				    	 if (msg.obj.getClass().equals(String.class))
				    	{	 
				    		 theView.setText(msg.obj.toString());
				    	}
				     }
    				 break;
    			 case SyncService.MSG_HARD_SYNC_COMPLETED:
    				 curSyncButton.collapseActionView();
    				 curSyncButton.setActionView(null);
    				 
    				

    				 if (theView != null)
				     {
				    	 if (msg.obj.getClass().equals(String.class))
				    	{	 
				    		 theView.setText(msg.obj.toString());
				    	}
				     }
    		//		 UiFunctions.setViewGroupEnebled((ViewGroup)rootView, true);
   				  break;
    			 case SyncService.MSG_SYNC_CANCELED:
    				 curSyncButton.collapseActionView();
    				 curSyncButton.setActionView(null);
    		//		 UiFunctions.setViewGroupEnebled((ViewGroup)rootView, true);
   				  break;
    			
    			case SyncService.MSG_SYNC_FAILED:
   				 curSyncButton.collapseActionView();
   				 curSyncButton.setActionView(null);
   				if (theView != null)
			     {
			    	 if (msg.obj.getClass().equals(String.class))
			    	{	 
			    		 theView.setText(msg.obj.toString());
			    	}
			     }
   			//	UiFunctions.setViewGroupEnebled((ViewGroup)rootView, true);
  				  break;
  		    default:
  		    	break;
    				
    		  }
    	  }
    		  
    	 };

    	    @Override
    	    public boolean onCreateOptionsMenu(Menu menu) {
    	    	super.onCreateOptionsMenu(menu);
    	        // Inflate the menu; this adds items to the action bar if it is present.
    	        getMenuInflater().inflate(R.menu.base, menu);
    	        
    	        
    	        BaseActivity.curSyncButton = menu.findItem(R.id.menu_sync);
    	        if (syncTask == SyncService.SyncTask.HARD_SYNC_TASK)
            	{
    	        	Drawable icon = getResources().getDrawable(R.drawable.hard_sync);
    	        	BaseActivity.curSyncButton.setIcon(icon);
    	            
    	        	
            	}
    	        else
    	        {
    	        	Drawable icon = getResources().getDrawable(R.drawable.soft_sync);
    	        	BaseActivity.curSyncButton.setIcon(icon);
    	        }
    	        if (prevSyncButton != null)
    	        {
    	        	if (prevSyncButton.getActionView() != null)
    	        	{
    	        		curSyncButton.setActionView(R.layout.actionbar_sync);
    	        		curSyncButton.expandActionView();
    	        	}
    	        	
    	        }
    	        prevSyncButton = curSyncButton;
    	        
    	        //prevSyncDatails = curSyncDatails;
    	        curSyncDatails =  menu.findItem(R.id.menu_sync_details);
    	        curSyncDatails.setActionView(R.layout.actionbar_sync_details);
    	        curSyncDatails.expandActionView();
    	        curSyncDatails.setEnabled(false);
    	        TextView curView = (TextView)curSyncDatails.getActionView().findViewById(R.id.tvw_sync_details);
    	        if (prevSyncDatails != null)
    	        {
    	        	TextView prevView = (TextView)prevSyncDatails.getActionView().findViewById(R.id.tvw_sync_details);
    	        
    	        	curView.setText(prevView.getText());
    	        }
    	        prevSyncDatails = curSyncDatails;
    	        return true;
    	    }
    	    public boolean onMenuOpened (int featureId, Menu menu)
    	    {
    	    	disableSyncDetails();
    	    	return true;
    	    }
    	    
    	   
			private void disableSyncDetails() {
				TextView curView = (TextView)curSyncDatails.getActionView().findViewById(R.id.tvw_sync_details);
            	if (curView != null)
            	{
            		curSyncDatails.setEnabled(false);

            		
            	}
			}
    	    @Override
    	    public boolean onOptionsItemSelected(MenuItem item) {
    	        // Handle item selection
    	        switch (item.getItemId()) {
    	            case R.id.menu_sync:
    	            	if (syncTask == SyncService.SyncTask.HARD_SYNC_TASK)
    	            	{
    	            		AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
    	                    builder.setMessage("Warning!   This operation will remove the database and reload all current data from the server.")
    	                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	                        public void onClick(DialogInterface dialog, int id) {
    	                            dialog.dismiss();
    	                            
    	                //            UiFunctions.setViewGroupEnebled((ViewGroup)rootView, false);
    	    	            		SyncService.getInstance().StartHardSync();
									//Changes made by Jissa on Issue 10 of mobile changes.
//                    toggleButton1.setEnabled(true);
//                    toggleButton1.setChecked(true);
    	                        }

    	                    })
    	                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	                        public void onClick(DialogInterface dialog, int id) {
    	                            dialog.dismiss();
    	                        }
    	                    }).show();
    	                    
    	            		
    	            	}
    	            	else
    	            	{
    	            		SyncService.getInstance().StartManualSync();
    	            	}
    	                return true;
    	            case R.id.menu_settings:
    	            	disableSyncDetails();
    	            	return true;
    	            default:
    	                return super.onOptionsItemSelected(item);
    	        }
    	    }
}
