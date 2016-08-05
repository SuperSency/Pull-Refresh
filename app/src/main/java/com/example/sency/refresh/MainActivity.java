package com.example.sency.refresh;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements RefreshListView.IReflashListener{

    private RefreshListView listView;
    private List<ItemBean> list;
    ListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initDatas();

       showList(list);
    }

    private void showList(List<ItemBean> list) {
        if (adapter == null) {
            listView = (RefreshListView) findViewById(R.id.list);
            listView.setInterfase(this);
            adapter = new ListAdapter(MainActivity.this, list);
            listView.setAdapter(adapter);
        }else{
            adapter.onDateChange(list);
        }
    }

    private void initDatas() {
        list = new ArrayList<>();
        ItemBean one = new ItemBean("Hello,蛋酱!!!",R.drawable.img);
        list.add(one);
        ItemBean two = new ItemBean("Hello,蛋酱!!!",R.drawable.img);
        list.add(two);
        ItemBean three = new ItemBean("Hello,蛋酱!!!",R.drawable.img);
        list.add(three);
        ItemBean four = new ItemBean("Hello,蛋酱!!!",R.drawable.img);
        list.add(four);
        ItemBean five = new ItemBean("Hello,蛋酱!!!",R.drawable.img);
        list.add(five);
        ItemBean six = new ItemBean("Hello,蛋酱!!!",R.drawable.img);
        list.add(six);
        ItemBean seven = new ItemBean("Hello,蛋酱!!!",R.drawable.img);
        list.add(seven);
    }


    @Override
    public void onReflash() {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            //两秒后执行
            @Override
            public void run() {
                //获取最新数据
                ItemBean one = new ItemBean("Hello,蛋酱!!!", R.drawable.img);
                list.add(0, one);
                ItemBean two = new ItemBean("Hello,蛋酱!!!", R.drawable.img);
                list.add(0, two);
                //通知界面显示
                if (adapter == null) {
                    showList(list);
                }
                //通知listview刷新数据完毕
                listView.reflashComplete();
            }
        }, 2000);

    }
}

class ItemBean {
    String content;
    int imgId;
    public ItemBean(String content, int imgId) {
        this.content = content;
        this.imgId = imgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
