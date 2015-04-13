package com.alvin.database;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by Alvin on 2015/4/12.
 */
@Table(name = "tb_count")
public class TbCount {
    @Id(column = "_id")
    private int id;
    @Column(column = "type")
    private String type;
    @Column(column = "remark")
    private String remark;
    @Column(column = "money")
    private float money;
    @Column(column = "year")
    private int year;
    @Column(column = "month")
    private int month;
    @Column(column = "day")
    private int day;
    @Column(column = "week")
    private int week;
    @Column(column = "article")
    private String article;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }
}
