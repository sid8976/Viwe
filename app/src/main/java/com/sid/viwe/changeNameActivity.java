package com.sid.viwe;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class changeNameActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private Toolbar mToolbar;
    private Button change_name;
    private EditText name;
    private FirebaseUser mFirebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_user_id = mFirebaseUser.getUid();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);

        mToolbar=(Toolbar) findViewById(R.id.changeNameToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Name");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = (EditText) findViewById(R.id.changenameText);
        change_name = (Button) findViewById(R.id.updt_name);


        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newname = name.getText().toString();

                if(!TextUtils.isEmpty(newname))
                {
                    mDatabaseReference.child("name").setValue(newname).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if(task.isSuccessful())
                            {
                                Toast.makeText(changeNameActivity.this,"Successfully Updated",Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(changeNameActivity.this,settingsActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(changeNameActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(changeNameActivity.this,"Field is Empty",Toast.LENGTH_SHORT).show();
                }

                    }
        });



    }
}
