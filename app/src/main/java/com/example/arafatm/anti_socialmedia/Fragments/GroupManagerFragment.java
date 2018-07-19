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

import java.util.ArrayList;
import java.util.List;


public class GroupManagerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    GroupAdapter groupAdapter;
    ArrayList<ParseObject> groupList;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GroupManagerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_manager, container, false);

        // Find the toolbar view inside the fragment layout
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.tb_toolbar);
        // Sets the Toolbar to act as the ActionBar for this fragment window.
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

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

        groupList = new ArrayList<>();
        ImageView add_group = (ImageView) view.findViewById(R.id.ic_add_icon);

        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Adding a new group", Toast.LENGTH_SHORT).show();

                /*Navigates to the groupManagerFragment*/
                Fragment fragment = new GroupCreationFragment();

                mListener.navigate_to_fragment(fragment);
            }
        });

//        GridView gridview = (GridView) view.findViewById(R.id.gv_group_list);
//        gridview.setAdapter(new GroupAdapter(getContext()));
//
//        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View v,
//                                    int position, long id) {
//                Toast.makeText(getContext(), "" + position,
//                        Toast.LENGTH_SHORT).show();
//
//
//                Fragment fragment = new GroupFeedFragment();
//                Bundle args = new Bundle();
//                args.putInt(ARG_PARAM1,position); //TO BE CHANGED LATER
//                fragment.setArguments(args);
//
//                /*Navigates to the groupManagerFragment*/
//                mListener.navigate_to_fragment(fragment);
//            }
//        });
        loadAllGroups(view);
    }



    private void loadAllGroups(final View view) {
        final Group.Query postQuery = new Group.Query();
        postQuery.findInBackground(new FindCallback<Group>() {
            @Override
            public void done(final List<Group> objects, ParseException e) {
                if (e == null) {
                    groupList.addAll(objects);

                    groupAdapter = new GroupAdapter(getContext(), groupList);
                    GridView gridview = (GridView) view.findViewById(R.id.gv_group_list);
                    gridview.setAdapter(groupAdapter);

                    gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            Toast.makeText(getContext(), "" + position,
                                    Toast.LENGTH_SHORT).show();
                            Fragment fragment = new GroupFeedFragment();
                            Bundle args = new Bundle();
                            args.putInt(ARG_PARAM1,position); //TO BE CHANGED LATER
                            fragment.setArguments(args);

                            /*Navigates to the groupManagerFragment*/
                            mListener.navigate_to_fragment(fragment);
                        }
                    });

                } else {
                    e.printStackTrace();
                }
            }
        });
    }




}
