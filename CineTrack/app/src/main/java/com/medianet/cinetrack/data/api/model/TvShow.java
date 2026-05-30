package com.medianet.cinetrack.data.api.model;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class TvShow {

    @SerializedName(value = "id", alternate = {"imdbId"})
    private String id;

    @SerializedName(value = "primaryTitle", alternate = {"title"})
    private String title;

    @SerializedName(value = "primaryImage", alternate = {"image"})
    private String image;

    @SerializedName(value = "averageRating", alternate = {"rating"})
    private double averageRating;

    @SerializedName(value = "startYear", alternate = {"year"})
    private int startYear;

    @SerializedName(value = "endYear")
    private int endYear;

    @SerializedName(value = "genres", alternate = {"genre"})
    private List<String> genres;

    @SerializedName(value = "description", alternate = {"plot", "summary"})
    private String description;

    @SerializedName(value = "directors", alternate = {"creators", "director"})
    private List<Person> directors;

    @SerializedName(value = "cast", alternate = {"stars", "actors"})
    private List<Person> cast;

    @SerializedName(value = "trailer")
    private String trailer;

    public TvShow() {}

    // Helper methods
    public String getDisplayTitle()  { return title != null ? title : ""; }
    public String getFullPosterUrl() { return image != null ? image : ""; }
    public String getVoteAverageStr() {
        return averageRating > 0 ? "★ " + String.format("%.1f", averageRating) : "";
    }
    public String getYearDisplay() {
        if (startYear > 0 && endYear > 0) return startYear + "–" + endYear;
        if (startYear > 0) return String.valueOf(startYear);
        return "";
    }

    // Getters
    public String getId()               { return id; }
    public String getTitle()            { return title; }
    public String getImage()            { return image; }
    public double getAverageRating()    { return averageRating; }
    public int getStartYear()           { return startYear; }
    public int getEndYear()             { return endYear; }
    public List<String> getGenres()     { return genres; }
    public String getDescription()      { return description; }
    public List<Person> getDirectors()  { return directors; }
    public List<Person> getCast()       { return cast; }
    public String getTrailer()          { return trailer; }

    // Extract plain name lists for display / CastAdapter
    public List<String> getDirectorNames() { return extractNames(directors); }
    public List<String> getCastNames()     { return extractNames(cast); }

    private static List<String> extractNames(List<Person> people) {
        List<String> names = new ArrayList<>();
        if (people != null) {
            for (Person p : people) {
                if (p != null && !p.getName().isEmpty()) names.add(p.getName());
            }
        }
        return names;
    }

    // Setters for Firestore
    public void setId(String id)                        { this.id = id; }
    public void setTitle(String title)                  { this.title = title; }
    public void setImage(String image)                  { this.image = image; }
    public void setAverageRating(double averageRating)  { this.averageRating = averageRating; }
    public void setStartYear(int startYear)             { this.startYear = startYear; }
    public void setEndYear(int endYear)                 { this.endYear = endYear; }
    public void setGenres(List<String> genres)          { this.genres = genres; }
    public void setDescription(String description)      { this.description = description; }
    public void setDirectors(List<Person> directors)    { this.directors = directors; }
    public void setCast(List<Person> cast)              { this.cast = cast; }
    public void setTrailer(String trailer)              { this.trailer = trailer; }
}
