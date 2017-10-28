package ch.ecommunicate.email;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by amlevin on 8/25/2017.
 */

public class EmailArrayAdapter extends ArrayAdapter<EmailsActivity.Email> {

    private static final String TAG = "EmailArrayAdapter";

    private final Context context;
    private final List<EmailsActivity.Email> emails_list;

    private Boolean sent = false;

    public EmailArrayAdapter(Context context, List<EmailsActivity.Email> emails_list, Boolean sent) {
        super(context, R.layout.email_in_list, emails_list);
        this.context = context;
        this.emails_list = emails_list;
        this.sent = sent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View email_view;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView email_subject_textview;
        TextView email_from_textview;
        TextView email_to_textview;

        if(sent) {
            email_view = inflater.inflate(R.layout.sent_email_in_list, parent, false);
            email_subject_textview = (TextView) email_view.findViewById(R.id.subject);
            email_to_textview = (TextView) email_view.findViewById(R.id.to);
            email_subject_textview.setText(emails_list.get(position).subject);
            email_to_textview.setText(emails_list.get(position).to);
        }
        else {
            if (emails_list.get(position).is_read) {
                email_view = inflater.inflate(R.layout.email_in_list, parent, false);
                email_subject_textview = (TextView) email_view.findViewById(R.id.subject);
                email_from_textview = (TextView) email_view.findViewById(R.id.from);

            } else {
                email_view = inflater.inflate(R.layout.email_in_list_unread, parent, false);
                email_subject_textview = (TextView) email_view.findViewById(R.id.subject);
                email_from_textview = (TextView) email_view.findViewById(R.id.from);

            }

            email_subject_textview.setText(emails_list.get(position).subject);
            email_from_textview.setText(emails_list.get(position).from);
        }

        return email_view;
    }
}
