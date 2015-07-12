package com.qweri.phonenumbermanager;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import cn.domob.android.ads.AdEventListener;
import cn.domob.android.ads.AdManager.ErrorCode;
import cn.domob.android.ads.AdView;

public class MainActivity extends Activity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private static final String DOMOB_PUBLIC_ID = "56OJxqJouN2zT9GFmy";
	private static final String DOMOB_INLINE_ID = "16TLejFvApq3cNUvs9Jg-QMs";
	

	private TextView des,result;
	private EditText blockNumber;
	private Button blockBtn, cancel,allNumbersBtn;
	RelativeLayout mAdContainer;
	AdView mAdview;
	StringBuilder stringBuilder = new StringBuilder();
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		loadADView();
	}

	private void initView() {
		blockNumber = (EditText) findViewById(R.id.block_number);
		des = (TextView) findViewById(R.id.text);
		blockBtn = (Button) findViewById(R.id.btnEnable);
		cancel = (Button) findViewById(R.id.cancel);
		allNumbersBtn = (Button) findViewById(R.id.all_numbers);
		result = (TextView) findViewById(R.id.result);
		
		blockBtn.setOnClickListener(this);
		cancel.setOnClickListener(this);
		allNumbersBtn.setOnClickListener(this);

		if (BlackListUtils.getBlackListNumbers(this).equals("")) {
			blockNumber.setVisibility(View.VISIBLE);
			blockBtn.setVisibility(View.VISIBLE);
		} else {
			des.setText("已经拦截的号码是：" + BlackListUtils.getBlackListNumbers(this));
			blockNumber.setVisibility(View.GONE);
			blockBtn.setVisibility(View.GONE);
		}
	}
	
	private void loadADView(){
		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		// Create ad view
		mAdview = new AdView(this, DOMOB_PUBLIC_ID, DOMOB_INLINE_ID);
		mAdview.setAdEventListener(new AdEventListener() {
			@Override
			public void onAdOverlayPresented(AdView adView) {
			}

			@Override
			public void onAdOverlayDismissed(AdView adView) {
			}

			@Override
			public void onAdClicked(AdView arg0) {
			}

			@Override
			public void onLeaveApplication(AdView arg0) {
			}

			@Override
			public Context onAdRequiresCurrentContext() {
				return MainActivity.this;
			}

			@Override
			public void onAdFailed(AdView arg0, ErrorCode arg1) {
			}

			@Override
			public void onEventAdReturned(AdView arg0) {
			}
		});
		RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layout.addRule(RelativeLayout.CENTER_HORIZONTAL);
		mAdview.setLayoutParams(layout);
		mAdContainer.addView(mAdview);
	}

	
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.btnEnable:
			String number = blockNumber.getText().toString();
			if (number != null && number.length() == 11) {
				BlackListUtils.addNumber(MainActivity.this, number);
				des.setText("已经拦截的号码是："
						+ BlackListUtils.getBlackListNumbers(MainActivity.this));
				blockNumber.setVisibility(View.GONE);
				blockBtn.setVisibility(View.GONE);
				Toast.makeText(MainActivity.this, "添加成功！", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(MainActivity.this, "请输入正确的手机号码！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.cancel:
			BlackListUtils.resetNumber(MainActivity.this);
			des.setText("请输入想要拦截的号码：");
			blockNumber.setText("");
			blockNumber.setVisibility(View.VISIBLE);
			blockBtn.setVisibility(View.VISIBLE);

			Toast.makeText(MainActivity.this, "删除成功！", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.all_numbers:
			Intent intent = new Intent(MainActivity.this, AllContactActivity.class);
			startActivity(intent);
			break;
		}
	}
}
