package com.example.arafatm.anti_socialmedia.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.example.arafatm.anti_socialmedia.Util.CommentAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentFragment extends Fragment {
    @BindView(R.id.btCommentPost)
    Button btCommentSubmit;
    @BindView(R.id.etComment)
    EditText etCommentText;
    @BindView(R.id.rvComments)
    RecyclerView rvComments;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayoutManager linearLayoutManager;
    CommentAdapter commentAdapter;
    ArrayList<Post> comments;
    Post originalPost;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        originalPost = Parcels.unwrap(getArguments().getParcelable(Post.class.getSimpleName()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null) {
            container.removeAllViews();
        }
        return inflater.inflate(R.layout.fragment_comments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        //set up ArrayList of pointers to comments
        final ArrayList<Post> pointToComment = originalPost.getComments();
        pointToComment.add(0, originalPost);      //adds original post to comment fragment
        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(pointToComment);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rvComments.setLayoutManager(linearLayoutManager);
        rvComments.setAdapter(commentAdapter);

        // Setup refresh listener which triggers new data loading
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.commentSwipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });
        loadTopPosts();
        btCommentSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String commentString = etCommentText.getText().toString();
                createComment(commentString, pointToComment);
            }
        });
    }

    public static CommentFragment newInstance(Post post) {
        CommentFragment commentFragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(Post.class.getSimpleName(), Parcels.wrap(post));
        commentFragment.setArguments(args);
        return commentFragment;
    }

    private void createComment(String commentString, final ArrayList<Post> pointToComment) {
        final Post comment = new Post();
        comment.pinInBackground("comments");
        comment.saveEventually();

        // save comment to Parse
        comment.setUser(ParseUser.getCurrentUser());
        comment.setCommentString(commentString);
        pointToComment.add(comment);

        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    originalPost.setComments(pointToComment);
                    commentAdapter.notifyDataSetChanged();
                    etCommentText.setText("");
                    Toast.makeText(getContext(), "Comment posted!", Toast.LENGTH_SHORT).show();

                    rvComments.scrollToPosition(comments.size() - 1);
                    comments.addAll(comments);
                } else {
                    e.printStackTrace();
                }
            }

        });
    }

    private void loadTopPosts() {
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop();
        postsQuery.fromLocalDatastore();

        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    objects.add(0, originalPost);
                    comments.addAll(objects);
                    commentAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void refreshFeed() {
        CommentAdapter adapter = new CommentAdapter(comments);
        adapter.clear();
        loadTopPosts();
        rvComments.scrollToPosition(comments.size() - 1);
    }
}