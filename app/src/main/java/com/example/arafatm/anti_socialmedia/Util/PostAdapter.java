package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.arafatm.anti_socialmedia.Fragments.CommentFragment;
import com.example.arafatm.anti_socialmedia.Fragments.ProfileFragment;
import com.example.arafatm.anti_socialmedia.Models.Post;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_BLUE;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_GREEN;
import static com.example.arafatm.anti_socialmedia.Fragments.GroupCustomizationFragment.KEY_RED;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> mPosts;
    Context context;
    FragmentManager manager;
    HashMap<String, String> nicknames;
    OnAdapterInteractionListener mListener;
    ParseUser sender = null;
    String theme;

    public PostAdapter(FragmentManager m, Context c, List<Post> posts, HashMap<String, String> hashMap, String color) {
        manager = m;
        context = c;
        mPosts = posts;
        nicknames = hashMap;
        mListener = (OnAdapterInteractionListener) context;
        theme = color;
    }

    public interface OnAdapterInteractionListener {
        void startUserChat(String contactId, String contactName);
    }

    @NonNull
    @Override
    public PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View postView = inflater.inflate(R.layout.item_feed, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapter.ViewHolder viewHolder, final int position) {
        // get the data according to this position
        final Post post = mPosts.get(position);

        //added this because debugger asked us to
        try {
            sender = post.getSender().fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String message = post.getMessage();
        final String senderName = sender.getString("fullName");
        String number = Integer.toString(post.getCommentsCount());
        String relativeTime = post.getTimestamp();

        final String objectId = sender.getObjectId();
        if (nicknames != null) {
            String nickname = nicknames.get(objectId);
            if (nickname != null) {
                viewHolder.tvFullName.setText(nickname);
            } else {
                viewHolder.tvFullName.setText(senderName);
            }
        } else {
            viewHolder.tvFullName.setText(senderName);
        }

        viewHolder.tvPostText.setText(message);
        viewHolder.tvNumberComments.setText(number);
        viewHolder.tvTimestamp.setText(relativeTime);
        displayLikeImage(viewHolder.ivLike, post);
        displayLikesCount(viewHolder.tvNumLikes, post);

        if (post.getImage() != null || post.getImageURL() != null) {
            String imageURL = null;
            if (post.getImageURL() != null) {
                imageURL = post.getImageURL();
            } else {
                imageURL = post.getImage().getUrl();
            }
            //picture with post
            Glide.with(context)
                    .load(imageURL)
                    .into(viewHolder.imagePost);
        }

        PhotoHelper.displayPropic(sender, viewHolder.ivPropic, context);

        viewHolder.ivDirectMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.startUserChat(objectId, senderName);
            }
        });

        viewHolder.btCommentExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentFragment commentFragment = CommentFragment.newInstance(post, theme);
                manager.beginTransaction()
                        .replace(R.id.layout_child_activity, commentFragment)
                        .commit();
            }
        });

        viewHolder.ivDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "deleting post", Toast.LENGTH_SHORT).show();
                Post toBeRemoved = mPosts.get(position);
                mPosts.remove(position);
                notifyDataSetChanged();
                toBeRemoved.deleteInBackground();
            }
        });

        viewHolder.tvFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(sender);
                manager.beginTransaction()
                        .replace(R.id.layout_child_activity, profileFragment)
                        .commit();
            }
        });

        viewHolder.ivPropic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment profileFragment = ProfileFragment.newInstance(sender);
                manager.beginTransaction()
                        .replace(R.id.layout_child_activity, profileFragment)
                        .commit();
            }
        });

        viewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLike(viewHolder.ivLike, post);
                displayLikesCount(viewHolder.tvNumLikes, post);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    //create ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvFullNameFeed)
        TextView tvFullName;
        @BindView(R.id.tvPostBody)
        TextView tvPostText;
        @BindView(R.id.ivProPicPost)
        ImageView ivPropic;
        @BindView(R.id.tvNumberOfComments)
        TextView tvNumberComments;           //comment
        @BindView(R.id.btCommentIcon)
        ImageButton btCommentExpand;              //comment
        @BindView(R.id.ivImagePost)
        ImageView imagePost;
        @BindView(R.id.ivDirectMessage)
        ImageView ivDirectMessage;
        @BindView(R.id.ivLike)
        ImageView ivLike;
        @BindView(R.id.tvNumLikes)
        TextView tvNumLikes;
        @BindView(R.id.tvTimestamp)
        TextView tvTimestamp;
        @BindView(R.id.iv_delete)
        ImageView ivDeletePost;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void handleLike(ImageView view, Post post) {
        List<String> likes = post.getLikes();
        ParseUser user = post.getSender();
        String objectId = user.getObjectId();
        if (likes == null) {
            likes = new ArrayList<>();
        }
        if (likes.contains(objectId)) {
            likes.remove(objectId);
            post.setLikes(likes);
            displayLikeImage(view, post);
        } else {
            likes.add(objectId);
            post.setLikes(likes);
            displayLikeImage(view, post);
        }
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("Likes", "Successfully liked");
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void displayLikeImage(ImageView imageView, Post post) {
        int emptyLikeResId;
        int filledLikeResId;
        switch (theme) {
            case KEY_RED:
                emptyLikeResId = R.drawable.ic_fire_empty;
                filledLikeResId = R.drawable.ic_fire_color;
                break;
            case KEY_GREEN:
                emptyLikeResId = R.drawable.ic_clover_empty;
                filledLikeResId = R.drawable.ic_clover_color;
                break;
            case KEY_BLUE:
                emptyLikeResId = R.drawable.ic_diamond_empty;
                filledLikeResId = R.drawable.ic_diamond_color;
                break;
            default:
                emptyLikeResId = R.drawable.ic_fire_empty;
                filledLikeResId = R.drawable.ic_fire_color;
                break;
        }
        List<String> likes = post.getLikes();
        if (likes == null) {
            imageView.setImageResource(emptyLikeResId);
        } else if (likes.contains(post.getSender().getObjectId())) {
            imageView.setImageResource(filledLikeResId);
        } else {
            imageView.setImageResource(emptyLikeResId);
        }
    }

    private void displayLikesCount(TextView textView, Post post) {
        List<String> likes = post.getLikes();
        String numLikes = (likes == null) ? "0" : Integer.toString(likes.size());
        textView.setText(numLikes);
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }
}
