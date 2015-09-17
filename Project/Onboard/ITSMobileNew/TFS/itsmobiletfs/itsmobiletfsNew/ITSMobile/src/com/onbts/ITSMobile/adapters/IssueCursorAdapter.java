package com.onbts.ITSMobile.adapters;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.interfaces.OnRefreshDrawer;
import com.onbts.ITSMobile.services.DbService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import util.GetColorByPriority;
@Deprecated
public class IssueCursorAdapter extends CursorAdapter {
    public static long DAY_THRESHOLD = 86400000; //24*1000*60 ms
    private static String LEVEL_4 = "4";
    private static String LEVEL_3 = "3";
    private static String LEVEL_2 = "2";
    private static String LEVEL_1 = "1";
    private LayoutInflater inflater;
    private Context mContext;
    private String LEVEL_5 = "5";
    private OnRefreshDrawer listener;

    public IssueCursorAdapter(Context context, Cursor c, OnRefreshDrawer refreshListener) {
        super(context, c);
        inflater = LayoutInflater.from(context);
        mContext = context;
        listener = refreshListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        View view = inflater.inflate(R.layout.item_issue, parent, false);
        holder.tvIssueStatus = (TextView) view.findViewById(R.id.tvIssueStatus);
        holder.tvIssueType = (TextView) view.findViewById(R.id.tvIssueType);
        holder.tvIssueDesc = (TextView) view.findViewById(R.id.tvIssueDesc);
        holder.tvIssueDate = (TextView) view.findViewById(R.id.tvIssueDate);
        holder.tvIssueId = (TextView) view.findViewById(R.id.tvIssueId);
        holder.tvLocationDesc = (TextView) view.findViewById(R.id.tvIssueLocation);
        holder.v = (View) view.findViewById(R.id.vPrior);
        holder.imgStar = (ImageView) view.findViewById(R.id.imgFavorite);
        holder.llIssueBackground = (LinearLayout) view.findViewById(R.id.llIssueLayout);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        int priorityLevel = Integer.parseInt(cursor.getString(cursor.getColumnIndex("PriorityLevel")));
        holder.v.setBackgroundColor(GetColorByPriority.getColor(priorityLevel, mContext));
        holder.tvIssueStatus.setText(cursor.getString(cursor.getColumnIndex("StatusDesc")));
        holder.tvIssueType.setText(cursor.getString(cursor.getColumnIndex("IssueTypeDesc")));
        holder.tvIssueDate.setText(timestampToDate(cursor.getString(cursor.getColumnIndex("CreateDate"))));
        if (holder.tvIssueDesc != null) {
            holder.tvIssueDesc.setText(cursor.getString(cursor.getColumnIndex("Notes")));
        }
        final String id = cursor.getString(cursor.getColumnIndex("_id"));
        holder.tvIssueId.setText(id);
        holder.tvLocationDesc.setText(cursor.getString(cursor.getColumnIndex("LocationDesc")));
        boolean fav = DbService.getInstance(mContext).isFavorite(id);
        if (!fav) {
            holder.imgStar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star));
        }
        else {
            holder.imgStar.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star_pressed));
        }
        holder.imgStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!DbService.getInstance(mContext).isFavorite(id)) {
                    DbService.getInstance(mContext).setFavorite(id, true);
                    notifyDataSetChanged();
                }
                else {
                    DbService.getInstance(mContext).setFavorite(id, false);
                    notifyDataSetChanged();
                }
                Log.d("click", "star clicked");
//                listener.onLeftMenuRefresh(true);
            }
        });
        boolean isViewed = DbService.getInstance(mContext).isViewed(id);
        Log.d("view", "id  " + id + isViewed);
        if(isViewed) {
            holder.llIssueBackground.setBackgroundColor(mContext.getResources().getColor(R.color.viewed));
        }
        else {
            holder.llIssueBackground.setBackgroundColor(mContext.getResources().getColor(android.R.color.white));
        }
    }

            public String timestampToDate(String timestamp) {
                long time = Long.parseLong(timestamp);
                Calendar cal = Calendar.getInstance();

                long nowStamp = System.currentTimeMillis();
                long diff = nowStamp - time;
                if (diff < DAY_THRESHOLD) {
                    long hours = TimeUnit.MILLISECONDS.toHours(diff);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                    long seconds = TimeUnit.MILLISECONDS.toMinutes(diff);

                    if (hours > 0) {
                        return String.valueOf(hours) + " hour(s) ago.";
                    }
                    if (minutes > 0) {
                        return String.valueOf(minutes) + " minute(s) ago.";
                    }
                    if (seconds > 0) {
                        return String.valueOf(seconds) + " second(s) ago.";
                    }
                }
                // Log.d("time", String.valueOf(nowStamp) + "  " + timestamp + " diff: " + diff);
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.US); // the format of your date
                cal.setTimeInMillis(time); // *1000 is to convert seconds to milliseconds
                Date date = cal.getTime();
                String formattedDate = sdf.format(date);
                return formattedDate;
            }

            private static class ViewHolder {
                TextView tvIssueStatus;
                TextView tvIssueType;
                TextView tvIssueDesc;
                TextView tvIssueDate;
                TextView tvIssueId;
                TextView tvLocationDesc;
                ImageView imgStar;
                LinearLayout llIssueBackground;
                View v;
            }
        }
