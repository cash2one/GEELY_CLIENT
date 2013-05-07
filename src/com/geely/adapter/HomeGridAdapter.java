package com.geely.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.geely.geely_client.R;

import com.geely.po.Channel;

import com.geely.util.ResourceUtil;

import java.util.ArrayList;
import java.util.List;


public class HomeGridAdapter extends BaseAdapter {
    private Context context;
    private List<Channel> channels;
    private ResourceUtil resourceUtil;
    private TextView txt;
    private ImageView img;

    public HomeGridAdapter(Context mContext, GridView listview,
        ArrayList<Channel> channels) {
        this.context = mContext;
        this.channels = channels;
        resourceUtil = new ResourceUtil(mContext);
    }

    @Override
    public int getCount() {
        return this.channels.size();
    }

    @Override
    public Object getItem(int location) {
        return this.channels.get(location);
    }

    @Override
    public long getItemId(int location) {
        return this.channels.get(location).getId();
    }

    @Override
    public View getView(int position, View convertView2, ViewGroup parent) {
        RelativeLayout layout = (RelativeLayout) LayoutInflater.from(context)
                                                               .inflate(R.layout.home_item,
                null);
        txt = (TextView) layout.findViewById(R.id.ItemText);
        img = (ImageView) layout.findViewById(R.id.ItemImage);

        Channel channel = channels.get(position);
        txt.setText(channel.getTitle());
        img.setBackgroundResource(resourceUtil.getDrawableIdentifier(
                channel.getIcon()));

        return layout;
    }
}
