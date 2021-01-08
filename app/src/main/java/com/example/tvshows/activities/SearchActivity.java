package com.example.tvshows.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.tvshows.R;
import com.example.tvshows.adapters.TVShowsAdapter;
import com.example.tvshows.databinding.ActivitySearchBinding;
import com.example.tvshows.listeners.TVShowListener;
import com.example.tvshows.models.TVShow;
import com.example.tvshows.viewmodels.SearchViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity implements TVShowListener {

    private ActivitySearchBinding binding;
    private SearchViewModel viewModel;
    private List<TVShow> tvShows = new ArrayList<>();
    private TVShowsAdapter tvShowsAdapter;
    private int currentPage = 1;
    private int totalAvailablePages = 1;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);

        doInitialization();
    }

    private void doInitialization() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.tvShowRecyclerView.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        tvShowsAdapter = new TVShowsAdapter(tvShows, this);
        binding.tvShowRecyclerView.setAdapter(tvShowsAdapter);
        binding.inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (!editable.toString().trim().isEmpty()) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                currentPage = 1;
                                totalAvailablePages = 1;
                                searchTVShow(editable.toString());
                            });
                        }
                    }, 800);
                } else {
                    tvShows.clear();
                    tvShowsAdapter.notifyDataSetChanged();
                }

            }
        });

        binding.tvShowRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!binding.tvShowRecyclerView.canScrollVertically(1)) {
                    if (!binding.inputSearch.getText().toString().isEmpty()) {
                        if (currentPage < totalAvailablePages) {
                            currentPage += 1;
                            searchTVShow(binding.inputSearch.getText().toString());
                        }
                    }
                }
            }
        });

        binding.inputSearch.requestFocus();
    }

    private void searchTVShow(String query) {
        toggleLoading();
        viewModel.searchTVShow(query, currentPage).observe(this, tvShowResponses -> {
            toggleLoading();
            if (tvShowResponses != null) {
                totalAvailablePages = tvShowResponses.getTotalPages();
                if (tvShowResponses.getTvShows() != null) {
                    int oldCount = tvShows.size();
                    tvShows.addAll(tvShowResponses.getTvShows());
                    tvShowsAdapter.notifyItemRangeInserted(oldCount, tvShows.size());
                }
            }
        });
    }

    private void toggleLoading() {
        if (currentPage == 1) {
            if (binding.getIsLoading() != null && binding.getIsLoading()) {
                binding.setIsLoading(false);
            } else {
                binding.setIsLoading(true);
            }
        } else {
            if (binding.getIsLoadingMore() != null && binding.getIsLoadingMore()) {
                binding.setIsLoadingMore(false);
            } else {
                binding.setIsLoadingMore(true);
            }
        }
    }

    @Override
    public void onTVShowClicked(TVShow tvShow) {
        Intent intent = new Intent(getApplicationContext(), TVShowDetailsActivity.class);
        intent.putExtra("tvShow", tvShow);
        startActivity(intent);
    }
}