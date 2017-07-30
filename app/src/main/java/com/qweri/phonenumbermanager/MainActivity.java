package com.qweri.phonenumbermanager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.qweri.phonenumbermanager.adapter.BlackListViewAdapter;
import com.qweri.phonenumbermanager.utils.StatusBarUtil;
import com.tendcloud.tenddata.TCAgent;

import java.util.ArrayList;
import java.util.List;

import er.kj.iy.AdManager;
import er.kj.iy.br.AdSize;
import er.kj.iy.br.AdView;
import er.kj.iy.st.SpotDialogListener;
import er.kj.iy.st.SpotManager;

public class MainActivity extends AppCompatActivity implements OnClickListener {

	private static final String TAG = MainActivity.class.getSimpleName();

	private ListView blackListView;
	private BlackListViewAdapter blackListViewAdapter;
	private ImageView mAdd;
	private Toolbar mToolbar;
	private ImageView mEmptyView;
	private List<ItemBean> blackList;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color));
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle(getString(R.string.blocked_number_des));// 标题的文字需在setSupportActionBar之前，不然会无效
		setSupportActionBar(mToolbar);
		initView();
		
		Intent service = new Intent(this, InterceptService.class);
		startService(service);
		if(getIntent().getBooleanExtra(InterceptService.EXTRA_CLICKED_NOTIFY, false)) {
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(NotifyManager.NOTIFICATION_ID_ALART);
		}
		AdManager.getInstance(this).init("a6c6d83fa417ae12", "c0608df0ff8cd5ec", false);
		initDuoMiAd();
		loadADView();
		NotifyManager.showKeepAliveNotification(this.getApplicationContext());
	}

	private void initDuoMiAd() {
		SpotManager.getInstance(this).loadSpotAds();
		SpotManager.getInstance(this).showSpotAds(this);
		SpotManager.getInstance(this).showSpotAds(this, new SpotDialogListener() {
		    @Override
		    public void onShowSuccess() {
		        Log.i("Youmi", "onShowSuccess");
		    }

		    @Override
		    public void onShowFailed() {
		        Log.i("Youmi", "onShowFailed");
		    }

		    @Override
		    public void onSpotClosed() {
		        Log.e("sdkDemo", "closed");
		    }

            @Override
            public void onSpotClick(boolean isWebPath) {
                Log.i("YoumiAdDemo", "插屏点击");
            }
		});
	}
	
	private void loadADView(){
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);

		// 获取要嵌入广告条的布局
		LinearLayout adLayout=(LinearLayout)findViewById(R.id.adLayout);

		// 将广告条加入到布局中
		adLayout.addView(adView);

	}

	@Override
	protected void onStop() {
		super.onStop();
		TCAgent.onPageEnd(this, TAG);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		Log.d("ttt", "ondestory");
		Intent intent = new Intent(this, CoreService.class);
		startService(intent);
	}

	private Menu mMenu;
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		mMenu = menu;
		MenuItem m1 = mMenu.add(0, 1, 0, "").setIcon(R.drawable.tips);
		MenuItem m2 = mMenu.add(0, 2, 0, "").setIcon(R.drawable.delete);
		MenuItem m3 = mMenu.add(0, 3, 0, "").setIcon(R.drawable.menu_settings);
		MenuItemCompat
				.setShowAsAction(m1, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		MenuItemCompat
				.setShowAsAction(m2, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		MenuItemCompat
				.setShowAsAction(m3, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 1:
				Intent tipsIntent = new Intent(MainActivity.this,TipsActivity.class);
				startActivity(tipsIntent);
				break;
			case 2:
				new AlertDialog.Builder(this).setMessage("要清空所有纪录？").setPositiveButton("是", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						BlackListUtils.resetNumber(MainActivity.this);
						if(blackList != null) {
							blackList.clear();
							if(blackListViewAdapter != null) {
								blackListViewAdapter.notifyDataSetChanged();
							}
						}

					}

				}).setNegativeButton("否", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				}).create().show();
				break;
			case 3:
				Intent settingIntent = new Intent(MainActivity.this,SettingActivity.class);
				startActivity(settingIntent);
				break;
		}

		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		blackList = getBlackList();
		blackListViewAdapter = new BlackListViewAdapter(this, blackList);
		blackListView.setAdapter(blackListViewAdapter);
		super.onResume();
		TCAgent.onPageStart(this, TAG);
	}

	@SuppressLint("NewApi")
	private void initView() {
		mEmptyView = (ImageView) findViewById(R.id.empty_view);
		blackListView = (ListView) findViewById(R.id.black_list_view);

		blackListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
										   int arg2, long arg3) {
				showDeleteDialog(arg2);
				return false;
			}
		});
		blackListView.setEmptyView(mEmptyView);
		mAdd = (ImageView) findViewById(R.id.add);
		mAdd.setOnClickListener(this);
		
	}
	
	private void showDeleteDialog(final int position) {
		new AlertDialog.Builder(this)
				.setMessage("不拦截此号码了？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						BlackListUtils.deleteNumber(MainActivity.this,
								blackList.get(position).number);
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

	private List<ItemBean> getBlackList() {
		List<ItemBean> result = new ArrayList<ItemBean>();
		String blackListString = BlackListUtils.getBlackListNumbers(this);

		String[] blackList = blackListString.split(BlackListUtils.SPLIT);
		
		for(String str:blackList) {
			if(!"".equals(str)) {
				ItemBean itemBean = new ItemBean();
				itemBean.number = str;
				itemBean.name = CallLogsFragment.getContactNameByPhoneNumber(this, str);
				result.add(itemBean);
			}
		}
		return result;
	}

	public class ItemBean {
		public  String number;
		public String name;
	}
	
	
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
//		case R.id.add_number:
//			String number = blockNumber.getText().toString();
//			if (!isValidNumber(number)) {
//				BlackListUtils.addNumber(MainActivity.this, number);
//				ItemBean bean = new ItemBean();
//				bean.number = number;
//				bean.name = CallLogsFragment.getContactNameByPhoneNumber(this, number);
//				blackList.add(bean);
//				blackListViewAdapter.notifyDataSetChanged();
//				Toast.makeText(MainActivity.this, "添加成功！", Toast.LENGTH_SHORT)
//						.show();
//			} else {
//				Toast.makeText(MainActivity.this, "请输入正确的手机号码！",
//						Toast.LENGTH_SHORT).show();
//			}
//			break;
		case R.id.add:
			Intent intent = new Intent(MainActivity.this,
					AddBlockNumberActivity.class);
			startActivity(intent);
			break;
//		case R.id.call_logs_btn:
//			Intent callLogIntent = new Intent(MainActivity.this,
//					CallLogsActivity.class);
//			startActivity(callLogIntent);
//			break;

//		case R.id.off_wall:
//			DOW.getInstance(this).show(this);
//			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		// 如果有需要，可以点击后退关闭插播广告。
	    if (!SpotManager.getInstance(this).disMiss()) {
	        // 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
	        super.onBackPressed();
	    }
	}
	private boolean isShowedAd = false;

	
	
}
