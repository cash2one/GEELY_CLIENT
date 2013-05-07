package com.geely.geely_client;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;

import com.geely.db.MessageDao;

import com.geely.po.Warning;

import com.geely.util.StringUtil;


public class WarningDetailActivity extends BaseActivity {
    private Long id;
    private Warning warning;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_warning_detail);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        init();

        TextView sendView = (TextView) findViewById(R.id.warning_send_user);
        sendView.setText(warning.getSendUser());

        TextView timeView = (TextView) findViewById(R.id.warning_mtime);
        timeView.setText(warning.getMtime());

        TextView titleView = (TextView) findViewById(R.id.warning_title);
        titleView.setText(warning.getTitle());

        TextView solveView = (TextView) findViewById(R.id.warning_solve_users);
        solveView.setText(warning.getSolveUsers());

        TextView explainView = (TextView) findViewById(R.id.warning_explain);
        explainView.setText(warning.getExplain());

        TextView contentView = (TextView) findViewById(R.id.warning_content);
        contentView.setText(StringUtil.ToDBC(warning.getContent()));

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    finish();
                }
            });
    }

    public void init() {
        MessageDao dao = new MessageDao(this);
        warning = dao.findWarningById(id.intValue());
        dao.updateWarningReadFlag(id.intValue());
        dao.close();
    }
}
