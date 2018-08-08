package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.GroupManagerAdapter;
import com.example.arafatm.anti_socialmedia.Util.MemberAdapter;
import com.example.arafatm.anti_socialmedia.Util.PhotoHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_BLUE;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_GREEN;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_RED;

public class GroupSettingsFragment extends Fragment implements EditNicknameFragment.OnFragmentInteractionListener {
    @BindView(R.id.etGroupName) EditText etGroupName;
    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.ivCamera) ImageView ivCamera;
    @BindView(R.id.ivUpload) ImageView ivUpload;
    @BindView(R.id.btSave) Button btSave;

    @BindView(R.id.rvMembers) RecyclerView rvMembers;
    MemberAdapter memberAdapter;
    ArrayList<ParseUser> members;
    HashMap<String, String> nicknamesDict;

    @BindView(R.id.ivColorRed) ImageView ivColorRed;
    @BindView(R.id.ivColorGreen)  ImageView ivColorGreen;
    @BindView(R.id.ivColorBlue)  ImageView ivColorBlue;
    @BindView(R.id.ivCheckmarkRed)  ImageView ivCheckmarkRed;
    @BindView(R.id.ivCheckmarkGreen)  ImageView ivCheckmarkGreen;
    @BindView(R.id.ivCheckmarkBlue)  ImageView ivCheckmarkBlue;
    ArrayList<ImageView> checkmarks = new ArrayList<>();
    String theme;

    private Group currentGroup;
    private PhotoHelper photoHelper;
    private Boolean hasNewPic = false;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1035;
    private GroupSettingsFragment.OnFragmentInteractionListener mListener;
    private GroupSettingsFragment.OnSettingsUpdatedListener refreshListener;

    public GroupSettingsFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        void navigate_to_fragment(Fragment fragment);
    }

    public interface OnSettingsUpdatedListener {
        void refreshManager(int position, Group currentGroup);
    }

    public static GroupSettingsFragment newInstance(Group group) {
        GroupSettingsFragment fragment = new GroupSettingsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Group.class.getSimpleName(), Parcels.wrap(group));

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof GroupSettingsFragment.OnFragmentInteractionListener) {
            mListener = (GroupSettingsFragment.OnFragmentInteractionListener) context;
//            refreshListener = (GroupSettingsFragment.OnSettingsUpdatedListener) new GroupManagerFragment();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentGroup = Parcels.unwrap(getArguments().getParcelable(Group.class.getSimpleName()));
        }
        members = new ArrayList<>();
        nicknamesDict = currentGroup.getNicknamesDict();
        memberAdapter = new MemberAdapter(members, nicknamesDict, GroupSettingsFragment.this, getActivity().getSupportFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMembers.setAdapter(memberAdapter);
        getGroupMembers();

        etGroupName.setText(currentGroup.getGroupName());
        checkmarks.addAll(Arrays.asList(ivCheckmarkRed, ivCheckmarkGreen, ivCheckmarkBlue));

        if (currentGroup.getTheme() != null) {
            switch (currentGroup.getTheme()) {
                case KEY_RED:
                    ivCheckmarkRed.setVisibility(View.VISIBLE);
                    break;
                case KEY_GREEN:
                    ivCheckmarkGreen.setVisibility(View.VISIBLE);
                    break;
                case KEY_BLUE:
                    ivCheckmarkBlue.setVisibility(View.VISIBLE);
                    break;
                default:
                    ivCheckmarkGreen.setVisibility(View.VISIBLE);
            }
        }

        ivColorRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleColorSelection(KEY_RED, ivCheckmarkRed);
            }
        });

        ivColorGreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleColorSelection(KEY_GREEN, ivCheckmarkGreen);
            }
        });

        ivColorBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleColorSelection(KEY_BLUE, ivCheckmarkBlue);
            }
        });

        ParseFile currentImage = currentGroup.getGroupImage();
        if (currentImage != null) {
            Glide.with(getContext()).load(currentImage.getUrl()).apply(RequestOptions.centerCropTransform()).into(ivPreview);
        } else {
            ivPreview.setImageResource(R.drawable.ic_group_default);
        }

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoHelper = new PhotoHelper(getContext());
                Intent intent = photoHelper.takePhoto();
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoHelper = new PhotoHelper(getContext());
                Intent intent = photoHelper.uploadImage();
                startActivityForResult(intent, UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateGroupSettings();
            }
        });
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void updateGroupSettings() {
        String newName = etGroupName.getText().toString();
        if (!newName.matches("")) {
            currentGroup.setGroupName(newName);
        }
        if (hasNewPic) {
            currentGroup.setGroupImage(photoHelper.grabImage());
        }
        if (theme != null) {
            currentGroup.setTheme(theme);
        }
        currentGroup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                GroupFeedFragment groupFeedFragment = GroupFeedFragment.newInstance(currentGroup.getObjectId(), currentGroup.getTheme());
                int position = GroupManagerFragment.groupAdapter.currentGroupPosition;
                GroupManagerFragment.refreshManager(position, currentGroup);
                mListener.navigate_to_fragment(groupFeedFragment);
            }
        });
    }

    private void handleColorSelection(String color, ImageView checkmark) {
        theme = color;
        for (int i = 0; i < checkmarks.size(); i++) {
            checkmarks.get(i).setVisibility(View.INVISIBLE);
        }
        checkmark.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                hasNewPic = true;
                if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                    ivPreview.setImageBitmap(photoHelper.handleTakenPhoto());
                } else if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE) {
                    Uri photoUri = data.getData();
                    ivPreview.setImageBitmap(photoHelper.handleUploadedImage(photoUri));
                }
            }
        } else {
            Toast.makeText(getContext(), "No picture chosen", Toast.LENGTH_SHORT).show();
        }
    }

    private void getGroupMembers() {
        List<String> memberIds = currentGroup.getUsers();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereContainedIn("objectId", memberIds);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    members.addAll(objects);
                    memberAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onFinishEditNickname(String nickname, ParseUser member, int position) {
        nicknamesDict.put(member.getObjectId(), nickname);
        currentGroup.setNicknamesDict(nicknamesDict);
        memberAdapter.notifyItemChanged(position);
    }
}
