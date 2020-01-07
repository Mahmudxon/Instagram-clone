package com.example.instagram.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.instagram.Adapters.MyFotosAdapter;
import com.example.instagram.EditProfileActivity;

import com.example.instagram.Helpers.InitHelper;
import com.example.instagram.Models.Post;
import com.example.instagram.Models.User;
import com.example.instagram.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileFragment extends Fragment {

    TextView username, posts, followers, following, fullname, bio, info;
    ImageView profilePic;
    Button btn_edit;

    User user;
    FirebaseUser firebaseUser;

    DatabaseReference reference;

    String userid;

    RecyclerView recyclerView;
    MyFotosAdapter fotosAdapter;
    List<Post> postList;


    ImageButton grid, saved;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        username = view.findViewById(R.id.pr_username);
        posts = view.findViewById(R.id.pr_count_posts);
        followers = view.findViewById(R.id.pr_count_followers);
        following = view.findViewById(R.id.pr_count_following);
        profilePic = view.findViewById(R.id.pr_profile_img);
        btn_edit = view.findViewById(R.id.pr_edit_btn);

        fullname = view.findViewById(R.id.pr_full_name);
        info = view.findViewById(R.id.pr_info_text);
        bio = view.findViewById(R.id.pr_bio_text);

        grid = view.findViewById(R.id.pr_grid);
        saved = view.findViewById(R.id.pr_saved);

        grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosts(userid, posts);
            }
        });
        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSavedPostsList();
            }
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userid = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE).getString("profileid", "nano");

        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    setUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!firebaseUser.getUid().equals(userid))
        {
            saved.setEnabled(false);
        }

        recyclerView = view.findViewById(R.id.pr_list_posts);
        postList = new ArrayList<>();
        fotosAdapter = new MyFotosAdapter(postList, getContext());
        recyclerView.setAdapter(fotosAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));


        getPosts(userid, posts);
        countFollowers(userid, followers);
        countFollowing(userid, following);


            btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(firebaseUser.getUid().equals(user.getId()))
                    {

                        Intent intent = new Intent(getContext(), EditProfileActivity.class);
                       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                       // getActivity().finish();

                    }
                    else
                    {
                        if (btn_edit.getText().toString().equals("follow")) {
                            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                    .child("following").child(user.getId()).setValue(true);
                            FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                                    .child("followers").child(firebaseUser.getUid()).setValue(true);


                        } else {
                            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                                    .child("following").child(user.getId()).removeValue();
                            FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                                    .child("followers").child(firebaseUser.getUid()).removeValue();

                        }
                    }
                }
            });

        return view;
    }



    void countFollowing(String userid, final TextView textView)
    {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(userid).child("following");

            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    textView.setText(dataSnapshot.getChildrenCount() + "");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }

    void countFollowers(String userid, final TextView textView)
    {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Follow").child(userid).child("followers");

            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    textView.setText(dataSnapshot.getChildrenCount() + "");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void setUserInfo() {

        username.setText(user.getUsername());
        fullname.setText(user.getFullname());

        bio.setText(user.getBio());

        if(user.getUsername().trim().toLowerCase().equals("mahmudxon"))
        {
            info.setVisibility(View.VISIBLE);
            info.setText("Developer");
        }

        Glide.with(InitHelper.context).load(user.getImgUrl()).into(profilePic);

        if(firebaseUser.getUid().equals(user.getId()))
        {
            btn_edit.setText("Edit Profile");
        }

        else
        {
                isFollowing(user.getId(), btn_edit);
        }


    }



    private void isFollowing(final String userid, final Button button) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(userid).exists()) {
                    button.setText("following");
                } else {
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    void getPosts(final String userid, final TextView textView)
    {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Posts");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                postList.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {

                    Post post = snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(userid))
                    postList.add(post);
                }
                textView.setText(postList.size() + "");
                fotosAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




   void  getSavedPostsList()
    {
        final ArrayList<String> savedPosts = new ArrayList<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref =  FirebaseDatabase.getInstance().getReference("Saved")
                .child(firebaseUser.getUid());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    savedPosts.add(snapshot.getKey());

                }

               DatabaseReference rf = FirebaseDatabase.getInstance().getReference("Posts");
                rf.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                        postList.clear();

                        for (DataSnapshot snapshot : dataSnapshot1.getChildren())
                        {
                            Post post = snapshot.getValue(Post.class);
                            if(savedPosts.indexOf(post.getPostid()) >= 0 && savedPosts.indexOf(post.getPostid()) < savedPosts.size())
                            {
                                postList.add(post);
                            }
                        }
                        fotosAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

}
