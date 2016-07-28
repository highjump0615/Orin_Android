/**
 * @author Ry
 * @date 2013.12.17
 * @filename API_Manager.java
 */

package com.orin.orin.api;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.orin.orin.R;
import com.orin.orin.data.UserProfile;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class API_Manager {

    private static final String TAG = API_Manager.class.getSimpleName();

    public static final String API_PATH = "http://orin.io/webservice";

    public static final String REGISTER_ACTION = "register";
    public static final String LOGIN_ACTION = "login";
    public static final String FACEBOOK_LOGIN_ACTION = "loginfacebook";
    public static final String TWITTER_LOGIN_ACTION = "logintwitter";
    public static final String REQUEST_PASSWORD_ACTION = "requestpassword";

    public static final String GET_USER_PROFILE_ACTION = "getuserprofile";
    public static final String GET_MUSIC_LIKE_COUNT_ACTION = "getmusiclikecount";
    public static final String GET_MUSIC_ACTION = "getmusic";

    public static final String LIKE_MUSIC_ACTION = "like_music";
    public static final String UNLIKE_MUSIC_ACTION = "unlike_music";
    public static final String LIKE_VIDEO_ACTION = "like_video";
    public static final String UNLIKE_VIDEO_ACTION = "unlike_video";

    public static final String YOUTUBE_FEED_API = "https://gdata.youtube.com/feeds/api/users/iroking/uploads?v=2&alt=jsonc";
    public static final String GET_VIDEO_LIKE_COUNT_ACTION = "get_videolikenum";

    public static final String GET_MUSIC_COMMENT_ACTION = "getmusiccomment";
    public static final String GET_VIDEO_COMMENT_ACTION = "getvideocomment";
    public static final String ADD_MUSIC_COMMENT_ACTION = "addmusiccomment";
    public static final String ADD_VIDEO_COMMENT_ACTION = "addvideocomment";

    public static final String SETUP_PROFILE_ACTION = "setuserprofile";
    public static final String UPLOAD_PHOTO_ACTION = "uploadphoto";

    public static final String CHANGE_PASSWORD_ACTION = "changepassword";
    public static final String UPDATE_COUNT_ACTION = "updatecountvalue";
    public static final String SEARCH_USER_ACTION = "searchuser";

    public static final String CHECK_FOLLOWING_ACTION = "isfollowing";
    public static final String FOLLOWING_ACTION = "follow";
    public static final String UNFOLLOWING_ACTION = "unfollow";

    public static final String USER_NAME = "username";
    public static final String CONTENT = "content";
    public static final String MUSIC_ID = "musicid";
    public static final String YOUTUBE_ID = "youtubeid";
    public static final String TAG_NUMBER = "tagnumber";

    private static final String ID_FIELD_INFO = "info";


    public interface RunningServiceListener {
        public void finishedRunningServiceListener(ServiceResponse response, String result);
    }

    private static API_Manager mInstance = null;
    private RunningServiceListener mServiceListener;
    private static Dialog mProgressDialog = null;

    // Variable that was parsed JSON object
    public static ServiceResponse mServiceResponse = new ServiceResponse();

    public static API_Manager getInstance() {
        if (mInstance == null) {
            mInstance = new API_Manager();
        }
        return mInstance;
    }

    public void runWebService(String type, HashMap<String, Object> param, Object object, boolean showWaiting) {
        mServiceListener = (RunningServiceListener) object;
        String strApi = String.format("%s/webservice.php?type=%s", API_PATH, type);

        if (type.equals(API_Manager.UPLOAD_PHOTO_ACTION)) {
            ImageView imageView = (ImageView) param.get("photo");
            String fileName = (String) param.get("name");
            imageView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createScaledBitmap(imageView.getDrawingCache(), 400, 400, false);
            new UploadPhotoTask(bitmap, fileName, object, showWaiting).execute();
            return;
        }

        if (param != null) {
            for (String strKey : param.keySet()) {
                Object data = param.get(strKey);
                if (data instanceof String) {
                    String str = URLEncoder.encode((String) data);
                    strApi = String.format("%s&%s=%s", strApi, strKey, str);
                } else {
                    return;
                }
            }
        }

        new RunWebServiceTask(object, showWaiting).execute(strApi);
    }

    public void runWebService(String strApi, Object object, boolean showWaiting) {
        mServiceListener = (RunningServiceListener) object;
        new RunWebServiceTask(object, showWaiting).execute(strApi);
    }

    public String runWebService(String type, HashMap<String, Object> param) {
        String strApi = String.format("%s/webservice.php?type=%s", API_PATH, type);

        if (param != null) {
            for (String strKey : param.keySet()) {
                Object data = param.get(strKey);
                if (data instanceof String) {
                    String str = URLEncoder.encode((String) data);
                    strApi = String.format("%s&%s=%s", strApi, strKey, str);
                }
            }
        }

        return runServiceAPI(strApi);
    }

    /**
     * Read JSON message from web server
     *
     * @param uri String that will execute in web server
     * @return String that represent by JSON style
     */
    public String runServiceAPI(String uri) {

		/*StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);*/

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(uri);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                // Parse service response
                mServiceResponse = new Gson().fromJson(builder.toString(), ServiceResponse.class);
            } else {
                if (Config.DEBUG) Log.e(TAG, "Failed getting result");
            }
        } catch (ClientProtocolException e) {
            if (Config.DEBUG) e.printStackTrace();
            mServiceResponse.serviceName = null;
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
            mServiceResponse.serviceName = null;
        } catch (Exception e) {
            if (Config.DEBUG) e.printStackTrace();
            mServiceResponse.serviceName = null;
        }

        return builder.toString();
    }

    /**
     * Initialize progress dialog
     */
    public static void initProgressDialog(Context context) {
        mProgressDialog = new Dialog(context, android.R.style.Theme_Translucent);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.loading);
        mProgressDialog.setCancelable(false);
    }

    /**
     * Show progress dialog
     */
    public static void showProgress() {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    /**
     * Dismiss progress dialog
     */
    public static void dismissProgress() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    /**
     * Parse the "DATA" object from service response
     *
     * @param response received from service api
     * @param valueType
     * @return result of parsing response
     */
    public static <T> T parseData(String response, Class<T> valueType) {

        try {
            // Get the "DATA" object array from service response
            JSONObject dataObj = new JSONObject(response).getJSONObject(ID_FIELD_INFO);

            return new Gson().fromJson(dataObj.toString(), valueType);
        } catch (JSONException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return null;
    }

    /**
     * Parse the "DATA" object array from service response
     *
     * @param response received from service api
     * @param valueType
     * @return result of parsing response
     */
    public static <T> T parseDataArray(String response, Class<T> valueType) {

        try {
            // Get the "DATA" object array from service response
            JSONArray dataArray = new JSONObject(response).getJSONArray(ID_FIELD_INFO);

            // Parse Response into our object
            //Type collectionType = new TypeToken<ArrayList<LoginResult>>(){}.getType();

            return new Gson().fromJson(dataArray.toString(), valueType);
        } catch (JSONException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        return null;
    }

    /**
     * AsyncTask to run web api
     */
    private class RunWebServiceTask extends AsyncTask<String, Integer, String> {
        private Context mContext;
        private boolean mShowWaiting;

        public RunWebServiceTask(Object object, boolean showWaiting) {
            if (object instanceof Activity)
                mContext = (Context) object;
            else if (object instanceof Fragment)
                mContext = ((Fragment) object).getActivity();
            else
                mContext = null;
            mShowWaiting = showWaiting;
        }

        @Override
        protected void onPreExecute() {
            if (mContext != null && mShowWaiting) {
                dismissProgress();
                initProgressDialog(mContext);
                showProgress();
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = runServiceAPI(strings[0]);

            Log.i(TAG, "result" + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mContext != null) {
                dismissProgress();
                if (TextUtils.isEmpty(result))
                    Utils.createErrorAlertDialog(mContext, "Network fail");
            }

            if (mServiceListener != null)
                mServiceListener.finishedRunningServiceListener(mServiceResponse, result);
        }
    }

    /**
     * AsyncTask to upload user's photo
     */
    private class UploadPhotoTask extends AsyncTask<Void, Integer, String> {
        private Context mContext;
        private boolean mShowWaiting;
        private String mFilename;
        private Bitmap mBitmap;

        public UploadPhotoTask(Bitmap bitmap, String filename, Object object, boolean showWaiting) {
            if (object instanceof Activity)
                mContext = (Context) object;
            else if (object instanceof Fragment)
                mContext = ((Fragment) object).getActivity();
            else
                mContext = null;

            mFilename = filename;
            mBitmap = bitmap;
            mShowWaiting = showWaiting;
        }

        @Override
        protected void onPreExecute() {
            if (mContext != null && mShowWaiting) {
                dismissProgress();
                initProgressDialog(mContext);
                showProgress();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            String result = uploadPhoto(mBitmap, mFilename);

            Log.i(TAG, "result" + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mContext != null) {
                dismissProgress();
                if (TextUtils.isEmpty(result))
                    Utils.createErrorAlertDialog(mContext, "Network fail");
            }
            mServiceListener.finishedRunningServiceListener(mServiceResponse, result);
        }
    }

    private String uploadPhoto(Bitmap bitmap, String filename) {

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
//        byte[] byte_arr = stream.toByteArray();
//        String image_str = Base64.encodeBytes(byte_arr);
//        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//
//        nameValuePairs.add(new BasicNameValuePair("type", UPLOAD_PHOTO_ACTION));
//        nameValuePairs.add(new BasicNameValuePair("name", filename));
//        nameValuePairs.add(new BasicNameValuePair("image", image_str));
//
//        try {
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(String.format("%s/webservice.php", API_PATH));
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//            httpclient.execute(httppost, new PhotoUploadResponseHandler());
//        } catch (Exception e) {
//            System.out.println("Error in http connection " + e.toString());
//        }

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("type", UPLOAD_PHOTO_ACTION));
        nameValuePairs.add(new BasicNameValuePair("name", filename));

        StringBuilder builder = new StringBuilder();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        String upload_url = String.format("%s/webservice.php", API_PATH);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

        byte[] data = byteArrayOutputStream.toByteArray();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(upload_url);
        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        try {
            //Set Data and Content-type header for the image
            entity.addPart("type", new StringBody(UPLOAD_PHOTO_ACTION));
            entity.addPart("name", new StringBody(filename));
            entity.addPart("image", new ByteArrayBody(data, "image/jpeg", filename));
            postRequest.setEntity(entity);

            HttpResponse response = httpClient.execute(postRequest);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == HttpStatus.SC_OK) {
                mServiceResponse.serviceName = UPLOAD_PHOTO_ACTION;
                mServiceResponse.status = "OK";
                Config.mUserProfile.imageProfile = bitmap;
            } else {
                mServiceResponse.status = "Error";
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return builder.toString();
    }

    public void updateCountValue() {
        UserProfile profile = Config.mUserProfile;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", profile.userName);
        params.put("invite", profile.inviteCount);
        params.put("itune", profile.downloadCount);
        params.put("share", profile.shareCount);

        runWebService(API_Manager.UPDATE_COUNT_ACTION, params, null, false);
    }

}
