package com.geely.geely_client;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import android.widget.AdapterView;

import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.geely.adapter.ChannelAdapter;

import com.geely.data.GetData;

import com.geely.po.Channel;

import com.geely.util.StringUtil;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class QueryActivity extends BaseActivity implements OnItemClickListener {
    private ListView listView;
    private List<Channel> channelList;
    private long exitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_query);
        listView = (ListView) findViewById(R.id.channel_list);
        initData();

        ChannelAdapter adapter = new ChannelAdapter(this, channelList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void initData() {
        channelList = new ArrayList<Channel>();

        Channel channel1 = new Channel(1, "质量", true);
        Channel channel2 = new Channel(2, "市场质量", false);
        Channel channel3 = new Channel(3, "制造质量", false);
        Channel channel4 = new Channel(4, "成本/利润", true);
        Channel channel5 = new Channel(5, "效率", true);
        Channel channel6 = new Channel(6, "生产在线查询", false);
        Channel channel7 = new Channel(7, "安环", true);
        Channel channel8 = new Channel(8, "管理", true);
        channelList.add(channel1);
        channelList.add(channel2);
        channelList.add(channel3);
        channelList.add(channel4);
        channelList.add(channel5);
        channelList.add(channel6);
        channelList.add(channel7);
        channelList.add(channel8);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
        boolean isGroup = (Boolean) view.getTag();

        if (!isGroup) {
            TextView text = (TextView) view.findViewById(R.id.channel_item_text);

            if ("市场质量".equals(text.getText().toString())) {
                Intent intent = new Intent(this, QueryDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("title", "市场质量");

                //把一个Activity转换成一个View
                Window w = QueryGroupTab.group.getLocalActivityManager()
                                              .startActivity("QueryDetailActivity",
                        intent);
                View decorview = w.getDecorView();
                //把View添加大ActivityGroup中
                QueryGroupTab.group.setContentView(decorview);
            } else if ("生产在线查询".equals(text.getText().toString())) {
                Intent intent = new Intent(this, LoadingActivity.class);
                intent.putExtra("keys", new String[] { "startTime", "endTime" });

                Date date = new Date();

                DateFormat format = new SimpleDateFormat("yyyyMMdd");
                String now = format.format(date);
                intent.putExtra("values", new String[] { now, now });
                intent.putExtra("url",
                    GetData.SERVER_IP + GetData.GET_MES_INFO_URL);
                intent.putExtra("msg", "数据加载...");
                getParent().startActivityForResult(intent, 0);
            } else {
                Toast.makeText(this, "该栏目正在建设中，敬请期待...", Toast.LENGTH_SHORT)
                     .show();
            }
        }
    }

    @Override
    public void handleActivityResult(int requestCode, int resultCode,
        Intent data) {
        if (resultCode == 0) {
            switch (requestCode) {
            case 0:

                String result = data.getStringExtra("result");

                if (StringUtil.isEmpty(result)) {
                    Toast.makeText(this, "网络异常", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject obj = null;

                    try {
                        obj = new JSONObject(result);

                        if ("200".equals(obj.getString("RESULTCODE"))) {
                            Intent intent = new Intent(this,
                                    MesInfoActivity.class);
                            intent.putExtra("result", result);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "服务器异常", Toast.LENGTH_SHORT)
                                 .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
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
}
