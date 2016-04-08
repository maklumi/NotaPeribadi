package com.akemal.notaperibadi;


import android.os.Bundle;

/**
 * Created by dev on 12/08/15.
 */
public class HelpFeedActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_feedback_layout);
        mToolBar = activateToolbar();
        setUpNavigationDrawer();
    }
}