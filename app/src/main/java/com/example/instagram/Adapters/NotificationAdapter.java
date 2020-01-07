package com.example.instagram.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragment.PostDetailFragment;
import com.example.instagram.Fragment.ProfileFragment;
import com.example.instagram.MainActivity;
import com.example.instagram.Models.Notification;
import com.example.instagram.Models.Post;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context mContext;
    List<Notification> data;

    public NotificationAdapter(Context mContext, List<Notification> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.BindData(data.get(data.size() - 1 - position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView profile_img, post_img;
        TextView username, text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_img = itemView.findViewById(R.id.ni_profile_image);
            post_img = itemView.findViewById(R.id.ni_post_image);
            username = itemView.findViewById(R.id.ni_username);
            text = itemView.findViewById(R.id.ni_comment);

        }

        public void BindData(final Notification notification) {

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(notification.getUserid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(mContext).load(user.getImgUrl()).into(profile_img);
                    username.setText(user.getUsername());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            if(notification.isIspost()) {
                DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(notification.getPostid());
                postRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Post post = dataSnapshot.getValue(Post.class);
                        Glide.with(mContext).load(post.getPostimage()).into(post_img);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else
            {
                post_img.setVisibility(View.INVISIBLE);
            }
            text.setText(notification.getText());

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if(notification.isIspost()){
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", notification.getPostid()).apply();
                    ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                    MainActivity.close_meesage = "notification";
                }
                else
                    {
                        MainActivity.close_meesage = "notification";
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                        editor.putString("profileid", notification.getUserid()).apply();
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new ProfileFragment()).commit();


                    }
                }

            });

    }
}
}
