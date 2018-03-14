package test.qun.com.weishi.views;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

public class CustomCheckBoxPreference extends CheckBoxPreference {
    public CustomCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomCheckBoxPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public CustomCheckBoxPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
