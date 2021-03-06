package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PictureAdapter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadedImages.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UploadedImages#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadedImages extends SupportBlurDialogFragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ArrayList<String> pictureList;
    private PictureAdapter pictureAdapter;
    private OnFragmentInteractionListener mListener;

    public UploadedImages() {
        // Required empty public constructor
    }

    public static UploadedImages newInstance(Group group) {
        UploadedImages fragment = new UploadedImages();
        Bundle args = new Bundle();
        args.putParcelable(Group.class.getSimpleName(), Parcels.wrap(group));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        Group currentGroup = Parcels.unwrap(getArguments().getParcelable(Group.class.getSimpleName()));
        pictureList = new ArrayList<>();
        pictureAdapter = new PictureAdapter(getContext(), pictureList, currentGroup, getActivity().getSupportFragmentManager(), UploadedImages.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
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
        String token = "8379540590.7601641.40a698a312bd4027b4d9548b746a8f0e"; //access token to the right account
        String url = "https://api.instagram.com/v1/users/self/media/recent?access_token="
                + token;

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
                        JSONObject pictureThumbnail = (JSONObject) images.getJSONObject("standard_resolution");
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
        GridView gridview = (GridView) view.findViewById(R.id.gv_gridview);
        gridview.setAdapter(pictureAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("imageURL", pictureList.get(position));
                GroupFeedFragment.goToPost = true;
                Fragment groupFeedFragment = new GroupFeedFragment();
                groupFeedFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.preview_frame, groupFeedFragment).addToBackStack(null).commit();
                dismiss();
            }
        });
        loadAllPIctureURL();
    }

    @Override
    protected float getDownScaleFactor() {
        // Allow to customize the down scale factor.
        return (float) 5.0;
    }

    @Override
    protected int getBlurRadius() {
        // Allow to customize the blur radius factor.
        return 7;
    }

    @Override
    protected boolean isActionBarBlurred() {
        // Enable or disable the blur effect on the action bar.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDimmingEnable() {
        // Enable or disable the dimming effect.
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        // Enable or disable the use of RenderScript for blurring effect
        // Disabled by default.
        return true;
    }

    @Override
    protected boolean isDebugEnable() {
        // Enable or disable debug mode.
        // False by default.
        return false;
    }
}