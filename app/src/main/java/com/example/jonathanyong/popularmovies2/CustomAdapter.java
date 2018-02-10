package com.example.jonathanyong.popularmovies2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jonathanyong on 9/1/18.
 */

public class CustomAdapter extends BaseAdapter {


    private Context mContext;
    private List<Movie> movies;

    public CustomAdapter(Context mContext, List<Movie> movies) {
        this.mContext = mContext;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Object getItem(int i) {
        return movies.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        View customView = View.inflate(mContext, R.layout.custom_row,null);

        ImageView movieImage = (ImageView)customView.findViewById(R.id.imageView);
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185" + movies.get(i).getMovieImage()).into(movieImage);

        return customView;
    }
}
