package com.bill.android.myapplication.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bill.android.myapplication.R;
import com.bill.android.myapplication.adapters.ReviewAdapter;
import com.bill.android.myapplication.adapters.TrailerAdapter;
import com.bill.android.myapplication.database.FavoritesContract;
import com.bill.android.myapplication.database.FavoritesContract.FavoriteEntry;
import com.bill.android.myapplication.models.Movie;
import com.bill.android.myapplication.models.Review;
import com.bill.android.myapplication.models.Trailer;
import com.bill.android.myapplication.utils.NetworkUtils;
import com.bill.android.myapplication.utils.TmdbJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private Movie mMovie;
    private boolean isFavorite = false;
    private ArrayList<Review> mReview = new ArrayList<>();
    private ArrayList<Review> mReviewList = new ArrayList<>();
    private ArrayList<Trailer> mTrailer = new ArrayList<>();
    private ArrayList<Trailer> mTrailerList = new ArrayList<>();
    private ReviewAdapter mReviewAdapter;
    private TrailerAdapter mTrailerAdapter;
    @BindView(R.id.iv_poster) ImageView mPoster;
    @BindView(R.id.rb_rating) RatingBar mRating;
    @BindView(R.id.tv_release_date) TextView mRelease;
    @BindView(R.id.tv_synopsis) TextView mSynopsis;
    @BindView(R.id.rv_reviews) RecyclerView mReviewsRecyclerView;
    @BindView(R.id.rv_trailers) RecyclerView mTrailersRecyclerView;
    @BindView(R.id.btn_favorite) Button mFavoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ButterKnife.bind(this);
        mMovie = getIntent().getParcelableExtra("movie_extra");
        Uri uri = Uri.parse(mMovie.getPoster());
        Picasso.get().load(uri).into(mPoster);

        String id = mMovie.getId();
        initializeUI();

        loadTrailers();
        loadReviews();
    }

    private void initializeUI() {
        setTitle(mMovie.getTitle());
        mSynopsis.setText(mMovie.getSynopsis());
        mRelease.setText(format(mMovie.getReleaseDate()));
        mRating.setRating((float) mMovie.getUserRating() / 2);

        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mReviewAdapter = new ReviewAdapter(this, mReview);
        mReviewsRecyclerView.setAdapter(mReviewAdapter);

        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerAdapter = new TrailerAdapter(this, mTrailer);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);

        isFavorite = isFavorited(mMovie.getId());
        setFavoriteText(isFavorite);

        mFavoriteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFavorite) {
                    String mSelectionClause = FavoriteEntry.COLUMN_NAME_MOVIE_ID + " = ?";
                    String[] mSelectionArgs = {mMovie.getId()};
                    int mRowsDeleted = getContentResolver().delete(FavoritesContract.FavoriteEntry.CONTENT_URI, mSelectionClause, mSelectionArgs);
                    Toast.makeText(DetailActivity.this, mMovie.getTitle() + " " + DetailActivity.this.getString(R.string.unfavorited_text), Toast.LENGTH_SHORT).show();
                } else {
                    new AddFavoriteTask().execute(mMovie);

                }
                isFavorite = !isFavorite;
                setFavoriteText(isFavorite);
            }
        });


    }

    private void setFavoriteText(boolean favorite) {
        if (favorite) {
            mFavoriteButton.setText(getResources().getString(R.string.unfavorite_text));
        } else {
            mFavoriteButton.setText(getResources().getString(R.string.favorite_text));
        }
    }

    private boolean isFavorited(String movieId) {
        String mSelectionClause = FavoriteEntry.COLUMN_NAME_MOVIE_ID + " = ?";
        String[] mSelectionArgs = {movieId};
        Cursor mCursor = getContentResolver().query(FavoritesContract.FavoriteEntry.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);                       // The sort order for the returned rows
        boolean isFavorite = (mCursor != null && mCursor.getCount() == 1);
        mCursor.close();
        return isFavorite;
    }

    private void loadTrailers() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            new FetchTrailerTask().execute(mMovie.getId());
        }
    }

    private void loadReviews() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            new FetchReviewTask().execute(mMovie.getId());
        }
    }

    private String format(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = new Date();
        try {
            newDate = format.parse(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (String) DateFormat.format("MM-dd-yyyy",newDate);
    }


    //TODO Put this in separate class
    public class FetchTrailerTask extends AsyncTask<String, Void, ArrayList<Trailer>> {

        @Override
        protected ArrayList<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String id = params[0];
            URL trailerRequestUrl = NetworkUtils.buildTrailerUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(trailerRequestUrl);

                mTrailerList = TmdbJsonUtils
                        .getSimpleTrailerStringsFromJson(DetailActivity.this, jsonMovieResponse);

                return mTrailerList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {
            if (trailers != null) {
                mTrailer.clear();
                mTrailer.addAll(mTrailerList);
                mTrailerAdapter.notifyDataSetChanged();
            }
        }
    }

    // TODO Put this in separate class
    public class FetchReviewTask extends AsyncTask<String, Void, ArrayList<Review>> {

        @Override
        protected ArrayList<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String id = params[0];
            URL reviewRequestUrl = NetworkUtils.buildReviewUrl(id);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(reviewRequestUrl);

                mReviewList = TmdbJsonUtils
                        .getSimpleReviewStringsFromJson(DetailActivity.this, jsonMovieResponse);

                return mReviewList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            if (reviews != null) {
                mReview.clear();
                mReview.addAll(mReviewList);
                mReviewAdapter.notifyDataSetChanged();
            }
        }
    }

    // TODO Put this in separate class
    public class AddFavoriteTask extends AsyncTask<Movie, Void, Uri> {
        Movie movie;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Uri doInBackground(Movie... params) {
            movie = params[0];
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteEntry.COLUMN_NAME_TITLE, movie.getTitle());
            contentValues.put(FavoriteEntry.COLUMN_NAME_SYNOPSIS, movie.getSynopsis());
            contentValues.put(FavoriteEntry.COLUMN_NAME_POSTER_PATH, movie.getPoster());
            contentValues.put(FavoriteEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getUserRating());
            contentValues.put(FavoriteEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
            contentValues.put(FavoriteEntry.COLUMN_NAME_MOVIE_ID, movie.getId());
            return getContentResolver().insert(FavoritesContract.FavoriteEntry.CONTENT_URI, contentValues);
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                Toast.makeText(DetailActivity.this, movie.getTitle() + " " + DetailActivity.this.getString(R.string.favorited_text), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
