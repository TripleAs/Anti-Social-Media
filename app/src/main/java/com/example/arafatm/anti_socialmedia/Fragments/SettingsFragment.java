package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Authentification.LoginActivity;
import com.example.arafatm.anti_socialmedia.Models.GroupRequestNotif;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.NotifsAdapter;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsFragment extends Fragment {
    private Context mContext;

    @BindView(R.id.log_out_button) Button logOutBtn;
    @BindView(R.id.ivPropic) ImageView ivPropic;
    @BindView(R.id.tvFullName) TextView tvFullName;
    @BindView(R.id.tvViewProfile) TextView tvViewProfile;
    @BindView(R.id.rlViewProfile) RelativeLayout rlViewProfile;
    @BindView(R.id.rvNotifs) RecyclerView rvNotifs;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.tvNoNotifs) TextView tvNoNotifs;

    private ArrayList<GroupRequestNotif> requestList;
    private NotifsAdapter requestAdapter;
    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        void onViewProfileSelected();
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
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        rvNotifs = view.findViewById(R.id.rvNotifs);

        requestList = new ArrayList<>();
        getGroupRequests();

        requestAdapter = new NotifsAdapter(requestList);
        rvNotifs.setAdapter(requestAdapter);
        rvNotifs.setLayoutManager(new LinearLayoutManager(container.getContext()));

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        ParseUser user = ParseUser.getCurrentUser();

        // for Parse profile pictures
        String propicUrl = user.getString("propicUrl");
        if (propicUrl != null && !(propicUrl.equals("")))  {
            Glide.with(mContext).load(propicUrl).into(ivPropic);
        }
        else if(user.getParseFile("profileImage") != null){
            Glide.with(mContext).load(user.getParseFile("profileImage").getUrl()).into(ivPropic);
        }
        tvFullName.setText(user.getString("fullName"));

        logOutBtn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SettingsFragment.this.getContext())
                        .setMessage("Do you want to log out?")
                        .setTitle("Log Out")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();

                                //close the dialog
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });

        rlViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onViewProfileSelected();
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNotifs();
            }
        });
    }

    public void getGroupRequests() {
        GroupRequestNotif.Query query = new GroupRequestNotif.Query();
        ParseUser currentUser = ParseUser.getCurrentUser();
        query.getInvitesReceived(currentUser).withAll();
        query.fromNetwork();

        query.findInBackground(new FindCallback<GroupRequestNotif>() {
            @Override
            public void done(List<GroupRequestNotif> objects, ParseException e) {
                if (e == null) {
                    if (objects == null || objects.size() == 0) {
                        tvNoNotifs.setVisibility(View.VISIBLE);
                    } else {
                        tvNoNotifs.setVisibility(View.GONE);
                        for (int i = 0; i < objects.size(); i++) {
                            requestList.add(objects.get(i));
                            requestAdapter.notifyDataSetChanged();
                            swipeContainer.setRefreshing(false);
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshNotifs() {
        requestAdapter.clear();
        getGroupRequests();
        requestAdapter.notifyDataSetChanged();
    }

    private void logout() {
        //Check if user is currently logged in
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (isLoggedIn){
            LoginManager.getInstance().logOut();
        }

        // unpin everything from local datastore
        ParseObject.unpinAllInBackground("friends");
        ParseObject.unpinAllInBackground("groups");
        ParseObject.unpinAllInBackground("posts");
        ParseObject.unpinAllInBackground("comments");
        ParseObject.unpinAllInBackground("notifs");
        ParseObject.unpinAllInBackground("stories");
        ParseObject.unpinAllInBackground();

        // This will log out for Parse
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.logOut();
        Intent intent = new Intent(SettingsFragment.this.getContext(), LoginActivity.class);
        startActivity(intent);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
