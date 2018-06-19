package com.bill.android.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Credit to https://github.com/udacity/android-content-provider
public class FavoritesDbHelper extends SQLiteOpenHelper{

    private static final String LOG_TAG = FavoritesDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table if it doesn't exist
        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                FavoritesContract.FavoriteEntry.TABLE_NAME + " (" +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID + " INTEGER PRIMARY KEY, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_SYNOPSIS + " TEXT, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_RELEASE_DATE + " TEXT, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_VOTE_COUNT + " INTEGER, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_POSTER_PATH + " TEXT, " +
                FavoritesContract.FavoriteEntry.COLUMN_NAME_BACKDROP_PATH + " TEXT" +
                ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoriteEntry.TABLE_NAME);

        onCreate(db);
    }
}
