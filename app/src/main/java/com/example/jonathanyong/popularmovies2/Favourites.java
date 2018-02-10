package com.example.jonathanyong.popularmovies2;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.jonathanyong.popularmovies2.data.TaskContract;

import java.util.ArrayList;
import java.util.List;

public class Favourites extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    List<String> movies = new ArrayList<>();
    private CustomCursorAdapter adapter;
    RecyclerView recyclerView;

    // Constants for logging and referring to a unique loader
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        setTitle("Favourites");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomCursorAdapter(this);
        recyclerView.setAdapter(adapter);

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        // re-queries for all tasks
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
        adapter.notifyDataSetChanged();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if(mTaskData != null){
                    deliverResult(mTaskData);
                }else{
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                Log.i(TAG, "loadInBackground: LOADING");
                try{
                    Cursor c = getContentResolver().query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                    int movieIndex = c.getColumnIndex(TaskContract.TaskEntry._ID);
                    System.out.println(movieIndex);

                    for(int i = 0; i < c.getCount(); i++)
                    {
                        c.moveToPosition(i);
                        int movieID = c.getInt(movieIndex);

                        System.out.println(movieID);
                    }


                    return c;
                }catch (Exception e){
                    Log.e(TAG, "Failed to asynchronously load data");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data){
                mTaskData = data;
                Log.i("SOMETHING IS FOUND: ", data.toString());
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);

    }
}
