package com.example.jonathanyong.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.jonathanyong.popularmovies2.data.TaskContract;
import com.squareup.picasso.Picasso;

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

import com.example.jonathanyong.popularmovies2.data.TaskContract;


public class MovieInfo extends SwipeActivityClass implements MovieTrailerAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    ImageView movieImage;
    TextView title;
    TextView rating;
    TextView popularity;
    TextView overview;
    ImageView favourite;
    Button review;
    int movieId;
    public static List<String> keys = new ArrayList<>();
    private Toast mToast;

    private static final String TAG = MovieInfo.class.getSimpleName();
    private static final int TASK_LOADER_ID = 0;


    private MovieTrailerAdapter movieTrailerAdapter;
    private RecyclerView recyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_info);

        movieImage = (ImageView)findViewById(R.id.moviePoster);
        title = (TextView)findViewById(R.id.title);
        rating = (TextView)findViewById(R.id.rating);
        popularity = (TextView)findViewById(R.id.popularity);
        overview = (TextView)findViewById(R.id.overview);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        favourite = (ImageView)findViewById(R.id.buttonFav);
        review = (Button)findViewById(R.id.buttonReview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        Intent intent = getIntent();
        movieId = intent.getIntExtra("id",0);
        String movieURL = intent.getStringExtra("image");
        String movieTitle = intent.getStringExtra("title");
        double movieRating = intent.getDoubleExtra("rating",0);
        double moviePopularity = intent.getDoubleExtra("popularity",0);
        String movieDate = intent.getStringExtra("releaseDate");
        movieDate = movieDate.substring(0,4);
        String movieOverview = intent.getStringExtra("overview");

        setTitle(movieTitle);

        String movieTitleFinal = movieTitle + "\n" + "(" + movieDate + ")";
        String movieRatingFinal = "Rating: " + Double.toString(movieRating).concat("/10");
        String moviePopularityFinal = "Popularity: " + Double.toString(moviePopularity);

        if (movieTitleFinal.length() > 37 && movieTitleFinal.length() <= 41)
        {
            title.setTextSize(21);
        }else if(movieTitleFinal.length() > 41){
            title.setTextSize(19);
        }

        title.setText(movieTitleFinal);
        rating.setText(movieRatingFinal);
        popularity.setText(moviePopularityFinal);
        overview.setText(movieOverview);

        Picasso.with(getApplicationContext()).load("http://image.tmdb.org/t/p/w185" + movieURL).into(movieImage);

        Log.i("MOVIE ID: ", Integer.toString(movieId));

        new getVideo().execute("http://api.themoviedb.org/3/movie/" + movieId + "/videos?api_key=d8e87f45e30ef3289e4caec90a864fcb");
        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));



    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        if (mToast != null) {
            mToast.cancel();
        }

        int trailerNumber = clickedItemIndex + 1;
        String toastMessage = "Trailer #" + trailerNumber + " clicked.";
        mToast = Toast.makeText(this, toastMessage, Toast.LENGTH_LONG);

        mToast.show();

        String currentKey = keys.get(clickedItemIndex);
        String websiteURL = "https://www.youtube.com/watch?v=" + currentKey;

        Uri webpage = Uri.parse(websiteURL);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(webpage);

        if(intent.resolveActivity(getPackageManager()) != null)
        {
            startActivity(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
                            "_id = ?",
                            new String[]{Integer.toString(movieId)},
                            null);

                    if(c.getCount()>0){
                        favourite.setImageResource(R.drawable.yellowstar);
                        favourite.setTag("yellowstar");
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
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class getVideo extends AsyncTask<String, Void, String>{

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

            try {
                JSONObject jsonObject = new JSONObject(s);
                String movieURL = jsonObject.getString("results");

                JSONArray arr = new JSONArray(movieURL);
                int total = 0;

                for(int i = 0; i < arr.length(); i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    String key = jsonPart.getString("key");
                    keys.add(key);
                    total = i;
                }

                movieTrailerAdapter = new MovieTrailerAdapter(total+1, keys, MovieInfo.this);
                recyclerView.setAdapter(movieTrailerAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }

    public void buttonReviews(View view)
    {
        Intent intent = new Intent(this, MovieReview.class);
        intent.putExtra("movieId", movieId);
        startActivity(intent);
    }

    public void setFavourite(View view){
        if(favourite.getTag() == null || favourite.getTag() == "whitestar"){
            favourite.setImageResource(R.drawable.yellowstar);
            favourite.setTag("yellowstar");

            ContentValues contentValues = new ContentValues();

            //contentValues.put(TaskContract.TaskEntry._ID, movieId);
            contentValues.put(TaskContract.TaskEntry._ID, movieId);
            contentValues.put(TaskContract.TaskEntry.COLUMN_MOVIE, title.getText().toString());
            contentValues.put(TaskContract.TaskEntry.COLUMN_FAVOURITE, "yes");

            Uri uri = getContentResolver().insert(TaskContract.TaskEntry.CONTENT_URI, contentValues);


        }else{
            favourite.setImageResource(R.drawable.whitestar);
            favourite.setTag("whitestar");

            String stringId = Integer.toString(movieId);
            Uri uri = TaskContract.TaskEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(stringId).build();

            getContentResolver().delete(uri, null, null);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: HELLO");
        super.onBackPressed();
    }

    @Override
    protected void onSwipeRight() {
        Intent intent = new Intent(MovieInfo.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onSwipeLeft() {

    }


}