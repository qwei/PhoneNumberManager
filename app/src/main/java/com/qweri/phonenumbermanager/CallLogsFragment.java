package com.qweri.phonenumbermanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qweri.phonenumbermanager.adapter.CallLogsAdapter;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class CallLogsFragment extends Fragment {

	private static final String TAG = CallLogsFragment.class.getName();
	private ListView callLogsListview;
	private List<CallLogBean> callLogs;
	private CallLogsAdapter callLogAdapter;
	private final static int LOAD_ONE_LOG_ID = 1101;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == LOAD_ONE_LOG_ID) {
				callLogAdapter.add((CallLogBean)msg.obj);
			}
		};
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.call_logs, null);
		callLogsListview = (ListView) view.findViewById(R.id.call_logs_list);
		callLogs = new ArrayList<CallLogBean>();
		callLogAdapter = new CallLogsAdapter(getActivity(), callLogs);
		callLogsListview.setAdapter(callLogAdapter);
		getCallLogs();
		return view;
	}
	
	private void getCallLogs() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ContentResolver resolver = getActivity().getApplicationContext().getContentResolver();
				try {
					Cursor cursor = resolver.query(Calls.CONTENT_URI, null, null, null, Calls.DEFAULT_SORT_ORDER);
					SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					while(cursor != null && cursor.moveToNext()) {
						String number = cursor.getString(cursor.getColumnIndex(Calls.NUMBER));
						Date date = new Date(Long.parseLong(cursor.getString(cursor.getColumnIndex(Calls.DATE))));
						String name = getContactNameByPhoneNumber(getActivity(), number);
						CallLogBean bean = new CallLogBean();
						bean.setType(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Calls.TYPE))));
						bean.setName(name);
						bean.setTelephone(number);
						bean.setTime(sfd.format(date));
						if(getActivity() == null) {
							return;
						}
						bean.setInBlackList(BlackListUtils.isNumberInBlackList(getActivity().getApplicationContext(), number));
//					Log.d(TAG, "name: " + name + ", number: " + bean.getTelephone() + ", time: " + bean.getTime());
						Message msg = mHandler.obtainMessage(LOAD_ONE_LOG_ID);
						msg.obj = bean;
						mHandler.sendMessage(msg);
					}
				} catch (Exception e) {
				}

			}
		}).start();
	}
	
	/*
     * 根据电话号码取得联系人姓名
     */
    public static String getContactNameByPhoneNumber(Context context, String address) {
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER };
		if(context == null || context.getContentResolver() == null) {
			return null;
		}
		try {
			// 将自己添加到 msPeers 中
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					projection, // Which columns to return.
					ContactsContract.CommonDataKinds.Phone.NUMBER + " = '"
							+ address + "'", // WHERE clause.
					null, // WHERE clause value substitution
					null); // Sort order.

			if (cursor == null) {
				Log.d(TAG, "getPeople null");
				return null;
			}
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);

				// 取得联系人名字
				int nameFieldColumnIndex = cursor
						.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
				String name = cursor.getString(nameFieldColumnIndex);
				return name;
			}
		} catch (Exception e) {

		}

        return null;
    }
}