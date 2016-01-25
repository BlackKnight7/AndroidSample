package com.samples.blackknight7.staticlistdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Legolas on 2016/1/24.
 */
public class MyAdapter extends BaseAdapter {

    private List<MyItem> lists;
    private Context context;
    private LayoutInflater layoutInflater;

    public MyAdapter(Context context, List<MyItem> lists) {
        this.context = context;
        this.lists = lists;
        layoutInflater = LayoutInflater.from(this.context);
    }


    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.listview_custom, null);
        }
        //注意findViewById的时候，要使用convertView的这个方法，因为是在它里面进行控件的寻找
        TextView viewTitle = (TextView) convertView.findViewById(R.id.title);
        TextView viewContent = (TextView) convertView.findViewById(R.id.content);
        TextView viewAuthor= (TextView) convertView.findViewById(R.id.author);
        TextView viewCreateDate = (TextView) convertView.findViewById(R.id.create_date);
//        //将数据与控件进行绑定
//        img.setBackgroundResource(lists.get(position).getPicture());
        viewTitle.setText(lists.get(position).getTitle());
        viewContent.setText(lists.get(position).getContent());
        viewAuthor.setText(lists.get(position).getAuthor());
        viewCreateDate.setText(lists.get(position).getCreateDate().toString());

        return convertView;
    }


}
