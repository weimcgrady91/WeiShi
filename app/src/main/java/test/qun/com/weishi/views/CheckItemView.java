package test.qun.com.weishi.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import test.qun.com.weishi.R;

/**
 * Created by Administrator on 2018/3/19 0019.
 */

public class CheckItemView extends LinearLayout {

    private CheckBox mCheckBox;
    private TextView mContent;
    private String mContentOn;
    private String mContentOff;

    public CheckItemView(Context context) {
        this(context, null);
    }

    public CheckItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.view_check_item, this);
        mCheckBox = findViewById(R.id.checkbox);
        mContent = findViewById(R.id.tv_content);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CheckItemView);
        mContentOn = ta.getString(R.styleable.CheckItemView_civ_contentOn);
        mContentOff = ta.getString(R.styleable.CheckItemView_civ_contentOff);
        ta.recycle();

    }

    public boolean isChecked() {
        return mCheckBox.isChecked();
    }

    public void setChecked(boolean checked) {
        mCheckBox.setChecked(checked);
        if (checked) {
            mContent.setText(mContentOn);
        } else {
            mContent.setText(mContentOff);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
