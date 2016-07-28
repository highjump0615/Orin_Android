/**
 * @author Ry
 * @Date 2013.12.17
 * @FileName GetMusicResult.java
 *
 */

package com.orin.orin.api;

import com.google.gson.annotations.SerializedName;

public class GetMusicResult {

    @SerializedName("id")
    public int id;

    @SerializedName("name")
    public String name;

    @SerializedName("itunelink")
    public String itunelink;

    @SerializedName("time")
    public String time;

    @SerializedName("imagefile")
    public String imagefile;

    @SerializedName("songfile")
    public String songfile;

    @SerializedName("likes")
    public int likes;

    @SerializedName("likedusers")
    public String[] likedusers;

    @SerializedName("liked")
    public int liked;

    @SerializedName("comments")
    public int comments;

}
