package com.example.jonathanyong.popularmovies2;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MovieReview extends AppCompatActivity {

    //ArrayList<String> author = new ArrayList<>();
    //ArrayList<String> review = new ArrayList<>();
    //HashMap<String, String> review = new HashMap<>();

    List<Review> review = new ArrayList<>();
    CustomReviewAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_review);

        setTitle("Reviews");

        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setCacheColorHint(Color.TRANSPARENT);
        listView.requestFocus(0);
        Intent intent = getIntent();
        int movieId = intent.getIntExtra("movieId",0);

        listAdapter= new CustomReviewAdapter(getApplicationContext(), review);
        listView.setAdapter(listAdapter);

        new getReview().execute("http://api.themoviedb.org/3/movie/" + movieId + "/reviews?api_key=d8e87f45e30ef3289e4caec90a864fcb");

    }

    public class getReview extends AsyncTask<String, Void, String>
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
            try {
                JSONObject jsonObject = new JSONObject(s);
                String movieURL = jsonObject.getString("results");

                JSONArray arr = new JSONArray(movieURL);

                for(int i = 0; i < arr.length(); i++)
                {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    System.out.println(jsonPart);
                    String authorJSON = jsonPart.getString("author");
                    String reviewJSON = jsonPart.getString("content");
                    review.add(new Review(authorJSON, reviewJSON));
                }
                listAdapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(s);
        }
    }
}
