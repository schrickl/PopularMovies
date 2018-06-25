package com.bill.android.myapplication.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.bill.android.myapplication.R;
import com.bill.android.myapplication.adapters.MovieAdapter;
import com.bill.android.myapplication.database.FavoritesContract;
import com.bill.android.myapplication.database.FavoritesContract.FavoriteEntry;
import com.bill.android.myapplication.models.Movie;
import com.bill.android.myapplication.utils.NetworkUtils;
import com.bill.android.myapplication.utils.TmdbJsonUtils;

import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ArrayList<Movie> mMovie = new ArrayList<>();
    private ArrayList<Movie> mMovieList = new ArrayList<>();
    private MovieAdapter mAdapter;
    @BindView(R.id.rvMovies) RecyclerView mRecyclerView;

    private static final String BUNDLE_RECYCLER_LAYOUT = "recycler_key";
    private Parcelable mSavedRecyclerLayoutState;

    private int sortOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new MovieAdapter(this, mMovie);
        mRecyclerView.setAdapter(mAdapter);

        sortOrder = getSortOrderSetting();

        if (savedInstanceState == null) {
            loadMovieData();
        } else {
            if (sortOrder == R.id.sort_favorites) {
                loadFavorites();
            } else if (sortOrder == R.id.sort_top_rated) {
                new FetchMovieTask().execute(getResources().getString(R.string.endpoint_top_rated));
            } else {
                new FetchMovieTask().execute(getResources().getString(R.string.endpoint_popular));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        saveSortOrderSetting(item.getItemId());

        switch (item.getItemId()) {
            case R.id.sort_most_popular:
                new FetchMovieTask().execute(getResources().getString(R.string.endpoint_popular));
                break;
            case R.id.sort_top_rated:
                new FetchMovieTask().execute(getResources().getString(R.string.endpoint_top_rated));
                break;
            case R.id.sort_favorites:
                loadFavorites();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null)
        {
            mSavedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
        }
    }

    private void loadFavorites() {
        Cursor mCursor = getContentResolver().query(FavoritesContract.FavoriteEntry.CONTENT_URI, null, null, null, null);                       // The sort order for the returned rows

        if (mCursor != null && mCursor.getCount() > 0) {
            mMovieList = new ArrayList<>(mCursor.getCount());
            mCursor.moveToFirst();
            for (int i = 0; i < mCursor.getCount(); i++, mCursor.moveToNext()) {
                mMovieList.add(i, getMovieFromCursor(mCursor));
            }

            mMovie.clear();
            mMovie.addAll(mMovieList);
            mAdapter.notifyDataSetChanged();
        }
        mCursor.close();
    }

    private Movie getMovieFromCursor(Cursor c) {
        String title = c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_NAME_TITLE));
        String id = c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_NAME_MOVIE_ID));
        String synopsis = c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_NAME_SYNOPSIS));
        double voteAverage = c.getDouble(c.getColumnIndex(FavoriteEntry.COLUMN_NAME_VOTE_AVERAGE));
        String releaseDate = c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_NAME_RELEASE_DATE));
        String posterPath = c.getString(c.getColumnIndex(FavoriteEntry.COLUMN_NAME_POSTER_PATH));

        return new Movie(title, id, synopsis, voteAverage, releaseDate, posterPath);
    }

    private void loadMovieData() {
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            new FetchMovieTask().execute(getResources().getString(R.string.endpoint_popular));
        }
    }

    public int getSortOrderSetting() {
        SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        return mSettings.getInt("sortOrder", R.id.sort_most_popular);
    }

    public void saveSortOrderSetting(int newSortOrder) {
        SharedPreferences mSettings = this.getSharedPreferences("Settings", 0);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt("sortOrder", newSortOrder);
        editor.apply();
    }

    // TODO Put this in separate class
    public class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>> {

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String sort_order = params[0];
            URL movieRequestUrl = NetworkUtils.buildMovieUrl(sort_order);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                mMovieList = TmdbJsonUtils
                        .getSimpleMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return mMovieList;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movieData) {
            if (movieData != null) {
                mMovie.clear();
                mMovie.addAll(mMovieList);
                mAdapter.notifyDataSetChanged();
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedRecyclerLayoutState);
            }
        }
    }
}
