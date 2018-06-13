package com.sid.viwe;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class changeStatusActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private Toolbar mToolbar;
    private Button change_stat;
    private EditText status;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_status);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String cuid = mFirebaseUser.getUid().toString();

        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(cuid);


        mToolbar=(Toolbar) findViewById(R.id.change_stat_tool);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        change_stat=(Button) findViewById(R.id.updt_stat);

        status=(EditText) findViewById(R.id.update_status);


        change_stat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String sts = status.getText().toString();
                mDatabaseReference.child("status").setValue(sts).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        if(task.isSuccessful())
                        {
                            Toast.makeText(changeStatusActivity.this,"Successfully Updated",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(changeStatusActivity.this,settingsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(changeStatusActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });



    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(changeStatusActivity.this,settingsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
