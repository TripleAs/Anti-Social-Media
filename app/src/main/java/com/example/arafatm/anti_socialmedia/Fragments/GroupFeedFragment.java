package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
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
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PhotoHelper;
import com.example.arafatm.anti_socialmedia.Util.PostAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupFeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFeedFragment extends Fragment implements CreatePostFragment.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String groupObjectId;
    private String groupName;
    private int groupId;
    private Group group;

    private TextView tvGroupName;
    private TextView tvCommentCount;
    private ImageView ivGroupPic;
    private ImageView ivStartChat;
    private ImageView ivThreeDots;
    private ImageView ivLaunchNewPost;

    //posts

    //for posting
    PostAdapter postAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    private EditText messageInput;
    private Button createButton;
    private SwipeRefreshLayout swipeContainer;
    String themeName;


    private OnFragmentInteractionListener mListener;

    public GroupFeedFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void navigate_to_fragment(Fragment fragment);
        void startGroupChat(int groupId, String groupName);
        void navigateToDialog(DialogFragment dialogFragment);
    }

    public static GroupFeedFragment newInstance(String mParam1, String theme) {
        GroupFeedFragment fragment = new GroupFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, mParam1);
        args.putString("theme", theme);
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
        Bundle bundle = this.getArguments();

        if (bundle != null) {
             groupObjectId = bundle.getString(ARG_PARAM1, groupObjectId);
             themeName = bundle.getString("theme", "red");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Equivalent to setContentView
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper;
        if (themeName.matches("red")) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GroupRedTheme);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GroupBlueTheme);
        }
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        return localInflater.inflate(R.layout.fragment_group_feed, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //creating a post
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");

        ivStartChat = view.findViewById(R.id.ivStartChat);
        ivThreeDots = view.findViewById(R.id.ivThreeDots);
        ivLaunchNewPost = view.findViewById(R.id.ivLaunchNewPost);
        rvPosts = view.findViewById(R.id.rvPostsFeed);
        tvCommentCount = view.findViewById(R.id.tvNumberOfComments);

        //displaying the posts
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(posts);

        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(GroupFeedFragment.this.getContext()));
        rvPosts.setAdapter(postAdapter);

        // Setup refresh listener which triggers new data loading
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });

        ivLaunchNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreatePostFragment cpFragment = CreatePostFragment.newInstance(group);
                cpFragment.setTargetFragment(GroupFeedFragment.this, 1);
                mListener.navigateToDialog(cpFragment);
            }
        });

        query.getInBackground(groupObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    Toast.makeText(getContext(), object.getString("groupName") + " Successfully Loaded", Toast.LENGTH_SHORT).show();
                    group = (Group) object;

                    tvGroupName = (TextView) view.findViewById(R.id.tvGroupName);
                    groupName = object.getString("groupName");
                    tvGroupName.setText(groupName);
                    groupId = convert(object.getObjectId());

                    ivGroupPic = (ImageView) view.findViewById(R.id.ivCoverPhoto);
                    ParseFile groupImage = object.getParseFile("groupImage");

                    if (groupImage != null) {
                        /*shows group image on gridView*/
                        Glide.with(getContext())
                                .load(groupImage.getUrl())
                                .into(ivGroupPic);
                    }

//                    loadTopPosts();
                    loadGroupPosts();

                } else {
                    e.printStackTrace();
                }
            }

        });

        ivStartChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startGroupChat(groupId, groupName);
            }
        });

        ivThreeDots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupSettingsFragment groupSettingsFragment = GroupSettingsFragment.newInstance(group);
                mListener.navigate_to_fragment(groupSettingsFragment);
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadTopPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser().forGroup(group);

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    postAdapter.notifyDataSetChanged();
                    posts.addAll(objects);
                    swipeContainer.setRefreshing(false);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadGroupPosts() {
        List<Post> groupPosts = group.getPosts();
        posts = (ArrayList<Post>) groupPosts;
    }

    private void refreshFeed(){
        PostAdapter adapter = new PostAdapter(posts);

        adapter.clear();
        loadTopPosts();
        rvPosts.scrollToPosition(0);
    }

    // for converting group objectId to integer (used for chat channel ID)
    // credit to https://stackoverflow.com/questions/30404946/how-to-convert-parse-objectid-string-to-long
    private static final String CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static int convertChar(char c) {
        int ret = CHARS.indexOf( c );
        if (ret == -1)
            throw new IllegalArgumentException( "Invalid character encountered: "+c);
        return ret;
    }

    public static int convert(String s) {
        if (s.length() != 10)
            throw new IllegalArgumentException( "String length must be 10, was "+s.length() );
        int ret = 0;
        for (int i = 0; i < s.length(); i++) {
            ret = (ret << 6) + convertChar( s.charAt( i ));
        }
        return ret;
    }

    @Override
    public void onFinishCreatePost(Post post) {
        if (posts == null) {
            posts = new ArrayList<>();
        }
        posts.add(0, post);
        postAdapter.notifyItemInserted(0);
        rvPosts.scrollToPosition(0);
        Toast.makeText(getContext(), "New post created", Toast.LENGTH_SHORT).show();
    }
}
