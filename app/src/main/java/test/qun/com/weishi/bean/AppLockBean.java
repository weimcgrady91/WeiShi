package test.qun.com.weishi.bean;

/**
 * Created by Administrator on 2018/3/24.
 */

public class AppLockBean {
    private int id;
    private String pckName;

    @Override
    public String toString() {
        return "AppLockBean{" +
                "id=" + id +
                ", pckName='" + pckName + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPckName() {
        return pckName;
    }

    public void setPckName(String pckName) {
        this.pckName = pckName;
    }
}
