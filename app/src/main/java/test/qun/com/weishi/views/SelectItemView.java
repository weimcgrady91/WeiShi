package test.qun.com.weishi.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import test.qun.com.weishi.R;

/**
 * Created by Administrator on 2018/3/16 0016.
 */

public class SelectItemView extends RelativeLayout {

    private String mTitle;
    private String mSummaryOn;
    private String mSummaryOff;
    private TextView mTv_title;
    private TextView mTv_summary;
    private CheckBox mCb_selected;

    public SelectItemView(Context context) {
        this(context, null);
    }

    public SelectItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_select_item, this);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectItemView);
        mTitle = typedArray.getString(R.styleable.SelectItemView_stv_title);
        mSummaryOn = typedArray.getString(R.styleable.SelectItemView_stv_summaryOn);
        mSummaryOff = typedArray.getString(R.styleable.SelectItemView_stv_summaryOff);
        typedArray.recycle();

        mTv_title = findViewById(R.id.tv_title);
        mTv_summary = findViewById(R.id.tv_summary);
        mCb_selected = findViewById(R.id.cb_selected);

        mTv_title.setText(mTitle);
    }

    public boolean isChecked() {
        return mCb_selected.isChecked();
    }

    public void setChecked(boolean checked) {
        mCb_selected.setChecked(checked);
        if (checked) {
            mTv_summary.setText(mSummaryOn);
        } else {
            mTv_summary.setText(mSummaryOff);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    //    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return true;
//    }

}
