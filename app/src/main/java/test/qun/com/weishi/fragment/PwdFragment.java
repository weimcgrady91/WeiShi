package test.qun.com.weishi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import test.qun.com.weishi.R;
import test.qun.com.weishi.engine.PwdEngine;

public class PwdFragment extends DialogFragment implements View.OnClickListener {
    private static final String ARG_MODE = "mode";
    public static final int inputMode = 0;
    public static final int validateMode = 1;
    // TODO: Rename and change types of parameters
    private int mMode;

    private OnPwdFragmentInteractionListener mListener;
    private EditText mPwd;
    private EditText mPwdConfim;
    private PwdEngine mPwdEngine;

    public PwdFragment() {
    }

    public static PwdFragment newInstance(int mode) {
        PwdFragment fragment = new PwdFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MODE, mode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMode = getArguments().getInt(ARG_MODE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        if (mMode == inputMode) {
            view = inflater.inflate(R.layout.fragment_pwd_input, container, false);
            Button btnCancel = view.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(this);
            Button btnOk = view.findViewById(R.id.btn_ok);
            btnOk.setOnClickListener(this);
            mPwd = view.findViewById(R.id.et_pwd);
            mPwdConfim = view.findViewById(R.id.et_pwd_confim);
        } else {
            view = inflater.inflate(R.layout.fragment_pwd_validate, container, false);
            Button btnCancel = view.findViewById(R.id.btn_cancel);
            btnCancel.setOnClickListener(this);
            Button btnValidate = view.findViewById(R.id.btn_validate);
            btnValidate.setOnClickListener(this);
            mPwd = view.findViewById(R.id.et_pwd);
        }
        mPwdEngine = new PwdEngine();
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_validate:
                String Pwd = mPwd.getText().toString();
                if (TextUtils.isEmpty(Pwd)) {
                    Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mPwdEngine.validatePwd(Pwd)) {
                    if (mListener != null) {
                        mListener.onValidateSuccess();
                        dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(),"密码不正确", Toast.LENGTH_SHORT).show();
                    if (mListener != null) {
                        mListener.onValidateFailure();
                    }
                }
                break;
            case R.id.btn_cancel:
                if (mListener != null) {
                    mListener.onCancel();
                }
                dismiss();
                break;
            case R.id.btn_ok:
                String pwd = mPwd.getText().toString();
                String pwdConfirm = mPwdConfim.getText().toString();
                if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwdConfirm)) {
                    Toast.makeText(getActivity(), "密码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!pwd.equals(pwdConfirm)) {
                    Toast.makeText(getActivity(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                mPwdEngine.savePwd(pwd);
                if (mListener != null) {
                    mListener.onSetupPwdSuccess();
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
//        win.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        WindowManager.LayoutParams params = win.getAttributes();
//        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }

    //    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPwdFragmentInteractionListener) {
            mListener = (OnPwdFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPwdFragmentInteractionListener {
        void onValidateSuccess();

        void onValidateFailure();

        void onSetupPwdSuccess();

        void onCancel();
    }
}
