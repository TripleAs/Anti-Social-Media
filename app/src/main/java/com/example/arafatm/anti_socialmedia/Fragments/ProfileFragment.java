package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.GroupAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private ParseUser user;
    private ImageView ivPropic;
    private TextView tvFullName;
    private Context mContext;
    private GridView profileGroups;
    private String mParam1;
    private ImageView ivStartChat;
    GroupAdapter groupAdapter;
    ArrayList<Group> groupList;

    private static final String ARG_PARAM1 = "param1";
    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void navigate_to_fragment(Fragment fragment);
        void startUserChat(String contactName, String message);
    }

    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable(ParseUser.class.getSimpleName(), Parcels.wrap(user));
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement ProfileFragment.OnFragmentInteractionListener");
        }
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        user = Parcels.unwrap(getArguments().getParcelable(ParseUser.class.getSimpleName()));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_profile, parent, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvFullName = view.findViewById(R.id.tvGroupName);
        tvFullName.setText(user.getString("fullName"));

        ivPropic = view.findViewById(R.id.ivGroupPic);
        String propicUrl = user.getString("propicUrl");
//        propicUrl = (propicUrl == null) ? user.getParseFile("profileImage").getUrl() : propicUrl;

        // for Parse profile pictures
        if (propicUrl != null && !(propicUrl.equals("")))  {
            Glide.with(mContext).load(propicUrl).into(ivPropic);
        }
        else if(user.getParseFile("profileImage") != null){
            Glide.with(mContext).load(user.getParseFile("profileImage").getUrl()).into(ivPropic);
        }

        profileGroups = view.findViewById(R.id.gvProfileGroups);
        groupList = new ArrayList<>();

        loadAllGroups(view, profileGroups);

        ivStartChat = view.findViewById(R.id.ivStartChat);
        ivStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startUserChat(user.getString("fullName"), "testing");
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadAllGroups(final View view, final GridView profileGroups){
        //loadAllGroups method from GroupManagerFragment
        final Group.Query groupQuery = new Group.Query();
        groupQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(final List<Group> objects, ParseException e) {
                if (e == null) {

                    groupList.addAll(objects);
                    displayOnGridView(objects, view, profileGroups);
                    groupAdapter = new GroupAdapter(getContext(), groupList);
                    profileGroups.setAdapter(groupAdapter);

                    profileGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Toast.makeText(getContext(), "" + position,
                                    Toast.LENGTH_SHORT).show();
                            Fragment fragment = new GroupFeedFragment();
                            Bundle args = new Bundle();

                            ParseObject selectedGroup = groupList.get(position);
                            args.putString(ARG_PARAM1,selectedGroup.getObjectId()); //pass group objectId
                            fragment.setArguments(args);

                            // TODO: Figure out a way to pass the selected group to the next fragment (feed)
                            /*Navigates to the groupFeedFragment*/
                            mListener.navigate_to_fragment(fragment);
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /*this initializes the adapter, and pass the groupList into it and navigates to GroupFeed fragment*/
    private void displayOnGridView(List<Group> objects, View view, final GridView gridview) {
        groupAdapter = new GroupAdapter(getContext(), groupList);
        gridview.setAdapter(groupAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
                Fragment fragment = new GroupFeedFragment();
                Bundle args = new Bundle();
                args.putString(ARG_PARAM1, groupList.get(position).getObjectId()); //TO BE CHANGED LATER
                fragment.setArguments(args);

                /*Navigates to the groupManagerFragment*/
                mListener.navigate_to_fragment(fragment);
            }
        });
    }

}
