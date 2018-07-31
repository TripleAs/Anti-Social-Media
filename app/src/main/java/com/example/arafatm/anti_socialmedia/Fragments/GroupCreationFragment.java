package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.FriendListAdapter;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupCreationFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    @BindView(R.id.rvFriends) RecyclerView recyclerView;
    @BindView(R.id.btNext) Button nextButton;

    private FriendListAdapter friendListAdapter;
    private ArrayList<ParseUser> friendList;
    private String mParam1;
    private String mParam2;
    ParseUser currentUser;

    private OnFragmentInteractionListener mListener;

    public GroupCreationFragment() {
        // Required empty public constructor
    }

    public static GroupCreationFragment newInstance(String param1, String param2) {
        GroupCreationFragment fragment = new GroupCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_creation, container, false);
        ButterKnife.bind(this, view);

        friendList = new ArrayList<>();
        fetchAllFriendList();

        friendListAdapter = new FriendListAdapter(friendList);
        recyclerView.setAdapter(friendListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        return view;
    }

    private void fetchAllFriendList() {
        //get current user
        try {
            currentUser = ParseUser.getQuery().get("mK88SMmv6C"); //ParseUser.getCurrentUser(); //Change this!
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //get the list of friends(Ids)
        final List<String> friendListIds = currentUser.getList("friendList");

        //TODO
        //Change this way to Amy way of finding facebook friends

        // use Ids to find users
        for (int i = 0; i < friendListIds.size(); i++) {
            //   for each id, find corresponding use
            try {
                ParseUser user = ParseUser.getQuery().get(friendListIds.get(i));
//                ParseUser.getQuery().whereEqualTo("username", friendListIds.get(i))
                friendList.add(user);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

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
        void onFragmentInteraction(Uri uri);
        void navigate_to_fragment(Fragment fragment);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SearchView searchView = (SearchView) view.findViewById(R.id.sv_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String friendName) {
                //gets the user to be searched
                fetchFriend(friendName);
                searchView.clearFocus();
                return true;
            }

            /*Reloads to show all friendList when the user stops searching*/
            @Override
            public boolean onQueryTextChange(String friendName) {
                if (friendName == null || friendName.isEmpty()) {
                    fetchAllFriendList();
                    friendListAdapter.notifyDataSetChanged(); //updates the adapter
                    return true;
                }
                return false;
            }

        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passToCustomization();
            }
        });
    }

    private void fetchFriend(String friendName) {
        //get the list of friends(Ids)
        final List<String> friendListIds = currentUser.getList("friendList");

        //TODO
        //Change this way to Amy way of finding facebook friends
        friendList.clear();
        for (int i = 0; i < friendListIds.size(); i++) {
            try {
                ParseUser user = ParseUser.getQuery().get(friendListIds.get(i));
                if (user.getString("fullName").compareTo(friendName) == 0) {
                  friendList.add(user);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        friendListAdapter.notifyDataSetChanged(); //updates the adapter
    }

    //TODO
    //get empty search to fetch all users

    private void passToCustomization() {
        ArrayList<String> newMembers = friendListAdapter.getNewGroupMembers();
        GroupCustomizationFragment gcFragment = GroupCustomizationFragment.newInstance(newMembers);
        mListener.navigate_to_fragment(gcFragment);
    }
}

