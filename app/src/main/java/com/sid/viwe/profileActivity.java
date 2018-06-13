package com.sid.viwe;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity extends AppCompatActivity {

    private CircleImageView profilecircleImageView;
    private TextView name, pstatus;
    private Button back, make_contact,dec;
    private DatabaseReference mDatabaseReference;
    private String current_state;
    private DatabaseReference rootref;
    private DatabaseReference friendreqdata;
    private DatabaseReference friendatabase;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //String uid = getIntent().getStringExtra("users_id");

        final String user_id = getIntent().getStringExtra("user_id");

        rootref=FirebaseDatabase.getInstance().getReference();
        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        friendreqdata=FirebaseDatabase.getInstance().getReference().child("Contact_req");
        back=(Button) findViewById(R.id.custom_back);
        profilecircleImageView=(CircleImageView) findViewById(R.id.profile_image);
        pstatus=(TextView) findViewById(R.id.profile_status);
        name=(TextView) findViewById(R.id.profile_name);
        make_contact=(Button) findViewById(R.id.make_contact);
        dec=(Button) findViewById(R.id.decline_req);
        friendatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        current_state = "not_friends";
        dec.setEnabled(false);
        dec.setVisibility(View.INVISIBLE);

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String disp_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String img = dataSnapshot.child("image").getValue().toString();

                name.setText(disp_name);
                pstatus.setText(status);
                Picasso.with(profileActivity.this).load(img).placeholder(R.drawable.default_avatar).fit().into(profilecircleImageView);

                //---------------------------FRIENDS LIST--------------------------

                friendreqdata.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id)) {
                            String requestContact = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (requestContact.equals("received"))
                            {
                                current_state="request_received";
                                make_contact.setText(R.string.accept_contact_req);
                                dec.setVisibility(View.VISIBLE);
                                dec.setEnabled(true);
                            }
                            else if(requestContact.equals("sent"))
                            {
                                current_state="request_sent";
                                make_contact.setText(R.string.cancel_contact_req);
                                dec.setVisibility(View.INVISIBLE);
                                dec.setEnabled(false);
                            }
                        }
                        else
                        {
                            friendatabase.child(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id))
                                    {
                                        current_state="friends";
                                        make_contact.setEnabled(true);
                                        make_contact.setText(R.string.unfriend);
                                        dec.setVisibility(View.INVISIBLE);
                                        dec.setEnabled(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        make_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                make_contact.setEnabled(false);

                //--------------------------NOT CONTACT STATE---------------------------
                if(current_state.equals("not_friends"))
                {
                    dec.setEnabled(false);
                    dec.setVisibility(View.INVISIBLE);
                    friendreqdata.child(firebaseUser.getUid()).child(user_id).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {

                            friendreqdata.child(user_id).child(firebaseUser.getUid()).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {

                                    if(task.isSuccessful())
                                    {
                                        make_contact.setEnabled(true);
                                        current_state="request_sent";
                                        make_contact.setText(R.string.cancel_contact_req);
                                    }
                                    else
                                    {
                                        Toast.makeText(profileActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }


                //----------------------------CANCEL REQUEST STATE-------------------
                if (current_state.equals("request_sent"))
                {

                    friendreqdata.child(firebaseUser.getUid()).child(user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            friendreqdata.child(user_id).child(firebaseUser.getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        current_state="not_friends";
                                        Toast.makeText(profileActivity.this,"Done",Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(profileActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    make_contact.setEnabled(true);
                    make_contact.setText(R.string.make_new_contact);
                }


                //---------------------REQUEST RECEIVED STATE------------------------
                if(current_state.equals("request_received"))
                {

                    final String curDate = DateFormat.getDateTimeInstance().format(new Date());
                    friendatabase.child(firebaseUser.getUid()).child(user_id).child("date").setValue(curDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            friendatabase.child(user_id).child(firebaseUser.getUid()).child("date").setValue(curDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {


                                    friendreqdata.child(firebaseUser.getUid()).child(user_id).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {


                                            friendreqdata.child(user_id).child(firebaseUser.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {


                                                    if(task.isSuccessful())
                                                    {
                                                        make_contact.setEnabled(true);
                                                        current_state="friends";
                                                        make_contact.setText("Unfriend");
                                                        dec.setVisibility(View.INVISIBLE);
                                                        dec.setEnabled(false);
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(profileActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                        }
                                    });

                                }
                            });

                        }
                    });

                }


                //-------------------------UNFRIEND STATE------------------------
                if(current_state.equals("friends"))
                {
                    friendatabase.child(firebaseUser.getUid()).child(user_id).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            friendatabase.child(user_id).child(firebaseUser.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful())
                                    {
                                        current_state="not_friends";
                                        dec.setEnabled(false);
                                        dec.setVisibility(View.INVISIBLE);
                                    }
                                    else
                                    {
                                        Toast.makeText(profileActivity.this,"Some Error Occurred",Toast.LENGTH_LONG).show();
                                    }

                                }
                            });

                        }
                    });
                    make_contact.setEnabled(true);
                    make_contact.setText(R.string.make_new_contact);
                }
            }
        });

        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                friendreqdata.child(firebaseUser.getUid()).child(user_id).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        friendreqdata.child(user_id).child(firebaseUser.getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {


                                if(task.isSuccessful())
                                {
                                    current_state="not_friends";
                                    make_contact.setEnabled(true);
                                    make_contact.setText(R.string.make_new_contact);
                                    dec.setEnabled(false);
                                    dec.setVisibility(View.INVISIBLE);
                                }
                                else
                                {
                                    Toast.makeText(profileActivity.this,"Cannot decline",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii = new Intent(profileActivity.this,UsersActivity.class);
                startActivity(ii);
                finish();
            }
        });

    }
}
