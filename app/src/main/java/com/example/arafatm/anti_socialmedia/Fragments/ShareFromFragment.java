package com.example.arafatm.anti_socialmedia.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareFromFragment extends DialogFragment{
    @BindView(R.id.ivFacebook) ImageView ivFacebook;
    @BindView(R.id.ivSnapchat) ImageView ivSnapchat;
    @BindView(R.id.ivWhatsApp) ImageView ivWhatsApp;
    @BindView(R.id.ivTwitter) ImageView ivTwitter;
    @BindView(R.id.ivInstagram) ImageView ivInstagram;
    @BindView(R.id.ivVkontakte) ImageView ivVkontakte;
    @BindView(R.id.ivVine) ImageView ivVine;
    @BindView(R.id.ivLinkedIn) ImageView ivLinkedIn;
    @BindView(R.id.ivTumblr) ImageView ivTumblr;

    private CreatePostFragment.OnFragmentInteractionListener mListener;
    private Group currentGroup;
    private Fragment callback;

    public interface OnFragmentInteractionListener {
        void onFinishCreatePost(Post post);
    }

    public ShareFromFragment(){
        //required constructor for fragment interaction
    }

    public static ShareFromFragment newInstance(Group group) {
        ShareFromFragment frag = new ShareFromFragment();
        Bundle args = new Bundle();
        args.putParcelable(Group.class.getSimpleName(), Parcels.wrap(group));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentGroup = Parcels.unwrap(getArguments().getParcelable(Group.class.getSimpleName()));
        try {
            callback = getTargetFragment();
            mListener = (CreatePostFragment.OnFragmentInteractionListener) callback;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement onFinishCreatePost interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_from, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
}
