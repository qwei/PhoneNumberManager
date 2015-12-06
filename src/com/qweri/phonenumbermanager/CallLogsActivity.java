package com.qweri.phonenumbermanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.qweri.phonenumbermanager.adapter.CallLogsAdapter;

public class CallLogsActivity extends ActionBarActivity{

	private ListView callLogsListview;
	private List<CallLogBean> callLogs;
	private CallLogsAdapter callLogAdapter;
	private final static int LOAD_ONE_LOG_ID = 1101;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == LOAD_ONE_LOG_ID) {
				callLogAdapter.add((CallLogBean)msg.obj);
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_logs);
		getSupportActionBar().setTitle("Í¨»°¼ÇÂ¼");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable((Drawable)(new ColorDrawable(Color.parseColor("#66555555"))));
		callLogsListview = (ListView) findViewById(R.id.call_logs_list);
		callLogs = new ArrayList<CallLogBean>();
		callLogAdapter = new CallLogsAdapter(this, callLogs);
		callLogsListview.setAdapter(callLogAdapter);
		getCallLogs();
		
	}
	
	private void getCallLogs() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ContentResolver resolver = getApplicationContext().getContentResolver();
				Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null);
				while(cursor.moveToNext()) {
					String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER)); 
					SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
					Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Calls.DATE))));
					String name = cursor.getString(cursor.getColumnIndexOrThrow(Calls.CACHED_NAME)); 
					CallLogBean bean = new CallLogBean();
					bean.setName(name);
					bean.setTelephone(number);
					bean.setTime(sfd.format(date));
					bean.setInBlackList(BlackListUtils.isNumberInBlackList(getApplicationContext(), number));
					Message msg = mHandler.obtainMessage(LOAD_ONE_LOG_ID);
					msg.obj = bean;
					mHandler.sendMessage(msg);
				}
			}
		}).start();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
		case android.R.id.home:
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
