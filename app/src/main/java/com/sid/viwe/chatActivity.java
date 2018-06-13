package com.sid.viwe;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity {

    private String chatUser_id, chatUser_name;
    private Toolbar mToolbar;
    private TextView title;
    private CircleImageView img;
    private DatabaseReference rootRef;
    private FirebaseAuth mAuth;
    private String curren_user;
    private EditText mess;
    private ImageButton sendbtn;
    private RecyclerView reCyclerMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;
    private messagesAdapter mMessagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        curren_user = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        mToolbar = (Toolbar) findViewById(R.id.chatToolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        chatUser_id = getIntent().getStringExtra("user_id");
        chatUser_name = getIntent().getStringExtra("user_name");


        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_view = layoutInflater.inflate(R.layout.chat_custom_toolbar,null);

        actionBar.setCustomView(action_view);

        title = (TextView) findViewById(R.id.chat_user_name);
        img = (CircleImageView) findViewById(R.id.chat_user_image);

        title.setText(chatUser_name);

        mess = (EditText) findViewById(R.id.message_text);
        sendbtn = (ImageButton) findViewById(R.id.chat_send_btn);

        mMessagesAdapter = new messagesAdapter(messagesList);

        reCyclerMessageList = (RecyclerView) findViewById(R.id.list_of_messages);
        mLinearLayoutManager = new LinearLayoutManager(this);

        reCyclerMessageList.setHasFixedSize(true);
        reCyclerMessageList.setLayoutManager(mLinearLayoutManager);

        reCyclerMessageList.setAdapter(mMessagesAdapter);
        loadMessages();
        rootRef.child("Users").child(chatUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imgsrc = dataSnapshot.child("image").getValue().toString();
                Picasso.with(chatActivity.this).load(imgsrc).placeholder(R.drawable.default_avatar).fit().into(img);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        rootRef.child("Chat").child(curren_user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.hasChild(chatUser_id))
                {
                    Map chatMap = new HashMap();


                    Map userMap = new HashMap();
                    userMap.put("Chat/"+curren_user+"/"+chatUser_id,chatMap);
                    userMap.put("Chat/"+chatUser_id+"/"+curren_user,chatMap);

                    rootRef.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null)
                            {
                                Log.d("CHAT_LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmessage();
            }
        });

    }

    private void loadMessages()
    {
        rootRef.child("Messages").child(curren_user).child(chatUser_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                mMessagesAdapter.notifyDataSetChanged();
                reCyclerMessageList.scrollToPosition(messagesList.size() - 1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendmessage() {
        String message = mess.getText().toString();
        if(!TextUtils.isEmpty(message))
        {
            String user_reference = "Messages/"+curren_user+"/"+chatUser_id;
            String chatUser_referenc = "Messages/"+chatUser_id+"/"+curren_user;

            DatabaseReference message_push_id = rootRef.child("Messages").child(curren_user).child(chatUser_id).push();
            String push_id = message_push_id.getKey();

            Map message_map = new HashMap();
            message_map.put("message",message);
            message_map.put("from",curren_user);

            mess.setText("");

            Map userMessagemap = new HashMap();
            userMessagemap.put(user_reference +"/"+ push_id,message_map);
            userMessagemap.put(chatUser_referenc + "/" + push_id,message_map);

            rootRef.updateChildren(userMessagemap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                {
                    if(databaseError != null)
                    {
                        Log.d("CHAT_LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
}
