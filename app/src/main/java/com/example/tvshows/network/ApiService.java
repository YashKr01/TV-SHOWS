package com.example.tvshows.network;

import com.example.tvshows.responses.TVShowResponses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("most-popular")
    Call<TVShowResponses> getMostPopularTVShows(@Query("page") int page);

}
