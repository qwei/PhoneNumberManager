package com.qweri.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import cn.domob.android.ads.AdEventListener;
import cn.domob.android.ads.AdManager.ErrorCode;
import cn.domob.android.ads.AdView;

import com.umeng.analytics.MobclickAgent;

public class AllContactActivity extends Activity{

	
	private static final String DOMOB_PUBLIC_ID = "56OJxqJouN2zT9GFmy";
	private static final String DOMOB_INLINE_ID = "16TLejFvApq3cNUvs9Jg-QMs";
	
	RelativeLayout mAdContainer;
	AdView mAdview;
	private ListView listView;
	private AllContactAdapter allContactAdapter;
	private List<ContactBean> contactList = new ArrayList<>();
	private static final int WHAT_GET_ONE_PERSION = 0;
//	private ProgressBar
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == WHAT_GET_ONE_PERSION) {
				ContactBean bean = (ContactBean)msg.obj;
				allContactAdapter.add(bean);
			}
			
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_contact);
		
		listView = (ListView) findViewById(R.id.all_contact_list);
		allContactAdapter = new  AllContactAdapter(this, contactList);
		listView.setAdapter(allContactAdapter);
		loadADView();
		getAllContact();
		
		Intent service = new Intent(this, InterceptService.class);
		startService(service);
		if(getIntent().getBooleanExtra(InterceptService.EXTRA_CLICKED_NOTIFY, false)) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(1);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	private void getAllContact() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri uri = Uri.parse("content://com.android.contacts/contacts");
		        ContentResolver resolver = AllContactActivity.this.getContentResolver();
		        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
		        ContactBean bean = null;
		        while (cursor.moveToNext()) {
		            int contractID = cursor.getInt(0);
		            uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
		            Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
		            bean = new ContactBean();
		            while (cursor1.moveToNext()) {
		            	
		                String data1 = cursor1.getString(cursor1.getColumnIndex("data1"));
		                String mimeType = cursor1.getString(cursor1.getColumnIndex("mimetype"));
		                if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
		                	bean.setName(data1);
		                } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机
		                    bean.setTelephone(data1);
		                }                
		            }
		            cursor1.close();
		            if(bean.getTelephone() != null && bean.getTelephone().length() == 11) {
		            	bean.setInBlackList(BlackListUtils.isNumberInBlackList(AllContactActivity.this, bean.getTelephone()));
		            	Message msg = handler.obtainMessage(WHAT_GET_ONE_PERSION);
			            msg.obj = bean;
			            handler.sendMessage(msg);
		            }
		            
		            Log.i("ttt", bean.getName()+","+bean.getTelephone()+",");
		        }
		        cursor.close();
			}
		}).start();
		
	}
	
	private void loadADView(){
		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		// Create ad view
		mAdview = new AdView(this, DOMOB_PUBLIC_ID, DOMOB_INLINE_ID);
		mAdview.setAdEventListener(new AdEventListener() {
			@Override
			public void onAdOverlayPresented(AdView adView) {
				Log.d("tt", "onAdOverlayPresented");
			}

			@Override
			public void onAdOverlayDismissed(AdView adView) {
				Log.d("tt", "onAdOverlayDismissed");
			}

			@Override
			public void onAdClicked(AdView arg0) {
				Log.d("tt", "onAdClicked");
			}

			@Override
			public void onLeaveApplication(AdView arg0) {
				Log.d("tt", "onLeaveApplication");
			}

			@Override
			public Context onAdRequiresCurrentContext() {
				Log.d("tt", "onAdRequiresCurrentContext");
				return AllContactActivity.this;
			}

			@Override
			public void onAdFailed(AdView arg0, ErrorCode arg1) {
				Log.d("tt", "onAdFailed");
			}

			@Override
			public void onEventAdReturned(AdView arg0) {
				Log.d("tt", "onEventAdReturned");
			}
		});
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mAdview.setLayoutParams(layout);
		mAdContainer.addView(mAdview);
	}
}
