package com.geely.geely_client;

import android.content.Intent;

import android.os.Bundle;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class QueryDetailActivity extends BaseActivity {
    private static Map<String, List<Channel>> map = new HashMap<String, List<Channel>>();

    static {
        Channel channel1 = new Channel(1, "3MIS数据", false);
        Channel channel2 = new Channel(1, "6MIS数据", false);
        Channel channel3 = new Channel(1, "12MIS数据", false);
        List<Channel> list1 = new ArrayList<Channel>();
        list1.add(channel1);
        list1.add(channel2);
        list1.add(channel3);
        map.put("市场质量", list1);
    }

    String misType = "";
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        setContentView(R.layout.layout_query);
        listView = (ListView) findViewById(R.id.channel_list);

        ChannelAdapter adapter = new ChannelAdapter(this, map.get(title));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    String title = ((TextView) view.findViewById(R.id.channel_item_text)).getText()
                                    .toString();

                    if ("3MIS数据".endsWith(title)) {
                        misType = "3MIS";
                    } else if ("6MIS数据".equals(title)) {
                        misType = "6MIS";
                    } else if ("12MIS数据".equals(title)) {
                        misType = "12MIS";
                    }

                    Intent intent = new Intent(QueryDetailActivity.this,
                            LoadingActivity.class);
                    intent.putExtra("keys",
                        new String[] { "startTime", "endTime", "misType" });

                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    date = calendar.getTime();
                    DateFormat format = new SimpleDateFormat("yyyyMMdd");
                    String now = format.format(date);
                    intent.putExtra("values", new String[] { now, now, misType });
                    intent.putExtra("url",
                        GetData.SERVER_IP + GetData.GET_MIS_INFO_URL);
                    intent.putExtra("msg", "数据加载...");
                    getParent().startActivityForResult(intent, 0);

                    startActivity(intent);
                }
            });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, QueryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //把一个Activity转换成一个View
        Window w = QueryGroupTab.group.getLocalActivityManager()
                                      .startActivity("QueryActivity", intent);
        View view = w.getDecorView();
        //把View添加大ActivityGroup中
        QueryGroupTab.group.setContentView(view);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();

            return true;
        }

        return super.onKeyDown(keyCode, event);
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
                                    MisInfoActivity.class);
                            intent.putExtra("result", result);
                            intent.putExtra("misType", misType);
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
}
