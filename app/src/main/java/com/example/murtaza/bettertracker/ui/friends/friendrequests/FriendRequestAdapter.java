package com.example.murtaza.bettertracker.ui.friends.friendrequests;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.murtaza.bettertracker.MvpApp;
import com.example.murtaza.bettertracker.R;
import com.example.murtaza.bettertracker.data.DataManager;
import com.example.murtaza.bettertracker.network.MySingleton;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by hassans on 3/12/18.
 */

public class FriendRequestAdapter  extends RecyclerView.Adapter<FriendRequestAdapter.ViewHolder> {

    private List<FriendRequestModel> model;
    private Context context;

    final String accept_request_url = "https://geotracker-app.herokuapp.com/api/acceptRequest/";

    final String reject_request_url = "https://geotracker-app.herokuapp.com/api/rejectRequest/";

    public FriendRequestAdapter(List<FriendRequestModel> model, Context context){
        this.model = model;
        this.context = context;
    }

    @Override
    public FriendRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.friend_requests, parent, false);
        return new FriendRequestAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FriendRequestAdapter.ViewHolder holder, final int position) {
        final FriendRequestModel friendRequestModel = model.get(position);

        holder.friendName.setText(friendRequestModel.getName());

        holder.acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Toast.makeText(context, "Accept Button Clicked && Email is " + friendRequestModel.getId() , Toast.LENGTH_SHORT).show();
                  acceptRequest(friendRequestModel.getId(), new acceptRequest() {
                      @Override
                      public void onAcceptRequest(JSONObject jsonObject) {
                          Log.d("AR", String.valueOf(jsonObject));
                          Snackbar.make(view, "Friend Request Accepted", Snackbar.LENGTH_LONG)
                                  .setAction("Action", null).show();

                          model.remove(position);
                          notifyDataSetChanged();
                      }
                  });
            }
        });

        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
//                Toast.makeText(context, "Reject Button Clicked", Toast.LENGTH_SHORT).show();
                  rejectRequest(friendRequestModel.getId(), new rejectRequest() {
                      @Override
                      public void onRejectRequest(JSONObject jsonObject) {
                          Log.d("RR", String.valueOf(jsonObject));
                          Snackbar.make(view, "Friend Request Rejected", Snackbar.LENGTH_LONG)
                                  .setAction("Action", null).show();

                          model.remove(position);
                          notifyDataSetChanged();
                      }
                  });

            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
    }


    @Override
    public int getItemCount() {
        return model.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        ImageView friendImg;
        Button acceptBtn;
        Button rejectBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            acceptBtn = itemView.findViewById(R.id.acceptBtn);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
            friendImg = itemView.findViewById(R.id.friendProfilePicture);
        }
    }

    private void acceptRequest(String id, final acceptRequest ar){

        DataManager dataManager = ((MvpApp) context.getApplicationContext()).getDataManager();
        String userEmailId = dataManager.getEmailId();

        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.GET, accept_request_url+userEmailId+"&"+id ,null ,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        ar.onAcceptRequest(jsonObject);
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("newTag", String.valueOf(error));
                Toast.makeText(context, "Unknown Error Occurs", Toast.LENGTH_SHORT).show();

            }


        });

        MySingleton.getmInstance(context).addToRequestque(jobReq);
    }

    private void rejectRequest(String id, final rejectRequest rr){

        DataManager dataManager = ((MvpApp) context.getApplicationContext()).getDataManager();
        String userEmailId = dataManager.getEmailId();

        JsonObjectRequest jobReq = new JsonObjectRequest(Request.Method.GET, reject_request_url +userEmailId+"&"+id ,null ,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        rr.onRejectRequest(jsonObject);

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("newTag", String.valueOf(error));
                Toast.makeText(context, "Unknown Error Occurs", Toast.LENGTH_SHORT).show();
            }

        });

        MySingleton.getmInstance(context).addToRequestque(jobReq);
    }


    public interface acceptRequest {
        void onAcceptRequest(JSONObject jsonObject);
    }

    private interface rejectRequest {
        void onRejectRequest(JSONObject jsonObject);
    }
}
