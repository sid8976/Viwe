package com.sid.viwe;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {
    private DatabaseReference mfriendsdata, usersdata;
    private RecyclerView mRecyclerView;
    private FirebaseAuth mAuth;
    private View view;
    private String m_current_user_id;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.fragment_friends,container,false);
        mRecyclerView=(RecyclerView)view.findViewById(R.id.friendsRecycler);
        mAuth=FirebaseAuth.getInstance();
        m_current_user_id = mAuth.getCurrentUser().getUid();
        mfriendsdata = FirebaseDatabase.getInstance().getReference().child("Friends").child(m_current_user_id);
        usersdata = FirebaseDatabase.getInstance().getReference().child("Users");
        usersdata.keepSynced(true);
        mfriendsdata.keepSynced(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerAdapter<Friends, friendsViewholder> recyclerAdapter = new FirebaseRecyclerAdapter<Friends, friendsViewholder>
                (
                        Friends.class,
                        R.layout.single_friends_layout,
                        friendsViewholder.class,
                        mfriendsdata
                )
        {
            @Override
            protected void populateViewHolder(final friendsViewholder viewHolder, final Friends model, int position)
            {
                final String list_user_id = getRef(position).getKey();
                usersdata.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        final String username = dataSnapshot.child("name").getValue().toString();
                        String thumb_img = dataSnapshot.child("thumb_image").getValue().toString();
                        String stat = dataSnapshot.child("status").getValue().toString();

                        viewHolder.setName(username);
                        viewHolder.setImg(thumb_img,getContext());
                        viewHolder.setStatus(stat);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                CharSequence option[] = new CharSequence[]{"View Profile","Send Message"};
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Item");
                                builder.setItems(option, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        if(i == 0)
                                        {
                                            Intent pro = new Intent(getContext(),profileActivity.class);
                                            pro.putExtra("user_id",list_user_id);
                                            startActivity(pro);
                                        }
                                        if(i == 1)
                                        {
                                            Intent chat = new Intent(getContext(),chatActivity.class);
                                            chat.putExtra("user_id",list_user_id);
                                            chat.putExtra("user_name",username);
                                            startActivity(chat);
                                        }

                                    }
                                });
                                builder.show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                   });
            }
        };
        mRecyclerView.setAdapter(recyclerAdapter);
    }

    public static class friendsViewholder extends RecyclerView.ViewHolder
    {
        View mView;
        public friendsViewholder(View itemView)
        {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name)
         {
            TextView showname = (TextView) mView.findViewById(R.id.single_friend_name);
            showname.setText(name);
         }
        public void setImg(String img, Context context)
        {
            CircleImageView imageView = (CircleImageView) mView.findViewById(R.id.single_friend_dp);
            Picasso.with(context).load(img).placeholder(R.drawable.default_avatar).into(imageView);
        }
        public void setStatus(String status)
        {
            TextView statusView = (TextView) mView.findViewById(R.id.single_friend_status);
            statusView.setText(status);
        }
    }
}


