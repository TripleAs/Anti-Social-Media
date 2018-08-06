package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PictureAdapter;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.ivGroupPic) ImageView ivPropic;
    @BindView(R.id.tvGroupName) TextView tvFullName;
    @BindView(R.id.gvProfileGroups) GridView profileGroups;
    @BindView(R.id.ivStartChat) ImageView ivStartChat;
    private String mParam1;
    private ParseUser user;
    private Context mContext;
    PictureAdapter groupAdapter;
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
        ButterKnife.bind(this, view);

        tvFullName.setText(user.getString("fullName"));
        String propicUrl = user.getString("propicUrl");
        propicUrl = (propicUrl == null) ? user.getParseFile("profileImage").getUrl() : propicUrl;

        // for Parse profile pictures
        if (propicUrl != null && !(propicUrl.equals("")))  {
            Glide.with(mContext).load(propicUrl).into(ivPropic);
        }
        else if(user.getParseFile("profileImage") != null){
            Glide.with(mContext).load(user.getParseFile("profileImage").getUrl()).into(ivPropic);
        }

        groupList = new ArrayList<>();
        ivStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startUserChat(user.getObjectId(), user.getString("fullName"));
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
