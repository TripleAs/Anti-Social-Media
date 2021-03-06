package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class GroupCustomizationFragment extends Fragment {

    @BindView(R.id.etGroupName) EditText etGroupName;
    @BindView(R.id.btNext) Button btNext;
    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.ivCamera) ImageView ivCamera;
    @BindView(R.id.ivUpload) ImageView ivUpload;

    private ImageView ivColorRed;
    private ImageView ivColorGreen;
    private ImageView ivColorBlue;
    private ImageView ivCheckmarkRed;
    private ImageView ivCheckmarkGreen;
    private ImageView ivCheckmarkBlue;
    private ArrayList<ImageView> checkmarks = new ArrayList<>();
    private List<String> newMembers;
    private PhotoHelper photoHelper;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1035;
    private Boolean hasNewPic = false;
    public final static String KEY_RED = "red";
    public final static String KEY_GREEN = "green";
    public final static String KEY_BLUE = "blue";
    private String theme = "green";

    private String newName;
    private ParseFile newGroupPic;

    private OnFragmentInteractionListener mListener;

    public GroupCustomizationFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        void navigate_to_fragment(Fragment fragment);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_customization, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        ivColorRed = view.findViewById(R.id.ivColorRed);
        ivColorGreen = view.findViewById(R.id.ivColorGreen);
        ivColorBlue = view.findViewById(R.id.ivColorBlue);
        ivCheckmarkRed = view.findViewById(R.id.ivCheckmarkRed);
        ivCheckmarkGreen = view.findViewById(R.id.ivCheckmarkGreen);
        ivCheckmarkBlue = view.findViewById(R.id.ivCheckmarkBlue);
        checkmarks.addAll(Arrays.asList(ivCheckmarkRed, ivCheckmarkGreen, ivCheckmarkBlue));

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoHelper = new PhotoHelper(getContext());
                Intent intent = photoHelper.takePhoto();
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        ivPreview.setOnClickListener(new View.OnClickListener() {
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

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasNewPic) {
                    Toast.makeText(getContext(), "Please choose a group picture", Toast.LENGTH_LONG).show();
                } else {
                    passToGroupCreation();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data != null) {
                hasNewPic = true;
                if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                    ivPreview.setImageBitmap(photoHelper.handleTakenPhoto(data));
                } else if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE) {
                    Uri photoUri = data.getData();
                    ivPreview.setImageBitmap(photoHelper.handleUploadedImage(photoUri));
                }
            }
        } else {
            Toast.makeText(getContext(), "No picture chosen", Toast.LENGTH_SHORT).show();
        }
    }

    private void passToGroupCreation() {
        newGroupPic = photoHelper.grabImage();
        newGroupPic.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                newName = etGroupName.getText().toString();
                GroupCreationFragment groupCreationFragment = GroupCreationFragment.newInstance(newName, theme, newGroupPic);
                mListener.navigate_to_fragment(groupCreationFragment);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

}
