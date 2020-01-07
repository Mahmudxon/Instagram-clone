package com.example.instagram.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.instagram.Fragment.PostDetailFragment;
import com.example.instagram.Fragment.ProfileFragment;
import com.example.instagram.MainActivity;
import com.example.instagram.Models.Post;
import com.example.instagram.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class MyFotosAdapter extends RecyclerView.Adapter<MyFotosAdapter.ViewHolder> {


    List<Post> data;
    Context context;

    public MyFotosAdapter(List<Post> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new  MyFotosAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.fotos_item, parent, false));
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
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView  = itemView.findViewById(R.id.fotos_img);
        }

        public void BindData(final Post post) {

            Glide.with(context).load(post.getPostimage()).into(imageView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(MainActivity.close_meesage.equals("search"))
                        MainActivity.close_meesage = "profile_b";
                    else
                        if(MainActivity.close_meesage.equals("notification"))
                            MainActivity.close_meesage = "profile_c";
                        else
                    MainActivity.close_meesage = "profile";

                        SharedPreferences.Editor editor = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                    editor.putString("postid", post.getPostid()).apply();
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                }
            });
        }
    }
}
