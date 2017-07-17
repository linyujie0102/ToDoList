package me.linyujie.todolist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import me.linyujie.todolist.models.Todo;

/**
 * Created by linyujie on 7/15/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final int notificationId = 100;
        Todo todo = intent.getParcelableExtra(TodoEditActivity.KEY_TODO);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_adb_blue_grey_700_24dp)
                .setContentTitle(todo.text)
                .setContentText(todo.text);


        Intent resultIntent = new Intent(context, TodoEditActivity.class);
        resultIntent.putExtra(TodoEditActivity.KEY_TODO, todo);
        resultIntent.putExtra(TodoEditActivity.KEY_NOTIFICATION_ID, notificationId);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                                                                        0,
                                                                        resultIntent,
                                                                    PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(notificationId, builder.build());


    }
}
