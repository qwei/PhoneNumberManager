package com.qweri.phonenumbermanager.adapter;

import java.util.List;

import com.qweri.phonenumbermanager.MainActivity.ItemBean;
import com.qweri.phonenumbermanager.R;
import com.qweri.phonenumbermanager.R.id;
import com.qweri.phonenumbermanager.R.layout;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BlackListViewAdapter extends BaseAdapter{

	private Context context;
	private List<ItemBean> numbers;
	
	public BlackListViewAdapter(Context context, List<ItemBean> numbers) {
		this.context = context;
		this.numbers = numbers;
	}
	@Override
	public int getCount() {
		return numbers.size();
	}

	@Override
	public ItemBean getItem(int position) {
		return numbers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = View.inflate(context, layout.black_list_item, null);
		}
		
		String telephone = (getItem(position)).number;
		TextView numberView = (TextView) convertView.findViewById(id.telephone);
		TextView nameView = (TextView) convertView.findViewById(id.name);
		nameView.setText(getItem(position).name);
//		ImageView deleteView = (ImageView) convertView.findViewById(R.id.delete);
		numberView.setText(telephone);
//		deleteView.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				numbers.remove(position);
//				notifyDataSetChanged();
//			}
//		});
		
		
		return convertView;
	}

}
