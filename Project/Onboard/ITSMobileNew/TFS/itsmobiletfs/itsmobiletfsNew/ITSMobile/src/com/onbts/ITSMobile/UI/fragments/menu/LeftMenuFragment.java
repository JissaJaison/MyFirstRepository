package com.onbts.ITSMobile.UI.fragments.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.onbts.ITSMobile.R;
import com.onbts.ITSMobile.UI.activities.LoginActivityIts;
import com.onbts.ITSMobile.adapters.NavDrawerLeftAdapter;
import com.onbts.ITSMobile.interfaces.OnLeftMenuCallBack;
import com.onbts.ITSMobile.interfaces.OnNavigationChange;
import com.onbts.ITSMobile.model.NavDrawerItemLeft;
import com.onbts.ITSMobile.model.UserModel;

import java.util.ArrayList;

import util.Util;

/**
 * Created by tigre on 16.04.14.
 */
public class LeftMenuFragment extends BaseMenuFragment implements AdapterView.OnItemClickListener {

    protected OnLeftMenuCallBack mNavigator;
    private UserModel user;
    private ArrayList<NavDrawerItemLeft> leftNavDrawerItems = new ArrayList<>();
    private String[] navMenuLeftTitles;
    private NavDrawerLeftAdapter mLeftAdapter;
    private boolean newState;
    private ListView list;

    private LinearLayout logout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnNavigationChange) {
            mNavigator = (OnLeftMenuCallBack) activity;
        } else {
            throw new ClassCastException("Activity must implements fragment's OnLeftMenuCallBack");
        }
        Log.d("life", "onattach created issuesfragment");
    }

    @Override
    public void onDetach() {
        mNavigator = null;
        super.onDetach();
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.menu_left_fragment, container, false);
        mLeftAdapter = new NavDrawerLeftAdapter(getActivity(), leftNavDrawerItems);
        fillLeftMenu();
        logout = (LinearLayout) v.findViewById(R.id.logout_view);
        list = (ListView) v.findViewById(R.id.list);
        View userInfoHeader = inflater.inflate(R.layout.header_account_info, null);
        ((TextView) userInfoHeader.findViewById(R.id.tvUserPosition)).setText(user.getName());
        ((TextView) userInfoHeader.findViewById(R.id.tvUserDepartment)).setText(user.getDepartmentName());
        list.addHeaderView(userInfoHeader, null, false);
        list.setOnItemClickListener(this);
        list.setAdapter(mLeftAdapter);
        if (savedInstanceState == null) {
            list.setItemChecked(1, true);
            mNavigator.onCategorySelected(leftNavDrawerItems.get(0));
        } else {

        }
        return v;
    }

    public NavDrawerItemLeft getLeftNavDraverPositions(int position) {
        return leftNavDrawerItems.get(position);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void fillLeftMenu() {
        navMenuLeftTitles = getResources().getStringArray(R.array.nav_drawer_left_titles);
        user = mNavigator.onGetUser();
        leftNavDrawerItems.clear();
        if (user != null) {
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[0],
                    user.getCountAssigned(), user.getCountNewAssigned(),
                    NavDrawerItemLeft.NavDrawerItemLeftType.USER_ASSIGNED, user.getAlertAssigned() > 0, user.getPreAlertAssigned() > 0));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[1],
                    user.getCountDepAssigned(), user.getCountNewDepAssigned(),
                    NavDrawerItemLeft.NavDrawerItemLeftType.DEPARTMENT_ASSIGNED, user.getAlertDepAssigned() > 0, user.getPreAlertDepAssigned() > 0));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[2],
                    user.getCountCreate(), user.getCountNewCreate(),
                    NavDrawerItemLeft.NavDrawerItemLeftType.USER_CREATED, user.getAlertCreate() > 0, user.getPreAlertCreate() > 0));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[3],
                    user.getCountDepCreate(), user.getCountNewDepCreate(),
                    NavDrawerItemLeft.NavDrawerItemLeftType.DEPARTMENT_CREATED, user.getAlertDepCreated() > 0, user.getPreAlertDepCreated() > 0));
            leftNavDrawerItems.add(new NavDrawerItemLeft(navMenuLeftTitles[4],
                    user.getCountFavorite(), user.getCountNewFavorite(),
                    NavDrawerItemLeft.NavDrawerItemLeftType.USER_FAVORITE, user.getAlertFavorite() > 0, user.getPreAlertFavorite() > 0));
            if (mLeftAdapter != null) {
                mLeftAdapter.notifyDataSetChanged();
            }
            if (list != null) {
                int i = list.getCheckedItemPosition();
                mNavigator.onCategorySelected(leftNavDrawerItems.get(i - 1));
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLogoutAlert();
            }
        });
    }

    private void showLogoutAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doLogout();
            }
        });
        builder.setTitle("Logout").setMessage("Are you sure want to logout? ").setCancelable(false);
        AlertDialog logoutDialog = builder.create();
        logoutDialog.show();

    }

    private void doLogout() {
        Util.changeLoginStatus(false, getActivity());
        Intent loginIntent = new Intent(getActivity(), LoginActivityIts.class);
        startActivity(loginIntent);
        getActivity().finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object o = view.getTag();
        if (o != null && o instanceof NavDrawerItemLeft) {
            mNavigator.onCategorySelected((NavDrawerItemLeft) o);
        }
    }

    public void getCurrnet() {

    }

    public void onUserUpdate(UserModel user) {
        fillLeftMenu();
    }
}
