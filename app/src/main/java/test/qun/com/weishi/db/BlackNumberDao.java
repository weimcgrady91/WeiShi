package test.qun.com.weishi.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import test.qun.com.weishi.App;
import test.qun.com.weishi.bean.BlackNumberBean;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class BlackNumberDao {
    private static volatile BlackNumberDao sBlackNumberDao;
    private BlackNumberOpenHelper mBlackNumberOpenHelper;

    private BlackNumberDao() {
        mBlackNumberOpenHelper = new BlackNumberOpenHelper(App.sContext);
    }

    public static BlackNumberDao getInstance() {
        if (sBlackNumberDao == null) {
            synchronized (BlackNumberDao.class) {
                if (sBlackNumberDao == null) {
                    sBlackNumberDao = new BlackNumberDao();
                }
            }
        }
        return sBlackNumberDao;
    }

    public void insert(String phoneNumber, int mode) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone", phoneNumber);
        values.put("mode", mode);
        db.insert("black_number", null, values);
        db.close();
    }

    public void delete(String phoneNumber) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        db.delete("black_number", "phone=?", new String[]{phoneNumber});
        db.close();
    }

    public void update(String phone, int mode) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mode", mode);
        db.update("black_number", contentValues, "phone = ?", new String[]{phone});
        db.close();
    }

    public List<BlackNumberBean> findAll() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("black_number", new String[]{"phone", "mode"}, null, null, null, null, "_id desc");
        List<BlackNumberBean> blackNumberList = new ArrayList<BlackNumberBean>();
        while (cursor.moveToNext()) {
            BlackNumberBean bean = new BlackNumberBean();
            bean.setPhone(cursor.getString(0));
            bean.setMode(cursor.getInt(1));
            blackNumberList.add(bean);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }

    public List<BlackNumberBean> find(int index) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();

//        Cursor cursor = db.rawQuery("select phone,mode from black_number order by _id desc limit ?,20", new String[]{index + ""});
        Cursor cursor = db.query("black_number", new String[]{"phone", "mode"},
                null, null, null, null, "_id desc limit " + index + ",20");
        List<BlackNumberBean> blackNumberList = new ArrayList<BlackNumberBean>();
        while (cursor.moveToNext()) {
            BlackNumberBean blackNumberInfo = new BlackNumberBean();
            blackNumberInfo.setPhone(cursor.getString(0));
            blackNumberInfo.setMode(cursor.getInt(1));
            blackNumberList.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return blackNumberList;
    }

    public int getCount() {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        int count = 0;
        Cursor cursor = db.rawQuery("select count(*) from black_number;", null);
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return count;
    }

    public int getMode(String phone) {
        SQLiteDatabase db = mBlackNumberOpenHelper.getWritableDatabase();
        int mode = -1;
        Cursor cursor = db.query("black_number", new String[]{"mode"}, "phone = ?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getInt(0);
        }

        cursor.close();
        db.close();
        return mode;
    }
}
