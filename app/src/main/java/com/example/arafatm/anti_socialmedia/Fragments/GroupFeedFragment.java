package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PostAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //general feed setup
    private String mParam1;
    private String mParam2;
    private String groupObjectId;

    //for posting
    private EditText messageInput;
    private Button createButton;
    PostAdapter postAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;

    //list of users

    private OnFragmentInteractionListener mListener;

    public GroupFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFeedFragment newInstance(String param1, String param2) {
        GroupFeedFragment fragment = new GroupFeedFragment();
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
//            mParam1 = getArguments().getString(ARG_PARAM1);
             mParam2 = getArguments().getString(ARG_PARAM2);
             groupObjectId = getArguments().getString(ARG_PARAM1);
//            ParseQuery<ParseObject> gameQuery = ParseQuery.getQuery("Group");
//            gameQuery.whereEqualTo("objectId", ParseUser.getCurrentUser());
//
//            ParseUser.getQuery().get();  // how to get the "User"'s "objectId"

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Equivalent to setContentView
        return inflater.inflate(R.layout.fragment_group_feed, container, false);
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

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //creating a post
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        messageInput = view.findViewById(R.id.etNewPost);
        createButton = view.findViewById(R.id.btCreatePost);

        //displaying the posts
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);
        rvPosts = view.findViewById(R.id.rvPostsFeed);

        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(GroupFeedFragment.this.getContext()));
        rvPosts.setAdapter(postAdapter);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                PostAdapter adapter = new PostAdapter(posts);

                adapter.clear();
                loadTopPosts();
                rvPosts.scrollToPosition(0);
            }
        });

        query.getInBackground(groupObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), object.getString("groupName") + " Successfully Loaded", Toast.LENGTH_SHORT).show();

                    TextView name = (TextView) view.findViewById(R.id.tvGroupName);
                    name.setText(object.getString("groupName"));

                    ImageView profile = (ImageView) view.findViewById(R.id.ivCoverPhoto);
                    ParseFile groupImage = object.getParseFile("groupImage");

                    if (groupImage != null) {
                        /*shows group image on gridView*/
                        Glide.with(getContext())
                                .load(groupImage.getUrl())
                                .into(profile);
                    }

                } else {
                    // something went wrong
                }
            }

        });

        loadTopPosts();

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String message = messageInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                createPost(message, user);

//                final ParseFile parseFile = new ParseFile(textPost);
                //debugger says that there's something wrong around here
                //ParseObject Post that we created

                //save file to parse
                //this is code from Parsetagram, but probably don't need a Parsefile because we aren't posting with an image
//                parseFile.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(ParseException e) {
//                        if(e == null){
//                            createPost(message, user);
//                        }
//                        else{
//                            e.printStackTrace();
//                        }
//                    }
//
//                });
            }
        });


    }

    private void createPost(final String message, ParseUser user){    // +param -> Parsefile imageFile
        final Post newPost = new Post();
        newPost.setMessage(message);
//        newPost.setImage(imageFile);          <== figure out image posting later
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.d("PostActivity", "Create post success!");
                    Toast.makeText(GroupFeedFragment.this.getContext(), "Posted!", Toast.LENGTH_LONG).show();
                    messageInput.setText("");
                }
                else{
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadTopPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop();       //<== this gets the post from a specific user. Won't cause harm, but don't need it rn
        // ^ this line originally had ".withUser", so this should fix it

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {

                    posts.addAll(objects);
                    postAdapter.notifyDataSetChanged();

                    swipeContainer.setRefreshing(false);

                } else {
                    e.printStackTrace();
                }
            }
        });

    }


}
