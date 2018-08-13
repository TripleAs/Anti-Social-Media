package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.GroupManagerAdapter;
import com.example.arafatm.anti_socialmedia.Util.SpacesItemDecoration;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class GroupManagerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static GroupManagerAdapter groupAdapter;
    public static ArrayList<Group> groupList;
    Context mContext;

    @BindView(R.id.ic_add_white)
    ImageView add_group;
    RecyclerView rvGroups;
    Toolbar toolbar;
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
        groupList = new ArrayList<>();
        groupAdapter = new GroupManagerAdapter(groupList, getActivity().getSupportFragmentManager(), GroupManagerFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            if (container != null) {
                container.removeAllViews();
            }
        View view = inflater.inflate(R.layout.fragment_group_manager, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        return view;
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
        ButterKnife.bind(this, view);
        rvGroups = view.findViewById(R.id.rvGroups);
        rvGroups.addItemDecoration(new SpacesItemDecoration(20));

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager( 2, LinearLayoutManager.VERTICAL);
        rvGroups.setLayoutManager(gridLayoutManager);
        rvGroups.setAdapter(groupAdapter);
        loadAllGroups();

        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Navigates to the groupManagerFragment*/
                Fragment fragment = new GroupCustomizationFragment();
                mListener.navigate_to_fragment(fragment);
            }
        });
    }

    /*loads all groups from parse and display it*/
    private void loadAllGroups() {
        ParseUser user = ParseUser.getCurrentUser();
        List<Group> groups = user.getList("groups");
        groupList.clear();
        if (groups != null) {
            for (int i = 0; i < groups.size(); i++) {
                try {
                    Group group = groups.get(i).fetchIfNeeded();
                    groupList.add(group);
                    groupAdapter.notifyItemInserted(groupList.size() - 1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void refreshManager(int position, Group currentGroup) {
        if (groupList.size() > 0)
        groupList.remove(position);
        groupList.add(position, currentGroup);
        groupAdapter.notifyItemChanged(position);
    }
}
