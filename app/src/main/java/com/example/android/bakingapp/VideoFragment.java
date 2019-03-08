package com.example.android.bakingapp;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.android.bakingapp.model.Step;
import com.example.android.bakingapp.databinding.FragmentVideoBinding;
import com.example.android.bakingapp.repo.Constants;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class VideoFragment extends Fragment implements Player.EventListener {

    private static MediaSessionCompat mMediaSession;
    private FullScreenModeListener mFullScreenModeListener;
    private View.OnClickListener mHomeListener;
    private DetailsViewModel mViewModel;
    private FragmentVideoBinding mBinding;
    private SimpleExoPlayer mExoPlayer;
    private PlaybackStateCompat.Builder mStateBuilder;
    private AudioManager mAudioManager = null;
    // setting up listener for audio focus
    private AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                            mExoPlayer.setPlayWhenReady(false);
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS:
                            mExoPlayer.setPlayWhenReady(false);
                            releasePlayer();
                            break;
                        case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                            mExoPlayer.setPlayWhenReady(false);
                            break;
                        case AudioManager.AUDIOFOCUS_GAIN:
                            mExoPlayer.setPlayWhenReady(true);
                            break;
                    }
                }
            };

    public VideoFragment() {
    }
    // saving player state to view model for rotation or pause
    // releasing exoplayer early if this is SDK 23 or below
    @Override
    public void onPause() {
        super.onPause();
        mViewModel.setPlayPosition(mExoPlayer.getCurrentPosition());
        mViewModel.setIsPlaying(mExoPlayer.getPlayWhenReady());
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }
    // releasing exoplayer for SDKs greater than 23
    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
    // sets up interface for communicating when full screen mode to details activity,
    // sets up audio manager, sets up media session, sets up listener for home button on phone layout if device is a phone
    // calls setUpUi() to set up UIs for all devices, detects if phone is playing video in landscape mode and,
    // if so, puts exoplayer into full screen mode
    @Override
    public void onResume() {
        super.onResume();
        mFullScreenModeListener = (FullScreenModeListener) getActivity();
        setUpAudioManager();
        setUpMediaSession();
        if (!mViewModel.getThisIsAPhone()) {
            mHomeListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.setCurrentStep(0);
                }
            };
        }
        setUpUi();
        if (mViewModel.getThisIsAPhone()
                && getString(R.string.orientation).equals(Constants.LANDSCAPE)) {
            mFullScreenModeListener.setFullScreenMode();
            mBinding.exoplayerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    }
    // sets up UI depending on various conditions
    private void setUpUi() {

        mViewModel.getCurrentStep().observe(this, new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                if (mExoPlayer != null) {
                    mExoPlayer.setPlayWhenReady(false);
                }
                if (step != null && mViewModel.getThisIsAPhone() && !getString(R.string.orientation).equals(Constants.LANDSCAPE)) {
                    setUpPhoneUi(step);
                }
                if (step != null && !mViewModel.getThisIsAPhone()) {
                    setUpTabletUi(step);
                }
                if (mBinding.videoFragmentThumbnailView != null) {
                    mBinding.videoFragmentThumbnailView.setVisibility(View.INVISIBLE);
                }
                if (step != null && !step.getVideoUrl().equals("")) {
                    setUpExoplayer(step.getVideoUrl());
                } else if (step != null && (step.getThumbnailUrl()).endsWith(Constants.MP4)) {
                    setUpExoplayer(step.getThumbnailUrl());
                } else if (step != null
                        && step.getVideoUrl().equals("")
                        && !step.getThumbnailUrl().equals("")
                        && mBinding.videoFragmentThumbnailView != null) {
                    mBinding.videoFragmentThumbnailView.setVisibility(View.VISIBLE);
                    Picasso.with(getContext())
                            .load(step.getThumbnailUrl())
                            .error(R.drawable.no_video_placeholder)
                            .into(mBinding.videoFragmentThumbnailView);
                    setUpExoplayer("");
                } else {
                    if (mBinding.videoFragmentThumbnailView != null) {
                        mBinding.videoFragmentThumbnailView.setVisibility(View.VISIBLE);
                        mBinding.videoFragmentThumbnailView.setImageDrawable(getResources().getDrawable(R.drawable.no_video_placeholder, null));
                    }
                    setUpExoplayer("");
                }
            }
        });
    }

    // sets up intro layout for step 0
    private void setUpIntroLayout() {
        mBinding.videoMainStepsView.setVisibility(View.GONE);
        mBinding.videoIntroView.setVisibility(View.VISIBLE);
        mBinding.videoIntroViewRecipeTitle.setText(mViewModel.getCurrentRecipe().getName());
        setForwardSelectionListener(mBinding.videoIntroViewNextButton);
    }
    // sets up general layout when step > 0
    private void setUpMainLayout(Step step) {
        mBinding.videoIntroView.setVisibility(View.GONE);
        mBinding.videoMainStepsView.setVisibility(View.VISIBLE);
        mBinding.videoMainStepsNumberTextView.setText(String.valueOf(step.getId()));
        mBinding.videoMainStepsTitleTextView.setText(step.getShortDescription());
        mBinding.videoMainStepsInstructionsTextView.setText(step.getDescription());
        setBackwardSelectionListener(mBinding.videoMainStepsBackwardButton);
        if (mViewModel.thisIsTheFinalStep()) {
            mBinding.videoMianStepsForwardButton.setVisibility(View.GONE);
        } else {
            mBinding.videoMianStepsForwardButton.setVisibility(View.VISIBLE);
            setForwardSelectionListener(mBinding.videoMianStepsForwardButton);
        }
    }
    // controls layout for tablets depending on step number
    private void setUpTabletUi(Step step) {
        if (step.getId() == 0) {
            setUpIntroLayout();
        } else {
            setUpMainLayout(step);
            mBinding.videoMainTabletHomeButton.setOnClickListener(mHomeListener);
        }
    }
    // controls layout for tablets depending on step number
    private void setUpPhoneUi(Step step) {
        if (step.getId() == 0) {
            setUpIntroLayout();
        } else {
            setUpMainLayout(step);
        }
    }
    // sets listener on "forward" button
    private void setForwardSelectionListener(ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.switchStep(1);
            }
        });
    }
    // sets listener on "backward" button
    private void setBackwardSelectionListener(ImageButton imageButton) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.switchStep(-1);
            }
        });
    }
    // sets up view model, data binding object and returns rootview
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setUpViewModel();
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_video, container, false);
        return mBinding.getRoot();
    }
    // creates audio manager instance
    private void setUpAudioManager() {
        mAudioManager = (AudioManager) Objects.requireNonNull(getActivity()).getSystemService(Context.AUDIO_SERVICE);
    }
    // method to gain audio focus
    private boolean requestAudioFocus() {
        if (mAudioManager != null) {
            int result = mAudioManager.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            return false;
        }
    }
    // creates media session
    public void setUpMediaSession() {
        if (getActivity() != null) {
            mMediaSession = new MediaSessionCompat(getActivity(), Constants.VIDEO_FRAGMENT);
            mMediaSession.setFlags(
                    MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
            mMediaSession.setMediaButtonReceiver(null);
            mStateBuilder = new PlaybackStateCompat.Builder()
                    .setActions(
                            PlaybackStateCompat.ACTION_PLAY |
                                    PlaybackStateCompat.ACTION_PLAY_PAUSE);
            mMediaSession.setPlaybackState(mStateBuilder.build());
            mMediaSession.setCallback(new MySessionCallback());
            MediaControllerCompat mediaController =
                    new MediaControllerCompat(getActivity(), mMediaSession);
            MediaControllerCompat.setMediaController(getActivity(), mediaController);
        }
    }
    // releases media session and abandons audio focus
    private void releasePlayer() {
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        mAudioManager.abandonAudioFocus(afChangeListener);
        mMediaSession.setActive(false);
    }
    // sets up exoplayer
    private void setUpExoplayer(String videoUrlString) {
        if (mExoPlayer == null) {
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getContext()),
                    new DefaultTrackSelector(), new DefaultLoadControl());
            mBinding.exoplayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);
        }
        if (!videoUrlString.equals("")) {
            if (requestAudioFocus()) {
                mExoPlayer.setPlayWhenReady(mViewModel.getIsPlaying());
            } else {
                mExoPlayer.setPlayWhenReady(false);
            }
            MediaSource videoSource =
                    new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(Constants.BAKING_APP))
                            .createMediaSource(Uri.parse(videoUrlString));
            mExoPlayer.prepare(videoSource, true, false);
            mExoPlayer.seekTo(mViewModel.getPlayPosition());
        }
    }
    // sets up view model
    private void setUpViewModel() {
        mViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(DetailsViewModel.class);
    }
    // Eventlistener override methods
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        if (isLoading) {
            mBinding.progressBar.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBinding.progressBar.setVisibility(View.INVISIBLE);
                }
            }, 1000);
        }
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady && !requestAudioFocus()) {
            mExoPlayer.setPlayWhenReady(false);
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
            mMediaSession.setPlaybackState(mStateBuilder.build());
            return;
        }

        if ((playbackState == Player.STATE_ENDED) && playWhenReady) {
            mExoPlayer.setPlayWhenReady(false);
            mExoPlayer.seekTo(0);
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    0, 1f);
            mAudioManager.abandonAudioFocus(afChangeListener);
        }
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
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
    // interface method for communicating with details activity and setting full screen mode properly
    public interface FullScreenModeListener {
        void setFullScreenMode();
    }
    // media receiver to handle media controls from outside the app
    public static class MediaReceiver extends BroadcastReceiver {
        public MediaReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(mMediaSession, intent);
        }
    }
    // callback class for media session
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            if (requestAudioFocus()) mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }
    }
}







































