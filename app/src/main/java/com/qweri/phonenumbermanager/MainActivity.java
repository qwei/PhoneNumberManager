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
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.qweri.phonenumbermanager.adapter.BlackListViewAdapter;
import com.qweri.phonenumbermanager.utils.StatusBarUtil;
import com.tendcloud.tenddata.TCAgent;

import net.youmi.android.nm.cm.ErrorCode;
import net.youmi.android.nm.sp.SplashViewSettings;
import net.youmi.android.nm.sp.SpotListener;
import net.youmi.android.nm.sp.SpotManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private ListView blackListView;
    private BlackListViewAdapter blackListViewAdapter;
    private ImageView mAdd;
    private Toolbar mToolbar;
    private ImageView mEmptyView;
    private List<ItemBean> blackList;
    boolean mIsShowSpotAd = false;


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
        if (getIntent().getBooleanExtra(InterceptService.EXTRA_CLICKED_NOTIFY, false)) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(NotifyManager.NOTIFICATION_ID_ALART);
        }
        NotifyManager.showKeepAliveNotification(this.getApplicationContext());
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
        setButtonAnim(mAdd);
    }

    private void setButtonAnim(View view){
        final Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
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

        for (String str : blackList) {
            if (!"".equals(str)) {
                ItemBean itemBean = new ItemBean();
                itemBean.number = str;
                itemBean.name = CallLogsFragment.getContactNameByPhoneNumber(this, str);
                result.add(itemBean);
            }
        }
        return result;
    }

    public class ItemBean {
        public String number;
        public String name;
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.add:
                v.clearAnimation();
                if (mIsShowSpotAd) {
                    Intent intent = new Intent(MainActivity.this,
                            AddBlockNumberActivity.class);
                    startActivity(intent);
                } else {
                    setupSpotAd();
                    mIsShowSpotAd = true;
                }
                break;
        }
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
                Intent tipsIntent = new Intent(MainActivity.this, TipsActivity.class);
                startActivity(tipsIntent);
                break;
            case 2:
                new AlertDialog.Builder(this).setMessage("要清空所有纪录？").setPositiveButton("是", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BlackListUtils.resetNumber(MainActivity.this);
                        if (blackList != null) {
                            blackList.clear();
                            if (blackListViewAdapter != null) {
                                blackListViewAdapter.notifyDataSetChanged();
                            }
                        }

                    }

                }).setNegativeButton("否", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).create().show();
                break;
            case 3:
                Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
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

    @Override
    protected void onPause() {
        super.onPause();
        // 插屏广告
        SpotManager.getInstance(this).onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        TCAgent.onPageEnd(this, TAG);
        // 插屏广告
        SpotManager.getInstance(this).onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 插屏广告
        SpotManager.getInstance(this).onDestroy();
        SpotManager.getInstance(this).onAppExit();
        Intent intent = new Intent(this, CoreService.class);
        startService(intent);
    }

    /**
     * 设置插屏广告
     */
    private void setupSpotAd() {
        // 设置插屏图片类型，默认竖图
        SpotManager.getInstance(this).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);
        // 高级动画
        SpotManager.getInstance(this)
                .setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);

        // 展示插屏广告
        SpotManager.getInstance(this).showSpot(this, new SpotListener() {

            @Override
            public void onShowSuccess() {
                Log.i("YouMi", "插屏展示成功");
            }

            @Override
            public void onShowFailed(int errorCode) {
                Log.i("YouMi", "插屏展示失败");
                switch (errorCode) {
                    case ErrorCode.NON_NETWORK:
                        Log.i("YouMi", "网络异常");
                        break;
                    case ErrorCode.NON_AD:
                        Log.i("YouMi", "暂无插屏广告");
                        break;
                    case ErrorCode.RESOURCE_NOT_READY:
                        Log.i("YouMi", "插屏资源还没准备好");
                        break;
                    case ErrorCode.SHOW_INTERVAL_LIMITED:
                        Log.i("YouMi", "请勿频繁展示");
                        break;
                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                        Log.i("YouMi", "请设置插屏为可见状态");
                        break;
                    default:
                        Log.i("YouMi", "请稍后再试");
                        break;
                }
            }

            @Override
            public void onSpotClosed() {
                Log.i("YouMi", "插屏被关闭");
            }

            @Override
            public void onSpotClicked(boolean isWebPage) {
                Log.i("YouMi", "插屏被点击");
                Log.i("YouMi", "是否是网页广告？%s" + (isWebPage ? "是" : "不是"));
            }
        });
    }

    @Override
    public void onBackPressed() {
        // 点击后退关闭插屏广告
        if (SpotManager.getInstance(this).isSpotShowing()) {
            Log.i("ygx", "is spotShowing");
            SpotManager.getInstance(this).hideSpot();
        } else {
            Log.i("ygx", "back");
            super.onBackPressed();
        }
    }
}
