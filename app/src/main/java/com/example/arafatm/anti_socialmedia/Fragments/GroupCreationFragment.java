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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupCreationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupCreationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupCreationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private FriendListAdapter friendListAdapter;
    private ArrayList<ParseUser> friendList;
    ParseUser currentUser = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GroupCreationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupCreationFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        //RECYCLERVIEW SETUP
        // Lookup the recyclerview in activity layout
        recyclerView = (RecyclerView) view.findViewById(R.id.rvFriends);

        friendList = new ArrayList<>();
        fetchAllFriendList();

        //done!
        // Create adapter passing in the sample user data
        friendListAdapter = new FriendListAdapter(friendList);
        // Attach the adapter to the recyclerview to populate items
        recyclerView.setAdapter(friendListAdapter);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));

        return view;
    }

    private void fetchAllFriendList() {
        //initialize friendList
     //   ArrayList<ParseUser> list = new ArrayList<>();

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

        // use Ids to find usres
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

        void navigate_to_fragment(Fragment fragment);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button nextButton = (Button) view.findViewById(R.id.btNext);

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
        friendList.clear(); //Clears all friends
        // use Ids to find users
        for (int i = 0; i < friendListIds.size(); i++) {
            //   for each id, find corresponding use
            try {
                ParseUser user = ParseUser.getQuery().get(friendListIds.get(i));
//                ParseUser.getQuery().whereEqualTo("username", friendListIds.get(i))
                //looks up users whose names match the input
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

