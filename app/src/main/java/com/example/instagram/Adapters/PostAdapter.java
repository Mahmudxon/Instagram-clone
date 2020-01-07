package com.example.instagram.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.CommitActivity;
import com.example.instagram.MainActivity;
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

import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    List<Post> posts;
    Context mContext;
    FirebaseUser firebaseUser;

    public PostAdapter(List<Post> posts, Context mContext) {
        this.posts = posts;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.BindData(posts.get(posts.size() - position - 1));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView profile, post_img, like, comment, save;
        TextView username, likes, publisher, description, comments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.img_publisher);
            username = itemView.findViewById(R.id.publisher_username);
            post_img = itemView.findViewById(R.id.img_post);
            like = itemView.findViewById(R.id.post_heart);
            comment = itemView.findViewById(R.id.post_commit);
            save = itemView.findViewById(R.id.save_post);
            likes = itemView.findViewById(R.id.likes_post);
            publisher = itemView.findViewById(R.id.publisher_post);
            description = itemView.findViewById(R.id.description_post);
            comments = itemView.findViewById(R.id.commit_post);

        }

        public void BindData(final Post post) {

            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            Glide.with(mContext).load(post.getPostimage())

                    .into(post_img);


            if(post.getDescription() == "")
            {
                description.setVisibility(View.GONE);
            }
            else
            {
                description.setVisibility(View.VISIBLE);
                description.setText(post.getDescription());
            }

            publisherInfo(profile, username, publisher, post.getPublisher());
            isLike(post.getPostid(), like, likes);
            getCommentsCount(post.getPostid(), comments);
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(like.getTag().equals("like"))
                    {
                        FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).setValue(true);


                        HashMap<String, Object> hashMap =  new HashMap<>();
                        hashMap.put("userid", firebaseUser.getUid());
                        hashMap.put("text", "Like your post");
                        hashMap.put("postid", post.getPostid());
                        hashMap.put("ispost", true);

                         DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications")
                                 .child(post.getPublisher());
                         String notId = ref.push().getKey();

                         ref.child(notId).setValue(hashMap);

                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                    }


                }
            });


            isSaved(post.getPostid(), save);

            save.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    if(!save.getTag().toString().trim().equals("saved"))
                    {
                        FirebaseDatabase.getInstance().getReference("Saved")
                                .child(firebaseUser.getUid()).child(post.getPostid()).setValue(true);
                    }
                    else
                    {
                        FirebaseDatabase.getInstance().getReference("Saved")
                                .child(firebaseUser.getUid()).child(post.getPostid()).removeValue();
                    }
                }
            });


            comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommitActivity.class);
                    intent.putExtra("postid", post.getPostid());
                    intent.putExtra("publisher", post.getPublisher());
                    mContext.startActivity(intent);
                }
            });

 comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, CommitActivity.class);
                    intent.putExtra("postid", post.getPostid());
                    intent.putExtra("publisher", post.getPublisher());
                    mContext.startActivity(intent);
                }
            });

        }
    }


    void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mContext).load(user.getImgUrl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    void isLike(String postid, final ImageView imageView, final TextView likes)
    {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Likes")
                .child(postid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(firebaseUser.getUid()).exists())
                {
                    imageView.setImageResource(R.drawable.ic_liked);
                    imageView.setTag("liked");
                    likes.setText(dataSnapshot.getChildrenCount() + " likes");



                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_heart);
                    imageView.setTag("like");
                    likes.setText(dataSnapshot.getChildrenCount() + " likes");
                }

                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    void getCommentsCount(String postid, final TextView comments)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            comments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void isSaved(final String postid, final ImageView imageView)
    {


        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference =  FirebaseDatabase.getInstance().getReference("Saved")
                .child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postid).exists()){
                   imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("saved");
                }
                else
                {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("not save");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
