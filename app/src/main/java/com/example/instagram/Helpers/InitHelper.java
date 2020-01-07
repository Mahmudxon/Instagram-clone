package com.example.instagram.Helpers;

import android.app.Application;
import android.content.Context;

import com.google.firebase.FirebaseApp;

public class InitHelper extends Application {
  public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseOptions options = new FirebaseOptions.Builder()
//                .setApiKey("AIzaSyCa-AeUo3UqTWu2ZvhhDVEkNeWQDBYn6Is ")
////                .setApplicationId("instagram-8cb7f")
////                .setDatabaseUrl("https://console.firebase.google.com/project/instagram-8cb7f/database/instagram-8cb7f/data")
////                .setStorageBucket()
////
//                .setApplicationId("<AppID>")
//                .setApiKey("<APIKey>")
//                .setDatabaseUrl("https://<url>.firebaseio.com")
//                .setStorageBucket("*.appspot.com")
//                .setGcmSenderId("<senderID")
//                .build();
        FirebaseApp.initializeApp(getApplicationContext());

        /*
        * var options = new FirebaseOptions.Builder()
      .SetApplicationId("<AppID>")
      .SetApiKey("<APIKey>")
      .SetDatabaseUrl("https://<url>.firebaseio.com")
      .SetStorageBucket("*.appspot.com")
      .SetGcmSenderId("<senderID").Build();
      var fapp = FirebaseApp.InitializeApp(this, options);
        * */

        context = getApplicationContext();
    }
}
