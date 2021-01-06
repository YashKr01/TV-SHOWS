package com.example.tvshows.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tvshows.network.ApiClient;
import com.example.tvshows.network.ApiService;
import com.example.tvshows.responses.TVShowResponses;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MostPopularTVShowsRepository {

    private ApiService apiService;

    public MostPopularTVShowsRepository() {
        apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public LiveData<TVShowResponses> getMostPopularTVShows(int page) {

        MutableLiveData<TVShowResponses> data = new MutableLiveData<>();
        apiService.getMostPopularTVShows(page).enqueue(new Callback<TVShowResponses>() {
            @Override
            public void onResponse(@NonNull Call<TVShowResponses> call, @NonNull Response<TVShowResponses> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<TVShowResponses> call, Throwable t) {
                data.setValue(null);
            }
        });

        return data;

    }

}
