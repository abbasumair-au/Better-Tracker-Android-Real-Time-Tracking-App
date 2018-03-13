package com.example.murtaza.bettertracker.ui.friends.individualplaces;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.murtaza.bettertracker.R;

import java.util.List;

/**
 * Created by murtaza on 2/18/18.
 */


    public class IPlaceAdapter extends RecyclerView.Adapter<IPlaceAdapter.ViewHolder> {

        private List<IPlaceModel> model;
        private Context context;

        public IPlaceAdapter(List<IPlaceModel> model, Context context){
            this.model = model;
            this.context = context;
        }

        @Override
        public IPlaceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.list_place, parent, false);
            return new IPlaceAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(IPlaceAdapter.ViewHolder holder, int position) {
            final IPlaceModel placeModel = model.get(position);

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
