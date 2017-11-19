package io.jokester.tidyj_android_demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A String > TextView adapter to show times
 * Created by mono on 11/8/17.
 */

public class MessageAdapter extends ArrayAdapter<MessageAdapter.Message> {

    public MessageAdapter(@NonNull Context context, int resource) {
        super(context, resource, new ArrayList<Message>());
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView rowView = (TextView) (convertView != null
                ? convertView
                : inflater.inflate(R.layout.test_item, parent, false));

        Message r = getItem(position);
        if (position == 0) {
            rowView.setText(String.format(Locale.ENGLISH,
                    "%d\n%s", r.createdAt.getTimeInMillis(), r.s));
        } else {
            Message prev = getItem(position - 1);
            rowView.setText(String.format(Locale.ENGLISH,
                    "%d (+%d ms)\n%s",
                    r.createdAt.getTimeInMillis(),
                    r.createdAt.getTimeInMillis() - prev.createdAt.getTimeInMillis(),
                    r.s));
        }

        return rowView;
    }

    public static class Message {
        private final Calendar createdAt = Calendar.getInstance();
        private final String s;

        public Message(String s) {
            this.s = s;
        }
    }
}
