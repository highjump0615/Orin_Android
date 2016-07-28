/**
 * @author Ry
 * @Date 2013.12.17
 * @FileName ServiceResponse.java
 *
 */

package com.orin.orin.api;

import com.google.gson.annotations.SerializedName;

public class ServiceResponse {
	
	@SerializedName("serviceName")
	public String serviceName;

	@SerializedName("status")
	public String status;
	
	@SerializedName("count")
	public int count;
	
	@SerializedName("info")
	public Object info;
	
}
