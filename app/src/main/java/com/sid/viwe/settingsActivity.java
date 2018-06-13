package com.sid.viwe;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class settingsActivity extends AppCompatActivity {

    private StorageReference dp_image;
    private CircleImageView mCircleImageView;
    private ProgressDialog mProgressDialog;
    private TextView dispname, stat;
    private Button change_img, change_status, delete_acc, change_name;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private int DP_INTENT = 1;

    @Override
    public void onBackPressed() {
        Intent i = new Intent(settingsActivity.this, mainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();
        delete_acc = (Button) findViewById(R.id.delete_acc);
        mCircleImageView = (CircleImageView) findViewById(R.id.settins_display_image);
        dispname = (TextView) findViewById(R.id.settings_disp_name);
        stat = (TextView) findViewById(R.id.settings_disp_status);

        change_img = (Button) findViewById(R.id.change_img);
        change_status = (Button) findViewById(R.id.change_status);
        change_name = (Button) findViewById(R.id.changeName);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mDatabaseReference.keepSynced(true);
        dp_image = FirebaseStorage.getInstance().getReference();

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String thumb_img = dataSnapshot.child("thumb_image").getValue().toString();

                dispname.setText(name);
                stat.setText(status);
                if (!image.equals("default")) {
                    Picasso.with(settingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default_avatar)
                            .fit().into(mCircleImageView, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                        Picasso.with(settingsActivity.this).load(image).placeholder(R.drawable.default_avatar).fit().into(mCircleImageView);                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(settingsActivity.this, "Some Error Occurred", Toast.LENGTH_LONG).show();
            }
        });

        delete_acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent deleteacc = new Intent(settingsActivity.this,deleteuserActivity.class);
                startActivity(deleteacc);

            }
        });

        change_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent changename = new Intent(settingsActivity.this,changeNameActivity.class);
                startActivity(changename);
            }
        });

        change_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(settingsActivity.this, changeStatusActivity.class);
                startActivity(i);
            }
        });
        change_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent img = new Intent();
                img.setType("image/*");
                img.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(img, "Choose your DP"), DP_INTENT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == DP_INTENT && resultCode == RESULT_OK) {
            Uri img_uri = data.getData();
            CropImage.activity(img_uri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            mProgressDialog = new ProgressDialog(settingsActivity.this);
            mProgressDialog.setTitle("Updating your new DP");
            mProgressDialog.setMessage("We are uploading your DP. Please wait...");
            mProgressDialog.show();
            mProgressDialog.setCanceledOnTouchOutside(false);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                File thumb_filepath = new File(resultUri.getPath());
                Bitmap thumb_path = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(75)
                        .compressToBitmap(thumb_filepath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_path.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                String current_user_id = mCurrentUser.getUid();
                StorageReference path = dp_image.child("dp_images").child(current_user_id + ".jpg");
                final StorageReference thumb_image_path = dp_image.child("dp_images").child("dp_thumbs").child(current_user_id+".jpg");



                path.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            final String imgurl = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_image_path.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumbimg_download = thumb_task.getResult().getDownloadUrl().toString();


                                    if(thumb_task.isSuccessful())
                                    {
                                        Map update_hashmap = new HashMap();
                                        update_hashmap.put("image",imgurl);

                                        update_hashmap.put("thumb_image",thumbimg_download);

                                        mDatabaseReference.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    mProgressDialog.dismiss();
                                                }
                                                else
                                                {
                                                    mProgressDialog.hide();
                                                    Toast.makeText(settingsActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(settingsActivity.this,"Failed",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else
                        {
                            mProgressDialog.hide();
                            Toast.makeText(settingsActivity.this,"Failed    ",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
