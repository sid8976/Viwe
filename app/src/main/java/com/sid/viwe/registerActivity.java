package com.sid.viwe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class registerActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mToolbar;
    private EditText disp, mail, pass;
    private Button reg;
    private FirebaseAuth mAuth;
    private ProgressDialog reg_ProgressDialog;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth=FirebaseAuth.getInstance();

        mToolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.register_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        reg_ProgressDialog=new ProgressDialog(registerActivity.this);
        disp=(EditText) findViewById(R.id.disp_name);
        mail=(EditText) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.pass);

        reg=(Button) findViewById(R.id.mregbtn);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                reg_ProgressDialog.setTitle("Registering as you.");
                reg_ProgressDialog.setMessage("Please wait until we create your account...");
                reg_ProgressDialog.setCanceledOnTouchOutside(false);
                reg_ProgressDialog.show();

                String dispname=disp.getText().toString();
                String email=mail.getText().toString();
                String password=pass.getText().toString();

                if(!TextUtils.isEmpty(dispname) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password))
                {
                    registerUser(dispname, email, password);
                }
                else
                {
                    reg_ProgressDialog.dismiss();
                    Toast.makeText(registerActivity.this,"Registration fields are empty",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void registerUser(final String dispname, String email, String password)
    {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = user.getUid();

                    mDatabaseReference=FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> usermap = new HashMap<>();
                    usermap.put("name",dispname);
                    usermap.put("status", "Hi I am a Viwe'er");
                    usermap.put("image","default");
                    usermap.put("thumb_image","default");

                    mDatabaseReference.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                reg_ProgressDialog.dismiss();
                                Intent i = new Intent(registerActivity.this,mainActivity.class);
                                startActivity(i);
                                finish();
                                sendEmailVerification();
                            }
                            else
                            {
                                Toast.makeText(registerActivity.this,"Some Error Occurred",Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                else
                {
                    reg_ProgressDialog.hide();
                    Toast.makeText(registerActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendEmailVerification() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(registerActivity.this,"Please check your email. We are verifying it's you",Toast.LENGTH_LONG).show();
                        FirebaseAuth.getInstance().signOut();
                        Intent home = new Intent(registerActivity.this,homeActivity.class);
                        startActivity(home);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(registerActivity.this,"Can't send verification mail",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(registerActivity.this,homeActivity.class);
        startActivity(home);
        finish();
    }
}
