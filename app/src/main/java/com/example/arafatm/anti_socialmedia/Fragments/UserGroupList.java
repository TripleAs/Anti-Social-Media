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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserGroupList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserGroupList#newInstance} factory method to
 * create an instance of this fragment.
 */

public class UserGroupList extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "dataType";
    private static final String ARG_PARAM2 = "caption";
    private static final String ARG_PARAM3 = "text";

    private ArrayList<ParseObject> groupList;
    private RecyclerView recyclerView;
    private GroupListAdapter groupListAdapter;
    private Button shareButton;

    // TODO: Rename and change types of parameters
    private String dataType;
    private String text;
    private String caption;

    private OnFragmentInteractionListener mListener;

    public UserGroupList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GameFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);

        void navigate_to_fragment(Fragment fragment);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shareButton = (Button) view.findViewById(R.id.bt_share);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<ParseObject> allGroupWithStories = groupListAdapter.getAllGroupWithStories();
                if (allGroupWithStories != null) {
                    //Create a new story
                    Story story = new Story();
                    story.setSender(ParseUser.getCurrentUser());
                    ParseFile parseFile = null;

                    if (dataType.compareTo("video") == 0) {
                        byte[] videoBytes = getArguments().getByteArray("byteData");
                           parseFile = new ParseFile("mynewStory.mp3", videoBytes);
                    } else {
                        final byte[] imageBytes = StoryActivity.compressedImageByte;
                        parseFile = new ParseFile("mynewStory.png", imageBytes);

                    }

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
                            Intent i = new Intent(getActivity(), StoryActivity.class);
                            startActivity(i);
                            ((Activity) getActivity()).overridePendingTransition(0, 0);//
                        }
                    });
                }
            }
        });
    }

    //TODO
    //implement next and prev
    //add caption
}

