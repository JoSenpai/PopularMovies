package com.example.jonathanyong.popularmovies2;

/**
 * Created by jonathanyong on 9/1/18.
 */

public class Movie{

    public int id;
    private String title;
    private double popularity;
    private double rating;
    private String movieImage;
    private int voteCount;
    private String language;
    private String overview;
    private String releaseDate;

    public Movie(int id, String title, double popularity, double rating, String movieImage, int voteCount, String language, String overview, String releaseDate) {
        this.title = title;
        this.popularity = popularity;
        this.rating = rating;
        this.movieImage = movieImage;
        this.voteCount = voteCount;
        this.language = language;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getMovieImage() {
        return movieImage;
    }

    public void setMovieImage(String movieImage) {
        this.movieImage = movieImage;
    }
}
