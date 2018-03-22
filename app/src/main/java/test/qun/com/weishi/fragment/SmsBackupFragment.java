package test.qun.com.weishi.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import test.qun.com.weishi.R;
import test.qun.com.weishi.engine.SmsEngine;

public class SmsBackupFragment extends DialogFragment {
    private static final String ARG_MAX_PROGRESS = "max_progress";

    private String mMaxProgress;

    private ProgressBar mProgressBar;
    private TextView mTv_count;

    public SmsBackupFragment() {
    }

    public static SmsBackupFragment newInstance(int maxProgress) {
        SmsBackupFragment fragment = new SmsBackupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MAX_PROGRESS, maxProgress);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMaxProgress = getArguments().getString(ARG_MAX_PROGRESS);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Window win = getDialog().getWindow();
        WindowManager.LayoutParams params = win.getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
        backup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sms_backup, container, false);
        mProgressBar = rootView.findViewById(R.id.progress_bar);
        mTv_count = rootView.findViewById(R.id.tv_count);
        return rootView;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                mProgressBar.setMax(msg.arg1);
            }
            if (msg.what == 2) {
                mProgressBar.setProgress(msg.arg1);
                mTv_count.setText(mProgressBar.getProgress() + "/" + mProgressBar.getMax());
            }
        }
    };

    public void backup() {
        final String path = Environment.getExternalStorageDirectory() + File.separator + "weishi_smsbackup.xml";
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SmsEngine.smsBackup(getActivity(), path, new SmsEngine.BackupListener() {
                        @Override
                        public void setMax(int max) {
                            Message message = mHandler.obtainMessage();
                            message.what = 1;
                            message.arg1 = max;
                            mHandler.sendMessage(message);
                        }

                        @Override
                        public void setProgress(int index) {
                            Message message = mHandler.obtainMessage();
                            message.what = 2;
                            message.arg1 = index;
                            mHandler.sendMessage(message);
                        }
                    });
                    dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void setMax(int max) {
        mTv_count.setText("aa");
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
        mTv_count.setText(mProgressBar.getMax() + "/" + mProgressBar.getProgress());
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
