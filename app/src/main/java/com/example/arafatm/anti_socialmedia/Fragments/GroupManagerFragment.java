package com.example.arafatm.anti_socialmedia.Fragments;

        import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.GroupAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

        import butterknife.BindView;


public class GroupManagerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    GroupAdapter groupAdapter;
    ArrayList<Group> groupList;
    Context mContext;

    @BindView(R.id.gv_group_list) GridView gridview;
    @BindView(R.id.ic_add_icon) ImageView add_group;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public GroupManagerFragment() {
        // Required empty public constructor
    }

    public static GroupManagerFragment newInstance(String param1, String param2) {
        GroupManagerFragment fragment = new GroupManagerFragment();
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
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_manager, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
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

        groupList = new ArrayList<>();

        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Adding a new group", Toast.LENGTH_SHORT).show();
                /*Navigates to the groupManagerFragment*/
                Fragment fragment = new GroupCreationFragment();
                mListener.navigate_to_fragment(fragment);
            }
        });

        loadAllGroups(view, gridview);

    }

    /*loads all groups from parse and display it*/
    private void loadAllGroups(final View view, final GridView gridview) {

        ParseUser user = ParseUser.getCurrentUser();
        List<Group> groups = user.getList("groups");

        if (groups == null) {
            final Group.Query postQuery = new Group.Query();
            postQuery.findInBackground(new FindCallback<Group>() {
                @Override
                public void done(final List<Group> objects, ParseException e) {
                    if (e == null) {
                        groupList.addAll(objects);
                        displayOnGridView(objects, view, gridview);
                        constructGridView(gridview);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            for (int i = 0; i < groups.size(); i++) {
                try {
                    Group group = groups.get(i).fetchIfNeeded();
                    groupList.add(group);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            displayOnGridView(groupList, view, gridview);
            constructGridView(gridview);
        }
    }

    private void constructGridView(GridView gridview) {
        groupAdapter = new GroupAdapter(getContext(), groupList);
        gridview.setAdapter(groupAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getContext(), "" + position,
                        Toast.LENGTH_SHORT).show();
                ParseObject selectedGroup = groupList.get(position);
                Fragment fragment = GroupFeedFragment.newInstance(selectedGroup.getObjectId(), selectedGroup.getString("theme"));
                /*Navigates to the groupFeedFragment*/
                mListener.navigate_to_fragment(fragment);
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
                ParseObject selectedGroup = groupList.get(position);
                Fragment fragment = GroupFeedFragment.newInstance(selectedGroup.getObjectId(), selectedGroup.getString("theme"));

                /*Navigates to the groupManagerFragment*/
                mListener.navigate_to_fragment(fragment);
            }
        });
    }
}
