package com.example.murtaza.bettertracker.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.ui.circles.CircleAdapter;
import com.example.murtaza.bettertracker.ui.circles.CircleModel;
import com.example.murtaza.bettertracker.ui.friends.location.FriendLocation;

import java.util.List;

/**
 * Created by murtaza on 2/17/18.
 */

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private List<FriendModel> model;
    private Context context;

    public FriendAdapter(List<FriendModel> model, Context context){
        this.model = model;
        this.context = context;
    }

    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.list_friend, parent, false);
        return new FriendAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendAdapter.ViewHolder holder, int position) {
        final FriendModel friendModel = model.get(position);

        holder.friendName.setText(friendModel.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, FriendLocation.class).putExtra("friendId", friendModel.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        ImageView friendImg;
        public ViewHolder(View itemView) {
            super(itemView);
            friendName = (TextView) itemView.findViewById(R.id.friendName);
//            friendImg = (ImageView) itemView.findViewById(R.id.friendImg);
        }
    }

}
