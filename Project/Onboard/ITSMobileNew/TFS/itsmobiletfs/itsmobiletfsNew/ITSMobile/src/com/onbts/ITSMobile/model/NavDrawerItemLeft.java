package com.onbts.ITSMobile.model;

import com.onbts.ITSMobile.R;

public class NavDrawerItemLeft {
    private final String title;
    private final NavDrawerItemLeftType type;
    private int count = 0;
    private int newCount = 0;
    private boolean alert, preAlert;

    public boolean isAlert() {
        return alert;
    }

    public boolean isPreAlert() {
        return preAlert;
    }

    public NavDrawerItemLeft(String title, NavDrawerItemLeftType type) {
        this.title = title;
        this.type = type;
    }

    public NavDrawerItemLeft(String title, int count, int newCount, NavDrawerItemLeftType type, boolean alert, boolean preAlert) {
        this.title = title;

        this.count = count;
        this.newCount = newCount;
        this.type = type;
        this.alert = alert;
        this.preAlert = preAlert;
        type.count = count;
    }

    public NavDrawerItemLeftType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        type.count = count;
    }

    public int getNewCount() {
        return newCount;
    }

    public void setNewCount(int newCount) {
        this.newCount = newCount;
    }

    public boolean isCounterVisible() {
        return newCount > 0;
    }

    public enum NavDrawerItemLeftType {

        USER_ASSIGNED {
            @Override
            public int getTitle() {
                return R.string.nav_drawer_left_user_ass;
            }

            @Override
            public int getCount() {
                return count;
            }
        },
        USER_CREATED {
            @Override
            public int getTitle() {
                return R.string.nav_drawer_left_user_create;
            }

            @Override
            public int getCount() {
                return count;
            }
        },
        USER_FAVORITE {
            @Override
            public int getTitle() {
                return R.string.nav_drawer_left_user_fav;
            }

            @Override
            public int getCount() {
                return count;
            }
        },
        DEPARTMENT_CREATED {
            @Override
            public int getTitle() {
                return R.string.nav_drawer_left_dep_create;
            }

            @Override
            public int getCount() {
                return count;
            }
        },
        DEPARTMENT_ASSIGNED {
            @Override
            public int getTitle() {
                return R.string.nav_drawer_left_dep_ass;
            }

            @Override
            public int getCount() {
                return count;
            }
        };

        public abstract int getTitle();
        public abstract int getCount();
        int count;

    }


}
