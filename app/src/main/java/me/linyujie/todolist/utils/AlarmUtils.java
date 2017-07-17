package me.linyujie.todolist.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.support.annotation.NonNull;

import java.util.Calendar;

import me.linyujie.todolist.AlarmReceiver;
import me.linyujie.todolist.TodoEditActivity;
import me.linyujie.todolist.models.Todo;

/**
 * Created by linyujie on 7/14/17.
 */

public class AlarmUtils {

    public static void setAlarm(@NonNull Context context, @NonNull Todo todo) {
        Calendar c = Calendar.getInstance();
        if (todo.remindDate.compareTo(c.getTime()) < 0) {
            return;
        }

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(TodoEditActivity.KEY_TODO, todo);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
                                                                    0,
                                                                    intent,
                                                                    PendingIntent.FLAG_UPDATE_CURRENT);

        // this will wake up the device at time
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                    todo.remindDate.getTime(),
                    alarmIntent);
    }
}
