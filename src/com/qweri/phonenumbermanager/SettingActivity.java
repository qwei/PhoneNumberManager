package com.qweri.phonenumbermanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends ActionBarActivity implements OnCheckedChangeListener, OnClickListener{

	private CheckBox interceptAll,interceptUnknow;
	private TextView clearDataView, share,about, feedback;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat);
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.setting);
		getSupportActionBar().setTitle("设置");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setBackgroundDrawable((Drawable)(new ColorDrawable(Color.parseColor("#66555555"))));
//		getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("F0F0F0")));
		interceptAll = (CheckBox) findViewById(R.id.intercept_all_checkbox);
		interceptUnknow = (CheckBox) findViewById(R.id.intercept_unknow_checkbox);
//		incomingFeedback = (TextView) findViewById(R.id.incoming_feedback);
		share = (TextView) findViewById(R.id.share);
		about = (TextView) findViewById(R.id.about);
		feedback = (TextView) findViewById(R.id.feedback);
		clearDataView = (TextView) findViewById(R.id.clear_data);
		interceptAll.setChecked(BlackListUtils.isInterceptAllNumber(context));
		interceptUnknow.setChecked(BlackListUtils.isInterceptUnknow(context));
		interceptAll.setOnCheckedChangeListener(this);
		interceptUnknow.setOnCheckedChangeListener(this);
		
		clearDataView.setOnClickListener(this);
		share.setOnClickListener(this);
		about.setOnClickListener(this);
		feedback.setOnClickListener(this);
		
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
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.clear_data:
			clearData();
			break;
		case R.id.about:
			about();
			break;
		case R.id.share:
			share();
			break;
		case R.id.feedback:
			feedback(getApplicationContext());
			break;
		}
	}
	
	private void about() {
		new AlertDialog.Builder(context).setMessage("V "+getString(R.string.version_name)).setNeutralButton("确定", new DialogInterface.OnClickListener(){

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
			intentFeedback.putExtra(Intent.EXTRA_TEXT, "Device:" + android.os.Build.MODEL + "\nAndroid OS:" + android.os.Build.VERSION.RELEASE + "\n\n");
			intentFeedback.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intentFeedback);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void clearData() {
		new AlertDialog.Builder(context).setMessage("要清空所有纪录？").setPositiveButton("是", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				BlackListUtils.resetNumber(context);
			}
			
		}).setNegativeButton("否", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
			
		}).create().show();
	}
}
