package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.instagram.Adapters.CommentAdapter;
import com.example.instagram.Models.Comment;
import com.example.instagram.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommitActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView profileImg;
    EditText commitText;
    TextView post;

    String postid;
    String publisherid;

    FirebaseUser firebaseUser;

    RecyclerView listCommits;
    CommentAdapter adapter;
    List<Comment> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commit);

        toolbar = findViewById(R.id.action_bar_comments);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        profileImg = findViewById(R.id.img_commit_prof);
        listCommits = findViewById(R.id.comments_list);
        post = findViewById(R.id.post);
        commitText = findViewById(R.id.add_commit_edittext);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        postid = getIntent().getStringExtra("postid");
        publisherid = getIntent().getStringExtra("publisher");

        listCommits = findViewById(R.id.comments_list);
        data = new ArrayList<>();
        adapter = new CommentAdapter(this, data);
        listCommits.setLayoutManager(new LinearLayoutManager(this));
        listCommits.setAdapter(adapter);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            data.clear();

            for (DataSnapshot snapshot : dataSnapshot.getChildren())
            {
                Comment comment = snapshot.getValue(Comment.class);
                data.add(comment);

            }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commitText.getText().toString().trim().equals("")){
                    Toast.makeText(CommitActivity.this, "You can't post empty post!", Toast.LENGTH_SHORT).show();
                }
                else {
                    postComment();
                }
            }
        });
        getImage();
    }

    void  postComment()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", commitText.getText().toString());
        hashMap.put("publisher", firebaseUser.getUid());

        reference.push().setValue(hashMap);
        commitText.setText("");



        HashMap<String, Object> hashMap1 =  new HashMap<>();
        hashMap1.put("userid", firebaseUser.getUid());
        hashMap1.put("text", "Comment on your post");
        hashMap1.put("postid", postid);
        hashMap1.put("ispost", true);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications")
                .child(publisherid);
        String notId = ref.push().getKey();

        ref.child(notId).setValue(hashMap1);

    }

    void getImage()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(CommitActivity.this).load(user.getImgUrl()).into(profileImg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
