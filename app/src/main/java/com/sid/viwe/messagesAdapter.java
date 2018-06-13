package com.sid.viwe;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

/**
 * Created by Siddharth on 24-05-2018.
 */

public class messagesAdapter extends RecyclerView.Adapter<messagesAdapter.MessageViewHolder> {

    private List<Messages> messagesList;
    private DatabaseReference messageReference;
    private FirebaseAuth mAuth;
    public messagesAdapter(List<Messages> messagesList) {
        this.messagesList = messagesList;
    }



    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout, parent,false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position)
    {
        String current_user = mAuth.getCurrentUser().getUid();
        Messages abc = messagesList.get(position);
        String from_user = abc.getFrom();
        if(from_user.equals(current_user))
        {
            holder.senderText.setMaxWidth(550);
            holder.senderText.setBackgroundColor(Color.WHITE);
            holder.senderText.setBackgroundResource(R.drawable.sender_drawble);
            holder.senderText.setTextColor(Color.BLACK);
            holder.senderText.setVisibility(View.VISIBLE);
            holder.messageText.setVisibility(View.INVISIBLE);
            holder.senderText.setText(abc.getMessage());
        }
        else
        {
            holder.messageText.setMaxWidth(550);
            holder.messageText.setBackgroundResource(R.drawable.message_drawable);
            holder.messageText.setTextColor(Color.WHITE);
            holder.senderText.setVisibility(View.INVISIBLE);
            holder.messageText.setVisibility(View.VISIBLE);
            holder.messageText.setText(abc.getMessage());

        }

    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView messageText, senderText;

        public MessageViewHolder(View itemView)
        {
            super(itemView);
            //sendermsgText = (TextView) itemView.findViewById(R.id.sender_message_text_layout);
            messageText = (TextView) itemView.findViewById(R.id.message_text_layout);
            senderText = (TextView) itemView.findViewById(R.id.sender_text);
        }
    }
}
