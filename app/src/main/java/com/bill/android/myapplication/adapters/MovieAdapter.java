package com.bill.android.myapplication.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bill.android.myapplication.R;
import com.bill.android.myapplication.activities.DetailActivity;
import com.bill.android.myapplication.database.FavoritesContract;
import com.bill.android.myapplication.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Movie> mMovieList;
    private Observer mObserver;

    public MovieAdapter(Context context, ArrayList<Movie> itemList) {
        mContext = context;
        this.mMovieList = itemList;
        mObserver = new Observer(new Handler());
    }

    @Override
    public int getItemCount() {
        return mMovieList == null ? 0 : mMovieList.size();
    }

    public void addData(ArrayList<Movie> movies) {
        mMovieList.clear();
        mMovieList.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        ViewHolder myViewHolder = new ViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Movie movie = mMovieList.get(position);

        Uri uri = Uri.parse(movie.getPoster());
        Picasso.get().load(uri).into(holder.poster);

        holder.poster.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getContentResolver().registerContentObserver(Uri.parse(FavoritesContract.FavoriteEntry.CONTENT_URI.toString() + "/" + movie.getId()), false, mObserver);
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("movie_extra", movie);
                mContext.startActivity(intent);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {

        ImageView poster;

        ViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.one_poster);
        }
    }

    class Observer extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public Observer(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            Log.v("test", "onchange");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Long id = null;
            Log.v("test", uri.toString());

            try {
                id = ContentUris.parseId(uri);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            if (id != null) {
                for (int i = 0; i < mMovieList.size(); i++) {
                    if (mMovieList.get(i).getId().equals(String.valueOf(id))) {
                        mMovieList.remove(i);
                        break;
                    }
                }
                notifyDataSetChanged();
            }
        }
    }
}