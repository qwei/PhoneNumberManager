package com.qweri.phonenumbermanager;

import com.qweri.phonenumbermanager.MainActivity.ItemBean;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ManualAddFragment extends Fragment {

    private EditText blockNumber;
    private Button blockBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_manual_add, null);
        blockNumber = (EditText) view.findViewById(R.id.block_number_editview);
        blockBtn = (Button) view.findViewById(R.id.add_number);
        blockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = blockNumber.getText().toString();
                if (!isValidNumber(number)) {
                    if (BlackListUtils.isNumberInBlackList(getContext(), number)){
                        Toast.makeText(getActivity(), "已添加过此手机号码！", Toast.LENGTH_SHORT).show();
                    } else {
                        BlackListUtils.addNumber(getActivity(), number);
                        getActivity().finish();
                        Toast.makeText(getActivity(), "添加成功！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "请输入正确的手机号码！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    private boolean isValidNumber(String number) {
        if (number == null || number.length() != 11)
            return true;
        for (int i = 0; i < number.length(); i++) {

            int c = Integer.parseInt(number.charAt(i) + "");
            if (c > 10 || c < 0) {
                return true;
            }
        }
        return false;
    }
}
