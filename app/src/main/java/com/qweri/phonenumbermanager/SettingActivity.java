package com.qweri.phonenumbermanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qweri.phonenumbermanager.utils.SharedPreferenceUtils;
import com.qweri.phonenumbermanager.utils.StatusBarUtil;
import com.tendcloud.tenddata.TCAgent;

public class SettingActivity extends AppCompatActivity implements OnCheckedChangeListener, OnClickListener{

	private static final String TAG = SettingActivity.class.getName();
	private CheckBox interceptAll,interceptUnknow, autoBlock, notificationCheckout;
	private TextView share,about, feedback;
	private Context context;
	private LinearLayout mVoiceSettingLayout;
	private TextView mVoiceView;
	private Toolbar mToolbar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.setting);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle(context.getString(R.string.setting));// 标题的文字需在setSupportActionBar之前，不然会无效
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color));
		interceptAll = (CheckBox) findViewById(R.id.intercept_all_checkbox);
		interceptUnknow = (CheckBox) findViewById(R.id.intercept_unknow_checkbox);
		mVoiceSettingLayout = (LinearLayout) findViewById(R.id.voice_setting_layout);
		mVoiceSettingLayout.setOnClickListener(this);
		mVoiceView = (TextView) findViewById(R.id.voice_view);
		autoBlock = (CheckBox) findViewById(R.id.auto_block_checkbox);
		notificationCheckout = (CheckBox) findViewById(R.id.notification_checkbox);
		notificationCheckout.setOnCheckedChangeListener(this);
		notificationCheckout.setChecked(SharedPreferenceUtils.isShowAliveNotification(context));
		
		
		share = (TextView) findViewById(R.id.share);
		about = (TextView) findViewById(R.id.about);
		feedback = (TextView) findViewById(R.id.feedback);
		interceptAll.setChecked(BlackListUtils.isInterceptAllNumber(context));
		interceptUnknow.setChecked(BlackListUtils.isInterceptUnknow(context));
		autoBlock.setChecked(BlackListUtils.isAutoBlockOpen(context));
		interceptAll.setOnCheckedChangeListener(this);
		interceptUnknow.setOnCheckedChangeListener(this);
		autoBlock.setOnCheckedChangeListener(this);
		
		share.setOnClickListener(this);
		about.setOnClickListener(this);
		feedback.setOnClickListener(this);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TCAgent.onPageStart(this, TAG);
		if(mVoiceView != null) {
			mVoiceView.setText(SetReturnVoiceActivity.
					sServiceDescList[SetReturnVoiceActivity.getIndexInServiceList(BlackListUtils.getReturnVoice(this))]);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		TCAgent.onPageEnd(this, TAG);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int viewId = buttonView.getId();
		switch(viewId) {
		case R.id.intercept_all_checkbox:
			BlackListUtils.setInterceptAllNumber(context, isChecked);
			break;
		case R.id.intercept_unknow_checkbox:
			BlackListUtils.setInterceptUnknow(context, isChecked);
			break;
		case R.id.auto_block_checkbox:
			BlackListUtils.setAutoBlockOpen(context, isChecked);
			break;
		case R.id.notification_checkbox:
			SharedPreferenceUtils.setShowAliveNotification(context, isChecked);
			if(isChecked) {
				NotifyManager.showKeepAliveNotification(context);
			} else {
				NotifyManager.cancelNotification(context, NotifyManager.NOTIFICATION_ID_ALIVE);
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.about:
			about();
			break;
		case R.id.share:
			share();
			break;
		case R.id.feedback:
			feedback(getApplicationContext());
			break;
		case R.id.voice_setting_layout:
			Intent intent = new Intent(this, SetReturnVoiceActivity.class);
			startActivity(intent);
			break;
		}
	}
	
	private void about() {
		new AlertDialog.Builder(context).setMessage("V "+getString(R.string.versionName)).setNeutralButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		}).create().show();
	}
	
	private void share() {
		Intent intent=new Intent(Intent.ACTION_SEND);   
        intent.setType("text/plain");   
        intent.putExtra(Intent.EXTRA_SUBJECT, "来电拦截");   
        intent.putExtra(Intent.EXTRA_TEXT, "<来电拦截>小巧强大，支持从通讯录和来电纪录中添加号码。http://a.app.qq.com/o/simple.jsp?pkgname=com.qweri.phonenumbermanager");    
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   
        startActivity(Intent.createChooser(intent, getTitle())); 
	}
	
	private void feedback(Context context) {
		try {
			Intent intentFeedback = new Intent(Intent.ACTION_SENDTO);
			intentFeedback.setData(Uri.parse("mailto:" + "hello0370@126.com"));
			intentFeedback.putExtra(Intent.EXTRA_SUBJECT,
					context.getString(R.string.app_name) + " - " + "反馈");
			intentFeedback.putExtra(Intent.EXTRA_TEXT, "Device:" + android.os.Build.MODEL + "\nAndroid OS:"
					+ android.os.Build.VERSION.RELEASE + "\n\n" + "version: "
					+ getResources().getString(R.string.versionName) + "\n\n");
			intentFeedback.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intentFeedback);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
		}
	}

}
