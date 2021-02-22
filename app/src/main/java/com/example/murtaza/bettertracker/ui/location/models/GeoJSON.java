
package com.example.murtaza.bettertracker.ui.location.models;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeoJSON {

    private final String FILENAME = "geo.json";
    @SerializedName("locations")
    @Expose
    private List<Location> locations = null;

    public GeoJSON(){
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FILENAME);
        Log.d("Path", file.toString());
        if(!file.exists()){
            try {
                Boolean test = file.createNewFile();
                Log.d("Created", test.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public List<Location> getLocations() {
        Gson gson = new Gson();
        BufferedReader br = null;
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FILENAME);
        try {
            br = new BufferedReader(new FileReader(file));
//            GeoJSON myGson = gson.fromJson(br, GeoJSON.class);
            locations = (List<Location>) gson.fromJson(br,List.class);

//            for (Location location : locations){
//                Log.i("Member name: ", location.getGeometry().toString());
//            }
            if(locations == null){
                locations = new ArrayList<>();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return locations;
    }

    public void add(Location location){
        locations.add(location);
    }
    public void update(){

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + FILENAME);

        if(file.exists()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(file);
                Gson gson = new Gson();
                String json = gson.toJson(locations);
                writer.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void clear() {
        locations = new ArrayList<>();
        update();
    }
//    public void setLocations(List<Location> locations) {
//        this.locations = locations;
//    }

}
