package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.GroupRequestNotif;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PhotoHelper;
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

import static android.app.Activity.RESULT_OK;

public class GroupCustomizationFragment extends Fragment {
    private EditText etGroupName;
    private Button btCreateGroup;
    private ImageView ivPreview;
    private ImageView ivCamera;
    private ImageView ivUpload;

    private List<ParseUser> newMembers;
    private PhotoHelper photoHelper;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1035;

    private OnFragmentInteractionListener mListener;

    public GroupCustomizationFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        void navigate_to_fragment(Fragment fragment);
    }

    public static GroupCustomizationFragment newInstance(ArrayList<ParseUser> friendsToAdd) {
        GroupCustomizationFragment fragment = new GroupCustomizationFragment();
        Bundle args = new Bundle();
        args.putParcelable("newMembers", Parcels.wrap(friendsToAdd));
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newMembers = Parcels.unwrap(getArguments().getParcelable("newMembers"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_customization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etGroupName = view.findViewById(R.id.etGroupName);
        btCreateGroup = view.findViewById(R.id.btCreateGroup);
        ivPreview = view.findViewById(R.id.ivPreview);
        ivCamera = view.findViewById(R.id.ivCamera);
        ivUpload = view.findViewById(R.id.ivUpload);

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

        btCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewGroup();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
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

    private void createNewGroup() {
        //Create new group and initialize it
        final Group newGroup = new Group();
        final String newName = etGroupName.getText().toString();
        final ParseFile newGroupPic = photoHelper.grabImage();

        newGroupPic.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                newGroup.initGroup(newName, newMembers, newGroupPic);
                newGroup.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        //bundle the group objectId and send to groupfeed fragment for later use
                        Bundle args = new Bundle();
                        String objectId = newGroup.getObjectId();
                        args.putString("param1", objectId);

                        /*Navigates to the GroupFeedFragment*/
                        Fragment fragment = new GroupFeedFragment();
                        fragment.setArguments(args);
                        mListener.navigate_to_fragment(fragment);
                    }
                });
            }
        });

        sendGroupRequests(newGroup);
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
            newRequest.initRequest(newMembers.get(i), newGroup);
            newRequest.saveInBackground();
//            ParseQuery<ParseUser> query = ParseUser.getQuery();
//            query.getInBackground(newMembers.get(i), new GetCallback<ParseUser>() {
//                @Override
//                public void done(ParseUser object, ParseException e) {
//                    newRequest.initRequest(object, newGroup);
//                    try {
//                        newRequest.save();
//                    } catch (ParseException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
