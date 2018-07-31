package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PhotoHelper;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import butterknife.BindView;

import static android.app.Activity.RESULT_OK;
// ...

public class CreatePostFragment extends DialogFragment {

    @BindView(R.id.etNewPost) EditText etNewPost;
    @BindView(R.id.ivCamera) ImageView ivCamera;
    @BindView(R.id.ivUpload) ImageView ivUpload;
    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.ivCreatePost) ImageView ivCreatePost;
    @BindView(R.id.ivShareFrom) ImageView ivShareFrom;

    PhotoHelper photoHelper;
    private Boolean hasNewPic = false;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1035;
    private Fragment callback;

    private Group currentGroup;

    public CreatePostFragment() {
        // Empty constructor is required for DialogFragment
    }

    private CreatePostFragment.OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFinishCreatePost(Post post);
    }

    public static CreatePostFragment newInstance(Group group) {
        CreatePostFragment frag = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putParcelable(Group.class.getSimpleName(), Parcels.wrap(group));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentGroup = Parcels.unwrap(getArguments().getParcelable(Group.class.getSimpleName()));
        try {
            callback = getTargetFragment();
            mListener = (CreatePostFragment.OnFragmentInteractionListener) callback;
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement onFinishCreatePost interface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Show soft keyboard automatically and request focus to field
        etNewPost.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

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

        ivCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPostToParse();
            }
        });
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

    private void sendPostToParse() {
        final Post newPost = new Post();
        if (hasNewPic) {
            ParseFile image = photoHelper.grabImage();
            newPost.setImage(image);
        }
        String newMessage = etNewPost.getText().toString();
        newPost.initPost(newMessage, currentGroup);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Toast.makeText(getContext(), "Saved post", Toast.LENGTH_SHORT).show();
                currentGroup.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        mListener.onFinishCreatePost(newPost);
                        dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
