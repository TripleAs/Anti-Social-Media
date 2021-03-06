package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.arafatm.anti_socialmedia.Models.Group;
import com.example.arafatm.anti_socialmedia.Models.GroupRequestNotif;
import com.example.arafatm.anti_socialmedia.R;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotifsAdapter extends RecyclerView.Adapter<NotifsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<GroupRequestNotif> requests;

    public NotifsAdapter(ArrayList<GroupRequestNotif> List) {
        this.requests = List;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View requestView = inflater.inflate(R.layout.item_group_notif, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(requestView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        GroupRequestNotif request = requests.get(position);

        ParseObject group = request.getRequestedGroup();
        String groupName = group.getString("groupName");
        String senderName = (request.getSender() == null) ? null : request.getSender().getString("fullName");
        String requestMessage = String.format("%s invites you to join the group %s.", senderName, groupName);

        String[] stringsToBold = new String[]{senderName, groupName};
        SpannableStringBuilder builder = makeSectionOfTextBold(requestMessage, stringsToBold);
        viewHolder.tvRequest.setText(builder);

        ParseFile groupPic = group.getParseFile("groupImage");
        if (groupPic != null) {
            Glide.with(context)
                    .load(groupPic.getUrl())
                    .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                    .into(viewHolder.ivCoverPhoto);
        } else {
            viewHolder.ivCoverPhoto.setImageResource(R.drawable.ic_group_default);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView tvRequest;
        public ImageView ivCoverPhoto;
        public Button btAccept;
        public Button btReject;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            super(itemView);
            tvRequest = itemView.findViewById(R.id.tvRequest);
            ivCoverPhoto = itemView.findViewById(R.id.ivCoverPhoto);
            btAccept = itemView.findViewById(R.id.btAccept);
            btReject = itemView.findViewById(R.id.btReject);

            btAccept.setOnClickListener(this);
            btReject.setOnClickListener(this);
        }

        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                GroupRequestNotif request = requests.get(position);
                if (view.getId() == btAccept.getId()) {
                    request.acceptRequest();
                    removeAt(position);
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    List<Group> currentGroups = currentUser.getList("groups");
                    if (currentGroups == null) {
                        currentGroups = new ArrayList<>();
                    }
                    currentGroups.add((Group) request.getRequestedGroup());
                    currentUser.put("groups", currentGroups);
                    currentUser.saveInBackground();
                } else if (view.getId() == btReject.getId()) {
                    request.rejectRequest();
                    removeAt(position);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void removeAt(int position) {
        requests.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, requests.size());
    }

    public static SpannableStringBuilder makeSectionOfTextBold(String text, String... textToBold) {
        SpannableStringBuilder builder = new SpannableStringBuilder(text);

        for (String textItem : textToBold) {
            if (textItem.length() > 0 && !textItem.trim().equals("")) {
                //for counting start/end indexes
                String testText = text.toLowerCase(Locale.US);
                String testTextToBold = textItem.toLowerCase(Locale.US);
                int startingIndex = testText.indexOf(testTextToBold);
                int endingIndex = startingIndex + testTextToBold.length();

                if (startingIndex >= 0 && endingIndex >= 0) {
                    builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
                }
            }
        }

        return builder;
    }

    // Clean all elements of the recycler
    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }
}
