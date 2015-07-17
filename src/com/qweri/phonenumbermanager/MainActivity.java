package com.qweri.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

	private LinearLayout hit;
	private TextView hitText1,hitText2;
	private EditText blockNumber;
	private Button blockBtn, allNumbersBtn;
	RelativeLayout mAdContainer;
	private ListView blackListView;
	private BlackListViewAdapter blackListViewAdapter;
	AdView mAdview;
	private List<String> blackList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		loadADView();
		
		Intent service = new Intent(this, InterceptService.class);
		startService(service);
		if(getIntent().getBooleanExtra(InterceptService.EXTRA_CLICKED_NOTIFY, false)) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(1);
		}
	}

	@Override
	protected void onResume() {
		blackList = getBlackList();
		blackListViewAdapter = new BlackListViewAdapter(this, blackList);
		blackListView.setAdapter(blackListViewAdapter);
//		if (blackList.size() == 0) {
//			hit.setText("没有拦截号码");
//		} else {
//			hit.setText("拦截的号码");
//		}
		super.onResume();
	}

	private void initView() {
		Typeface robotoRegularFace = Typeface.createFromAsset(getAssets(),
				"Roboto-Regular.ttf");
		blackListView = (ListView) findViewById(R.id.black_list_view);
		blockNumber = (EditText) findViewById(R.id.block_number);
		blockBtn = (Button) findViewById(R.id.add_number);
		allNumbersBtn = (Button) findViewById(R.id.all_numbers);
		hit = (LinearLayout) findViewById(R.id.hit);
		hitText1 = (TextView) findViewById(R.id.hit_text1);
		hitText2 = (TextView) findViewById(R.id.hit_text2);
		hitText1.setTypeface(robotoRegularFace);
		hitText2.setTypeface(robotoRegularFace);

		blockBtn.setOnClickListener(this);
		allNumbersBtn.setOnClickListener(this);

		blackListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				showDeleteDialog(arg2);
				return false;
			}
		});
	}

	
	
	private void showDeleteDialog(final int position) {
		new AlertDialog.Builder(this)
				.setMessage("不拦截此号码了？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						BlackListUtils.deleteNumber(MainActivity.this,
								blackList.get(position));
						blackList.remove(position);
						blackListViewAdapter.notifyDataSetChanged();
						dialog.dismiss();
					}

				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				}).show();
	}

	private List<String> getBlackList() {
		List<String> result = new ArrayList<String>();
		String blackListString = BlackListUtils.getBlackListNumbers(this);

		String[] blackList = blackListString.split(BlackListUtils.SPLIT);
		
		for(String str:blackList) {
			if(!"".equals(str)) {
				result.add(str);
			}
		}
		return result;
	}

	private void loadADView() {
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

	private boolean isValidNumber(String number) {
		Log.d("ttttt", number);
		if(number == null || number.length() != 11)
			return true;
		for(int i = 0; i< number.length(); i++) {
			
			int c = Integer.parseInt(number.charAt(i)+"");
			Log.d("ttttt", c+"+++");
			if(c > 10 || c < 0) {
				return true;
			}
		}
		return false;
	}
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.add_number:
			String number = blockNumber.getText().toString();
			if (!isValidNumber(number)) {
				BlackListUtils.addNumber(MainActivity.this, number);
				blackList.add(number);
				blackListViewAdapter.notifyDataSetChanged();
				Toast.makeText(MainActivity.this, "添加成功！", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(MainActivity.this, "请输入正确的手机号码！",
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.all_numbers:
			Intent intent = new Intent(MainActivity.this,
					AllContactActivity.class);
			startActivity(intent);
			break;
		}
	}
}
