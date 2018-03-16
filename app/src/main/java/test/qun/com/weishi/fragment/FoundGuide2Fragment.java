package test.qun.com.weishi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.R;
import test.qun.com.weishi.util.PreferencesUtil;
import test.qun.com.weishi.views.SelectItemView;

public class FoundGuide2Fragment extends Fragment implements View.OnClickListener {


    private SelectItemView mSiv;

    public FoundGuide2Fragment() {
    }

    public static FoundGuide2Fragment newInstance() {
        FoundGuide2Fragment fragment = new FoundGuide2Fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_found_guide2, container, false);
        mSiv = view.findViewById(R.id.stv);
        mSiv.setOnClickListener(this);
        String simNumber = (String) PreferencesUtil.getData(getActivity(), ConstantValue.KEY_SIM_BOUND, "");
        if (TextUtils.isEmpty(simNumber)) {
            mSiv.setChecked(false);
        } else {
            mSiv.setChecked(true);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stv:
                Log.e("weiqun12345", "onClick");
                boolean check = mSiv.isChecked();
                if (check) {
                    //解绑
                    PreferencesUtil.setData(getActivity(), ConstantValue.KEY_SIM_BOUND, "");
                    mSiv.setChecked(!check);
                } else {
                    //绑定
                    String simNumber = getSimNumber();
                    Log.e("weiqun12345","sim number=" + simNumber);
                    if(!TextUtils.isEmpty(simNumber)) {
                        PreferencesUtil.setData(getActivity(), ConstantValue.KEY_SIM_BOUND, simNumber);
                        mSiv.setChecked(!check);
                        return;
                    }
                    Toast.makeText(getActivity(),"未发现Sim卡",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private String getSimNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }
}
