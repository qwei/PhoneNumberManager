package com.qweri.phonenumbermanager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.qweri.phonenumbermanager.utils.StatusBarUtil;
import com.qweri.phonenumbermanager.widget.NavigationTabStrip;
import com.tendcloud.tenddata.TCAgent;

public class AddBlockNumberActivity extends AppCompatActivity {

    private static final String TAG = AddBlockNumberActivity.class.getName();

    private ViewPager mViewPager;
    private Fragment mCallLogFragment = null;
    private Fragment mAllContactFragment = null;
    private Fragment mManualAddFragment = null;
    private Toolbar mToolbar;
    private NavigationTabStrip mTabStrip;

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
        mTabStrip = (NavigationTabStrip) findViewById(R.id.tab_strip);

        mCallLogFragment = new CallLogsFragment();
        mAllContactFragment = new AllContactFragment();
        mManualAddFragment = new ManualAddFragment();

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mTabStrip.setViewPager(mViewPager, 1);
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
        TCAgent.onPageStart(this, TAG);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TCAgent.onPageEnd(this, TAG);
    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mManualAddFragment;
            } else if (position == 1) {
                return mAllContactFragment;
            } else {
                return mCallLogFragment;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
