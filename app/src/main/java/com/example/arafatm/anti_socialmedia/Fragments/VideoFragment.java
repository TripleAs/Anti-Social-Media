package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.arafatm.anti_socialmedia.Models.Story;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseException;

import java.util.ArrayList;

public class VideoFragment extends Fragment {
    private static final String ARG_PARAM1 = "videoPath";
    private static final String ARG_PARAM2 = "caption";
    private static final String ARG_PARAM3 = "text";
    private VideoView displayVideo;
    private String caption;
    private String videoPath;
    private Uri videoUri;
    private String text;
    private ArrayList<Story> allStories;
    private int storyIndex = 0;

    private OnFragmentInteractionListener mListener;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            caption = getArguments().getString(ARG_PARAM2);
            videoPath = getArguments().getString(ARG_PARAM1);
            text = getArguments().getString(ARG_PARAM3);
            allStories = getArguments().getParcelableArrayList("allStories");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView showCaption = (TextView) view.findViewById(R.id.tv_showCaption);
        TextView showText = (TextView) view.findViewById(R.id.tv_showText);

        displayVideo = (VideoView) view.findViewById(R.id.videoPreview);
        if (videoPath != null) {
            playVideo(Uri.parse(videoPath));
        } else {
            final String firstStoryType = allStories.get(storyIndex).getStoryType();
            if (firstStoryType.compareTo("video") == 0) {
                Story firstStory = allStories.get(storyIndex);
                try { //plays the first instance
                    videoUri = Uri.fromFile(firstStory.getStory().getFile());
                    playVideo(videoUri);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                //This takes care of the subsequent stories
                displayVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        if (storyIndex < allStories.size() - 1) storyIndex++;
                        if (storyIndex == allStories.size() - 1) storyIndex = 0;

                        String subseStoryType = allStories.get(storyIndex).getStoryType();
                        if (subseStoryType.compareTo("video") == 0) {
                            Story subseStory = allStories.get(storyIndex);
                            try {
                                videoUri = Uri.fromFile(subseStory.getStory().getFile());
                                playVideo(videoUri);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            storyIndex++;
                        }
                    }
                });
            }
        }

        if (text != null)
            showText.setText(text);

        if (caption != null)
            showCaption.setText(caption);
    }

    private void playVideo(Uri videoUri) {
        displayVideo.setVideoURI(videoUri);
        displayVideo.setMediaController(null);
        displayVideo.requestFocus();
        displayVideo.start();
    }
}

