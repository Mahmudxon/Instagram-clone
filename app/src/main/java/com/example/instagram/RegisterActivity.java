package com.example.instagram;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();

        auth = FirebaseAuth.getInstance();

        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });



        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd = new ProgressDialog(RegisterActivity.this);
                pd.setMessage("Please wait...");
                pd.show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                
                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) 
                        || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password))
                {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "All fields requared!", Toast.LENGTH_SHORT).show();
                }
                else if(str_password.length() < 6)
                {
                    pd.dismiss();
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    register(str_username, str_password, str_fullname, str_email);
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, StartActivity.class));
        finish();
    }

    private  void register (final String username, final String password, final String fullname, String email)
    {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            FirebaseUser user = auth.getCurrentUser();
                            String userid = user.getUid();

                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase().trim());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            hashMap.put("imgUrl", "https://firebasestorage.googleapis.com/v0/b/instagram-8cb7f.appspot.com/o/ProfilePic%2FplaceHolder.png?alt=media&token=7b739bbd-c9d6-4751-a6e4-9c3f687835c2");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        pd.dismiss();
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    finish();
                                    }
                                }
                            });

                        }
                        else
                        {
                            pd.dismiss();
                            Toast.makeText(RegisterActivity.this, "You can't register this email or password!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void findViews() {
        username = findViewById(R.id.username_reg);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password_reg);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
    }
}
