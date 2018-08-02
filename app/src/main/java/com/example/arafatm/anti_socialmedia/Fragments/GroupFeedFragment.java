package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PostAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_BLUE;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_GREEN;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_RED;

public class GroupFeedFragment extends Fragment implements CreatePostFragment.OnFragmentInteractionListener {
    private static final String ARG_PARAM1 = "param1";

    private String groupObjectId;
    private String groupName;
    private int groupId;
    private Group group;

    private ImageView next_story;
    private VideoView storyView;
    private ImageView prev_story;
    private String videoFilePath;

    @BindView(R.id.tvGroupName) TextView tvGroupName;
    //@BindView(R.id.tvNumberOfComments) TextView tvCommentCount;
    @BindView(R.id.ivCoverPhoto) ImageView ivGroupPic;
    @BindView(R.id.ivStartChat) ImageView ivStartChat;
    @BindView(R.id.ivThreeDots) ImageView ivThreeDots;
    @BindView(R.id.ivLaunchNewPost) ImageView ivLaunchNewPost;

    //for posting
    PostAdapter postAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
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
             themeName = bundle.getString("theme", KEY_BLUE);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Equivalent to setContentView
        // create ContextThemeWrapper from the original Activity Context with the custom theme
        Context contextThemeWrapper = null;
        switch (themeName) {
            case KEY_RED:
                contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GroupRedTheme);
                break;
            case KEY_GREEN:
                contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GroupGreenTheme);
                break;
            case KEY_BLUE:
                contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.GroupBlueTheme);
                break;
        }
        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        // inflate the layout using the cloned inflater, not default inflater
        return localInflater.inflate(R.layout.fragment_group_feed, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");


        next_story = view.findViewById(R.id.iv_next);
        prev_story = view.findViewById(R.id.iv_prev);
        rvPosts = view.findViewById(R.id.rvPostsFeed);
        storyView = (VideoView) view.findViewById(R.id.vv_groupStory);

        //displaying the posts
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity().getSupportFragmentManager(), getContext(), posts);

        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(GroupFeedFragment.this.getContext()));
        rvPosts.setAdapter(postAdapter);

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

                    groupName = object.getString("groupName");
                    tvGroupName.setText(groupName);
                    groupId = convert(object.getObjectId());

                    ParseFile groupImage = object.getParseFile("groupImage");

                    if (groupImage != null) {
                        /*shows group image on gridView*/
                        Glide.with(getContext())
                                .load(groupImage.getUrl())
                                .apply(RequestOptions.circleCropTransform())
                                .into(ivGroupPic);
                    }

                    loadTopPosts();

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


        //TODO: ARAFAT'S IMPLEMENTATION

        storyView = (VideoView) view.findViewById(R.id.vv_groupStory);
//        storyView.setVideoPath(videoFilePath);
//        storyView.setMediaController(null);
//        storyView.requestFocus();
     //   storyView.start();

        storyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        //        storyView.start();
            }
        });

        next_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(getContext(), "next story", Toast.LENGTH_SHORT).show();
            }
        });

        storyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "playing story", Toast.LENGTH_SHORT).show();
            }
        });

        prev_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "prev story", Toast.LENGTH_SHORT).show();
            }
        });

        //TODO: ARAFAT'S IMPLEMENTATION






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

    private void refreshFeed(){
        PostAdapter adapter = new PostAdapter(getActivity().getSupportFragmentManager(), getContext(), posts);

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
        posts.add(0, post);
        postAdapter.notifyItemInserted(0);
        rvPosts.scrollToPosition(0);
        Toast.makeText(getContext(), "New post created", Toast.LENGTH_SHORT).show();
    }
}
