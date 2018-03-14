package test.qun.com.weishi.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import test.qun.com.weishi.bean.UpdateBean;
import test.qun.com.weishi.engine.UpdateEngine;

public class UpdateFragment extends DialogFragment {

    private static final String TAG = UpdateFragment.class.getSimpleName();
    private static final String ARG_UPDATE_BEAN = "downPath";
    private UpdateBean mBean;
    private UpdateEngine mUpdateEngine;

    public UpdateFragment() {
    }

    public static UpdateFragment newInstance(UpdateBean updateBean) {
        UpdateFragment fragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_UPDATE_BEAN, updateBean);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mBean = bundle.getParcelable(ARG_UPDATE_BEAN);
        mUpdateEngine = new UpdateEngine();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // 设置主题的构造方法
        // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialog);
        builder.setTitle("提示：")
                .setMessage("发现新版本,是否升级?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mUpdateEngine.update(mBean);
                    }
                })
                .setNegativeButton("取消", null)
                .setCancelable(false);
        return builder.create();
    }
}
