package com.example.instagram;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class PostActivity extends AppCompatActivity {

    ImageView close;
    Button post;
    ImageView img;
    EditText desc;

    Uri imgUrl;
    StorageTask uploadTask;
    StorageReference storageReference;
    String myUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        findViews();

        storageReference = FirebaseStorage.getInstance().getReference("Posts");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg();
            }
        });


        CropImage.activity()
                .setAspectRatio(1, 1)
                .start(PostActivity.this);

    }

    private void uploadImg() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Posting");
        pd.show();

        if(imgUrl != null)
        {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + getFileExtention(imgUrl));
            uploadTask = fileReference.putFile(imgUrl);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful())
                    {
                        throw task.getException();
                    }
               return fileReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = reference.push().getKey();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myUrl);
                        hashMap.put("description", desc.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                        reference.child(postid).setValue(hashMap);
                        pd.dismiss();
                        a_finish();
                    }
                    else
                    {
                        Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                        a_finish();
                    }
                }
            });
        }
        else
        {
            Toast.makeText(this, "No Image selected!", Toast.LENGTH_SHORT).show();
            a_finish();
        }
    }

    private String getFileExtention(Uri imgUrl) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return  mime.getExtensionFromMimeType(contentResolver.getType(imgUrl));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imgUrl = result.getUri();
            img.setImageURI(imgUrl);
        }
        else
        {
            Toast.makeText(this, "Something gone wrong!", Toast.LENGTH_SHORT).show();
            a_finish();

        }
    }

    private void findViews() {
    close = findViewById(R.id.close_post);
    post = findViewById(R.id.btn_post);
    img = findViewById(R.id.img_add_post);
    desc = findViewById(R.id.post_description);
    close.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            a_finish();
        }
    });
    }

    @Override
    public void onBackPressed() {
        a_finish();
    }

    private void a_finish() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
