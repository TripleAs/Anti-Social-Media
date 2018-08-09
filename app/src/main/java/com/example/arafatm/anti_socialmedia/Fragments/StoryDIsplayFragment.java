package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.R;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoryDIsplayFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoryDIsplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoryDIsplayFragment extends SupportBlurDialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TEXT = "text";
    private static final String CAPTION = "caption";
    private static final String IMAGE_PATH = "imagePath";
    private static final String VIDEO_PATH = "videoPath";
    private static final String DATA_TYPE = "dataType";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String text;
    private String caption;
    private String imageFilePath;
    private String videoFilePath;
    private String getDataType;

    private OnFragmentInteractionListener mListener;

    public StoryDIsplayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment storyDIsplayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StoryDIsplayFragment newInstance(String param1, String param2) {
        StoryDIsplayFragment fragment = new StoryDIsplayFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            text = getArguments().getString(TEXT);
            caption = getArguments().getString(CAPTION);
            imageFilePath = getArguments().getString(IMAGE_PATH);
            videoFilePath = getArguments().getString(VIDEO_PATH);
            getDataType = getArguments().getString(DATA_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_story_display, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView next_story = (ImageView) view.findViewById(R.id.iv_next);
        ImageView prev_story = (ImageView) view.findViewById(R.id.iv_prev);

        next_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "next story", Toast.LENGTH_SHORT).show();
            }
        });

        prev_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "next story", Toast.LENGTH_SHORT).show();
            }
        });

        FrameLayout frameLayout = view.findViewById(R.id.fl_showStory);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //initialize fragment manager
        final FragmentManager fragmentManager = getFragmentManager(); //Initiates FragmentManager
        final FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        //check if video or picture
        if (getDataType.compareTo("picture") == 0) {
            //pass all required info
            //navigate to right fragment
            navigateToPictureFragment(imageFilePath, fragmentTransaction, R.id.fl_showStory);
        } else {
            //pass all required info
            //navigate to right fragment
            navigateToVideoFragment(videoFilePath,fragmentTransaction, R.id.fl_showStory);
        }
    }

    /*navigates to the Picture fragment and display the story*/
    private void navigateToPictureFragment(String imageFilePath,
                                           FragmentTransaction fragmentTransaction, int view_id) {
        Fragment pictureFragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString("imagePath", imageFilePath);
        args.putString("text", text);
        args.putString("caption", caption);
        pictureFragment.setArguments(args);
        fragmentTransaction.replace(view_id, pictureFragment)
                .commit();
    }

    /*navigates to the Video fragment and display the story*/
    private void navigateToVideoFragment(String videoFilePath,
                                         FragmentTransaction fragmentTransaction, int view_id) {
        final Fragment videoFragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        args.putString("caption", caption);
        args.putString("videoPath", videoFilePath);
        videoFragment.setArguments(args);
        fragmentTransaction.replace(view_id, videoFragment)
                .commit();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    protected float getDownScaleFactor() {
        // Allow to customize the down scale factor.
        return (float) 5.0;
    }

    @Override
    protected int getBlurRadius() {
        // Allow to customize the blur radius factor.
        return 7;
    }

    @Override
    protected boolean isActionBarBlurred() {
        // Enable or disable the blur effect on the action bar.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDimmingEnable() {
        // Enable or disable the dimming effect.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        // Enable or disable the use of RenderScript for blurring effect
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDebugEnable() {
        // Enable or disable debug mode.
        // False by default.
        return false;
    }
}

