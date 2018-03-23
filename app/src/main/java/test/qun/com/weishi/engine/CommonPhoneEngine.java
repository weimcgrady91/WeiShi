package test.qun.com.weishi.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.bean.CommonPhoneChildBean;
import test.qun.com.weishi.bean.CommonPhoneGroupBean;

/**
 * Created by Administrator on 2018/3/23 0023.
 */

public class CommonPhoneEngine {
    public static List<CommonPhoneGroupBean> fetchData(Context context) {
        List<CommonPhoneGroupBean> list = new ArrayList<>();
        String path = context.getFilesDir() + File.separator + "commonnum.db";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.query("classlist", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            CommonPhoneGroupBean bean = new CommonPhoneGroupBean();
            bean.setName(cursor.getString(0));
            bean.setIndex(cursor.getInt(1));
            Cursor innerCursor = db.query("table" + bean.getIndex(), null, null, null, null, null, null);
            List<CommonPhoneChildBean> list2 = new ArrayList<>();
            while (innerCursor.moveToNext()) {
                CommonPhoneChildBean child = new CommonPhoneChildBean();
                child.setId(innerCursor.getInt(0));
                child.setNumber(innerCursor.getString(1));
                child.setName(innerCursor.getString(2));
                list2.add(child);
            }
            innerCursor.close();
            bean.setChildBeanList(list2);
            list.add(bean);
        }
        cursor.close();
        db.close();
        return list;

    }
}
