package com.sid.viwe;

import android.app.ProgressDialog;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class loginActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText eemail, pass;
    private Button login, forgot_pass;
    private FirebaseAuth mAuth;
    private ProgressDialog log_progressDialog;

    @Override
    public void onBackPressed() {
        Intent home = new Intent(loginActivity.this,homeActivity.class);
        startActivity(home);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mAuth=FirebaseAuth.getInstance();
        mToolbar=(Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        log_progressDialog=new ProgressDialog(loginActivity.this);

        eemail= (EditText) findViewById(R.id.email);
        pass= (EditText) findViewById(R.id.pass);

        login=(Button) findViewById(R.id.mlogin);
        forgot_pass=(Button) findViewById(R.id.forgot_pass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_progressDialog.setTitle("Logging in as you");
                log_progressDialog.setMessage("Please wait until we check your credentials...");
                log_progressDialog.setCanceledOnTouchOutside(false);
                log_progressDialog.show();

                String email=eemail.getText().toString();
                String password=pass.getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        loginUser(email, password);
                }
                else
                {
                    log_progressDialog.dismiss();
                    Toast.makeText(loginActivity.this,"Login fields are empty",Toast.LENGTH_LONG).show();
                }
            }
        });
        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=eemail.getText().toString();
                if(email.isEmpty())
                {
                    Toast.makeText(loginActivity.this,"Email is required",Toast.LENGTH_LONG).show();
                }
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Toast.makeText(loginActivity.this,"Reset password mail sent to your email",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            Toast.makeText(loginActivity.this,"Some error occurred",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });

    }

    private void sendtohome()
    {

        Intent i = new Intent(loginActivity.this,homeActivity.class);
        startActivity(i);
        finish();
    }

    private void loginUser(String email, String password)
    {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    log_progressDialog.dismiss();
                    FirebaseUser user = mAuth.getCurrentUser();
                    boolean verified = user.isEmailVerified();
                    if(!verified==true)
                    {
                        Toast.makeText(loginActivity.this,"Your Email is not Verified.",Toast.LENGTH_LONG).show();
                        sendtohome();
                        finish();
                    }
                    else {
                        Intent i = new Intent(loginActivity.this, mainActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
                else
                {
                    log_progressDialog.hide();
                    Toast.makeText(loginActivity.this,"Some Error Occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
