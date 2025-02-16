package com.example.exo8;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SncfApiService {
    @GET("v1/coverage/sncf/journeys")
    Call<JourneyResponse> getJourneys(
            @Query("from") String from,
            @Query("to") String to,
            @Query("datetime") String datetime
    );

    @GET("v1/coverage/sncf/places") // Added @GET and the endpoint

    Call<PlaceResponse> getStations(@Query("q") String query);
}
