package com.firebase_info.firebaseandroidchat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    private FirebaseAuth mAuth;

    FirebaseUser user = mAuth.getInstance().getCurrentUser();
    String userEmail = user.getEmail();
    //стандартный конструктор для ArrayAdapter
    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
    }

    // отвечает за создание layout-элемента ListView (item_message) и отображения сообщений
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }
        mAuth = FirebaseAuth.getInstance();

        TextView messageTextView = (TextView) convertView.findViewById(R.id.messageTextView);
        TextView userTextView = (TextView) convertView.findViewById(R.id.userTextView);
        TextView byUserTextView = (TextView) convertView.findViewById(R.id.byUserTextView);
        Message message = getItem(position);
            messageTextView.setText(message.getText());
            userTextView.setText(message.getUser());
            byUserTextView.setText(message.getTo());
        return convertView;
    }
}
