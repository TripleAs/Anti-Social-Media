package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.GroupRequestNotif;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.FriendListAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupCreationFragment extends Fragment {
    @BindView(R.id.rvFriends) RecyclerView recyclerView;
    @BindView(R.id.btCreateGroup) Button btCreateGroup;

    private static final String ARGS_GROUP_NAME = "groupName";
    private static final String ARGS_GROUP_THEME = "groupTheme";
    private static final String ARGS_GROUP_IMAGEFILE = "groupImage";

    private String groupName;
    private String groupTheme;
    private ParseFile groupImage;
    private List<String> newMembers;

    private FriendListAdapter friendListAdapter;
    private ArrayList<ParseUser> friendList;
    ParseUser currentUser;

    private OnFragmentInteractionListener mListener;

    public GroupCreationFragment() {
        // Required empty public constructor
    }

    public static GroupCreationFragment newInstance(String name, String theme, ParseFile file) {
        GroupCreationFragment fragment = new GroupCreationFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_GROUP_NAME, name);
        args.putString(ARGS_GROUP_THEME, theme);
        args.putParcelable(ARGS_GROUP_IMAGEFILE, Parcels.wrap(file));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        if (args != null) {
            groupName = args.getString(ARGS_GROUP_NAME);
            groupTheme = args.getString(ARGS_GROUP_THEME);
            groupImage = Parcels.unwrap(args.getParcelable(ARGS_GROUP_IMAGEFILE));
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
        currentUser = ParseUser.getCurrentUser();
        //get the list of friends(Ids)
        List<String> friendListIds = currentUser.getList("friendList");
        friendList.clear();
        // use usernames/FB Ids to find users
        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery();
        friendsQuery.whereContainedIn("username", friendListIds);
//        friendsQuery.fromLocalDatastore();

        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    friendList.addAll(objects);
                    friendListAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
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
        void navigate_to_fragment(Fragment fragment);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final SearchView searchView = (SearchView) view.findViewById(R.id.sv_search);

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

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

        btCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (friendList.size() != 0) {
                    newMembers = friendListAdapter.getNewGroupMembers();
                    createNewGroup();
                } else {
                    Toast.makeText(getContext(), "Cannot create a group with no members", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void fetchFriend(final String friendName) {
        //get the list of friends(Ids)
        final List<String> friendListIds = currentUser.getList("friendList");
        ParseQuery<ParseUser> friendsQuery = ParseUser.getQuery().whereEqualTo("username", friendListIds);
        friendsQuery.whereContainedIn("username", friendListIds);
        friendList.clear();
        friendsQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseUser user = objects.get(i);
                        if (user.getString("fullName").toLowerCase().contains(friendName.toLowerCase())) {
                            friendList.add(user);
                        }
                    }
                    friendListAdapter.notifyDataSetChanged(); //updates the adapter
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createNewGroup() {
        //Create new group and initialize it
        final Group newGroup = new Group();
//        newGroup.pinInBackground("groups");
//        newGroup.saveEventually();

        groupImage.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                saveNewGroup(newGroup);
            }
        });

        sendGroupRequests(newGroup);
    }

    private void saveNewGroup(final Group newGroup) {
        newGroup.initGroup(groupName, newMembers, groupImage, groupTheme);
        newGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                String objectId = newGroup.getObjectId();
                Fragment fragment = GroupFeedFragment.newInstance(objectId, groupTheme);
                mListener.navigate_to_fragment(fragment);
            }
        });
    }

    private void sendGroupRequests(final Group newGroup) {
        ParseUser loggedInUser = ParseUser.getCurrentUser();

        List<ParseObject> currentGroups = loggedInUser.getList("groups");
        if (currentGroups == null) {
            currentGroups = new ArrayList<>();
        }
        currentGroups.add(newGroup);
        loggedInUser.put("groups", currentGroups);
        loggedInUser.saveInBackground();

        for (int i = 0; i < newMembers.size(); i++) {
            final GroupRequestNotif newRequest = new GroupRequestNotif();
//            newRequest.pinInBackground();
//            newRequest.saveEventually();
            ParseQuery<ParseUser> query = ParseUser.getQuery();
//            query.fromLocalDatastore();
            query.getInBackground(newMembers.get(i), new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser object, ParseException e) {
                    newRequest.initRequest(object, newGroup);
                    newRequest.saveInBackground();
                }
            });
        }
    }
}

