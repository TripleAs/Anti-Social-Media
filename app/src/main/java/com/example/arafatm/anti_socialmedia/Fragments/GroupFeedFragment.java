package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.Models.Story;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.PhotoHelper;
import com.example.arafatm.anti_socialmedia.Util.PostAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
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
    private String text;
    private String caption;
    public static boolean selected = false;
    private int groupId;
    private ImageView welcomeImage;
    public static Group publicCurrentGroup;
    private Group group;
    public static Group currentGroup;
    private String PREVIEW_TAG = "previewStory";
    private FrameLayout frameLayout;
    public static boolean goToShare = false;
    public static Uri VideouUri;
    public static boolean goToUpload = false;
    private ArrayList<Story> allStories;
    public static boolean goToPost = false;
    private String selectedImageURL;
    private String dataType;
    private String imageFilePath = null;
    @BindView(R.id.tvGroupName)
    TextView tvGroupName;
    @BindView(R.id.ivCoverPhoto)
    ImageView ivGroupPic;
    @BindView(R.id.ivStartChat)
    ImageView ivStartChat;
    @BindView(R.id.ivThreeDots)
    ImageView ivThreeDots;
    @BindView(R.id.ivLaunchNewPost)
    ImageView ivLaunchNewPost;
    public static PostAdapter postAdapter;
    public static ArrayList<Post> posts;
    public static
    RecyclerView rvPosts;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    @BindView(R.id.pbLoading) ProgressBar progressBar;
    String themeName;

    private OnFragmentInteractionListener mListener;

    public GroupFeedFragment() {
        // Required empty public constructor
    }

    public interface OnFragmentInteractionListener {
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
            selectedImageURL = bundle.getString("imageURL");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (goToPost) {
            goToPost = false;
            CreatePostFragment cpFragment = CreatePostFragment.newInstance(selectedImageURL);
            cpFragment.setTargetFragment(GroupFeedFragment.this, 1);
            mListener.navigateToDialog(cpFragment);
        } else if (goToShare) {
            goToShare = false;
            ShareFromFragment shareFromFragment = ShareFromFragment.newInstance();
            shareFromFragment.setTargetFragment(GroupFeedFragment.this, 1);
            mListener.navigateToDialog(shareFromFragment);
        } else if (goToUpload) {
            goToUpload = false;
            UploadedImages uploadedImages = UploadedImages.newInstance(group);
            uploadedImages.setTargetFragment(GroupFeedFragment.this, 1);
            mListener.navigateToDialog(uploadedImages);
        } else {
            // Inflate the layout for this fragment
            // Equivalent to setContentView
            // create ContextThemeWrapper from the original Activity Context with the custom theme
            Context contextThemeWrapper = null;
            if (themeName != null)
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
        return null;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        allStories = new ArrayList<>();
        rvPosts = view.findViewById(R.id.rvPostsFeed);
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_child);
        welcomeImage = (ImageView) view.findViewById(R.id.welcomeImage);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        query.fromLocalDatastore();
        query.getInBackground(groupObjectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    initiateGroup(object);
                } else {
                    e.printStackTrace();
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });

        ivLaunchNewPost.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                CreatePostFragment cpFragment = CreatePostFragment.newInstance(null);
                cpFragment.setTargetFragment(GroupFeedFragment.this, 1);
                mListener.navigateToDialog(cpFragment);
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

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allStories != null && allStories.size() != 0) {
                    selected = true;
                    //create a bundle
                    Bundle bundle = new Bundle();
                    //save all necessary info for story display
                    bundle.putString("text", text);
                    bundle.putString("caption", caption);
                    bundle.putParcelableArrayList("arraylist", allStories);
                    bundle.putString("dataType", dataType);

                    //create StoryDisplayfragment
                    StoryDIsplayFragment sFragment = StoryDIsplayFragment.newInstance(null, null);
                    //add bundle to it
                    sFragment.setArguments(bundle);
                    //navigate to StoryDisplayfragment
                    sFragment.setTargetFragment(GroupFeedFragment.this, 1);
                    mListener.navigateToDialog(sFragment);
                }
            }
        });
    }

    private void initiateGroup(final ParseObject object) {
        group = (Group) object;
        publicCurrentGroup = group; //set this static for easy access in other classes
        currentGroup = group;

        groupName = object.getString("groupName");
        tvGroupName.setText(groupName);
        groupId = convert(group.getObjectId());
        ParseFile groupImage = object.getParseFile("groupImage");

        if (groupImage != null) {
            Glide.with(getContext())
                    .load(groupImage.getUrl())
                    .apply(RequestOptions.circleCropTransform())
                    .into(ivGroupPic);
        }

        //displaying the posts
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity().getSupportFragmentManager(), getContext(), posts, group.getNicknamesDict(), themeName);
        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(GroupFeedFragment.this.getContext()));
        rvPosts.setAdapter(postAdapter);
        loadTopPosts(true);

        final Story.Query storyQuery = new Story.Query();
        storyQuery.fromLocalDatastore();
        storyQuery.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> objects, ParseException e) {
                if (e == null) {
                    //fetches all stories for current group
                    for (int i = 0; i < objects.size(); i++) {
                        List<String> recIDs = objects.get(i).getAllRecipient();
                        if (recIDs != null && recIDs.contains(publicCurrentGroup.getObjectId())) {
                            allStories.add(objects.get(i));
                        }
                    }
                    Collections.reverse(allStories); //reverse the order inorder to dosplay the most recent story
                    if (allStories.size() != 0) {
                        displayStory(R.id.fragment_child);
                    } else {
                        welcomeImage.setVisibility(View.VISIBLE);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /*gets current story, checks if its a video or picture, gets the right fragment to display the story*/
    private void displayStory(int view_id) {
        final FragmentManager fragmentManager = getFragmentManager(); //Initiates FragmentManager
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Story currentStory = ((allStories.size() == 0) ? null : allStories.get(0)); //selects a story
        if (currentStory != null) {
            if (currentStory.getStoryType().compareTo("video") == 0) {
                navigateToVideoFragment(fragmentTransaction, view_id);
            } else {
                try {
                    imageFilePath = currentStory.getStory().getFile().getAbsolutePath();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                navigateToPictureFragment(imageFilePath, fragmentTransaction, view_id);
            }
        }
    }

    /*navigates to the Picture fragment and display the story*/
    private void navigateToPictureFragment(String imageFilePath,
                                           FragmentTransaction fragmentTransaction, int view_id) {
        Fragment pictureFragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString("imagePath", imageFilePath);
        args.putString("text", text);
        args.putString("caption", caption);
        pictureFragment.setArguments(args);
        fragmentTransaction.replace(view_id, pictureFragment, PREVIEW_TAG)
                .commit();
    }

    /*navigates to the Video fragment and display the story*/
    private void navigateToVideoFragment(
            FragmentTransaction fragmentTransaction, int view_id) {
        final Fragment videoFragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        args.putString("caption", caption);
        args.putParcelableArrayList("allStories", allStories);
        videoFragment.setArguments(args);
        fragmentTransaction.replace(view_id, videoFragment, PREVIEW_TAG)
                .commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadTopPosts(Boolean fromLocal) {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser().forGroup(group);
        if (fromLocal) {
            postsQuery.fromLocalDatastore();
        } else {
            postsQuery.fromNetwork();
        }
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

    private void refreshFeed() {
        postAdapter.clear();
        displayStory(R.id.fragment_child);
        loadTopPosts(false);
        rvPosts.scrollToPosition(0);
    }

    // for converting group objectId to integer (used for chat channel ID)
    // credit to https://stackoverflow.com/questions/30404946/how-to-convert-parse-objectid-string-to-long
    private static final String CHARS = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0";

    private static int convertChar(char c) {
        int ret = CHARS.indexOf(c);
        if (ret == -1)
            throw new IllegalArgumentException("Invalid character encountered: " + c);
        return ret;
    }

    public static int convert(String s) {
        if (s.length() != 10)
            throw new IllegalArgumentException("String length must be 10, was " + s.length());
        int ret = 0;
        for (int i = 0; i < s.length(); i++) {
            ret = (ret << 6) + convertChar(s.charAt(i));
        }
        return ret;
    }

    public void passPostingToFeed(PhotoHelper photoHelper, String newMessage, Boolean hasNewPic, String imageUrl) {
        progressBar.setVisibility(View.VISIBLE);

        final Post newPost = new Post();
        newPost.pinInBackground("posts");
        newPost.saveEventually();
        ParseFile image;

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseACL acl = new ParseACL(currentUser);
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        newPost.setACL(acl);

        newPost.initPost(newMessage, currentGroup);

        if (hasNewPic) {
            if (imageUrl == null) {
                image = photoHelper.grabImage();
                final ParseFile finalImage = image;
                finalImage.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            newPost.setImage(finalImage);
                            saveNewPost(newPost);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                newPost.setImageURL(imageUrl);
                saveNewPost(newPost);
            }
        } else {
            saveNewPost(newPost);
        }
    }

    private void saveNewPost(final Post newPost) {
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    currentGroup.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                progressBar.setVisibility(View.GONE);
                                posts.add(0, newPost);
                                postAdapter.notifyItemInserted(0);
                                rvPosts.scrollToPosition(0);
                                Toast.makeText(getContext(), "New post created", Toast.LENGTH_SHORT).show();
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
