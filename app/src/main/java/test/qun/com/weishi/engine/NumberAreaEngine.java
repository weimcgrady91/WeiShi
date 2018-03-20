package test.qun.com.weishi.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;

/**
 * Created by Administrator on 2018/3/20 0020.
 */

public class NumberAreaEngine {

    public String obtainnumberArea(Context context, String number) {
        String result = "未知号码";
        String regex = "^1[3-8]\\d{9}";
        if (!number.matches(regex)) {
            return result;
        }
        String path = context.getFilesDir().getAbsolutePath() + "/" + "address.db";
        SQLiteDatabase sqLiteDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        String phone = number.substring(0, 7);
        Cursor cursor = sqLiteDatabase.query("data1", new String[]{"outKey"}, "id=?", new String[]{phone}, null, null, null);
        if (cursor.moveToNext()) {
            String outKey = cursor.getString(0);
            Cursor inCursor = sqLiteDatabase.query("data2", new String[]{"location"}, "id=?", new String[]{outKey}, null, null, null);
            if (inCursor.moveToNext()) {
                String address = inCursor.getString(0);
                inCursor.close();
                result = address;
            }
        }
        cursor.close();
        return result;
    }
}
