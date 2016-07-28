/**
 * @author Ry
 * @date 2013.12.21
 * @filename CommentResult.java
 */

package com.orin.orin.api;

import com.google.gson.annotations.SerializedName;

public class CommentResult {

    @SerializedName("username")
    public String username;

    @SerializedName("time")
    public String time;

    @SerializedName("content")
    public String content;

}
