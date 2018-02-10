package com.example.jonathanyong.popularmovies2;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jonathanyong.popularmovies2.data.TaskContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    List<Movie> moviesList = new ArrayList<>();
    private static final int TASK_LOADER_ID = 0;
    private static final String TAG = MainActivity.class.getSimpleName();
    ListAdapter listAdapter;
    GridView gridView;
    List<Integer> movieIds = new ArrayList<>();
    int layoutId = R.layout.activity_main;
    int gridCurrentIndex;
    int currentFilter = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);

        gridView = (GridView)findViewById(R.id.grid_view);
        listAdapter = new CustomAdapter(getApplicationContext(),moviesList);

        if(savedInstanceState != null){
            currentFilter = savedInstanceState.getInt("currentFilter");
        }
        Log.i("Current Filter is", Integer.toString(currentFilter));
        if(currentFilter == 0){
            setTitle("Top Rated");
            new getImages().execute("https://api.themoviedb.org/3/movie/top_rated?api_key=d8e87f45e30ef3289e4caec90a864fcb");
        }else if(currentFilter == 1){
            setTitle("Popular Movies");
            new getImages().execute("https://api.themoviedb.org/3/movie/popular?api_key=d8e87f45e30ef3289e4caec90a864fcb");
        }else if(currentFilter == 2){
            moviesList.clear();
            setTitle("Favourites");
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID,null,this);
        }else{
            setTitle("Popular Movies");
            new getImages().execute("https://api.themoviedb.org/3/movie/popular?api_key=d8e87f45e30ef3289e4caec90a864fcb");
        }

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), MovieInfo.class);

                intent.putExtra("id", moviesList.get(i).getId());
                intent.putExtra("image", moviesList.get(i).getMovieImage());
                intent.putExtra("title", moviesList.get(i).getTitle());
                intent.putExtra("rating", moviesList.get(i).getRating());
                intent.putExtra("popularity", moviesList.get(i).getPopularity());
                intent.putExtra("overview", moviesList.get(i).getOverview());
                intent.putExtra("votes", moviesList.get(i).getVoteCount());
                intent.putExtra("language", moviesList.get(i).getLanguage());
                intent.putExtra("releaseDate", moviesList.get(i).getReleaseDate());

                String title = moviesList.get(i).getTitle();
                Toast.makeText(MainActivity.this, title, Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        });
        System.out.println("On create called");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        gridCurrentIndex = 0;
        if(itemId == R.id.rating)
        {
            moviesList.clear();
            setTitle("Top Rated");
            new getImages().execute("https://api.themoviedb.org/3/movie/top_rated?api_key=d8e87f45e30ef3289e4caec90a864fcb");

        }else if(itemId == R.id.popularity)
        {
            moviesList.clear();
            setTitle("Popular Movies");
            new getImages().execute("https://api.themoviedb.org/3/movie/popular?api_key=d8e87f45e30ef3289e4caec90a864fcb");
        }else if(itemId == R.id.favourites){

            moviesList.clear();
            setTitle("Favourites");
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID,null,this);
        }
        listAdapter = new CustomAdapter(getApplicationContext(),moviesList);
        gridView.setAdapter(listAdapter);

        onSaveInstanceState(new Bundle());
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(getTitle() == "Favourites")
        {
            moviesList.clear();
            getSupportLoaderManager().restartLoader(TASK_LOADER_ID,null,this);
        }
        listAdapter = new CustomAdapter(getApplicationContext(),moviesList);
        gridView.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                movieIds.clear();
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

                    for(int i = 0; i < c.getCount(); i++)
                    {
                        c.moveToPosition(i);
                        int movieID = c.getInt(movieIndex);
                        movieIds.add(movieID);
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
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        for(int i = 0; i < movieIds.size(); i++) {
            new getImages().execute("https://api.themoviedb.org/3/movie/" + movieIds.get(i) + "?api_key=d8e87f45e30ef3289e4caec90a864fcb&language=en-US");
        }    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class getImages extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1) {
                    char current = (char)data;
                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                String movieURL = null;
                JSONObject jsonObject = new JSONObject(s);
                try{
                    movieURL = jsonObject.getString("results");
                }catch (Exception e)
                {

                }

                if(movieURL != null)
                {
                    JSONArray arr = new JSONArray(movieURL);

                    for(int i = 0; i < arr.length(); i++)
                    {
                        JSONObject jsonPart = arr.getJSONObject(i);

                        int id = jsonPart.getInt("id");
                        String title = jsonPart.getString("title");
                        double popularity  = jsonPart.getDouble("popularity");
                        double rating = jsonPart.getDouble("vote_average");
                        String imageURL = jsonPart.getString("poster_path");
                        int voteCount = jsonPart.getInt("vote_count");
                        String language = jsonPart.getString("original_language");
                        String overview = jsonPart.getString("overview");
                        String releaseDate = jsonPart.getString("release_date");

                        moviesList.add(new Movie(id,title,popularity,rating,imageURL, voteCount, language, overview, releaseDate));
                    }
                }else{

                    int id = jsonObject.getInt("id");
                    String title = jsonObject.getString("title");
                    double popularity  = jsonObject.getDouble("popularity");
                    double rating = jsonObject.getDouble("vote_average");
                    String imageURL = jsonObject.getString("poster_path");
                    int voteCount = jsonObject.getInt("vote_count");
                    String language = jsonObject.getString("original_language");
                    String overview = jsonObject.getString("overview");
                    String releaseDate = jsonObject.getString("release_date");

                    moviesList.add(new Movie(id,title,popularity,rating,imageURL, voteCount, language, overview, releaseDate));
                }

                //only set adapter when information is all collected
                gridView.setAdapter(listAdapter);
                gridView.setSelection(gridCurrentIndex-3);
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int index = gridView.getLastVisiblePosition();
        outState.putInt("gridIndex", index);
        if(getTitle() == "Top Rated"){
            outState.putInt("currentFilter",0);
        }else if(getTitle() == "Popular Movies")
        {
            outState.putInt("currentFilter",1);
        }else if(getTitle() == "Favourites")
        {
            outState.putInt("currentFilter",2);
        }
        Log.i("saved Index", Integer.toString(index));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        gridCurrentIndex = savedInstanceState.getInt("gridIndex");
        Log.i("returned Index", Integer.toString(savedInstanceState.getInt("gridIndex")));

        super.onRestoreInstanceState(savedInstanceState);

    }
}
