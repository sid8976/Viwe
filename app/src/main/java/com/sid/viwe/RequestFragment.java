package com.sid.viwe;


import android.content.Context;
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
public class RequestFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference usersdata, reqdata;
    private View view;
    private String current_user_id;
    private RecyclerView reqRecycle;

    public RequestFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_request, container, false);
        reqRecycle = (RecyclerView) view.findViewById(R.id.requestRecycler);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        reqdata = FirebaseDatabase.getInstance().getReference().child("Contact_req").child(current_user_id);
        usersdata = FirebaseDatabase.getInstance().getReference().child("Users");
        reqdata.keepSynced(true);
        usersdata.keepSynced(true);
        reqRecycle.setHasFixedSize(true);
        reqRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Request, requestViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Request, requestViewholder>
                (
                        Request.class,
                        R.layout.single_request_layout,
                        requestViewholder.class,
                        reqdata
                )
        {
            @Override
            protected void populateViewHolder(final requestViewholder viewHolder, Request model, int position) {

                final String list_user_id = getRef(position).getKey();
                usersdata.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String img = dataSnapshot.child("thumb_image").getValue().toString();

                        viewHolder.setName(userName);
                        viewHolder.setImg(img,getContext());

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent pro = new Intent(getContext(),profileActivity.class);
                                pro.putExtra("user_id",list_user_id);
                                startActivity(pro);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };
        reqRecycle.setAdapter(firebaseRecyclerAdapter);
    }

    public static class requestViewholder extends RecyclerView.ViewHolder
    {
        View mView;
        public requestViewholder(View itemView) {
            super(itemView);
            mView=itemView;
        }
        public void setName(String name)
        {
            TextView tv = (TextView) mView.findViewById(R.id.single_request_name);
            tv.setText(name);
        }
        public void setImg(String img, Context context)
        {
            CircleImageView imageView = mView.findViewById(R.id.single_request_image);
            Picasso.with(context).load(img).placeholder(R.drawable.default_avatar).fit().into(imageView);
        }
    }

}
