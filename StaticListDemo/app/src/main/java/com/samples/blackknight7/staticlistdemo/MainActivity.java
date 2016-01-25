package com.samples.blackknight7.staticlistdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);


        ArrayList<MyItem> items = new ArrayList<>();
        items.add(new MyItem("Test Title1", "Fish1", "This is one test", new Date()));
        items.add(new MyItem("Test Title2", "Fish2", "This is one test", new Date()));
        MyAdapter adapter = new MyAdapter(this, items);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        intent = new Intent(this, SampleActivity.class);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                startActivity(intent);
            }
        });
    }
}
