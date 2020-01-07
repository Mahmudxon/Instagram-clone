package com.example.instagram.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragment.ProfileFragment;
import com.example.instagram.MainActivity;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> users;
    Context context;
    FirebaseUser firebaseUser;

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_list, viewGroup, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = users.get(i);
        viewHolder.fallow.setVisibility(View.VISIBLE);
        viewHolder.username.setText(user.getUsername());
        viewHolder.fullname.setText(user.getFullname());
        isFollowing(user.getId(), viewHolder.fallow);

        Glide.with(context).load(user.getImgUrl())
                .into(viewHolder.imageView);

        if (user.getId().equals(firebaseUser.getUid())) {
            viewHolder.fallow.setVisibility(View.INVISIBLE);
        }


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.close_meesage = "search";
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("profileid", user.getId()).apply();
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        viewHolder.fallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.fallow.getText().toString().equals("follow")) {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);


                    HashMap<String, Object> hashMap =  new HashMap<>();
                    hashMap.put("userid", firebaseUser.getUid());
                    hashMap.put("text", "Following you");
                    hashMap.put("postid", "null");
                    hashMap.put("ispost", false);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Notifications")
                            .child(user.getId());
                    String notId = ref.push().getKey();

                    ref.child(notId).setValue(hashMap);




                } else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, fullname;
        public Button fallow;
        public CircleImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_item);
            fullname = itemView.findViewById(R.id.fullname_item);
            imageView = itemView.findViewById(R.id.list_image_profile);
            fallow = itemView.findViewById(R.id.btn_fallow_item);
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
}
