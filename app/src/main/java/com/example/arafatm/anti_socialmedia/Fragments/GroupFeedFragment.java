package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.Models.Story;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Story.PreviewStoryActivity;
import com.example.arafatm.anti_socialmedia.Util.PostAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_BLUE;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_GREEN;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_RED;
import static com.facebook.FacebookSdk.getCacheDir;

public class GroupFeedFragment extends Fragment implements CreatePostFragment.OnFragmentInteractionListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "text";
    private static final String ARG_PARAM3 = "caption";

    private String groupObjectId;
    private String groupName;
    private String text;
    private String caption;
    private boolean selected = false;
    private int groupId;
    public static Group publicCurrentGroup;
    private Group group;
    private String PREVIEW_TAG = "previewStory";
    private FrameLayout frameLayout;
    private FrameLayout frameLayoutPreview;
    private int storyIndex = 0;
    private ImageView next_story;
    private VideoView storyView;
    public static boolean goToShare = false;
    public static boolean goToUpload = false;
    ArrayList<Story> allStories;
    private ImageView prev_story;
    public static boolean goToPost = false;
    private String selectedImageURL;
    private String videoFilePath;

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
    //@BindView(R.id.tvNumberOfComments) TextView tvCommentCount;

    //for posting
    public static PostAdapter postAdapter;
    public static ArrayList<Post> posts;
    public static
    RecyclerView rvPosts;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
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
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");

        allStories = new ArrayList<>();
        next_story = view.findViewById(R.id.iv_next);
        prev_story = view.findViewById(R.id.iv_prev);
        rvPosts = view.findViewById(R.id.rvPostsFeed);
        frameLayout = (FrameLayout) view.findViewById(R.id.fragment_child);
        frameLayoutPreview = (FrameLayout) view.findViewById(R.id.preview_frame);


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




        next_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "story " + storyIndex, Toast.LENGTH_SHORT).show();
                if (storyIndex < allStories.size() - 1) //checks out of bounce exception
                    storyIndex++;
                displayStory(R.id.fragment_child);
                if (selected) //checks if in preview mode
                    displayStory(R.id.preview_frame);
            }
        });

        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected) { //checks if in preview mode
                    selected = false;
                    clearPreviewFragment(PREVIEW_TAG); // clears preview fragment
                } else {
                    selected = true;
                    displayStory(R.id.preview_frame); // display story on default fragment
                }
            }
        });

        prev_story.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "story " + storyIndex, Toast.LENGTH_SHORT).show();
                if (storyIndex > 1)  //checks out of bounce exception
                    storyIndex--;
                displayStory(R.id.fragment_child);
                if (selected)
                    displayStory(R.id.preview_frame);
            }
        });
    }

    private void initiateGroup(ParseObject object) {
        group = (Group) object;
        publicCurrentGroup = group;
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

        //displaying the posts
        posts = new ArrayList<>();
        postAdapter = new PostAdapter(getActivity().getSupportFragmentManager(), getContext(), posts, group.getNicknamesDict(), themeName);

        //RecyclerView setup (layout manager, use adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(GroupFeedFragment.this.getContext()));
        rvPosts.setAdapter(postAdapter);

        loadTopPosts();

        //TODO: ARAFAT'S IMPLEMENTATION
        //TODO:: :::::: Get video to show! , Take care of resizing images, make sure sharing works well
        /*Gets all the stories*/
        final Story.Query storyQuery = new Story.Query();
        storyQuery.findInBackground(new FindCallback<Story>() {
            @Override
            public void done(List<Story> objects, ParseException e) {
                if (e == null) {
                    //fetches all stories for current group
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i).getAllRecipient().contains(group.getObjectId())) {
                            allStories.add(objects.get(i));
                        }
                    }
                    Collections.reverse(allStories); //reverse the order inorder to dosplay the most recent story
                    displayStory(R.id.fragment_child);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    /*Removes the story preview fragment*/
    private void clearPreviewFragment(String TAG_FRAGMENT) {
        Fragment fragment = getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (fragment != null)
            getFragmentManager().beginTransaction().remove(fragment).commit();
    }


    /*gets current story, checks if its a video or picture, gets the right fragment to display the story*/
    private void displayStory(int view_id) {
        final FragmentManager fragmentManager = getFragmentManager(); //Initiates FragmentManager
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Story currentStory = ((allStories.size() == 0) ? null : allStories.get(storyIndex)); //selects a story
        if (currentStory != null) {
            text = currentStory.getStoryCaption();
            caption = currentStory.getStoryText();


            if (currentStory.getStoryType().compareTo("video") == 0) {
                try {
                    videoFilePath = getVideoPath(currentStory);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                navigateToVideoFragment(videoFilePath, fragmentTransaction, view_id);
            } else {
                String imageFilePath = null;
                try {
                    imageFilePath = currentStory.getStory().getFile().getAbsolutePath();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }
                navigateToPictureFragment(imageFilePath, fragmentTransaction, view_id);
            }
        }
    }

    /*gets the video path from video byte*/
    private String getVideoPath(Story currentStory) throws ParseException {
        File outputFile = null;
        try {
            byte[] videoByte = currentStory.getStory().getData();
            outputFile = File.createTempFile("file", "mp4", getCacheDir());
            outputFile.deleteOnExit();
            FileOutputStream fileoutputstream = new FileOutputStream("myVideo.mp4");
            fileoutputstream.write(videoByte);
            fileoutputstream.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return outputFile.getAbsolutePath();
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
    private void navigateToVideoFragment(String videoFilePath,
                                         FragmentTransaction fragmentTransaction, int view_id) {
        final Fragment videoFragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString("text", text);
        args.putString("caption", caption);
        args.putString("videoPath", PreviewStoryActivity.url); // FAKE
        //    args.putString("videoPath", videoFilePath); // Real
        videoFragment.setArguments(args);
        fragmentTransaction.replace(view_id, videoFragment, PREVIEW_TAG)
                .commit();
    }


    //TODO: ARAFAT'S IMPLEMENTATION

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

    private void refreshFeed() {
        PostAdapter adapter = new PostAdapter(getActivity().getSupportFragmentManager(), getContext(), posts, group.getNicknamesDict(), themeName);
        adapter.clear();
        loadTopPosts();
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

    @Override
    public void onFinishCreatePost(Post post) {
        posts.add(0, post);
        postAdapter.notifyItemInserted(0);
        rvPosts.scrollToPosition(0);
        Toast.makeText(getContext(), "New post created", Toast.LENGTH_SHORT).show();
    }
}
