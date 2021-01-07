package com.example.tvshows.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.tvshows.R;
import com.example.tvshows.adapters.EpisodesAdapter;
import com.example.tvshows.adapters.ImageSliderAdapter;
import com.example.tvshows.databinding.ActivityTVShowDetailsBinding;
import com.example.tvshows.databinding.LayoutEpisodesBottomSheetBinding;
import com.example.tvshows.models.TVShow;
import com.example.tvshows.viewmodels.TVShowDetailsViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Arrays;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class TVShowDetailsActivity extends AppCompatActivity {

    private ActivityTVShowDetailsBinding binding;
    private TVShowDetailsViewModel tvShowDetailsViewModel;
    private BottomSheetDialog episodesBSheetDialog;
    private LayoutEpisodesBottomSheetBinding layoutEpisodesBottomSheetBinding;
    private TVShow tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_t_v_show_details);

        doInitialization();

    }

    private void doInitialization() {
        tvShowDetailsViewModel = new ViewModelProvider(this).get(TVShowDetailsViewModel.class);
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        tvShow = (TVShow) getIntent().getSerializableExtra("tvShow");
        getTvShowDetails();
    }

    private void getTvShowDetails() {
        binding.setIsLoading(true);
        String tvShowId = String.valueOf(tvShow.getId());

        tvShowDetailsViewModel.getTVShowDetails(tvShowId).observe(this, tvShowDetailsResponse -> {

            Log.d("TAG", tvShowDetailsResponse.getTvShowDetails().getUrl());

            binding.setIsLoading(false);

            if (tvShowDetailsResponse.getTvShowDetails() != null) {
                if (tvShowDetailsResponse.getTvShowDetails().getPictures() != null) {
                    loadImageSlider(tvShowDetailsResponse.getTvShowDetails().getPictures());
                    Log.d("TAG", Arrays.toString(tvShowDetailsResponse.getTvShowDetails().getPictures()));
                }
                binding.setTvShowImageURL(
                        tvShowDetailsResponse.getTvShowDetails().getImagePath());
                binding.imageTVShow.setVisibility(View.VISIBLE);
                binding.setDescription(
                        String.valueOf(HtmlCompat.fromHtml(
                                tvShowDetailsResponse.getTvShowDetails().getDescription(),
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                        ))
                );
                binding.textDescription.setVisibility(View.VISIBLE);
                binding.textReadMore.setVisibility(View.VISIBLE);
                binding.textReadMore.setOnClickListener(v -> {
                    if (binding.textReadMore.getText().toString().equals("Read More")) {
                        binding.textDescription.setMaxLines(Integer.MAX_VALUE);
                        binding.textDescription.setEllipsize(null);
                        binding.textReadMore.setText(R.string.read_less);
                    } else {
                        binding.textDescription.setMaxLines(4);
                        binding.textDescription.setEllipsize(TextUtils.TruncateAt.END);
                        binding.textReadMore.setText(R.string.read_more);
                    }
                });

                binding.setRating(String.format(Locale.getDefault(),
                        "%.2f", Double.parseDouble(tvShowDetailsResponse.getTvShowDetails().getRating())));

                if (tvShowDetailsResponse.getTvShowDetails().getGenres() != null) {
                    binding.setGenre(tvShowDetailsResponse.getTvShowDetails().getGenres()[0]);
                } else {
                    binding.setGenre("N/A");
                }

                binding.setRuntime(tvShowDetailsResponse.getTvShowDetails().getRuntime() + " Min");
                binding.viewDivider.setVisibility(View.VISIBLE);
                binding.layoutMisc.setVisibility(View.VISIBLE);
                binding.viewDivider2.setVisibility(View.VISIBLE);

                binding.buttonWebsite.setOnClickListener(v -> {

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(tvShowDetailsResponse.getTvShowDetails().getUrl()));
                    startActivity(intent);
                });

                binding.buttonWebsite.setVisibility(View.VISIBLE);
                binding.buttonEpisodes.setVisibility(View.VISIBLE);

                binding.buttonEpisodes.setOnClickListener(v -> {

                    if (episodesBSheetDialog == null) {
                        episodesBSheetDialog = new BottomSheetDialog(TVShowDetailsActivity.this);
                        layoutEpisodesBottomSheetBinding = DataBindingUtil.inflate(
                                LayoutInflater.from(TVShowDetailsActivity.this),
                                R.layout.layout_episodes_bottom_sheet,
                                findViewById(R.id.episodesContainer), false);

                        episodesBSheetDialog.setContentView(layoutEpisodesBottomSheetBinding.getRoot());
                        layoutEpisodesBottomSheetBinding.episodeRecyclerView.setAdapter(
                                new EpisodesAdapter(tvShowDetailsResponse.getTvShowDetails().getEpisodes())
                        );

                        layoutEpisodesBottomSheetBinding.textTitle.setText(String.format("Episodes | %s",
                                tvShow.getName()));

                        layoutEpisodesBottomSheetBinding.imageClose.setOnClickListener(v1 -> episodesBSheetDialog.dismiss());

                    }

                    FrameLayout frameLayout = episodesBSheetDialog.findViewById(com.google.android.material.R.id.
                            design_bottom_sheet);
                    if (frameLayout != null) {
                        BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(frameLayout);
                        bottomSheetBehavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }

                    episodesBSheetDialog.show();

                });

                binding.imageWatchList.setOnClickListener(v -> new CompositeDisposable()
                        .add(tvShowDetailsViewModel.addToWatchList(tvShow)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    binding.imageWatchList.setImageResource(R.drawable.ic_check);
                                    Toast.makeText(getApplicationContext(), "Added To WatchList",
                                            Toast.LENGTH_SHORT).show();
                                })
                        ));

                binding.imageWatchList.setVisibility(View.VISIBLE);

                loadBasicTVShowDetails();
            }

        });

    }

    private void loadImageSlider(String[] sliderImages) {
        binding.sliderViewPager.setOffscreenPageLimit(1);
        binding.sliderViewPager.setAdapter(new ImageSliderAdapter(sliderImages));
        binding.sliderViewPager.setVisibility(View.VISIBLE);
        binding.viewFadingEdge.setVisibility(View.VISIBLE);

        setupSliderIndicators(sliderImages.length);
        binding.sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentSliderIndicator(position);
            }
        });
    }

    private void setupSliderIndicators(int count) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout
                .LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ImageView[] indicators = new ImageView[count];

        layoutParams.setMargins(8, 0, 8, 0);
        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.background_slider_indicator_inactive));

            indicators[i].setLayoutParams(layoutParams);
            binding.layoutSliderIndicators.addView(indicators[i]);
        }

        binding.layoutSliderIndicators.setVisibility(View.VISIBLE);
        setCurrentSliderIndicator(0);
    }

    private void setCurrentSliderIndicator(int position) {

        int childCount = binding.layoutSliderIndicators
                .getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.layoutSliderIndicators.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.background_slider_indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.background_slider_indicator_inactive));
            }
        }

    }

    private void loadBasicTVShowDetails() {
        binding.setTvShowName(tvShow.getName());
        binding.setNetworkCountry(tvShow.getNetwork() + " ("
                + tvShow.getCountry() + ")");

        binding.setStatus(tvShow.getStatus());
        binding.setStartedDate(tvShow.getStartDate());

        binding.textName.setVisibility(View.VISIBLE);
        binding.textNetworkCountry.setVisibility(View.VISIBLE);
        binding.textStatus.setVisibility(View.VISIBLE);
        binding.textStarted.setVisibility(View.VISIBLE);

    }

}