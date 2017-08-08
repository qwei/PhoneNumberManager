package com.qweri.phonenumbermanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.qweri.phonenumbermanager.utils.StatusBarUtil;
import com.tendcloud.tenddata.TCAgent;

public class SetReturnVoiceActivity extends AppCompatActivity {

    private static final String TAG = SetReturnVoiceActivity.class.getName();
    private Context context;
    private RadioGroup mGroup;
    private RadioButton mButton1, mButton2, mButton3, mButton4,mButton5,mButton6;
    private Toolbar mToolbar;

    public static String[] serviceNumberList = new String[]{"tel:**67*13800312309%23", "tel:**67*13800000000%23", "tel:**67*18210679767%23", "tel:%23%2367%23", "tel:**67*13810538911%23", "tel:**67*13910827493%23"};
    public static String[] sServiceDescList = new String[]{"暂时无法接通", "号码是空号", "直接挂断", "默认", "手机已关机", "手机已停机"};

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse((String) msg.obj));
            Toast.makeText(context, "客官，正在切换，稍等片刻~~", Toast.LENGTH_SHORT).show();
            startActivity(i);
            BlackListUtils.setReturnVoice(context, (String) msg.obj);
        }

        ;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.setting_return_voice);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("设置拦截提示音");// 标题的文字需在setSupportActionBar之前，不然会无效
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.main_color));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mGroup = (RadioGroup) findViewById(R.id.radio_group);
        mButton1 = (RadioButton) findViewById(R.id.button1);
        mButton2 = (RadioButton) findViewById(R.id.button2);
        mButton3 = (RadioButton) findViewById(R.id.button3);
        mButton4 = (RadioButton) findViewById(R.id.button4);
        mButton5 = (RadioButton) findViewById(R.id.button5);
        mButton6 = (RadioButton) findViewById(R.id.button6);

        String nowServiceNumber = BlackListUtils.getReturnVoice(context);

        int index = getIndexInServiceList(nowServiceNumber);
        Log.d("xxxxx", "nowServiceNumber:" + nowServiceNumber + ",index: " + index);
        switch (index) {
            case 0:
                mButton1.setChecked(true);
                break;
            case 1:
                mButton2.setChecked(true);
                break;
            case 2:
                mButton3.setChecked(true);
                break;
            case 3:
                mButton4.setChecked(true);
                break;
            case 4:
                mButton5.setChecked(true);
                break;
            case 5:
                mButton6.setChecked(true);
                break;
        }

        mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.button1:
                        mHandler.sendMessage(mHandler.obtainMessage(1, serviceNumberList[0]));
                        break;
                    case R.id.button2:
                        mHandler.sendMessage(mHandler.obtainMessage(1, serviceNumberList[1]));
                        break;
                    case R.id.button3:
                        mHandler.sendMessage(mHandler.obtainMessage(1, serviceNumberList[2]));
                        break;
                    case R.id.button4:
                        mHandler.sendMessage(mHandler.obtainMessage(1, serviceNumberList[3]));
                        break;
                    case R.id.button5:
                        mHandler.sendMessage(mHandler.obtainMessage(1, serviceNumberList[4]));
                        break;
                    case R.id.button6:
                        mHandler.sendMessage(mHandler.obtainMessage(1, serviceNumberList[5]));
                        break;
                }
            }
        });

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

    public static int getIndexInServiceList(String number) {
        for (int i = 0; i < serviceNumberList.length; i++) {
            if (serviceNumberList[i].equals(number)) {
                return i;
            }
        }
        return serviceNumberList.length - 1;
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
}
