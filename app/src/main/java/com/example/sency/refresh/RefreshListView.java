package com.example.sency.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sency on 2016/8/4.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener {
    //顶部布局文件
    View head;

    //顶部布局文件的高度
    int headHeight;

    //当前第一个可见item的位置
    int firstVisibleItem;

    //标记,当前是在listview最顶端摁下的
    boolean isRemark;

    //摁下时的Y值
    int startY;

    //当前的状态
    int state;
    //正常状态
    final int NONE = 0;
    //提示下拉刷新状态
    final int PULL = 1;
    //提示释放状态
    final int RELESE = 2;
    //正在刷新状态
    final int REFLASHING = 3;

    //listview,当前滚动状态
    int scrollState;

    IReflashListener listener;

    //现在的Y值
    int tempY;

    //三种构造方法都要重写
    public RefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    //初始化界面
    private void initView(Context context) {
        //添加顶部布局文件到listview
        LayoutInflater inflater = LayoutInflater.from(context);
        head = inflater.inflate(R.layout.head, null);
        //隐藏顶部布局文件
        measureView(head);
        //获取高度
        headHeight = head.getMeasuredHeight();
        Log.i("tag", "headHeight" + headHeight);
        //隐藏
        topPadding(-headHeight);
        //添加到listview里面
        this.addHeaderView(head);
        this.setOnScrollListener(this);
    }

    /**
     * 通知父布局占用的宽，高
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        /**
         * 第一个参数：当前head左边距
         * 第二个参数：当前head内边距
         * 第三个参数：子布局宽度
         */
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            //高度不为空,需要填充布局
            height = MeasureSpec.makeMeasureSpec(tempHeight, MeasureSpec.EXACTLY);
        } else {
            //高度是空,则第一个参数size可以为0
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }


    /**
     * 设置head布局的上边距
     *
     * @param topPadding
     */
    private void topPadding(int topPadding) {
        head.setPadding(head.getPaddingLeft(), topPadding,
                head.getPaddingRight(), head.getPaddingBottom());
        head.invalidate();
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        this.scrollState = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.i("tag", "onTouchEvent");
        switch (ev.getAction()) {
            //按下
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem == 0) {
                    isRemark = true;
                    startY = (int) ev.getY();
                    reflashViewByState();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("tag", "move");
                onMove(ev);
                //reflashViewByState();
                break;
            case MotionEvent.ACTION_UP:
                if (state == RELESE) {
                    state = REFLASHING;
                    reflashViewByState();
                    //加载最新数据
                    listener.onReflash();
                } else if (state == PULL) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;
        }
        if (state == RELESE || state == PULL) {
            reflashViewByState();
            this.setPressed(false);
            this.setFocusable(false);
            this.setFocusableInTouchMode(false);
            nowState();
            Log.i("tag", "true");
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public void nowState() {
        Log.i("tag","nowState");
        int space = tempY - startY;
        Log.i("tag","nowSpace"+space);
        //当间距大于一定高度并且状态为正在滚动时
        Log.i("tag","exactHeight:"+(headHeight+50));
        if (space > headHeight + 50 ) {
            state = RELESE;
            Log.i("tag","RELESE");
            reflashViewByState();
        } else if (space < (headHeight + 50)) {
            state = PULL;
            Log.i("tag","nowPull");
            reflashViewByState();
        } else if (space <= 0) {
            state = NONE;
            isRemark = false;
            reflashViewByState();
        }
    }

    /**
     * 判断移动过程中的操作
     *
     * @param ev
     */
    private void onMove(MotionEvent ev) {
        if (!isRemark) {
            return;
        }
        //获取到当前移动到什么位置
        tempY = (int) ev.getY();
        //移动的距离是多少
        int space = tempY - startY;
        Log.i("tag", "tempY:" + tempY);
        Log.i("tag", "startY:" + startY);
        int topPadding = space - headHeight;
        switch (state) {
            //正常状态
            case NONE:
                if (space > 0) {
                    state = PULL;
                    reflashViewByState();
                }
                break;
            //提示下拉刷新状态
            case PULL:
                topPadding(topPadding);
                Log.i("tag", "headHeight:" + headHeight);
                Log.i("tag", "space:" + space);
                //当间距大于一定高度并且状态为正在滚动时
                if (space > headHeight + 50 && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    state = RELESE;
                    reflashViewByState();
                }

                break;
            //提示释放状态
            case RELESE:
                topPadding(topPadding);
                Log.i("tag", "space2:" + space);
                //  Log.i("tag","topPadding:"+topPadding);
                if (space < (headHeight + 50)) {
                    state = PULL;
                    Log.i("Tag", "PULL");
                    reflashViewByState();
                } else if (space <= 0) {
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }
                break;
            case REFLASHING:
                reflashViewByState();
                break;
        }
    }

    /**
     * 根据当前状态改变界面显示
     */
    private void reflashViewByState() {
        TextView tip = (TextView) head.findViewById(R.id.tip);
        ImageView arrow = (ImageView) head.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) head.findViewById(R.id.progress);
        //箭头转换的动画
        RotateAnimation anim = new RotateAnimation(0, 180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        //设置时间间隔
        anim.setDuration(500);
        anim.setFillAfter(true);
        RotateAnimation anim1 = new RotateAnimation(180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        anim1.setDuration(500);
        anim1.setFillAfter(true);
        switch (state) {
            case NONE:
                arrow.clearAnimation();
                topPadding(-headHeight);
                break;
            case PULL:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("下拉可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim);
                break;
            case RELESE:
                arrow.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("松开可以刷新");
                arrow.clearAnimation();
                arrow.setAnimation(anim1);
                break;
            case REFLASHING:
                //正在刷新时的固定高度
                topPadding(50);
                arrow.clearAnimation();
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新.....");
                break;
        }
    }

    public void reflashComplete() {
        state = NONE;
        isRemark = false;
        reflashViewByState();
        TextView lastupdatetime = (TextView) head.findViewById(R.id.lastupdate_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastupdatetime.setText(time);
    }

    public void setInterfase(IReflashListener listener) {
        this.listener = listener;
    }

    /**
     * 刷新数据接口
     */
    public interface IReflashListener {
        public void onReflash();
    }
}
