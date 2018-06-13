package com.sid.viwe;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {
    private DatabaseReference mUsersdata;
    private Toolbar mToolbar;
    private RecyclerView mRecycler;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mToolbar=(Toolbar) findViewById(R.id.users_toolbar);
        mRecycler=(RecyclerView) findViewById(R.id.usersRecycler);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mUsersdata= FirebaseDatabase.getInstance().getReference().child("Users");

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<userss,UsersViewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<userss, UsersViewholder>(

                userss.class,
                R.layout.single_user_layout,
                UsersViewholder.class,
                mUsersdata
        ) {
            @Override
            protected void populateViewHolder(final UsersViewholder viewHolder, userss model, final int position) {

                uid = getRef(position).getKey();



                viewHolder.setName(model.getName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setImage(model.getThumb_image(),getApplicationContext());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                            Intent gotopro = new Intent(UsersActivity.this, profileActivity.class);
                            gotopro.putExtra("user_id", uid);
                            startActivity(gotopro);

                    }
                });

            }
        };
        mRecycler.setAdapter(firebaseRecyclerAdapter);
    }

    public static class UsersViewholder extends RecyclerView.ViewHolder {
        View mView;
        public UsersViewholder(View itemView) {

            super(itemView);
            mView=itemView;
        }
        public void setName(String name)
        {
            TextView singleusername = (TextView) mView.findViewById(R.id.single_user_name);
            singleusername.setText(name);
        }
        public void setStatus(String status)
        {
            TextView singlestat = (TextView) mView.findViewById(R.id.single_user_status);
            singlestat.setText(status);
        }
        public void setImage(String image, Context context)
        {
            CircleImageView singleimage = (CircleImageView) mView.findViewById(R.id.single_user_dp);
            Picasso.with(context).load(image).placeholder(R.drawable.default_avatar).fit().into(singleimage);
        }
    }

}
