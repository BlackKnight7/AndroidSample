package com.samples.blackknight7.tabbardemo;

import android.app.TabActivity;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1")
                .setIndicator("test1")
                .setContent(R.id.isanimal);
        tabHost.addTab(tab1);

        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2")
                .setIndicator("test2")
                .setContent(R.id.alwayswet);
        tabHost.addTab(tab2);

        TabHost.TabSpec tab3 = tabHost.newTabSpec("tab3")
                .setIndicator("test3")
                .setContent(R.id.nezha);
        tabHost.addTab(tab3);
    }
}
