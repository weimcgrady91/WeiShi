package test.qun.com.weishi.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class CommonPhoneGroupBean {
    private int id;
    private String name;
    private int index;
    private List<CommonPhoneChildBean> mChildBeanList;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<CommonPhoneChildBean> getChildBeanList() {
        return mChildBeanList;
    }

    public void setChildBeanList(List<CommonPhoneChildBean> childBeanList) {
        mChildBeanList = childBeanList;
    }
}
