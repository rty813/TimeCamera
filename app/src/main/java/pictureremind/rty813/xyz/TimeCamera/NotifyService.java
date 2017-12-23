package pictureremind.rty813.xyz.TimeCamera;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

import com.coolerfall.daemon.Daemon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Date;

import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;
import pictureremind.rty813.xyz.TimeCamera.util.SQLiteDBHelper;

public class NotifyService extends Service {
    private SQLiteDBHelper dbHelper;

    public NotifyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Daemon.run(this, NotifyService.class, 10);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dbHelper = new SQLiteDBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(SQLiteDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            int count = 0;
            do{
                try {
                    SimpleDateFormat dateFormat = null;
                    String cyc = cursor.getString(cursor.getColumnIndex("CYC"));
                    switch (cyc){
                        case "每天":
                            dateFormat = new SimpleDateFormat("HH-mm", Locale.getDefault());
                            break;
                        case "每周":
                            dateFormat = new SimpleDateFormat("E-HH-mm", Locale.getDefault());
                            break;
                        case "每月":
                            dateFormat = new SimpleDateFormat("dd-HH-mm", Locale.getDefault());
                            break;
                        case "每年":
                            dateFormat = new SimpleDateFormat("MM-dd-HH-mm", Locale.getDefault());
                            break;
                        case "不设置":
                            continue;
                    }
                    Date date = dateFormat.parse(cursor.getString(cursor.getColumnIndex("REMIND_TIME")));
                    Date nowDate = dateFormat.parse(dateFormat.format(new Date(System.currentTimeMillis())));
                    System.out.println(date.toString() + "\n" + nowDate.toString() + "\n");

                    boolean hasRemind = cursor.getInt(cursor.getColumnIndex("HAS_REMIND")) == 1;
                    String albumname = cursor.getString(cursor.getColumnIndex("NAME"));

                    if (nowDate.before(date)){
                        ContentValues values = new ContentValues();
                        values.put("HAS_REMIND", 0);
                        database.update(SQLiteDBHelper.TABLE_NAME, values, "NAME=?", new String[]{albumname});
                    }
                    else if (!hasRemind){
                        sendNotification(albumname, count);
                        count += 1;
                        ContentValues values = new ContentValues();
                        values.put("HAS_REMIND", 1);
                        database.update(SQLiteDBHelper.TABLE_NAME, values, "NAME=?", new String[]{albumname});
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }
        database.close();
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendNotification(String albumname, int id){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, id,
                new Intent(this, MainActivity.class), 0);
        Notification notification = new Notification.Builder(this)
                .setContentTitle("该拍照啦！")
                .setContentText(albumname)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setTicker(albumname + "：该拍照啦！")
                .build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(id, notification);
    }
}
