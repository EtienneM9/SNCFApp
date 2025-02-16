package com.example.exo8;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PlaceResponse {

    @SerializedName("places")
    private List<Place> places;

    public List<Place> getPlaces() {
        return places;
    }

    public static class Place {
        @SerializedName("id")
        private String id;

        @SerializedName("name")
        private String name;

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
