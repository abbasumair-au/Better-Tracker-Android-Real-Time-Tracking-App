package com.example.murtaza.bettertracker.ui.places;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.ui.friends.FriendAdapter;
import com.example.murtaza.bettertracker.ui.friends.FriendModel;
import com.example.murtaza.bettertracker.ui.friends.location.FriendLocation;

import java.util.List;

/**
 * Created by murtaza on 2/18/18.
 */


    public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

        private List<PlaceModel> model;
        private Context context;

        public PlaceAdapter(List<PlaceModel> model, Context context){
            this.model = model;
            this.context = context;
        }

        @Override
        public PlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_place, parent, false);
            return new PlaceAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PlaceAdapter.ViewHolder holder, int position) {
            final PlaceModel placeModel = model.get(position);

            holder.placeName.setText(placeModel.getPlaceName());

//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    context.startActivity(new Intent(context, FriendLocation.class).putExtra("friendId", friendModel.getId()));
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView placeName;
            ImageView placeImg;
            public ViewHolder(View itemView) {
                super(itemView);
                placeName = (TextView) itemView.findViewById(R.id.placeName);
//            placeImg = (ImageView) itemView.findViewById(R.id.placeImg);
            }
        }


}
