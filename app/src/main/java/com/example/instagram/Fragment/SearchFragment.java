package com.example.instagram.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.instagram.Adapters.UserAdapter;
import com.example.instagram.Models.User;
import com.example.instagram.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class SearchFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> users;
    EditText search_bar;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        this.view = view;

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_search);
        users = new ArrayList<>();
        adapter = new UserAdapter(users, getContext());
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);


        search_bar = (EditText) findViewById(R.id.search_bar);
        readAllUsers();
        search_bar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

     View findViewById(@IdRes int id)
     {
         return view.findViewById(id);
     }


     void searchUsers(String s)
     {
         Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                 .startAt(s)
                 .endAt(s+ "\uf8ff");
         query.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 users.clear();
                 for(DataSnapshot snapshot:dataSnapshot.getChildren())
                 {
                     User user = snapshot.getValue(User.class);
                     users.add(user);
                 }

                 adapter.notifyDataSetChanged();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }


     void readAllUsers()
     {
         DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
         reference.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if(search_bar.getText().toString().equals("")) {
                     for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                         User user = snapshot.getValue(User.class);
                         users.add(user);
                     }
                     adapter.notifyDataSetChanged();
                 }
             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
     }
}
