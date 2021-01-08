package com.example.tvshows.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.tvshows.repositories.SearchTVShowRepository;
import com.example.tvshows.responses.TVShowResponses;

public class SearchViewModel extends ViewModel {

    private SearchTVShowRepository repository;

    public SearchViewModel(){
        repository=new SearchTVShowRepository();
    }

    public LiveData<TVShowResponses> searchTVShow(String query,int page){
        return repository.searchTVShow(query,page);
    }
}
