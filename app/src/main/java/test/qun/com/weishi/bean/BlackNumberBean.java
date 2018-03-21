package test.qun.com.weishi.bean;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class BlackNumberBean {
    public String phone;
    public int mode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackNumberBean{" +
                "phone='" + phone + '\'' +
                ", mode=" + mode +
                '}';
    }
}
