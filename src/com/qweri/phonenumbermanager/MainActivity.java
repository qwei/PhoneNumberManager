package com.qweri.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import cn.domob.android.ads.AdEventListener;
import cn.domob.android.ads.AdView;
import cn.domob.android.ads.InterstitialAd;
import cn.domob.android.ads.InterstitialAdListener;
import cn.domob.android.ads.AdManager.ErrorCode;
import cn.dow.android.DOW;
import cn.dow.android.listener.DLoadListener;

import com.qweri.phonenumbermanager.adapter.BlackListViewAdapter;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.cardemulation.OffHostApduService;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private TextView hitText1,hitText2;
	private EditText blockNumber;
	private Button blockBtn, allNumbersBtn,callLogsBtn;
	RelativeLayout mAdContainer;
	AdView mAdview;
	private ListView blackListView;
	private BlackListViewAdapter blackListViewAdapter;
	private ImageView mOffWall;
	
	private List<String> blackList;
	
	InterstitialAd mInterstitialAd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTheme(R.style.Theme_AppCompat);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getSupportActionBar().setBackgroundDrawable((Drawable)(new ColorDrawable(Color.parseColor("#66555555"))));
		initView();
		initDoMobAd();
		initOffWall();
		
		Intent service = new Intent(this, InterceptService.class);
		startService(service);
		if(getIntent().getBooleanExtra(InterceptService.EXTRA_CLICKED_NOTIFY, false)) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(1);
		}
		
	}

	private void initDoMobAd() {
		//插屏
		mInterstitialAd = new InterstitialAd(this, Constants.DOMOB_PUBLIC_ID,
				Constants.DOMOB_INTERSITAL_ID);
		mInterstitialAd.setInterstitialAdListener(new InterstitialAdListener() {
			@Override
			public void onInterstitialAdReady() {
				Log.i("DomobSDKDemo", "onAdReady");
			}

			@Override
			public void onLandingPageOpen() {
				Log.i("DomobSDKDemo", "onLandingPageOpen");
			}

			@Override
			public void onLandingPageClose() {
				Log.i("DomobSDKDemo", "onLandingPageClose");
			}

			@Override
			public void onInterstitialAdPresent() {
				Log.i("DomobSDKDemo", "onInterstitialAdPresent");
			}

			@Override
			public void onInterstitialAdDismiss() {
				// Request new ad when the previous interstitial ad was closed.
				mInterstitialAd.loadInterstitialAd();
				Log.i("DomobSDKDemo", "onInterstitialAdDismiss");
			}

			@Override
			public void onInterstitialAdFailed(ErrorCode arg0) {
				Log.i("DomobSDKDemo", "onInterstitialAdFailed");				
			}

			@Override
			public void onInterstitialAdLeaveApplication() {
				Log.i("DomobSDKDemo", "onInterstitialAdLeaveApplication");
				
			}

			@Override
			public void onInterstitialAdClicked(InterstitialAd arg0) {
				Log.i("DomobSDKDemo", "onInterstitialAdClicked");
			}
		});
		
		mInterstitialAd.loadInterstitialAd();
		
		
		//Banner
		mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
		// Create ad view
		mAdview = new AdView(this, Constants.DOMOB_PUBLIC_ID, Constants.DOMOB_INLINE_ID);
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
	
	private void initOffWall() {
		DOW.getInstance(this).init("userid", new DLoadListener() {

			@Override
			public void onSuccess() {
				Log.v(TAG, "积分墙初始化完成");
			}

			@Override
			public void onStart() {
				Log.v(TAG, "积分墙初始化开始");
			}

			@Override
			public void onLoading() {
				Log.v(TAG, "积分墙初始化中...");
			}

			@Override
			public void onFail() {
				Log.v(TAG, "积分墙初始化失败");
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private Menu mMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		MenuItem m1 = mMenu.add(0, 1, 0, "setting").setIcon(R.drawable.menu_settings);
		MenuItemCompat
				.setShowAsAction(m1, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == 1) {
			Intent settingIntent = new Intent(MainActivity.this,SettingActivity.class);
			startActivity(settingIntent);
		}
		return super.onOptionsItemSelected(item);
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

	@SuppressLint("NewApi")
	private void initView() {
		Typeface robotoRegularFace = Typeface.createFromAsset(getAssets(),
				"Roboto-Regular.ttf");
		blackListView = (ListView) findViewById(R.id.black_list_view);
		blockNumber = (EditText) findViewById(R.id.block_number_editview);
		blockBtn = (Button) findViewById(R.id.add_number);
		allNumbersBtn = (Button) findViewById(R.id.all_numbers);
		callLogsBtn = (Button) findViewById(R.id.call_logs_btn);
		hitText1 = (TextView) findViewById(R.id.hit_text1);
		hitText2 = (TextView) findViewById(R.id.hit_text2);
		hitText1.setTypeface(robotoRegularFace);
		hitText2.setTypeface(robotoRegularFace);

		blockBtn.setOnClickListener(this);
		allNumbersBtn.setOnClickListener(this);
		callLogsBtn.setOnClickListener(this);

		blackListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				showDeleteDialog(arg2);
				return false;
			}
		});
		
		mOffWall = (ImageView) findViewById(R.id.off_wall);
		if(Build.VERSION.SDK_INT > 10) {
			final ObjectAnimator up = ObjectAnimator.ofFloat(mOffWall, "translationY", -100f);
			final ObjectAnimator down = ObjectAnimator.ofFloat(mOffWall, "translationY", 0f);
			down.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					up.start();
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					
				}
			});
			up.addListener(new AnimatorListener() {
				
				@Override
				public void onAnimationStart(Animator animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animator animation) {
					
				}
				
				@Override
				public void onAnimationEnd(Animator animation) {
					down.start();
				}
				
				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}
			});
			AnimatorSet set = new AnimatorSet();
			set.setDuration(600);
			set.play(up).before(down);
			set.start();
		}
		
		mOffWall.setOnClickListener(this);
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

	private boolean isValidNumber(String number) {
//		Log.d("ttttt", number);
		if(number == null || number.length() != 11)
			return true;
		for(int i = 0; i< number.length(); i++) {
			
			int c = Integer.parseInt(number.charAt(i)+"");
//			Log.d("ttttt", c+"+++");
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
		case R.id.call_logs_btn:
			Intent callLogIntent = new Intent(MainActivity.this,
					CallLogsActivity.class);
			startActivity(callLogIntent);
			break;
			
		case R.id.off_wall:
			DOW.getInstance(this).show(this);
			break;
		}
	}
	private boolean isShowedAd = false;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(isShowedAd) {
			return super.onKeyDown(keyCode, event);
		}
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if (mInterstitialAd.isInterstitialAdReady()){
				mInterstitialAd.showInterstitialAd(MainActivity.this);
				isShowedAd = true;
			} else {
				Log.i("DomobSDKDemo", "Interstitial Ad is not ready");
				mInterstitialAd.loadInterstitialAd();
				finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}
