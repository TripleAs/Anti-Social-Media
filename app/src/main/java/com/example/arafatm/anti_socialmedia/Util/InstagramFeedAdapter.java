package com.example.arafatm.anti_socialmedia.Util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InstagramFeedAdapter extends BaseAdapter {
    private final Context mContext;
//    private final Book[] books;

    // need constructor to instantiate
    public InstagramFeedAdapter(Context context) {  // + Book[] books
        mContext = context;
//        books = mbooks;
    }

    // number of cells to render
    @Override
    public int getCount() {
//        return books.length;
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    // return a dummy textview as the cell view for gridview
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView dummyTextView = new TextView(mContext);
        dummyTextView.setText(String.valueOf(position));
        return dummyTextView;
    }

}
