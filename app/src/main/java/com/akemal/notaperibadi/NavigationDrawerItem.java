package com.akemal.notaperibadi;

/**
 * Created by dev on 29/04/15.
 * Represent menu item in the drawer
 */
public class NavigationDrawerItem {
    private int iconId;
    private String title;

    public NavigationDrawerItem(int iconId, String title) {
        this.iconId = iconId;
        this.title = title;
    }

    public int getIconId() {
        return iconId;
    }

    public String getTitle() {
        return title;
    }
}