package com.geely.geely_client;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Activity2 extends Activity{
	 private ListView listView;
	private final static String TAG = "Activity2";
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        listView = new ListView(this);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,getData()));
        setContentView(listView);
    }
	
	private List<String> getData(){
        
        List<String> data = new ArrayList<String>();
        for(int i=1;i<30;i++){
        	data.add("测试数据"+i);
        }
        return data;
    }
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}