package com.geely.geely_client;

import android.app.AlertDialog;
import android.app.Service;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.os.Vibrator;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.Button;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import android.widget.ListView;

import com.geely.adapter.MessageAdapter;

import com.geely.db.MessageDao;

import java.util.List;


public class MessageListActivity extends BaseActivity
    implements OnItemClickListener, OnItemLongClickListener {
    private ListView listView;
    @SuppressWarnings("rawtypes")
    private List list;
    private String type;
    private String subType;
    private MessageAdapter adapter;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_message_list);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");
        subType = intent.getStringExtra("subType");
        listView = (ListView) findViewById(R.id.message_list);
        init();

        adapter = new MessageAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
    }

    private void init() {
        MessageDao dao = new MessageDao(this);
        SharedPreferences sharedPreferences = getSharedPreferences("loginpref",
                Context.MODE_PRIVATE);

        if ("meeting".equals(type)) {
            list = dao.selectMeetingByUserId(Integer.valueOf(
                        sharedPreferences.getString("userId", "")), subType);
        } else if ("warning".equals(type)) {
            list = dao.selectWarningByUserId(Integer.valueOf(
                        sharedPreferences.getString("userId", "")), subType);
        }

        dao.close();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
        long id) {
        if ("meeting".equals(type)) {
            Intent intent = new Intent(this, MeetingDetailActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        } else if ("warning".equals(type)) {
            Intent intent = new Intent(this, WarningDetailActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view,
        final int position, final long id) {
        vibrator.vibrate(100);
        new AlertDialog.Builder(this).setTitle("确认删除").setMessage("你确认要删除该条记录？")
                                     .setPositiveButton("确认",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i) {
                    MessageDao dao = new MessageDao(MessageListActivity.this);

                    if ("meeting".equals(type)) {
                        dao.deleteMettingById((int) id);
                    } else if ("warning".equals(type)) {
                        dao.deleteWarningById((int) id);
                    }

                    dao.close();
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                }
            }).setNegativeButton("取消",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialoginterface, int i) {
                }
            }).show();

        return false;
    }
}
