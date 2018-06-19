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

import android.net.Uri;
import android.util.Log;

import com.bill.android.myapplication.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    private static final String BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
    private static final String API_KEY = BuildConfig.API_KEY;

    final static String QUERY_PARAM = "api_key";

    public static URL buildMovieUrl(String order) {

        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(order)
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    public static URL buildTrailerUrl(String id) {

        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    public static URL buildReviewUrl(String id) {

        Uri builtUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter(QUERY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(LOG_TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d(LOG_TAG, "A network error occurred!");
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }
}