package com.bill.android.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bill.android.myapplication.R;
import com.bill.android.myapplication.activities.DetailActivity;
import com.bill.android.myapplication.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Movie> mMovieList;

    // Constructor of the class
    public MovieAdapter(Context context, ArrayList<Movie> itemList) {
        mContext = context;
        this.mMovieList = itemList;
    }

    // get the size of the list
    @Override
    public int getItemCount() {
        return mMovieList == null ? 0 : mMovieList.size();
    }

    // specify the row layout file and click for each row
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    // load data in each row element
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = mMovieList.get(position);

        Uri uri = Uri.parse(movie.getPoster());
        Picasso.get().load(uri).into(holder.poster);

        holder.poster.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("movie_extra", movie);
                mContext.startActivity(intent);
            }
        });
    }

    // Static inner class to initialize the views of rows
    static class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView poster;

        ViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.one_poster);
        }
    }
}