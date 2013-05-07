package com.geely.geely_client;

import android.content.Intent;

import android.os.Bundle;

import android.view.View;

import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;

import com.geely.db.MessageDao;

import com.geely.po.Meeting;


public class MeetingDetailActivity extends BaseActivity {
    private Long id;
    private Meeting meeting;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_metting_detail);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        init();

        TextView sendView = (TextView) findViewById(R.id.meeting_send_user);
        sendView.setText(meeting.getSendUser());

        TextView timeView = (TextView) findViewById(R.id.meeting_mtime);
        timeView.setText(meeting.getMtime());

        TextView addressView = (TextView) findViewById(R.id.meeting_address);
        addressView.setText(meeting.getAddress());

        TextView titleView = (TextView) findViewById(R.id.meeting_title);
        titleView.setText(meeting.getTitle());

        TextView joinView = (TextView) findViewById(R.id.meeting_join_users);
        joinView.setText(meeting.getJoinUsers());

        TextView requireView = (TextView) findViewById(R.id.meeting_require);
        requireView.setText(meeting.getRequire());

        TextView contentView = (TextView) findViewById(R.id.meeting_content);
        contentView.setText(meeting.getContent());

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
        meeting = dao.findMeetingById(id.intValue());
        dao.updateMettingReadFlag(id.intValue());
        dao.close();
    }
}
