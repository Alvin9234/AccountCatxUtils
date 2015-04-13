package com.alvin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.alvin.AccountCatxUtils.R;
import com.alvin.database.TbCount;

import java.util.List;
import java.util.Map;

/**
 * Created by Alvin on 2015/4/10.
 */
public class ListViewAdapter extends BaseAdapter {
    private List<TbCount> list=null;
    private Context context;
    private LayoutInflater inflater;
    public ListViewAdapter(List<TbCount> list, Context context) {
        //  健壮的写法
        if(context==null){
            throw new IllegalArgumentException("Context is null");
        }
        this.list = list;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int ret = 0;
        if(list!=null){
            ret = list.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int i) {
        Object ret = null;
        if(list!=null){
            ret = list.get(i);
        }
        return ret;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View ret = null;
        if(view !=null){
            ret = view;
        }else{
            ret = inflater.inflate(R.layout.item_listview,viewGroup,false);
        }
        ViewHolder viewHolder = (ViewHolder)ret.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.textView_inOrOut = (TextView) ret.findViewById(R.id.textView_inOrOut);
            viewHolder.textView_money = (TextView) ret.findViewById(R.id.textView_money);
            viewHolder.textView_article = (TextView) ret.findViewById(R.id.textView_article);
            viewHolder.textView_remark = (TextView) ret.findViewById(R.id.textView_remark);
            viewHolder.textView_date = (TextView) ret.findViewById(R.id.textView_date);
        }
        TbCount tbCount = list.get(i);
        String article = tbCount.getArticle();
        if (article != null) {
            viewHolder.textView_inOrOut.setText(article+":");
        }
        String money = String.valueOf(tbCount.getMoney());
        if (money != null) {
            viewHolder.textView_money.setText(money);
        }
        String type = tbCount.getType();
        if (type != null) {
            viewHolder.textView_article.setText(type);
        }
        String remark = tbCount.getRemark();
        if (remark != null) {
            viewHolder.textView_remark.setText(remark);
        }else{
            viewHolder.textView_remark.setText("无备注");
        }
        int year = tbCount.getYear();
        int month = tbCount.getMonth();
        int day = tbCount.getDay();
        if(year != 0 && month !=0 && day != 0){
            String date = year+"-"+month+"-"+day;
            viewHolder.textView_date.setText(date);
        }
        return ret;
    }

    class ViewHolder {
        TextView textView_inOrOut;
        TextView textView_money;
        TextView textView_article;
        TextView textView_remark;
        TextView textView_date;
    }
}
