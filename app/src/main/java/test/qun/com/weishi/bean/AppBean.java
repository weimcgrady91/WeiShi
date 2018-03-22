package test.qun.com.weishi.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class AppBean {
    private String name;
    private String packageName;
    private Drawable icon;
    private boolean isSystem;
    private boolean isSDCard;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isSDCard() {
        return isSDCard;
    }

    public void setSDCard(boolean SDCard) {
        isSDCard = SDCard;
    }
}
