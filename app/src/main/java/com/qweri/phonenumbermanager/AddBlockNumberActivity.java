package com.qweri.phonenumbermanager;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.qweri.phonenumbermanager.utils.StatusBarUtil;
import com.tendcloud.tenddata.TCAgent;

import er.kj.iy.br.AdSize;
import er.kj.iy.br.AdView;

public class AddBlockNumberActivity extends AppCompatActivity{

	private static final String TAG = AddBlockNumberActivity.class.getName();

	private ViewPager mViewPager;
	private String[] mTitleList = new String[3];
	private Fragment mCallLogFragment = null;
	private Fragment mAllContactFragment = null;
	private Fragment mManualAddFragment = null;
	private Toolbar mToolbar;
	private PagerTabStrip mTabStrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_block_number);
		StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color));
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle(getString(R.string.add_block_number));// 标题的文字需在setSupportActionBar之前，不然会无效
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mTabStrip = (PagerTabStrip) findViewById(R.id.page_tab_strip);
		mTabStrip.setTabIndicatorColor(Color.WHITE);
		mTabStrip.setTextColor(Color.WHITE);
		mTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mTitleList[1] = "通讯录";
		mTitleList[2] = "历史记录";
		mTitleList[0] = "手动添加";
		mCallLogFragment = new CallLogsFragment();
		mAllContactFragment = new AllContactFragment();
		mManualAddFragment = new ManualAddFragment();
		
		mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setCurrentItem(1);
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
		// TODO Auto-generated method stub
		super.onResume();
		TCAgent.onPageStart(this, TAG);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		  TCAgent.onPageEnd(this, TAG);
	}
	
	class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			if(position == 0) {
				return mManualAddFragment;
			} else if(position == 1){
				return mAllContactFragment;
			} else {
				return mCallLogFragment;
			}
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return mTitleList[position];
		}
		
	}

}
