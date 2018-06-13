package com.sid.viwe;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment
{
    private RecyclerView chats_list;

    private DatabaseReference conv_ref;
    private DatabaseReference user_ref;
    private DatabaseReference message_ref;

    private FirebaseAuth mAuth;
    private String curret_user_id;

    private View view;
    public ChatsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        chats_list = (RecyclerView) view.findViewById(R.id.chatRecycler);
        mAuth = FirebaseAuth.getInstance();
        curret_user_id = mAuth.getCurrentUser().getUid();


        user_ref = FirebaseDatabase.getInstance().getReference().child("Users");
        user_ref.keepSynced(true);

        message_ref = FirebaseDatabase.getInstance().getReference().child("Messages").child(curret_user_id);
        message_ref.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        chats_list.setHasFixedSize(true);
        chats_list.setLayoutManager(linearLayoutManager);

        return view;
    }


    @Override
    public void onStart()
    {
        super.onStart();


        FirebaseRecyclerAdapter<Conv, conViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Conv, conViewHolder>
                (
                        Conv.class,
                        R.layout.single_chat_layout,
                        conViewHolder.class,
                        message_ref
                )
        {
            @Override
            protected void populateViewHolder(final conViewHolder viewHolder, Conv model, int position)
            {
                final String list_user_id = getRef(position).getKey();

                //----------------------------------Users Data Model--------------------------------
                user_ref.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userThumb,getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view)
                            {
                                Intent chatIntent = new Intent(getContext(), chatActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                chatIntent.putExtra("user_name", userName);
                                startActivity(chatIntent);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {      }
                });

            }
        };

        chats_list.setAdapter(firebaseRecyclerAdapter);

    }


    public static class conViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        public conViewHolder(View itemView)
        {
            super(itemView);
            mView = itemView;
        }


        public void setName(String name){

            TextView userNameView = (TextView) mView.findViewById(R.id.chatUsername);
            userNameView.setText(name);
        }

        public void setUserImage(String thumb_image, Context context){

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.chatUserimage);
            Picasso.with(context).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }


    }

}



