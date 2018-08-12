package com.example.arafatm.anti_socialmedia.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Story;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Story.StoryActivity;
import com.example.arafatm.anti_socialmedia.Util.GroupListAdapter;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class UserGroupList extends Fragment {
    private static final String ARG_PARAM1 = "dataType";
    private static final String ARG_PARAM2 = "caption";
    private static final String ARG_PARAM3 = "text";
    private ArrayList<ParseObject> groupList;
    private RecyclerView recyclerView;
    private GroupListAdapter groupListAdapter;
    private Button shareButton;
    private String dataType;
    private String text;
    private String caption;
    private OnFragmentInteractionListener mListener;

    public UserGroupList() {
        // Required empty public constructor
    }

    public static UserGroupList newInstance(String param1, String param2) {
        UserGroupList fragment = new UserGroupList();
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
            dataType = getArguments().getString(ARG_PARAM1);
            caption = getArguments().getString(ARG_PARAM2);
            text = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_group_list, container, false);

        // Lookup the recyclerview in activity layout
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_groupList);
        groupList = new ArrayList<>();
        List<Group> groups = ParseUser.getCurrentUser().getList("groups");
        groupList.clear();
        if (groups != null) {
            for (int i = 0; i < groups.size(); i++) {
                try {
                    Group group = groups.get(i).fetchIfNeeded();
                    groupList.add(group);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // Create adapter passing in the sample user data
            groupListAdapter = new GroupListAdapter(getContext(), groupList);
            // Attach the adapter to the recyclerview to populate items
            recyclerView.setAdapter(groupListAdapter);
            // Set layout manager to position the items
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        return view;
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
        void onFragmentInteraction(Uri uri);
        void navigate_to_fragment(Fragment fragment);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareButton = (Button) view.findViewById(R.id.bt_share);

        shareButton.setOnClickListener(new View.OnClickListener() {
//TODO NOT SAVING :( FIX IT

            @Override
            public void onClick(View view) {
                final ArrayList<ParseObject> allGroupWithStories = groupListAdapter.getAllGroupWithStories();
                if (allGroupWithStories != null && allGroupWithStories.size() != 0) {
                    //Create a new story
                    final Story story = new Story();

                    story.pinInBackground("story");
                    story.saveEventually();

                    final ParseFile parseFile;

                    if (dataType.compareTo("video") == 0) {
                        byte[] videoBytes = getArguments().getByteArray("byteData");
                        parseFile = new ParseFile("mynewStory.mp3", videoBytes);
                    } else {
                        final byte[] imageBytes = StoryActivity.compressedImageByte;
                        parseFile = new ParseFile("mynewStory.png", imageBytes);
                    }

                    parseFile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                story.setSender(ParseUser.getCurrentUser());
                                story.setStoryType(dataType);
                                story.setStory(parseFile);
                                story.setStoryCaption(caption);
                                story.setStoryText(text);

                                //adds the group's id to the recipient list of story
                                for (ParseObject group : allGroupWithStories) {
                                    story.addRecipient(group.getObjectId());
                                }

                                story.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Toast.makeText(getContext(), "sharing!", Toast.LENGTH_SHORT).show();
                                            Intent i = new Intent(getActivity(), StoryActivity.class);
                                            startActivity(i);
                                            ((Activity) getActivity()).overridePendingTransition(0, 0);
                                        } else {
                                            Toast.makeText(getContext(), "something is wrong!", Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                              e.printStackTrace();
                            }

                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Select atleast a grooup to share", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

