package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.Models.User;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {


    ImageView close, profile_img;
    TextInputEditText username, fullname, bio;
    TextView change;
    FirebaseUser firebaseUser;
    Button save;

    Uri imgUri;
    StorageTask uploadTask;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        findViews();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("ProfilePic");


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                fullname.setText(user.getFullname());
                username.setText(user.getUsername());
                bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getImgUrl()).placeholder(R.drawable.place_holder).into(profile_img);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        setClickListerner();


    }
////
//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(this, MainActivity.class));
//        super.onBackPressed();
//    }

    private void setClickListerner() {

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
    //            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
                finish();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);

            }
        });


        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(EditProfileActivity.this);

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoadProfileInfo(fullname.getText().toString(), username.getText().toString(), bio.getText().toString());


            }
        });

    }

    private void findViews() {
        close = findViewById(R.id.pch_close);
        profile_img = findViewById(R.id.pch_prifile_img);
        username = findViewById(R.id.pch_username);
        fullname = findViewById(R.id.pch_fullname);
        bio = findViewById(R.id.pch_bio);
        change = findViewById(R.id.pch_change_photo);
        save = findViewById(R.id.pch_save);
    }


    void upLoadProfileInfo(String fullname, String username, String bio)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("username", username.toLowerCase());
        hashMap.put("fullname", fullname);
        hashMap.put("bio", bio);

        reference.updateChildren(hashMap);

      //  startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
        finish();
    }

    private String getFileExtention(Uri imgUrl) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(contentResolver.getType(imgUrl));
    }

    void uploadImage()
    {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if(imgUri != null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtention(imgUri));

            uploadTask = fileReference.putFile(imgUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String s = downloadUri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                                                            .child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imgUrl", s);

                        reference.updateChildren(hashMap);
                        pd.dismiss();
                }
                
                else
                {
                    Toast.makeText(EditProfileActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        }
        else
        {
            Toast.makeText(this, "No image selected!", Toast.LENGTH_SHORT).show();
            pd.dismiss();
        }

    }







    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUri = result.getUri();
            uploadImage();
        }
        else
        {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
        }


    }
}
