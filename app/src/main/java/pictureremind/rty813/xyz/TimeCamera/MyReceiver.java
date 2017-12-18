package pictureremind.rty813.xyz.TimeCamera;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import pictureremind.rty813.xyz.TimeCamera.activity.MainActivity;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, NotifyService.class));
    }
}
