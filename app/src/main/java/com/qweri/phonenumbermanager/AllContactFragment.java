package com.qweri.phonenumbermanager;

import java.util.ArrayList;
import java.util.List;

import com.qweri.phonenumbermanager.adapter.AllContactAdapter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class AllContactFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final String TAG = AllContactFragment.class.getName();
    private ListView mListView;
    private AllContactAdapter allContactAdapter;
    private List<ContactBean> contactList = new ArrayList<>();
    private static final int WHAT_GET_ONE_PERSION = 0;
    private Cursor mCursor,mCursor1;

    private Context mContext;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == WHAT_GET_ONE_PERSION) {
                ContactBean bean = (ContactBean) msg.obj;
                allContactAdapter.add(bean);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("ygx","allcontact onCreateView");
        mContext = getActivity();
        View view = inflater.inflate(R.layout.all_contact, null);
        mListView = (ListView) view.findViewById(R.id.all_contact_list);
        mListView.setEmptyView(view.findViewById(R.id.empty_view));
        mListView.setOnItemClickListener(this);
        allContactAdapter = new AllContactAdapter(getActivity(), contactList);
        mListView.setAdapter(allContactAdapter);
        getAllContact();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ContactBean contactBean = contactList.get(i);
        contactBean.setInBlackList(!contactBean.isInBlackList());
        if (contactBean.isInBlackList()) {
            BlackListUtils.addNumber(mContext, contactBean.getTelephone());
        } else {
            BlackListUtils.deleteNumber(mContext, contactBean.getTelephone());
        }
        allContactAdapter.notifyDataSetChanged();
    }

    private void getAllContact() {
        Log.i("ygx","allcontact getAllContact");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri uri = Uri.parse("content://com.android.contacts/contacts");
                if (getActivity() == null) {
                    return;
                }
                try {
                    ContentResolver resolver = mContext.getContentResolver();
                    mCursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
                    ContactBean bean = null;
                    while (mCursor != null && mCursor.moveToNext()) {
                        int contractID = mCursor.getInt(0);
                        uri = Uri.parse("content://com.android.contacts/contacts/" + contractID + "/data");
                        mCursor1 = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
                        bean = new ContactBean();
                        while (mCursor1 != null && mCursor1.moveToNext()) {
                            String data1 = mCursor1.getString(mCursor1.getColumnIndex("data1"));
                            String mimeType = mCursor1.getString(mCursor1.getColumnIndex("mimetype"));
                            if ("vnd.android.cursor.item/name".equals(mimeType)) { // 是姓名
                                bean.setName(data1);
                            } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { // 手机
                                bean.setTelephone(data1);
                            }
                        }
                        if (mCursor1 != null) {
                            mCursor1.close();
                        }

                        if (bean.getTelephone() != null && bean.getTelephone().length() == 11) {
                            if (getActivity() == null) {
                                return;
                            }
                            bean.setInBlackList(BlackListUtils.isNumberInBlackList(
                                    getActivity().getApplicationContext(), bean.getTelephone()));
                            Message msg = handler
                                    .obtainMessage(WHAT_GET_ONE_PERSION);
                            msg.obj = bean;
                            handler.sendMessage(msg);
                        }
                    }
                    if (mCursor != null) {
                        mCursor.close();
                    }
                    Log.i("ygx","allcontact getAllContact finish:"+allContactAdapter.getCount());
                    Log.i(TAG,"getAllContact");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        Log.i("ygx","all contact destroy");
        super.onDestroy();
        if (mCursor != null) {
            mCursor.close();
        }
        if (mCursor1 != null) {
            mCursor1.close();
        }
    }

    @Override
    public void onClick(View view) {}
}
