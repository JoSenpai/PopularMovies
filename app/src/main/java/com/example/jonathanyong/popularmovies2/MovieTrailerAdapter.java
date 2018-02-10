package com.example.jonathanyong.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.view.View;
import android.os.Bundle;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonathanyong on 1/2/18.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.TrailerHolder>{

    private static int trailerHolderCount;
    private int numberTrailers;
    private List<String> keys;
    private ListItemClickListener listItemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public MovieTrailerAdapter(int numberTrailers, List<String> keys, ListItemClickListener listener) {
        this.numberTrailers = numberTrailers;
        trailerHolderCount = 1;
        this.keys = keys;
        listItemClickListener = listener;
    }

    @Override
    public TrailerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        TrailerHolder trailerHolder = new TrailerHolder(view);

        trailerHolderCount++;

        return trailerHolder;
    }

    @Override
    public void onBindViewHolder(TrailerHolder holder, int position) {
        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return numberTrailers;
    }

    public class TrailerHolder extends RecyclerView.ViewHolder implements OnClickListener {

        TextView trailerHolderIndex;

        public TrailerHolder(View itemView) {
            super(itemView);

            trailerHolderIndex = (TextView)itemView.findViewById(R.id.trailerText);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            int number = position + 1;
            trailerHolderIndex.setText("Trailer #" + number);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            listItemClickListener.onListItemClick(clickedPosition);

        }
    }
}
