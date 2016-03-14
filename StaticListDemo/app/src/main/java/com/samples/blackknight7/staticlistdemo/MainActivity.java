package com.samples.blackknight7.staticlistdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements OnRefreshListener{
    MyListView listView;
    Intent intent;
    MyAdapter adapter;
    ArrayList<MyItem> items;
    Boolean isRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        // Get ListView object from xml
        listView = (MyListView) findViewById(R.id.list);


        items = new ArrayList<>();
        items.add(new MyItem("Test Title1", "Fish1", "This is one test", new Date()));
        items.add(new MyItem("Test Title2", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title3", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title4", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title5", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title6", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title7", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title8", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title9", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title10", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title11", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title12", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title13", "Fish2", "This is one test", new Date()));
        items.add(new MyItem("Test Title14", "Fish2", "This is one test", new Date()));
        adapter = new MyAdapter(this, items);

        // Assign adapter to ListView
        listView.setAdapter(adapter);
        listView.setOnRefreshListener(this);

        intent = new Intent(this, SampleActivity.class);

        // ListView Item Click Listener
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//
//                if(!isRefresh){
//                    startActivity(intent);
//                }
//            }
//        });

    }

    @Override
    public void onDownPullRefresh() {
        isRefresh = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(4000);
                for (int i = 0; i < 2; i++) {
                    items.add(new MyItem("Test Title0", "Fish0", "This is one test", new Date()));
                }
                isRefresh = false;
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                adapter.notifyDataSetChanged();
                listView.hideHeaderView();
            }
        }.execute(new Void[] {});
    }

    @Override
    public void onLoadingMore() {
        isRefresh = true;

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                SystemClock.sleep(1000);

                items.add(new MyItem("Test Title4", "Fish4", "This is one test", new Date()));
                items.add(new MyItem("Test Title5", "Fish5", "This is one test", new Date()));
                items.add(new MyItem("Test Title6", "Fish6", "This is one test", new Date()));

                isRefresh = false;
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                adapter.notifyDataSetChanged();
                listView.hideFooterView();
            }
        }.execute(new Void[] {});
    }
}
