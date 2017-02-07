package com.qweri.phonenumbermanager.adapter;

import java.util.List;

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
	private List<String> numbers;
	
	public BlackListViewAdapter(Context context, List<String> numbers) {
		this.context = context;
		this.numbers = numbers;
	}
	@Override
	public int getCount() {
		return numbers.size();
	}

	@Override
	public Object getItem(int position) {
		return numbers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = View.inflate(context, R.layout.black_list_item, null);
		}
		
		String telephone = numbers.get(position);
		TextView numberView = (TextView) convertView.findViewById(R.id.telephone);
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
