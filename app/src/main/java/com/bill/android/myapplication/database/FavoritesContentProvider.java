package com.bill.android.myapplication.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// Credit to https://github.com/udacity/android-content-provider
public class FavoritesContentProvider extends ContentProvider {

    private static final String LOG_TAG = FavoritesContentProvider.class.getSimpleName();
    public static final int FAVORITE = 100;
    public static final int FAVORITE_WITH_ID = 200;
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDbHelper favoritesDbHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoritesContract.FavoriteEntry.TABLE_NAME, FAVORITE);
        matcher.addURI(authority, FavoritesContract.FavoriteEntry.TABLE_NAME + "/#", FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        favoritesDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case FAVORITE: {
                return FavoritesContract.FavoriteEntry.CONTENT_DIR_TYPE;
            }
            case FAVORITE_WITH_ID: {
                return FavoritesContract.FavoriteEntry.CONTENT_ITEM_TYPE;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();

        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                retCursor = db.query(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case FAVORITE_WITH_ID: {
                retCursor = db.query(
                        FavoritesContract.FavoriteEntry.TABLE_NAME,
                        projection,
                        FavoritesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                long id = db.insert(FavoritesContract.FavoriteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = FavoritesContract.FavoriteEntry.buildFavoritesUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;

        switch(match) {
            case FAVORITE:
                numDeleted = db.delete(FavoritesContract.FavoriteEntry.TABLE_NAME, selection, selectionArgs);

                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FavoritesContract.FavoriteEntry.TABLE_NAME + "'");
                break;
            case FAVORITE_WITH_ID:
                numDeleted = db.delete(FavoritesContract.FavoriteEntry.TABLE_NAME,
                        FavoritesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});

                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + FavoritesContract.FavoriteEntry.TABLE_NAME + "'");
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int numUpdated = 0;

        if (values == null) {
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                numUpdated = db.update(FavoritesContract.FavoriteEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case FAVORITE_WITH_ID: {
                numUpdated = db.update(FavoritesContract.FavoriteEntry.TABLE_NAME, values,
                        FavoritesContract.FavoriteEntry.COLUMN_NAME_MOVIE_ID + " ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
