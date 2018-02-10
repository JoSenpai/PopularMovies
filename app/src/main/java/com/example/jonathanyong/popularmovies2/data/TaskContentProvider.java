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

package com.example.jonathanyong.popularmovies2.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import static com.example.jonathanyong.popularmovies2.data.TaskContract.TaskEntry.TABLE_NAME;

// COMPLETED (1) Verify that TaskContentProvider extends from ContentProvider and implements required methods
public class TaskContentProvider extends ContentProvider {

    public static final int TASKS = 100;
    public static final int TASKS_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS);
        uriMatcher.addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS + "/#", TASKS_WITH_ID);

        return uriMatcher;
    }

    // Member variable for a TaskDbHelper that's initialized in the onCreate() method
    private TaskDbHelper mTaskDbHelper;

    /* onCreate() is where you should initialize anything you’ll need to setup
    your underlying data source.
    In this case, you’re working with a SQLite database, so you’ll need to
    initialize a DbHelper to gain access to it.
     */
    @Override
    public boolean onCreate() {
        // COMPLETED (2) Complete onCreate() and initialize a TaskDbhelper on startup
        // [Hint] Declare the DbHelper as a global variable

        Context context = getContext();
        mTaskDbHelper = new TaskDbHelper(context);
        return true;
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch(match){
            case TASKS:
                long id = db.insert(TABLE_NAME, null, values);
                if(id > 0){
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id);
                }else
                {
                    throw new android.database.SQLException("Failed to insert row into database");
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        Log.i("INSERT:", "SUCCESS");
        return returnUri;
    }


    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mTaskDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch(match){
            case TASKS:
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mTaskDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int favouriteDeleted;
        String id = uri.getPathSegments().get(1);

        switch(match){
            case TASKS_WITH_ID:
                favouriteDeleted = db.delete(TABLE_NAME, "_id=?", new String[]{id});
                break;
            default:
                System.out.println(id);
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if(favouriteDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.i("DELETE", "SUCCESS");

        return favouriteDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

}
