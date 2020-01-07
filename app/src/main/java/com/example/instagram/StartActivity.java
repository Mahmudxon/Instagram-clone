package com.example.instagram;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    Button login, register;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        login = findViewById(R.id.btn_login);
        register = findViewById(R.id.btn_register);

      login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(StartActivity.this, LoginActivity.class));
              finish();
          }
      });

      register.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              startActivity(new Intent(StartActivity.this, RegisterActivity.class));
              finish();
          }
      });
    }

    @Override
    protected void onStart() {
        super.onStart();


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null)
        {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
}
