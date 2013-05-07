package com.geely.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.geely.geely_client.R;

import com.geely.po.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;


/**
 *
 *
 * <p>Title: 好爸妈客户端</p>
 * <p>Description:宝典频道列表数据适配器</p>
 * <p>创建日期:2013-1-31</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class MessageAdapter extends BaseAdapter {
    private Context context;
    private List<Message> list;
    private DateFormat format = new SimpleDateFormat("MM-dd HH:mm");

    public MessageAdapter(Context context) {
        this.context = context;
    }

    public MessageAdapter(Context context, List<Message> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int location) {
        return list.get(location);
    }

    @Override
    public long getItemId(int location) {
        return list.get(location).getId();
    }

    public void setList(List<Message> list) {
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Message message = list.get(position);

        convertView = LayoutInflater.from(context)
                                    .inflate(R.layout.message_list_item, null);

        ImageView readImage = (ImageView) convertView.findViewById(R.id.unread_flag);

        if (message.getReadFlag() == 0) {
            readImage.setVisibility(View.VISIBLE);
        }

        TextView title = (TextView) convertView.findViewById(R.id.message_item_title);
        title.setText(message.getSendUser());

        TextView time = (TextView) convertView.findViewById(R.id.message_item_time);
        time.setText(format.format(new Date(message.getReceiveTime())));

        TextView content = (TextView) convertView.findViewById(R.id.message_item_content);
        content.setText(message.getMtime() + "  " + message.getTitle());

        return convertView;
    }
}
