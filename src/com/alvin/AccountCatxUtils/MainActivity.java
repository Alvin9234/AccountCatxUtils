package com.alvin.AccountCatxUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.alvin.adapter.ListViewAdapter;
import com.alvin.database.TbCount;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    //-----------  控件 -----------
    @ViewInject(R.id.listView_all)
    private ListView listView_all;
    @ViewInject(R.id.textView_date)
    private TextView textView_date;
    @ViewInject(R.id.textView_emptyInfo)
    private TextView textView_emptyInfo;
    @ViewInject(R.id.editText_money)
    private EditText editText_money;
    @ViewInject(R.id.editText_remark)
    private EditText editText_remark;
    @ViewInject(R.id.radioButton_income)
    private RadioButton radioButton_income;
    @ViewInject(R.id.radioButton_expenditure)
    private RadioButton radioButton_expenditure;
    @ViewInject(R.id.btn_articles)
    private Button btn_articles;
    //-----------  控件 -----------

    private DbUtils dbUtils;
    private ListViewAdapter adapter;
    private List<TbCount> totalList;
    private Calendar calendar;
    private int year; // 获取选择日期时的 年
    private int monthOfYear;// 月
    private int dayOfMonth;// 日
    private int week;// 星期几
    private String dateString;// 拼接日期的字符串
    private String sqlInsert = "";
    private String type = "";
    private boolean isOK = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ViewUtils.inject(this);
        init();
    }
    /**
     * 通用的初始化方法
     */
    private void init() {
        // 第一个参数 上下文
        // 第二个参数 数据库名
        // 第三个参数 数据库版本号
        // 第四个参数 数据库升级接口 ，等同于 SQLiteOpenHelper 中的 onUpgrade
        dbUtils = DbUtils.create(this,
                "myCount.db",
                1,
                new DbUtils.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {

                    }
                });

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        week = calendar.get(Calendar.WEEK_OF_MONTH);
        dateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        textView_date.setText(dateString);

        try {
            totalList = dbUtils.findAll(TbCount.class);
            if (totalList != null) {
                adapter = new ListViewAdapter(totalList, this);
                listView_all.setAdapter(adapter);
            } else {
                listView_all.setEmptyView(textView_emptyInfo);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
    /**
     *   刷新 列表
     * @param list
     */
    public void reloadListView(List<TbCount> list){
        totalList.clear();
        totalList.addAll(list);
        adapter.notifyDataSetChanged();
    }
    @OnClick({R.id.textView_date,R.id.btn_articles,R.id.btn_save})
    public void clickButton(View view){
        switch (view.getId()) {
            case R.id.textView_date:
                DatePickerDialog dateDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m,
                                          int d) {
                        dateString = y + "-" + (m + 1) + "-" + d;
                        year = y;
                        monthOfYear = m;
                        dayOfMonth = d;
                        textView_date.setText(dateString);
                    }
                }, year, monthOfYear, dayOfMonth);
                dateDialog.show();
                break;
            case R.id.btn_articles:
                final String[] articles = getResources().getStringArray(R.array.articles);
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("选择类别");
                // 第二个参数 默认从0开始即第一个被选中 ，，-1 代表一个都不选
                builder.setSingleChoiceItems(articles, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                btn_articles.setText(articles[0]);
                                type = articles[0];
                                break;
                            case 1:
                                btn_articles.setText(articles[1]);
                                type = articles[1];
                                break;
                            case 2:
                                btn_articles.setText(articles[2]);
                                type = articles[2];
                                break;
                            case 3:
                                btn_articles.setText(articles[3]);
                                type = articles[3];
                                break;
                            case 4:
                                btn_articles.setText(articles[4]);
                                type = articles[4];
                                break;
                            case 5:
                                btn_articles.setText(articles[5]);
                                type = articles[5];
                                break;
                            case 6:
                                btn_articles.setText(articles[6]);
                                type = articles[6];
                                break;
                            case 7:
                                btn_articles.setText(articles[7]);
                                type = articles[7];
                                break;
                        }
                    }
                });
                builder.setPositiveButton("确认", null);
                builder.show();
                break;
            case R.id.btn_save:
                if (insertData(type)) {
                    List<TbCount> list = null;
                    try {
                        list = dbUtils.findAll(TbCount.class);
                        reloadListView(list);
                        clearEditText();
                        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                    } catch (DbException e) {
                        Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
                    clearEditText();
                }
                break;
        }
    }
    /**
     *   清空 输入框
     */
    public void clearEditText(){
        editText_money.setText("");
        editText_remark.setText("");
    }
    /**
     * 执行保存数据
     * @return
     */
    public boolean insertData(String type) {
        // 数据库操作
        String money = editText_money.getText().toString();
        String remark = editText_remark.getText().toString();
        String article = "";
        if(radioButton_income.isChecked()){
            article = "收入";
        }else if(radioButton_expenditure.isChecked()){
            article = "支出";
        }else{
            Toast.makeText(this, "请选则是收入还是支出", Toast.LENGTH_SHORT).show();
            return isOK;
        }
        TbCount tbCount = new TbCount();
        tbCount.setType(type);
        tbCount.setMoney(Float.parseFloat(money));
        tbCount.setRemark(remark);
        tbCount.setYear(year);
        tbCount.setMonth(monthOfYear + 1);
        tbCount.setDay(dayOfMonth);
        tbCount.setWeek(week);
        tbCount.setArticle(article);
        try {
            dbUtils.save(tbCount);
            isOK = true;
        } catch (DbException e) {
            e.printStackTrace();
            isOK = false;
        }
        return isOK;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_detail:
                Intent intent = new Intent();
                intent.setClass(this, DetailFragmentActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
