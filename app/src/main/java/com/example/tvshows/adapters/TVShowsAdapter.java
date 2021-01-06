package com.example.tvshows.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tvshows.R;
import com.example.tvshows.databinding.ItemContainerBinding;
import com.example.tvshows.models.TVShow;

import java.util.List;

public class TVShowsAdapter extends RecyclerView.Adapter<TVShowsAdapter.TVShowViewHolder> {

    private List<TVShow> tvShows;
    private LayoutInflater layoutInflater;

    public TVShowsAdapter(List<TVShow> tvShows) {
        this.tvShows = tvShows;
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


    static class TVShowViewHolder extends RecyclerView.ViewHolder {

        private ItemContainerBinding itemContainerBinding;

        public TVShowViewHolder(ItemContainerBinding itemContainerBinding) {
            super(itemContainerBinding.getRoot());
            this.itemContainerBinding = itemContainerBinding;
        }

        public void bindTVShow(TVShow tvShow) {
            itemContainerBinding.setTvShow(tvShow);
            itemContainerBinding.executePendingBindings();
        }

    }

}
