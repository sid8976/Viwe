package com.sid.viwe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class deleteuserActivity extends AppCompatActivity {

    private Button del_user;
    private EditText user_email,user_pass;
    private ProgressDialog mProgressDialog;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deleteuser);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        user_email=(EditText) findViewById(R.id.del_acc_email);
        user_pass=(EditText) findViewById(R.id.del_acc_pass);
        del_user = (Button) findViewById(R.id.de_delete_acc);
        del_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder ad = new AlertDialog.Builder(deleteuserActivity.this);
                ad.setTitle("Are you sure ?").setMessage("This will permanently delete your account").
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mProgressDialog= new ProgressDialog(deleteuserActivity.this);
                        mProgressDialog.setTitle("Please wait while we delete your account...");
                        mProgressDialog.setMessage("We are sorry to hear that you delete your VIWE account");
                        mProgressDialog.setCanceledOnTouchOutside(false);
                        mProgressDialog.show();
                        if(user_email.getText().toString().isEmpty() || user_pass.getText().toString().isEmpty())
                        {
                            mProgressDialog.hide();
                            Toast.makeText(deleteuserActivity.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String email,pass;
                            email=user_email.getText().toString();
                            pass=user_pass.getText().toString();
                            user=FirebaseAuth.getInstance().getCurrentUser();
                            AuthCredential authCredential = EmailAuthProvider.getCredential(email,pass);
                            user.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Deletion of Account is Successful",Toast.LENGTH_LONG).show();
                                                    Intent i = new Intent(deleteuserActivity.this,homeActivity.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                                else
                                                {
                                                    mProgressDialog.hide();
                                                    Toast.makeText(deleteuserActivity.this,"Some Error Occurred",Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {

                                    }
                                }
                            });
                        }
                    }
                }).
                setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                ad.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(deleteuserActivity.this,settingsActivity.class);
        startActivity(i);
    }
}
