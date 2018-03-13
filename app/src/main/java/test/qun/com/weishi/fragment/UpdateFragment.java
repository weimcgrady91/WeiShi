package test.qun.com.weishi.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import test.qun.com.weishi.bean.UpdateBean;
import test.qun.com.weishi.engine.UpdateEngine;
import test.qun.com.weishi.util.LogUtil;

public class UpdateFragment extends DialogFragment {

    private static final String TAG = UpdateFragment.class.getSimpleName();
    private static final String ARG_UPDATE_BEAN = "downPath";

    public UpdateFragment() {
    }

    public static UpdateFragment newInstance(UpdateBean updateBean) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_UPDATE_BEAN, updateBean);
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final UpdateBean bean = bundle.getParcelable(ARG_UPDATE_BEAN);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // 设置主题的构造方法
        // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        builder.setTitle("提示：")
                .setMessage("发现新版本,是否升级?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtil.i(TAG, "开始升级");
                        UpdateEngine updateEngine = new UpdateEngine();
                        updateEngine.update(bean);
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false);
        return builder.create();
    }
}
