package com.medianet.cinetrack.data.api.model;

import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName(value = "id", alternate = {"nconst"})
    private String id;

    @SerializedName(value = "name", alternate = {"primaryName", "fullName"})
    private String name;

    public Person() {}

    public String getId()   { return id; }
    public String getName() { return name != null ? name : ""; }
}
