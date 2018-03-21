package test.qun.com.weishi.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import test.qun.com.weishi.R;
import test.qun.com.weishi.bean.BlackNumberBean;

public class AddBlackNumberFragment extends DialogFragment {


    private OnAddBlackNumberListener mListener;
    private EditText mEtBlackNumber;

    public AddBlackNumberFragment() {
    }

    public static AddBlackNumberFragment newInstance() {
        AddBlackNumberFragment fragment = new AddBlackNumberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }

    private int mode = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_black_number, container, false);
        RadioGroup rgMode = rootView.findViewById(R.id.rg_mode);
        mEtBlackNumber = rootView.findViewById(R.id.et_blackNumber);
        Button btnCancel = rootView.findViewById(R.id.btn_cancel);
        Button btnOk = rootView.findViewById(R.id.btn_ok);
        rgMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_phone:
                        mode = 0;
                        break;
                    case R.id.rb_sms:
                        mode = 1;
                        break;
                    case R.id.rb_all:
                        mode = 2;
                        break;
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blackNumber = mEtBlackNumber.getText().toString();
                if (TextUtils.isEmpty(blackNumber)) {
                    Toast.makeText(getActivity(), "号码不能为空", Toast.LENGTH_SHORT).show();
                    mEtBlackNumber.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
                    return;
                }
                BlackNumberBean bean = new BlackNumberBean();
                bean.setMode(mode);
                bean.setPhone(blackNumber);
                mListener.onAddSuccess(bean);
                dismiss();
            }
        });


        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAddBlackNumberListener) {
            mListener = (OnAddBlackNumberListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddBlackNumberListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAddBlackNumberListener {
        void onAddSuccess(BlackNumberBean bean);
    }
}
