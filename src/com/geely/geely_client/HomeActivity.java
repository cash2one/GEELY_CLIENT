package com.geely.geely_client;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.ViewGroup.LayoutParams;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.geely.adapter.HomeGridAdapter;

import com.geely.data.GetData;

import com.geely.po.Channel;

import com.geely.util.StringUtil;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class HomeActivity extends BaseActivity implements OnItemClickListener {
    public static final int PAGE_SIZE = 8;
    private ViewPager viewPager;
    private ViewGroup main;
    private ViewGroup group;
    private ArrayList<View> pageViews;
    private int currentPage;
    private ImageView[] dots;
    private ImageView imageView;
    LinearLayout.LayoutParams param;
    ArrayList<ArrayList<Channel>> lists = new ArrayList<ArrayList<Channel>>(); // 全部数据的集合集lists.size()==countpage;
    List<Channel> lstDate = new ArrayList<Channel>(); // 每一页的数据
    ArrayList<GridView> gridviews = new ArrayList<GridView>();
    private long exitTime;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initData();

        LayoutInflater inflater = getLayoutInflater();
        main = (ViewGroup) inflater.inflate(R.layout.layout_home, null);
        pageViews = new ArrayList<View>();

        for (int i = 0; i < lists.size(); i++) {
            pageViews.add(addGridView(i));
        }

        viewPager = (ViewPager) main.findViewById(R.id.homePages);
        group = (ViewGroup) main.findViewById(R.id.viewGroup);

        // 初始化底部小点
        initDots();
        setContentView(main);
        viewPager.setAdapter(new HomePageAdapter());
        viewPager.setOnPageChangeListener(new HomePageChangeListener());
    }

    public void initData() {
        Channel channel1 = new Channel(1, 1, "生产在线", "product");
        Channel channel2 = new Channel(1, 1, "3MIS数据", "data_query");
        Channel channel3 = new Channel(1, 1, "一级预警", "one_warning");
        lstDate.add(channel1);
        lstDate.add(channel2);
        lstDate.add(channel3);

        //总共页数
        int countPages = (int) Math.ceil(lstDate.size() / (float) PAGE_SIZE);

        if (countPages == 0) {
            return;
        }

        //全部页的数据
        lists = new ArrayList<ArrayList<Channel>>();

        for (int i = 0; i < countPages; i++) {
            lists.add(new ArrayList<Channel>());

            int beginSize = PAGE_SIZE * i;
            int endSize = (((PAGE_SIZE * (i + 1)) > lstDate.size())
                ? lstDate.size() : (PAGE_SIZE * (i + 1)));

            for (int j = beginSize; j < endSize; j++)
                lists.get(i).add(lstDate.get(j));
        }
    }

    /**
     * 添加每一页频道列表
     * @param i
     * @return
     */
    public LinearLayout addGridView(int i) {
        LinearLayout linear = new LinearLayout(this);
        GridView gridView = new GridView(this);
        gridView.setId(i + 36661);

        HomeGridAdapter adapter = new HomeGridAdapter(this, gridView,
                lists.get(i));
        gridView.setAdapter(adapter);
        gridView.setNumColumns(3);
        gridView.setHorizontalSpacing(0);
        gridView.setVerticalSpacing(0);
        gridView.setOnItemClickListener(this);
        //设置当点击gradView时的背景为透明的
        //        gridView.setSelector(R.drawable.selector_null);
        gridviews.add(gridView);
        linear.addView(gridView, param);

        return linear;
    }

    public void init() {
        // relate = (RelativeLayout) findViewById(R.id.relate);
        //自定义可滑动组建
        param = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.FILL_PARENT,
                android.view.ViewGroup.LayoutParams.FILL_PARENT);
        param.rightMargin = 20;
        param.leftMargin = 20;
        param.topMargin = 20;
    }

    /**
     * @Title: initDots
     * @Description: TODO(这里用一句话描述这个方法的作用) 设定文件
     * @return void 返回类型
     * @throws
     */
    private void initDots() {
        dots = new ImageView[pageViews.size()];

        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(this);
            imageView.setLayoutParams(new LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setMaxHeight(20);
            imageView.setMaxWidth(40);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setAdjustViewBounds(true);
            imageView.setPadding(7, 0, 7, 0);
            imageView.setImageResource(R.drawable.dot);
            dots[i] = imageView;
            dots[i].setEnabled(false);
            dots[i].setTag(i);
            //			dots[i].setOnClickListener(this); 
            group.addView(dots[i]);
        }

        currentPage = 0;
        dots[currentPage].setEnabled(true);
    }

    private void setCurDot(int positon) {
        if ((positon < 0) || (positon > (pageViews.size() - 1)) ||
                (currentPage == positon)) {
            return;
        }

        dots[positon].setEnabled(true);
        dots[currentPage].setEnabled(false);
        currentPage = positon;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
        String text = ((TextView) view.findViewById(R.id.ItemText)).getText()
                       .toString();
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyyMMdd");

        if ("生产在线".equals(text)) {
            String now = format.format(date);
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.putExtra("keys", new String[] { "startTime", "endTime" });

            intent.putExtra("values", new String[] { now, now });
            intent.putExtra("url", GetData.SERVER_IP +
                GetData.GET_MES_INFO_URL);
            intent.putExtra("msg", "数据加载...");
            startActivityForResult(intent, 0);
        } else if ("3MIS数据".equals(text)) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            date = calendar.getTime();

            String now = format.format(date);
            Intent intent = new Intent(this, LoadingActivity.class);
            intent.putExtra("keys",
                new String[] { "startTime", "endTime", "misType" });

            intent.putExtra("values", new String[] { now, now, "3MIS" });
            intent.putExtra("url", GetData.SERVER_IP +
                GetData.GET_MIS_INFO_URL);
            intent.putExtra("msg", "数据加载...");
            startActivityForResult(intent, 1);
        } else if ("一级预警".equals(text)) {
            Intent intent = new Intent(this, MessageListActivity.class);
            intent.putExtra("type", "warning");
            intent.putExtra("subType", "一级预警");
            startActivity(intent);
        } else {
            Toast.makeText(this, "频道建设中，敬请期待...", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            String result = data.getStringExtra("result");
            JSONObject obj = null;

            if (StringUtil.isEmpty(result)) {
                Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();

                return;
            } else {
                try {
                    obj = new JSONObject(result);

                    if (!"200".equals(obj.getString("RESULTCODE"))) {
                        Toast.makeText(this, "服务器异常", Toast.LENGTH_SHORT).show();

                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Intent intent = null;

            switch (requestCode) {
            case 0:
                intent = new Intent(this, MesInfoActivity.class);

                break;

            case 1:
                intent = new Intent(this, MisInfoActivity.class);
                intent.putExtra("misType", "3MIS");

                break;
            }

            intent.putExtra("result", result);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再点击一次退出",
                    Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent = new Intent();
                intent.setAction("ExitApp");
                //发送广播退出所有activity
                sendBroadcast(intent);

                //延迟0.1秒退出应用
                new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 100);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    class HomePageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));

            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public void finishUpdate(View arg0) {
        }
    }

    class HomePageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot(arg0);
            currentPage = arg0;
        }
    }
}
