package com.qweri.phonenumbermanager;

import java.text.CollationKey;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AllContactAdapter extends BaseAdapter {

	private Context context;
	private List<ContactBean> contacts;
	
	public AllContactAdapter(Context context, List<ContactBean> contacts) {
		this.context = context;
		this.contacts = contacts;
	}
	@Override
	public int getCount() {
		
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
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
			convertView = View.inflate(context, R.layout.contact_item, null);
			viewHolder.stopImage = (ImageView)convertView.findViewById(R.id.stop_image);
			viewHolder.name = (TextView)convertView.findViewById(R.id.name);
			viewHolder.telephone = (TextView) convertView.findViewById(R.id.telephone);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		final ContactBean bean = contacts.get(position);
		viewHolder.name.setText(bean.getName());
		viewHolder.telephone.setText(bean.getTelephone());
		
		if(bean.isInBlackList()) {
			viewHolder.stopImage.setImageResource(R.drawable.blocked);
		} else {
			viewHolder.stopImage.setImageResource(R.drawable.not_blocked);
		}
		viewHolder.stopImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				bean.setInBlackList(!bean.isInBlackList());
				if(bean.isInBlackList()) {
					BlackListUtils.addNumber(context, bean.getTelephone());
				} else {
					BlackListUtils.deleteNumber(context, bean.getTelephone());
				}
				notifyDataSetChanged();
			}
		});
		return convertView;
	}

	public void add(ContactBean bean) {
		int index = Collections.binarySearch(contacts, bean,new Comparator<ContactBean>() {
			Collator collator = Collator.getInstance();
			@Override
			public int compare(ContactBean lhs, ContactBean rhs) {
				CollationKey key1 = collator  
	                    .getCollationKey(lhs.getName());  
	            CollationKey key2 = collator  
	                    .getCollationKey(rhs.getName()); 
				return key1.compareTo(key2);
			}
		});
		if(index < 0) {
			index = -(index + 1);
		}
		contacts.add(index, bean);
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
		TextView name,telephone;
	}
}
