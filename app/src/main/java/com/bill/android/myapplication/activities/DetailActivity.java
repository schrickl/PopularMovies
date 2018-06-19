package com.bill.android.myapplication.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bill.android.myapplication.R;
import com.bill.android.myapplication.adapters.ReviewAdapter;
import com.bill.android.myapplication.adapters.TrailerAdapter;
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

        mReviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mReviewAdapter = new ReviewAdapter(this, mReview);
        mReviewsRecyclerView.setAdapter(mReviewAdapter);

        mTrailersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mTrailerAdapter = new TrailerAdapter(this, mTrailer);
        mTrailersRecyclerView.setAdapter(mTrailerAdapter);
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
                Log.d(LOG_TAG, "trailerList size: " + mTrailerList.size());
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
                Log.d(LOG_TAG, "ReviewList size: " + mReviewList.size());
                mReviewAdapter.notifyDataSetChanged();
            }
        }
    }
}
