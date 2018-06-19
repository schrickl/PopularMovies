package com.bill.android.myapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private static final String LOG_TAG = Movie.class.getSimpleName();
    private String mTitle;
    private String mId;
    private String mSynopsis;
    private double mUserRating;
    private String mReleaseDate;
    private String mPoster;
    private String mMovieUrl = "http://image.tmdb.org/t/p/w185";

    public Movie(String title, String id, String synopsis, double userRating, String releaseDate, String poster) {
        this.mTitle = title;
        this.mId = id;
        this.mSynopsis = synopsis;
        this.mUserRating = userRating;
        this.mReleaseDate = releaseDate;
        this.mPoster = poster;
    }

    private Movie(Parcel in) {
        mTitle = in.readString();
        mId = in.readString();
        mSynopsis = in.readString();
        mUserRating = in.readDouble();
        mReleaseDate = in.readString();
        mPoster = in.readString();
        mMovieUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mId);
        dest.writeString(mSynopsis);
        dest.writeDouble(mUserRating);
        dest.writeString(mReleaseDate);
        dest.writeString(mPoster);
        dest.writeString(mMovieUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public String getSynopsis() {
        return mSynopsis;
    }

    public void setSynopsis(String synopsis) {
        this.mSynopsis = synopsis;
    }

    public double getUserRating() {
        return mUserRating;
    }

    public void setUserRating(double userRating) {
        this.mUserRating = userRating;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.mReleaseDate = releaseDate;
    }

    public String getPoster() {
        return this.mMovieUrl + this.mPoster;
    }

    public void setPoster(String poster) {
        this.mPoster = poster;
    }
}
