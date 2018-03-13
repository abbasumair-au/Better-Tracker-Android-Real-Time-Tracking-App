package com.example.murtaza.bettertracker.ui.circles;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.ui.friendlist.FriendList;
import com.example.murtaza.bettertracker.ui.friends.Friend;
import com.example.murtaza.bettertracker.ui.friends.ViewFriend;
import com.example.murtaza.bettertracker.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by murtaza on 2/13/18.
 */

public class CircleAdapter extends RecyclerView.Adapter<CircleAdapter.ViewHolder>{

    private List<CircleModel> model;
    private Context context;

    public CircleAdapter(List<CircleModel> model, Context context){
        this.model = model;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_circle, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CircleModel circleModel = model.get(position);

        holder.circleHeading.setText(circleModel.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AppCompatActivity activity = (AppCompatActivity) view.getContext();

//                Log.d("myid",circleModel.getId());
                Bundle bundle = new Bundle();
                bundle.putString("circleId",circleModel.getId());
                Fragment fragment = null;

                fragment = new Friend();
                fragment.setArguments(bundle);

                if(fragment != null) {
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.content_main, fragment);
                    ft.commit();
                }
//
//                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//                drawer.closeDrawer(GravityCompat.START);

//                new Circle().openFriendList(circleModel.getId());

//                context.startActivity(new Intent(context, FriendList.class));

            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView circleHeading;
        TextView circleDesc;
        public ViewHolder(View itemView) {
            super(itemView);
            circleHeading = (TextView) itemView.findViewById(R.id.circleHeading);
//            circleDesc = (TextView) itemView.findViewById(R.id.circleDesc);
        }
    }
}
