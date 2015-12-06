package com.qweri.phonenumbermanager;

import java.text.SimpleDateFormat;

import android.view.ViewGroup.LayoutParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.domob.android.ads.AdManager.ErrorCode;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RelativeLayout;
import cn.domob.android.ads.AdEventListener;
import cn.domob.android.ads.AdView;

import com.qweri.phonenumbermanager.adapter.AllContactAdapter;
import com.umeng.analytics.MobclickAgent;

public class AllContactActivity extends ActionBarActivity {


	RelativeLayout mAdContainer;
	AdView mAdview;
	private ListView listView;
	private AllContactAdapter allContactAdapter;
	private List<ContactBean> contactList = new ArrayList<>();
	private static final int WHAT_GET_ONE_PERSION = 0;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == WHAT_GET_ONE_PERSION) {
				ContactBean bean = (ContactBean) msg.obj;
				allContactAdapter.add(bean);
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_contact);

		getSupportActionBar().setTitle("通讯录");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable((Drawable)(new ColorDrawable(Color.parseColor("#66555555"))));
		listView = (ListView) findViewById(R.id.all_contact_list);
		allContactAdapter = new AllContactAdapter(this, contactList);
		listView.setAdapter(allContactAdapter);
		loadADView();
		getAllContact();
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

	@Override
	protected void onResume() {
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
				ContentResolver resolver = AllContactActivity.this.getApplicationContext()
						.getContentResolver();
				Cursor cursor = resolver.query(uri, new String[] { "_id" },
						null, null, null);
				ContactBean bean = null;
				while (cursor.moveToNext()) {
					int contractID = cursor.getInt(0);
					uri = Uri.parse("content://com.android.contacts/contacts/"
							+ contractID + "/data");
					Cursor cursor1 = resolver.query(uri, new String[] {
							"mimetype", "data1", "data2" }, null, null, null);
					bean = new ContactBean();
					while (cursor1.moveToNext()) {

						String data1 = cursor1.getString(cursor1
								.getColumnIndex("data1"));
						String mimeType = cursor1.getString(cursor1
								.getColumnIndex("mimetype"));
						if ("vnd.android.cursor.item/name".equals(mimeType)) { // 是姓名
							bean.setName(data1);
						} else if ("vnd.android.cursor.item/phone_v2"
								.equals(mimeType)) { // 手机
							bean.setTelephone(data1);
						}
					}
					cursor1.close();
					if (bean.getTelephone() != null
							&& bean.getTelephone().length() == 11) {
						bean.setInBlackList(BlackListUtils.isNumberInBlackList(
								AllContactActivity.this, bean.getTelephone()));
						Message msg = handler
								.obtainMessage(WHAT_GET_ONE_PERSION);
						msg.obj = bean;
						handler.sendMessage(msg);
					}

					Log.i("ttt", bean.getName() + "," + bean.getTelephone()
							+ ",");
				}
				cursor.close();
			}
		}).start();

	}
	
	private void loadADView(){
		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		// Create ad view
		mAdview = new AdView(this, Constants.DOMOB_PUBLIC_ID, Constants.DOMOB_INLINE_ID);
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
