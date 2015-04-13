package com.alvin.fragment.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.alvin.AccountCatxUtils.R;
import com.alvin.adapter.ListViewAdapter;
import com.alvin.database.TbCount;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Alvin on 2015/4/10.
 */
public class DummyFragment extends Fragment {
    private DbUtils dbUtils;
    private ListViewAdapter adapter;
    private List<TbCount> totalList;// 数据库查询结果
    private int tabIndex;//  保存导航标签的下标，默认从0开始
    private Calendar calendar;
    private int year; // 获取选择日期时的 年
    private int monthOfYear;// 月
    private int dayOfMonth;// 日
    private int week;// 在当月是第几周
    private String dateString="";// 拼接日期的字符串
    private String[] arrTabTitles = null;

    @ViewInject(R.id.listView_inOrOutLog)
    private ListView listView_inOrOutLog;
    @ViewInject(R.id.charView)
    private DetailArcChartView charView;
    @ViewInject(R.id.emptyInfo)
    private TextView emptyInfo;
    @ViewInject(R.id.txt_date)
    private TextView txt_date;//日期
    // -------   比例，文本控件
    @ViewInject(R.id.ratio_salary)
    private TextView ratio_salary;//工资
    @ViewInject(R.id.ratio_windfall)
    private TextView ratio_windfall;//外快
    @ViewInject(R.id.ratio_entertainment)
    private TextView ratio_entertainment;//娱乐
    @ViewInject(R.id.ratio_repast)
    private TextView ratio_repast;//餐饮
    @ViewInject(R.id.ratio_rent)
    private TextView ratio_rent;//房租
    @ViewInject(R.id.ratio_traffic)
    private TextView ratio_traffic;//交通
    @ViewInject(R.id.ratio_shopping)
    private TextView ratio_shopping;//购物
    @ViewInject(R.id.ratio_others)
    private TextView ratio_others;//其他
    // --------按条件查看 按钮控件
    @ViewInject(R.id.btn_year)
    private Button btn_year;
    @ViewInject(R.id.btn_month)
    private Button btn_month;
    @ViewInject(R.id.btn_week)
    private Button btn_week;
    @ViewInject(R.id.btn_day)
    private Button btn_day;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        week = calendar.get(Calendar.WEEK_OF_MONTH);
        dateString = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;

        Bundle bundle = getArguments();
        tabIndex = bundle.getInt("tabIndex");

        dbUtils = DbUtils.create(getActivity(),"myCount.db");

        totalList = new ArrayList<TbCount>();
        arrTabTitles = new String[]{"收入","支出"};
        switch (tabIndex){
            case 0:
                try {
                    totalList = dbUtils.findAll(Selector.from(TbCount.class).where("article","=",arrTabTitles[0]));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    totalList = dbUtils.findAll(Selector.from(TbCount.class).where("article","=",arrTabTitles[1]));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                break;
        }
        adapter = new ListViewAdapter(totalList,getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);
        ViewUtils.inject(this, view);
        //findView(view);
        listView_inOrOutLog.setAdapter(adapter);
        listView_inOrOutLog.setEmptyView(emptyInfo);
        txt_date.setText(dateString);
        chooseDate();// 选择日期
        float[] totalData = setChartData(totalList);
        drawChart(totalData); // 画图
        setRatioText(totalData);// 设置 比例示意文本
        setBtnClick();
        return view;
    }

    /**
     *  求 数据 总和
     * @param data
     * @return
     */
    private float getSum(float[] data){
        float sum=0;
        for (int i = 0; i < data.length; i++) {
            sum+=data[i];
        }
        return sum;
    }

    /**
     *  设置 饼图 图例
     * @param data
     */
    private void setRatioText(float[] data) {
        float sum = getSum(data);
        switch (tabIndex){
            case 0:
                ratio_salary.setText("工资:"+new DecimalFormat("#.00").format((data[0]/sum)*100)+"%");// 保留两位小数
                ratio_windfall.setText("外快:"+new DecimalFormat("#.00").format((data[1]/sum)*100)+"%");
                //sum=0;
                break;
            case 1:
                ratio_entertainment.setText("娱乐:"+new DecimalFormat("#.00").format((data[0]/sum)*100)+"%");
                ratio_repast.setText("餐饮:"+new DecimalFormat("#.00").format((data[1]/sum)*100)+"%");
                ratio_rent.setText("房租:"+new DecimalFormat("#.00").format((data[2]/sum)*100)+"%");
                ratio_traffic.setText("交通:"+new DecimalFormat("#.00").format((data[3]/sum)*100)+"%");
                ratio_shopping.setText("购物:"+new DecimalFormat("#.00").format((data[4]/sum)*100)+"%");
                ratio_others.setText("其他:"+new DecimalFormat("#.00").format((data[5]/sum)*100)+"%");
                break;
        }
    }

    /**
     *  设置 饼图 数据
     * @param list
     * @return
     */
    private float[] setChartData(List<TbCount> list) {
        float[] totalData = null;
        switch (tabIndex){
            case 0://  设置只有 收入 的饼图数据
                float salary = 0;
                float sum = 0;
                if(list!=null)
                for (TbCount tbCount : list) {
                    float money = tbCount.getMoney();
                    String type = tbCount.getType();
                    if(type.equals("工资")){
                        salary += money;
                    }
                    sum += money;
                }
                totalData = new float[]{salary,sum-salary};
                break;
            case 1:// 设置只有 支出 的饼图数据
                float entertainment=0;
                float repast=0;
                float rent=0;
                float traffic=0;
                float shopping=0;
                float others=0;
                if(list!=null)
                for (TbCount tbCount : list) {
                    float money = tbCount.getMoney();
                    String type = tbCount.getType();
                    if(type.equals("娱乐")){
                        entertainment += money;
                    }else if(type.equals("餐饮")){
                        repast += money;
                    }
                    else if(type.equals("房租")){
                        rent += money;
                    }
                    else if(type.equals("交通")){
                        traffic += money;
                    }else if(type.equals("购物")){
                        shopping += money;
                    }else if(type.equals("其他")){
                        others += money;
                    }
                }
                totalData = new float[]{entertainment,repast,rent,traffic,shopping,others};
                break;
        }
        return totalData;
    }

    /**
     *    选择 日期
     */
    private void chooseDate() {
        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int y, int m,
                                          int d) {
                        dateString = y + "-" + (m + 1) + "-" + d;
                        txt_date.setText(dateString);
                         List<TbCount> list = null;
                        try {
                            list = dbUtils.findAll(Selector.from(TbCount.class).where("day","=",d).
                                                                        and("month", "=", m + 1).
                                                                        and("year", "=", y).
                                                                        and("article", "=", arrTabTitles[tabIndex]));
                            year = y;
                            monthOfYear = m;
                            dayOfMonth = d;
                            year = y;
                            monthOfYear = m;
                            dayOfMonth = d;
                            if (list != null && list.size()!=0) {
                                reloadListView(list);
                                // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                                float[] data = setChartData(list);
                                drawChart(data); // 画图
                                setRatioText(data);// 设置 比例示意文本
                            }else{
                                Toast.makeText(getActivity(),"暂无数据",Toast.LENGTH_SHORT).show();
                            }

                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, monthOfYear, dayOfMonth);
                dateDialog.show();
            }
        });
    }

    /**
     *  调用 画饼图 的方法
     * @param data
     */
    private void drawChart(float[] data) {
        switch (tabIndex){
            case 0:
                charView.setData(data,tabIndex);
                break;
            case 1:
                charView.setData(data,tabIndex);
                break;
        }
    }

    /**
     *   刷新 列表
     * @param list
     */
    private void reloadListView( List<TbCount> list) {
        totalList.clear();
        if(list!=null && list.size()!=0) {
            totalList.addAll(list);
        }else{

        }
        adapter.notifyDataSetChanged();
    }

    /**
     *   按钮 事件处理
     */
    @OnClick({R.id.btn_year,R.id.btn_month,R.id.btn_week,R.id.btn_day})
    private void setBtnClick(){
        //  按年查看
        btn_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TbCount> list = null;
                try {
                    list = dbUtils.findAll(Selector.from(TbCount.class).where("year", "=", year).
                            and("article", "=", arrTabTitles[tabIndex]));
                    reloadListView(list);
                    // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                    float[] data = setChartData(list);
                    drawChart(data); // 画图
                    setRatioText(data);// 设置 比例示意文本
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        // 按月查看
        btn_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TbCount> list = null;
                try {
                    list = dbUtils.findAll(Selector.from(TbCount.class).where("year","=",year).
                            and("month", "=", monthOfYear + 1).
                            and("article", "=", arrTabTitles[tabIndex]));
                    reloadListView(list);
                    // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                    float[] data = setChartData(list);
                    drawChart(data); // 画图
                    setRatioText(data);// 设置 比例示意文本
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
        //  按周 查看
        btn_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //------  先根据当前选择的 天，查询数据库，获得相对应的 周 是多少，然后在根据 周 查询数据库，按周查看
                List<TbCount> list_day = null;
                try {
                    list_day = dbUtils.findAll(Selector.from(TbCount.class).where("day","=",dayOfMonth).
                            and("month", "=", monthOfYear + 1).
                            and("year", "=", year).
                            and("article", "=", arrTabTitles[tabIndex]));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                if (list_day != null && list_day.size()!=0) {
                    week = list_day.get(0).getWeek();

                    List<TbCount> list_week = null;
                    try {
                        list_week = dbUtils.findAll(Selector.from(TbCount.class).where("week", "=", week).
                                and("month", "=", monthOfYear + 1).
                                and("year", "=", year).
                                and("article", "=", arrTabTitles[tabIndex]));
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                    reloadListView(list_week);
                    // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                    float[] data = setChartData(list_week);
                    drawChart(data); // 画图
                    setRatioText(data);// 设置 比例示意文本
                }
            }
        });
        //   按天查看
        btn_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<TbCount> list_day = null;
                try {
                    list_day = dbUtils.findAll(Selector.from(TbCount.class).where("day","=",dayOfMonth).
                            and("month", "=", monthOfYear + 1).
                            and("year", "=", year).
                            and("article", "=", arrTabTitles[tabIndex]));
                } catch (DbException e) {
                    e.printStackTrace();
                }
                reloadListView(list_day);
                // TODO--------由于 list 已经刷新，，所以需要重新画饼图-------
                float[] data = setChartData(list_day);
                drawChart(data); // 画图
                setRatioText(data);// 设置 比例示意文本
            }
        });
    }
}