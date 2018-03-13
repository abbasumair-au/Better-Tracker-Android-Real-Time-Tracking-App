package com.example.murtaza.bettertracker.ui.friendlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.ui.main.MainActivity;

public class FriendList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
    }

    //    returning main activity start intent
    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, FriendList.class);
        return intent;
    }

}
