package com.eminesa.erlabexoplayer.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.eminesa.erlabexoplayer.databinding.ItemExoPlayerBinding;
import com.eminesa.erlabexoplayer.model.VideoUrl;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {

    private final ArrayList<VideoUrl> mVideoList;
    private final Context mContext;
    public  SimpleExoPlayer simpleExoPlayer;

    public ViewPagerAdapter(Context context, ArrayList<VideoUrl> videoList) {
        mContext = context;
        mVideoList = videoList;
    }

    @NonNull
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {

        ItemExoPlayerBinding binding = ItemExoPlayerBinding.inflate(LayoutInflater.from(mContext));

        TrackSelection.Factory adaptiveTrackSelection = new AdaptiveTrackSelection.Factory(new DefaultBandwidthMeter());

        //initialize simple exo player
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(mContext),
                new DefaultTrackSelector(adaptiveTrackSelection),
                new DefaultLoadControl());

        //set player
        binding.playerView.setPlayer(simpleExoPlayer);

        //initialize band width meter
        DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

        //initialize extractors factory
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "Exo2"), defaultBandwidthMeter);

        String hls_url = mVideoList.get(position).getStringUrl();
        Uri uri = Uri.parse(hls_url);
        Handler mainHandler = new Handler();

        // initialize media source
        MediaSource mediaSource = new HlsMediaSource(uri, dataSourceFactory, mainHandler, null);

        playWithCaption(uri,dataSourceFactory);

        // keep screen on
        binding.playerView.setKeepScreenOn(true);

        // prepare media
        simpleExoPlayer.prepare(mediaSource);

        // play video when ready
        simpleExoPlayer.setPlayWhenReady(false);

       // playWithCaption(uri,dataSourceFactory, mediaSource);
        simpleExoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                simpleExoPlayer.setPlayWhenReady(false);
                binding.playerView.setKeepScreenOn(false);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                //control playbackState
                if (playbackState == Player.STATE_BUFFERING) {
                    // when buffering show progress
                    binding.progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_READY) {
                    // when ready hide progress
                    binding.progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });

        collection.addView(binding.getRoot());
        return binding.getRoot();
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, @NonNull Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mVideoList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    private void playWithCaption(Uri uri, DataSource.Factory dataSourceFactory) {

        //Add subtitles
        MediaSource mediaSource = new SingleSampleMediaSource(uri, dataSourceFactory, Format.createTextSampleFormat(null, MimeTypes.APPLICATION_SUBRIP, Format.NO_VALUE, "en", null), C.TIME_UNSET);

        mediaSource = new MergingMediaSource(mediaSource);

        // Prepare the player with the source.
        //player.seekTo(contentPosition);
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
    }
}