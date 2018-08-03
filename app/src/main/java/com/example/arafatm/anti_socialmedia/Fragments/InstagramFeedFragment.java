package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.example.arafatm.anti_socialmedia.Models.InstaPost;
import com.example.arafatm.anti_socialmedia.R;

import java.util.ArrayList;

import butterknife.BindView;

public class InstagramFeedFragment extends Fragment {
    //    @BindView(R.id.tbIgTitleBar) Toolbar IgTitleBar;
    @BindView(R.id.gvIgPictures) GridView gvPictures;
    @BindView(R.id.tvIgUsername) TextView igUsername;
    ArrayList<InstaPost> instaPostList;
    Context mContext;

    private OnFragmentInteractionListener mListener;

    public InstagramFeedFragment(){
        //required empty public constructor
    }

    public static InstagramFeedFragment newInstance(){
        InstagramFeedFragment fragment = new InstagramFeedFragment();
        Bundle args = new Bundle();
                //Todo: insert some putString
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_instagram_feed, container, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        instaPostList = new ArrayList<>();


        //addAll posts

    }

    public interface OnFragmentInteractionListener{
        void onFragmentInteraction(Uri uri);
        void navigate_to_fragment(Fragment fragment);
    }
}
