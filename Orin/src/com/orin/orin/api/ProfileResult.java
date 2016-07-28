/**
 * @author Ry
 * @date 2013.12.17
 * @filename ProfileResult.java
 */

package com.orin.orin.api;

import com.google.gson.annotations.SerializedName;

public class ProfileResult {

    @SerializedName("email")
    public String email;

    @SerializedName("fullname")
    public String fullname;

    @SerializedName("website")
    public String website;

    @SerializedName("aboutme")
    public String aboutme;

    @SerializedName("phone")
    public String phone;

    @SerializedName("gender")
    public int gender;

    @SerializedName("invitecount")
    public int invitecount;

    @SerializedName("itunecount")
    public int itunecount;

    @SerializedName("sharecount")
    public int sharecount;

    @SerializedName("following")
    public int following;

    @SerializedName("follower")
    public int follower;

}
