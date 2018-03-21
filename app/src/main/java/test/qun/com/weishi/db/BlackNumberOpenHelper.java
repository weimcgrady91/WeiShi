package test.qun.com.weishi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/3/21 0021.
 */

public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "black_number";

    public BlackNumberOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createBlackNumber = "create table black_number (_id integer primary key AUTOINCREMENT,phone varchar(20), mode varchar(1))";
        db.execSQL(createBlackNumber);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
