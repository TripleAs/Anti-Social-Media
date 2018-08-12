package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.arafatm.anti_socialmedia.R;

public class VideoFragment extends Fragment {
    private static final String ARG_PARAM1 = "videoPath";
    private static final String ARG_PARAM2 = "caption";
    private static final String ARG_PARAM3 = "text";
    private VideoView displayVideo;
    private Uri videoUri;
    private String caption;
    private String text;

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
            videoUri = Uri.parse(getArguments().getString(ARG_PARAM1));
            caption = getArguments().getString(ARG_PARAM2);
            text = getArguments().getString(ARG_PARAM3);
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
        if (videoUri != null) {
            displayVideo.setVideoURI(videoUri);
            displayVideo.setMediaController(null);
            displayVideo.requestFocus();
            displayVideo.start();
        }


        if (text != null)
            showText.setText(text);

        if (caption != null)
            showCaption.setText(caption);
    }
}

