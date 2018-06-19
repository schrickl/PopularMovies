/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bill.android.myapplication.utils;

import android.content.Context;
import android.util.Log;

import com.bill.android.myapplication.models.Movie;
import com.bill.android.myapplication.models.Review;
import com.bill.android.myapplication.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public final class TmdbJsonUtils {

    public static ArrayList<Movie> getSimpleMovieStringsFromJson(
            Context context, String movieJsonStr)
            throws JSONException {

        final String LOG_TAG = TmdbJsonUtils.class.getSimpleName();
        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_USER_RATING = "vote_average";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_MOVIE_ID = "id";

        ArrayList<Movie> movies = new ArrayList<>();

        if (movieJsonStr != null) {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < movieArray.length(); i++) {

                JSONObject movieResult = movieArray.getJSONObject(i);

                movies.add(i, new Movie(movieResult.getString(TMDB_TITLE),
                        movieResult.getString(TMDB_MOVIE_ID),
                        movieResult.getString(TMDB_OVERVIEW),
                        movieResult.getDouble(TMDB_USER_RATING),
                        movieResult.getString(TMDB_RELEASE_DATE),
                        movieResult.getString(TMDB_POSTER_PATH)));
            }
        }

        return movies;
    }

    public static ArrayList<Review> getSimpleReviewStringsFromJson(
            Context context, String reviewJsonStr)
            throws JSONException {

        final String LOG_TAG = TmdbJsonUtils.class.getSimpleName();
        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_URL = "url";

        ArrayList<Review> reviews = new ArrayList<>();

        if (reviewJsonStr != null) {
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < reviewArray.length(); i++) {

                JSONObject movieResult = reviewArray.getJSONObject(i);

                reviews.add(i, new Review(movieResult.getString(TMDB_AUTHOR),
                        movieResult.getString(TMDB_CONTENT),
                        movieResult.getString(TMDB_URL)));
            }
        }

        return reviews;
    }

    public static ArrayList<Trailer> getSimpleTrailerStringsFromJson(
            Context context, String trailerJsonStr)
            throws JSONException {

        final String LOG_TAG = TmdbJsonUtils.class.getSimpleName();
        final String TMDB_RESULTS = "results";
        final String TMDB_NAME = "name";
        final String TMDB_KEY = "key";

        ArrayList<Trailer> trailers = new ArrayList<>();

        if (trailerJsonStr != null) {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TMDB_RESULTS);

            for (int i = 0; i < trailerArray.length(); i++) {

                JSONObject movieResult = trailerArray.getJSONObject(i);

                trailers.add(i, new Trailer(movieResult.getString(TMDB_NAME),
                        movieResult.getString(TMDB_KEY)));
            }
        }

        return trailers;
    }
}