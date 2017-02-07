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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallLogsAdapter extends BaseAdapter {

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
			convertView = View.inflate(context, R.layout.call_log_item, null);
			viewHolder.stopImage = (ImageView)convertView.findViewById(R.id.stop_image);
			viewHolder.name = (TextView)convertView.findViewById(R.id.name);
			viewHolder.telephone = (TextView) convertView.findViewById(R.id.telephone);
			viewHolder.time = (TextView) convertView.findViewById(R.id.log_time);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final CallLogBean callLogBean = callLogs.get(position);
		if(callLogBean.getName() == null || "".equals(callLogBean.getName())) {
			viewHolder.name.setVisibility(View.GONE);	
		} else {
			viewHolder.name.setText(callLogBean.getName());
		}
		
		viewHolder.telephone.setText(callLogBean.getTelephone());
		viewHolder.time.setText(callLogBean.getTime());
		
		if(callLogBean.isInBlackList()) {
			viewHolder.stopImage.setImageResource(R.drawable.blocked);
		} else {
			viewHolder.stopImage.setImageResource(R.drawable.not_blocked);
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
		TextView name,telephone,time;
	}
}
