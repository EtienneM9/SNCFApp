package com.example.exo8;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class JourneyResponse {

    @SerializedName("journeys")
    private List<Journey> journeys;

    @SerializedName("places")
    private List<Place> places; // Liste des suggestions de gares

    public List<Journey> getJourneys() {
        return journeys;
    }

    public List<Place> getPlaces() {
        return places;
    }

    // Classe interne pour représenter un trajet
    public static class Journey {
        @SerializedName("departure_date_time")
        private String departureTime;

        @SerializedName("arrival_date_time")
        private String arrivalTime;

        @SerializedName("fare")
        private Fare fare;

        @SerializedName("id")
        private String id;

        @SerializedName("sections")
        private List<Section> sections;


        public String getDepartureTime() {
            return departureTime;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public String getFare() {
            return fare != null ? fare.total.getValue() / 100 + "€" : "N/A";
        }

        public String getId() {
            return id;
        }

        // Récupérer le type de train
        public String getTypeTrain() {
            if (sections != null) {
                for (Section section : sections) {
                    if (section.displayInformations != null && section.displayInformations.commercialMode != null) {
                        return section.displayInformations.commercialMode;
                    }
                }
            }
            return "Inconnu";
        }

        public static class Fare {
            @SerializedName("total")
            private Total total;
        }

        public static class Total {
            @SerializedName("value")
            private int value;  // Stocké en centimes

            public int getValue() {
                return value;
            }
        }

        public static class Section {
            @SerializedName("display_informations")
            private DisplayInformations displayInformations;
        }

        public static class DisplayInformations {
            @SerializedName("commercial_mode")
            private String commercialMode;
        }
    }


    // Classe interne pour représenter une gare (place)
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
