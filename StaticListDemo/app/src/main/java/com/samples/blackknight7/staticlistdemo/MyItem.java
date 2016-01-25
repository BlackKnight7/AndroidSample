package com.samples.blackknight7.staticlistdemo;

import java.util.Date;

/**
 * Created by Legolas on 2016/1/24.
 */
public class MyItem {
    private String title;
    private String author;
    private String content;
    private Date createDate;

    public MyItem(String title, String author, String content, Date createDate) {
        this.title = title;
        this.author = author;
        this.content = content;
        this.createDate = createDate;
    }

    public String getTitle() {
        return this.title;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getContent() {
        return this.content;
    }

    public Date getCreateDate() {
        return this.createDate;
    }
}
