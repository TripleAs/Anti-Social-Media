package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PictureAdapter;
import com.example.arafatm.anti_socialmedia.Util.SpacesItemDecoration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadedImages.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadedImages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadedImages extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<String> pictureList;
    private PictureAdapter pictureAdapter;
    private RecyclerView rvPictures;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UploadedImages() {
        // Required empty public constructor
    }

    //
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment UploadedImages.
//     */
    // TODO: Rename and change types and number of parameters
    public static UploadedImages newInstance(Group group) {
        UploadedImages fragment = new UploadedImages();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        args.putParcelable(Group.class.getSimpleName(), Parcels.wrap(group));
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

        Group currentGroup = Parcels.unwrap(getArguments().getParcelable(Group.class.getSimpleName()));

        pictureList = new ArrayList<>();
        pictureAdapter = new PictureAdapter(getContext(), pictureList, currentGroup, getActivity().getSupportFragmentManager(), UploadedImages.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_uploaded_images, container, false);
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
    }

    /*loads all groups from parse and display it*/
    private void loadAllPIctureURL() {
        String accessToken = "8379540590.7601641.40a698a312bd4027b4d9548b746a8f0e";
        String url = "https://api.instagram.com/v1/users/self/media/recent?access_token="
                + accessToken;

        //makes api call
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    JSONArray object = response.getJSONArray("data");
                    for (int i = 0; i < object.length(); i++) {
                        JSONObject userData = (JSONObject) object.get(i);
                        JSONObject images = (JSONObject) userData.getJSONObject("images");
                        JSONObject pictureThumbnail = (JSONObject) images.getJSONObject("thumbnail");
                        String pictureUrl = pictureThumbnail.getString("url");
                        pictureList.add(pictureUrl);
                    }
                    pictureAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPictures = view.findViewById(R.id.rvPictures);
        rvPictures.addItemDecoration(new SpacesItemDecoration(20));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        rvPictures.setLayoutManager(gridLayoutManager);
        rvPictures.setAdapter(pictureAdapter);
        loadAllPIctureURL();
    }
}