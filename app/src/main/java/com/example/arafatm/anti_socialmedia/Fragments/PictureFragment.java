package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.arafatm.anti_socialmedia.R;

import java.io.File;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PictureFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "imagePath";
    private static final String ARG_PARAM2 = "caption";
    private static final String ARG_PARAM3 = "text";
    private EditText status;
    private String imagePath;
    private String imageStoryURL;
    private int currentAngle = 90;

    @BindView(R.id.imagePreview) ImageView displayImage;
    private String caption;
    private String text;
    private String param1;
    private String param2;

    private OnFragmentInteractionListener mListener;

    public PictureFragment() {
        // Required empty public constructor
    }

    public static PictureFragment newInstance(String param1, String param2) {
        PictureFragment fragment = new PictureFragment();
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
            imagePath = getArguments().getString(ARG_PARAM1);
            caption = getArguments().getString(ARG_PARAM2);
            text = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_picture, container, false);
    }

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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        displayImage = (ImageView) view.findViewById(R.id.imagePreview);
        //loads the file
        File file = new File(imagePath);
        //creates a bitmap out of it
        final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        Bitmap rotated = rotateImage(currentAngle, bitmap);
        //displays the image
        displayImage.setImageBitmap(rotated);

        TextView showCaption = (TextView) view.findViewById(R.id.tv_showCaption);
        TextView showText = (TextView) view.findViewById(R.id.tv_showText);

        if (text != null)
            showText.setText(text);

        if (caption != null)
            showCaption.setText(caption);
    }

    private Bitmap rotateImage(int degree, Bitmap bitmap) {
        //rotate image to upright position
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                matrix, true);
    }

}
