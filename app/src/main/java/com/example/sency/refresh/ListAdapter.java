package com.example.sency.refresh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sency on 2016/8/4.
 */
public class ListAdapter extends BaseAdapter {
    private List<ItemBean> lists;
    LayoutInflater inflater;

    public ListAdapter(Context context, List<ItemBean> list) {
        this.lists = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view==null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item,null);
            holder.imageView = (ImageView) view.findViewById(R.id.img);
            holder.textView = (TextView) view.findViewById(R.id.content);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        holder.imageView.setImageResource(lists.get(i).getImgId());
        holder.textView.setText(lists.get(i).getContent());
        return view;
    }

    public void onDateChange(List<ItemBean> list) {
        this.lists = list;
        //如果数据改变界面跟随改变
        this.notifyDataSetChanged();
    }
}

class ViewHolder{
    ImageView imageView;
    TextView textView;
}