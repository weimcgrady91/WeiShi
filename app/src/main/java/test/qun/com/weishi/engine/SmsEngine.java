package test.qun.com.weishi.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;

/**
 * Created by Administrator on 2018/3/22 0022.
 */

public class SmsEngine {
    public interface BackupListener {
        public void setMax(int max);

        public void setProgress(int index);
    }

    public static void smsBackup(Context context, String path, BackupListener listener) throws Exception {
        int index = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                new String[]{"address", "date", "type", "body"}, null, null, null);

        if (listener != null) {
            listener.setMax(cursor.getCount());
        }
        FileOutputStream outputStream = new FileOutputStream(path);
        XmlSerializer xmlSerializer = Xml.newSerializer();
        xmlSerializer.setOutput(outputStream, "utf-8");
        xmlSerializer.startDocument("utf-8", true);
        xmlSerializer.startTag(null, "smss");
        while (cursor.moveToNext()) {
            xmlSerializer.startTag(null, "sms");

            xmlSerializer.startTag(null, "address");
            xmlSerializer.text(cursor.getString(0));
            xmlSerializer.endTag(null, "address");

            xmlSerializer.startTag(null, "date");
            xmlSerializer.text(cursor.getString(1));
            xmlSerializer.endTag(null, "date");

            xmlSerializer.startTag(null, "type");
            xmlSerializer.text(cursor.getString(2));
            xmlSerializer.endTag(null, "type");

            xmlSerializer.startTag(null, "body");
            xmlSerializer.text(cursor.getString(3));
            xmlSerializer.endTag(null, "body");

            xmlSerializer.endTag(null, "sms");

            index++;
            Thread.sleep(500);
            //ProgressDialog可以在子线程中更新相应的进度条的改变

            //A 如果传递进来的是对话框,指定对话框进度条的当前百分比
            //B	如果传递进来的是进度条,指定进度条的当前百分比
//				pd.setProgress(index);

            if (listener != null) {
                listener.setProgress(index);
            }
        }
        xmlSerializer.endTag(null, "smss");
        xmlSerializer.endDocument();
        cursor.close();
        if (outputStream != null) {
            outputStream.close();
        }
    }
}
