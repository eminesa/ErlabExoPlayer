package com.eminesa.erlabexoplayer.activity;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.eminesa.erlabexoplayer.R;
import com.eminesa.erlabexoplayer.adapter.ViewPagerAdapter;
import com.eminesa.erlabexoplayer.databinding.ActivityMainBinding;
import com.eminesa.erlabexoplayer.model.VideoUrl;
import com.google.android.exoplayer2.SimpleExoPlayer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<VideoUrl> videoList;
    boolean isFullScreen = false;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater();
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());
        videoList = new ArrayList<>();
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));
        videoList.add(new VideoUrl("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"));

        adapter = new ViewPagerAdapter(this, videoList);
        binding.viewPager.setAdapter(adapter);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                adapter.simpleExoPlayer.setPlayWhenReady(false);
            }

            @Override
            public void onPageSelected(int position) {
                //play video when ready
                adapter.simpleExoPlayer.setPlayWhenReady(false);
                // Returns the current state of the player.
                // adapter.simpleExoPlayer.getPlaybackState();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                adapter.simpleExoPlayer.setPlayWhenReady(false);
            }
        });

        clickEvent(videoList);
        //set activity full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void clickEvent(ArrayList<VideoUrl> videoList) {
        binding.nextImageView.setOnClickListener(v -> {
            //for next video
            int viewCurrentPosition = binding.viewPager.getCurrentItem();
            if (videoList.size() == viewCurrentPosition + 1) {
                binding.viewPager.setCurrentItem(0);

            } else {
                binding.viewPager.setCurrentItem(viewCurrentPosition + 1);
                binding.nextImageView.setColorFilter(getResources().getColor(R.color.gold));
            }
        });

        binding.previousImageView.setOnClickListener(v -> {
            int viewCurrentPosition = binding.viewPager.getCurrentItem();
            //for previous video
            if (viewCurrentPosition != 0) {

                binding.viewPager.setCurrentItem(viewCurrentPosition - 1);
                binding.previousImageView.setColorFilter(getResources().getColor(R.color.gold));

            } else {
                binding.viewPager.setCurrentItem(videoList.size());

            }
        });
        binding.fullScreenImageView.setOnClickListener(this::onClick);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pausePlayer(adapter.simpleExoPlayer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pausePlayer(adapter.simpleExoPlayer);
        adapter.simpleExoPlayer.stop();
       // adapter.simpleExoPlayer.release();
    }

    public static void startPlayer(SimpleExoPlayer exoPlayer) {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    public static void pausePlayer(SimpleExoPlayer exoPlayer) {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop(false);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void onClick(View v) {
        if (isFullScreen) {
            // when full screen is true
            binding.fullScreenImageView.setImageDrawable((getResources().getDrawable(R.drawable.ic_fullscreen)));
            //set portrait orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isFullScreen = false;

        } else {
            // when full screen is false
            // set exit full screen
            binding.fullScreenImageView.setImageDrawable((getResources().getDrawable(R.drawable.ic_fullscreen_exit)));
            // set landscape orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            isFullScreen = true;
        }
    }
}

