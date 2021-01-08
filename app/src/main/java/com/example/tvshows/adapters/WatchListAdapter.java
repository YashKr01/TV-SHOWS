package com.example.tvshows.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvshows.R;
import com.example.tvshows.databinding.ItemContainerBinding;
import com.example.tvshows.listeners.WatchListListener;
import com.example.tvshows.models.TVShow;

import java.util.List;

public class WatchListAdapter extends RecyclerView.Adapter<WatchListAdapter.TVShowViewHolder> {

    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;
    private WatchListListener watchListListener;

    public WatchListAdapter(List<TVShow> tvShows, WatchListListener watchListListener) {
        this.tvShows = tvShows;
        this.watchListListener = watchListListener;
    }

    @NonNull
    @Override
    public TVShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }

        ItemContainerBinding tvShowBinding = DataBindingUtil.inflate(
                layoutInflater, R.layout.item_container, parent, false);

        return new TVShowViewHolder(tvShowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull TVShowViewHolder holder, int position) {
        holder.bindTVShow(tvShows.get(position));
    }

    @Override
    public int getItemCount() {
        return tvShows.size();
    }


    class TVShowViewHolder extends RecyclerView.ViewHolder {

        private ItemContainerBinding itemContainerBinding;

        public TVShowViewHolder(ItemContainerBinding itemContainerBinding) {
            super(itemContainerBinding.getRoot());
            this.itemContainerBinding = itemContainerBinding;
        }

        public void bindTVShow(TVShow tvShow) {
            itemContainerBinding.setTvShow(tvShow);
            itemContainerBinding.executePendingBindings();
            itemContainerBinding.getRoot().setOnClickListener(v ->

                    watchListListener.onTVShowClicked(tvShow));

            itemContainerBinding.imageDelete.setOnClickListener(v ->
                    watchListListener.removeTVShowFromWatchList(tvShow, getAdapterPosition()));

            itemContainerBinding.imageDelete.setVisibility(View.VISIBLE);
        }

    }

}
