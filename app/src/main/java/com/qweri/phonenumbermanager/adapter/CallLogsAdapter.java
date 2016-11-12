package com.qweri.phonenumbermanager.adapter;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.qweri.phonenumbermanager.BlackListUtils;
import com.qweri.phonenumbermanager.CallLogBean;
import com.qweri.phonenumbermanager.ContactBean;
import com.qweri.phonenumbermanager.R;
import com.qweri.phonenumbermanager.R.drawable;
import com.qweri.phonenumbermanager.R.id;
import com.qweri.phonenumbermanager.R.layout;

import android.content.Context;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallLogsAdapter extends BaseAdapter {

	private static final String TAG = CallLogsAdapter.class.getName();
	private Context context;
	private List<CallLogBean> callLogs = null;
	public CallLogsAdapter(Context context, List<CallLogBean> callLogs) {
		this.context = context;
		this.callLogs = callLogs;
	}
	@Override
	public int getCount() {
		
		return callLogs.size();
	}

	@Override
	public Object getItem(int position) {
		return callLogs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null) {
			viewHolder = new ViewHolder();
			convertView = View.inflate(context, layout.call_log_item, null);
			viewHolder.stopImage = (ImageView)convertView.findViewById(id.stop_image);
			viewHolder.name = (TextView)convertView.findViewById(id.name);
			viewHolder.telephone = (TextView) convertView.findViewById(id.telephone);
			viewHolder.time = (TextView) convertView.findViewById(id.log_time);
			viewHolder.typeView = (TextView) convertView.findViewById(id.type);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final CallLogBean callLogBean = callLogs.get(position);
		Log.d(TAG, "name: " + callLogBean.getName() + ", number: " + callLogBean.getTelephone());
//		if(callLogBean.getName() == null) {
//			viewHolder.name.setVisibility(View.GONE);	
//		} else {
//			viewHolder.name.setVisibility(View.VISIBLE);
//			
//		}
		switch (callLogBean.getType()) {                 
		case Calls.INCOMING_TYPE:                                                                        
			viewHolder.typeView.setText("呼入");
			break;                                                                                       
		case Calls.OUTGOING_TYPE:              
			viewHolder.typeView.setText("呼出");
			break;                                                                                       
		case Calls.MISSED_TYPE:                
			viewHolder.typeView.setText("未接");
			break;                                                                                       
		default:                               
			viewHolder.typeView.setText("挂断");
			break;                                                                                       
		}
		if (callLogBean.getName() == null || "".equals(callLogBean.getName())) {
			viewHolder.name.setVisibility(View.GONE);
		} else {
			viewHolder.name.setVisibility(View.VISIBLE);
			viewHolder.name.setText(callLogBean.getName());
		}

		viewHolder.telephone.setText(callLogBean.getTelephone());
		viewHolder.time.setText(callLogBean.getTime());
		
		if(callLogBean.isInBlackList()) {
			viewHolder.stopImage.setImageResource(drawable.blocked);
		} else {
			viewHolder.stopImage.setImageResource(drawable.not_blocked);
		}
		viewHolder.stopImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				callLogBean.setInBlackList(!callLogBean.isInBlackList());
				if(callLogBean.isInBlackList()) {
					BlackListUtils.addNumber(context, callLogBean.getTelephone());
				} else {
					BlackListUtils.deleteNumber(context, callLogBean.getTelephone());
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	public void add(CallLogBean bean) {
//		int index = Collections.binarySearch(callLogs, bean,new Comparator<CallLogBean>() {
//			Collator collator = Collator.getInstance();
//			@Override
//			public int compare(CallLogBean lhs, CallLogBean rhs) {
//				CollationKey key1 = collator  
//	                    .getCollationKey(lhs.getTelephone());  
//	            CollationKey key2 = collator  
//	                    .getCollationKey(rhs.getTelephone()); 
//				return key1.compareTo(key2);
//			}
//		});
//		if(index < 0) {
//			index = -(index + 1);
//		}
		callLogs.add(bean);
		this.notifyDataSetChanged();
	}
	
	
	public int compare(ContactBean bean1, ContactBean bean2) {
		String name1 = bean1.getName();
		String name2 = bean2.getName();
		if(name2 == null)
			return 0;
		return name1.compareTo(name2);
	}
	class ViewHolder {
		ImageView stopImage;
		TextView name,telephone,time, typeView;
	}
}
