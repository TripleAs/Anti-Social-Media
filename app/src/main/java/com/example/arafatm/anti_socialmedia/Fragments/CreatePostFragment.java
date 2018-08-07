package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PhotoHelper;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static com.facebook.GraphRequest.TAG;
// ...

public class CreatePostFragment extends DialogFragment {

    @BindView(R.id.etNewPost)
    EditText etNewPost;
    @BindView(R.id.ivCamera)
    ImageView ivCamera;
    @BindView(R.id.ivUpload)
    ImageView ivUpload;
    @BindView(R.id.ivPreview)
    ImageView ivPreview;
    @BindView(R.id.ivCreatePost)
    ImageView ivCreatePost;
    @BindView(R.id.ivShareFrom)
    ImageButton ivShareFrom;

    PhotoHelper photoHelper;
    private Boolean hasNewPic = false;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public final static int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1035;
    private Fragment callback;
    private String imageURl;

    private Group currentGroup;

    public CreatePostFragment() {
        // Empty constructor is required for DialogFragment
    }

    private CreatePostFragment.OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFinishCreatePost(Post post);
    }

    public static CreatePostFragment newInstance(String imageURL) {
        CreatePostFragment frag = new CreatePostFragment();
        Bundle args = new Bundle();
        args.putString("imageURL", imageURL);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageURl = this.getArguments().getString("imageURL"); //get image url


        if (imageURl != null) {
            //set hasMew to true
            hasNewPic = true;
        }

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
        ButterKnife.bind(this, view);

        if (hasNewPic)
            Glide.with(getContext()).load(imageURl).into(ivPreview);

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

        ivShareFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupFeedFragment.goToShare = true;
                // come back after lunch!
                Fragment groupFeedFragment = new GroupFeedFragment();
                FragmentManager fragmentManager = getFragmentManager(); //Initiates FragmentManager
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.preview_frame, groupFeedFragment)
                        .commit();
                dismiss();
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
        ParseFile image = null;
        if (hasNewPic) {
            if (imageURl != null) {
                byte[] imageByte = createByteArrayFromURL(imageURl);
                image = new ParseFile("image", imageByte);
            } else {
                image = photoHelper.grabImage();
            }

            final ParseFile finalImage = image;
            image.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    newPost.setImage(finalImage);
                }
            });
        }
        String newMessage = etNewPost.getText().toString();

        if (currentGroup == null) {
          currentGroup = GroupFeedFragment.publicCurrentGroup;
        }

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

    private byte[] createByteArrayFromURL(String imageURL) {
        try {
            java.net.URL img_value = new java.net.URL(imageURL);
            Bitmap mIcon = BitmapFactory
                    .decodeStream(img_value.openConnection()
                            .getInputStream());
            if (mIcon != null)
                return encodeToByteArray(mIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] encodeToByteArray(Bitmap image) {
        Log.d(TAG, "encodeToByteArray");
        Bitmap b = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imgByteArray = baos.toByteArray();

        return imgByteArray;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
