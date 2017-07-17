package me.linyujie.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

import me.linyujie.todolist.models.Todo;
import me.linyujie.todolist.utils.AlarmUtils;
import me.linyujie.todolist.utils.DateUtils;
import me.linyujie.todolist.utils.UIUtils;

/**
 * Created by linyujie on 7/15/17.
 */

@SuppressWarnings("ConstantConditions")
public class TodoEditActivity extends AppCompatActivity implements
                            DatePickerDialog.OnDateSetListener,
                            TimePickerDialog.OnTimeSetListener{


    public static final String KEY_TODO = "todo";
    // put the id of list to intent and let main activity know this item need to be deleted.
    public static final String KEY_TODO_ID = "todo_id";
    public static final String KEY_NOTIFICATION_ID = "notification_id";

    private EditText todoEdit;
    private TextView dateTv;
    private TextView timeTv;
    private CheckBox completeCb;


    private Todo todo;
    private Date remindDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receive data from main activity,
        todo = getIntent().getParcelableExtra(KEY_TODO);
        remindDate = todo != null ? todo.remindDate : null;

        setupUI();
        cancelNotificationIfNeeded();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void cancelNotificationIfNeeded() {
        int notificationId = getIntent().getIntExtra(KEY_NOTIFICATION_ID, -1);
        if (notificationId != -1) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
        }
    }

    private void setupActionbar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        setTitle(null);
    }

    private void setupUI() {
        setContentView(R.layout.activity_edit_todo);
        setupActionbar();

        todoEdit = (EditText) findViewById(R.id.todo_detail_todo_edit);
        dateTv = (TextView) findViewById(R.id.todo_detail_date);
        timeTv = (TextView) findViewById(R.id.todo_detail_time);
        completeCb = (CheckBox) findViewById(R.id.todo_detail_complete);

        if (todo != null) {
            todoEdit.setText(todo.text);
            UIUtils.setTextViewStrikeThrough(todoEdit, todo.done);
            completeCb.setChecked(todo.done);

            findViewById(R.id.todo_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    delete();
                }
            });
        } else {
            // if this is not an existing item, the delete btn should not be visible.
            findViewById(R.id.todo_delete).setVisibility(View.GONE);
        }


        if (remindDate != null) {
            dateTv.setText(DateUtils.dateToStringDate(remindDate));
            timeTv.setText(DateUtils.dateToStringTime(remindDate));
        } else {
            dateTv.setText(R.string.set_date);
            timeTv.setText(R.string.set_time);
        }

        setupDatePicker();
        setupCheckbox();
        setupSaveButton();
    }


    private void setupDatePicker() {

        //setup date picker dialog
        dateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = getCalendarFromRemindDate();
                Dialog dialog = new DatePickerDialog(
                        TodoEditActivity.this,
                        TodoEditActivity.this,
                        c.get(Calendar.YEAR),
                        c.get(Calendar.MONTH),
                        c.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });

        //setup time picker dialog
        timeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = getCalendarFromRemindDate();
                Dialog dialog = new TimePickerDialog(
                        TodoEditActivity.this,
                        TodoEditActivity.this,
                        c.get(Calendar.HOUR_OF_DAY),
                        c.get(Calendar.MINUTE),
                        true);
                dialog.show();
            }
        });
    }

    private void setupCheckbox() {
        completeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                UIUtils.setTextViewStrikeThrough(todoEdit, b);
                todoEdit.setTextColor(b ? Color.GRAY : Color.WHITE);
            }
        });
    }

    private void setupSaveButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.todo_detail_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAndExit();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
        Calendar c = getCalendarFromRemindDate();
        c.set(year, monthOfYear, dayOfMonth);

        remindDate = c.getTime();
        dateTv.setText(DateUtils.dateToStringDate(remindDate));
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        Calendar c = getCalendarFromRemindDate();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);

        remindDate = c.getTime();
        timeTv.setText(DateUtils.dateToStringTime(remindDate));
    }


    private void saveAndExit() {
        if (todo == null) {
            todo = new Todo(todoEdit.getText().toString(), remindDate);
        } else {
            todo.text = todoEdit.getText().toString();
            todo.remindDate = remindDate;
        }

        todo.done = completeCb.isChecked();

        if (todo.remindDate != null) {
            AlarmUtils.setAlarm(this, todo);
        }

        Intent result = new Intent();
        result.putExtra(KEY_TODO, todo);
        setResult(Activity.RESULT_OK, result);
        finish();
    }
    // function to delete this list
    private void delete() {
        Intent result = new Intent();
        result.putExtra(KEY_TODO_ID, todo.id);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private Calendar getCalendarFromRemindDate() {
        Calendar c = Calendar.getInstance();
        if (remindDate != null) {
            c.setTime(remindDate);
        }
        return c;
    }

}
