package com.geely.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

import com.geely.geely_client.R;

import com.geely.po.Channel;

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
public class ChannelAdapter extends BaseAdapter {
    private Context context;
    private List<Channel> channelList;

    public ChannelAdapter(Context context) {
        this.context = context;
    }

    public ChannelAdapter(Context context, List<Channel> channelList) {
        this.context = context;
        this.channelList = channelList;
    }

    @Override
    public int getCount() {
        return channelList.size();
    }

    @Override
    public Object getItem(int location) {
        return channelList.get(location);
    }

    @Override
    public long getItemId(int location) {
        return channelList.get(location).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Channel chanel = channelList.get(position);

        if (chanel.isGroup()) {
            convertView = LayoutInflater.from(context)
                                        .inflate(R.layout.channel_group, null);

            TextView text = (TextView) convertView.findViewById(R.id.channel_item_group_text);
            text.setText(chanel.getTitle());
        } else {
            convertView = LayoutInflater.from(context)
                                        .inflate(R.layout.channel_item, null);

            TextView text = (TextView) convertView.findViewById(R.id.channel_item_text);
            text.setText(chanel.getTitle());
        }

        convertView.setTag(chanel.isGroup());

        return convertView;
    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }
}
