package test.qun.com.weishi.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/3/13 0013.
 */

public class UpdateBean implements Parcelable {
    private int versionCode;
    private String versionName;
    private float apkSize;
    private String describe;
    private String downLoadPath;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public float getApkSize() {
        return apkSize;
    }

    public void setApkSize(float apkSize) {
        this.apkSize = apkSize;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getDownLoadPath() {
        return downLoadPath;
    }

    public void setDownLoadPath(String downLoadPath) {
        this.downLoadPath = downLoadPath;
    }

    @Override
    public String toString() {
        return "UpdateBean{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", apkSize=" + apkSize +
                ", describe='" + describe + '\'' +
                ", downLoadPath='" + downLoadPath + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.versionCode);
        dest.writeString(this.versionName);
        dest.writeFloat(this.apkSize);
        dest.writeString(this.describe);
        dest.writeString(this.downLoadPath);
    }

    public UpdateBean() {
    }

    protected UpdateBean(Parcel in) {
        this.versionCode = in.readInt();
        this.versionName = in.readString();
        this.apkSize = in.readFloat();
        this.describe = in.readString();
        this.downLoadPath = in.readString();
    }

    public static final Parcelable.Creator<UpdateBean> CREATOR = new Parcelable.Creator<UpdateBean>() {
        @Override
        public UpdateBean createFromParcel(Parcel source) {
            return new UpdateBean(source);
        }

        @Override
        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };
}
