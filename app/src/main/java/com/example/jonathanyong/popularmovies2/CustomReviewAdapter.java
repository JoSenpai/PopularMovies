package com.example.jonathanyong.popularmovies2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jonathanyong on 2/2/18.
 */

public class CustomReviewAdapter extends BaseAdapter{

    private Context mContext;
    private List<Review> reviews;


    public CustomReviewAdapter(Context mContext, List<Review> reviews) {
        this.mContext = mContext;
        this.reviews = reviews;
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int i) {
        return reviews.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View customView = View.inflate(mContext, R.layout.review_adapter,null);

        TextView author = (TextView)customView.findViewById(R.id.author);
        TextView content = (TextView)customView.findViewById(R.id.content);

        author.setText(reviews.get(i).getAuthor());
        content.setText(reviews.get(i).getContent());

        return customView;
    }
}

