package com.example.tvshows.listeners;

import com.example.tvshows.models.TVShow;

public interface WatchListListener {

    void onTVShowClicked(TVShow tvShow);

    void removeTVShowFromWatchList(TVShow tvShow, int position);
}
