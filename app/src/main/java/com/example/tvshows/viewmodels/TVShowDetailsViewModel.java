package com.example.tvshows.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.tvshows.database.TVShowDatabase;
import com.example.tvshows.models.TVShow;
import com.example.tvshows.repositories.TVShowDetailsRepository;
import com.example.tvshows.responses.TVShowDetailsResponse;

import io.reactivex.Completable;

public class TVShowDetailsViewModel extends AndroidViewModel {

    private TVShowDetailsRepository tvShowDetailsRepository;
    private TVShowDatabase tvShowDatabase;

    public TVShowDetailsViewModel(@NonNull Application application) {
        super(application);
        tvShowDetailsRepository = new TVShowDetailsRepository();
        tvShowDatabase = TVShowDatabase.getTvShowDatabase(application);
    }

    public LiveData<TVShowDetailsResponse> getTVShowDetails(String tvShowId) {
        return tvShowDetailsRepository.getTvShowDetails(tvShowId);
    }

    public Completable addToWatchList(TVShow tvShow) {
        return tvShowDatabase.tvShowDao().addToWatchList(tvShow);
    }

}
