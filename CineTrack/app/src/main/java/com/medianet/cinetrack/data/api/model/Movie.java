package com.medianet.cinetrack.data.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movie {

    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("image")
    private String image;

    @SerializedName("rating")
    private String rating;

    @SerializedName("year")
    private String year;

    @SerializedName("genre")
    private java.util.List<String> genre;

    @SerializedName("description")
    private String description;

    @SerializedName("director")
    private java.util.List<String> director;

    @SerializedName("writers")
    private java.util.List<String> writers;

    @SerializedName("cast")
    private java.util.List<String> cast;

    @SerializedName("trailer")
    private String trailer;

    @SerializedName("imdb_link")
    private String imdbLink;

    // Required by Firestore toObject()
    public Movie() {}

    public String getId()          { return id; }
    public String getTitle()       { return title; }
    public String getImage()       { return image; }
    public String getRating()      { return rating; }
    public String getYear()        { return year; }
    public String getDescription()              { return description; }
    public String getTrailer()                  { return trailer; }
    public java.util.List<String> getDirector() { return director; }
    public java.util.List<String> getGenre()    { return genre; }
    public java.util.List<String> getCast()     { return cast; }
    public java.util.List<String> getWriters()  { return writers; }

    // Setters required by Firestore toObject() for private fields
    public void setId(String id)                           { this.id = id; }
    public void setTitle(String title)                     { this.title = title; }
    public void setImage(String image)                     { this.image = image; }
    public void setRating(String rating)                   { this.rating = rating; }
    public void setYear(String year)                       { this.year = year; }
    public void setDescription(String description)         { this.description = description; }
    public void setGenre(java.util.List<String> genre)     { this.genre = genre; }
    public void setDirector(java.util.List<String> director) { this.director = director; }
    public void setCast(java.util.List<String> cast)         { this.cast = cast; }
    public void setWriters(java.util.List<String> writers)   { this.writers = writers; }
    public void setTrailer(String trailer)                 { this.trailer = trailer; }

    // Compatibilité avec l'adapter existant
    public String getDisplayTitle()    { return title; }
    public String getFullPosterUrl()   { return image; }
    public String getVoteAverageStr()  { return rating != null ? "★ " + rating : ""; }
}