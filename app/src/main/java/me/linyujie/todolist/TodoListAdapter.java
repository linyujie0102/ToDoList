package me.linyujie.todolist;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

import me.linyujie.todolist.models.Todo;
import me.linyujie.todolist.utils.UIUtils;

/**
 * Created by linyujie on 7/16/17.
 */

public class TodoListAdapter extends BaseAdapter {

    private MainActivity activity;
    private List<Todo> data;

    public TodoListAdapter(MainActivity activity, List<Todo> data) {
        this.activity = activity;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.main_list_item, parent, false);

            vh = new ViewHolder();
            vh.todoText = (TextView) convertView.findViewById(R.id.main_list_item_text);
            vh.doneCheckbox = (CheckBox) convertView.findViewById(R.id.main_list_item_check);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Todo todo = (Todo) getItem(position);
        vh.todoText.setText(todo.text);
        vh.doneCheckbox.setChecked(todo.done);
        UIUtils.setTextViewStrikeThrough(vh.todoText, todo.done);

        vh.doneCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.updateTodo(position, isChecked);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, TodoEditActivity.class);
                intent.putExtra(TodoEditActivity.KEY_TODO, todo);
                activity.startActivityForResult(intent, MainActivity.REQ_CODE_TODO_EDIT);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView todoText;
        CheckBox doneCheckbox;
    }
}

