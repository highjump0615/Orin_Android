/**
 * @author Ry
 * @Date 2013.10.11
 * @FileName Utils.java
 *
 */

package com.orin.orin.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.orin.orin.R;
import com.orin.orin.data.UserProfile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static void showToastAlert(final Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static Dialog createErrorAlertDialog(final Context context, int msgResId) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(msgResId)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }

    public static Dialog createErrorAlertDialog(final Context context, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.error)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }

    public static Dialog createErrorAlertDialog(final Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // email
    ////////////////////////////////////////////////////////////////////////////////

    public static boolean isEmailValid(String email) {
        String regExpression = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches())
            return true;
        else
            return false;
    }

    public static void sendMail(Context context, String text) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String[] address = {""};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        //emailIntent.setType("message/rfc822");
        //emailIntent.setType("image/gif");
        emailIntent.setType("text/html");

        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public static void sendMail(Context context, String dstAddr,
                                String subject, String text, boolean isRequestResult) {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        String[] address = {dstAddr};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        emailIntent.setType("text/html");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        if (isRequestResult)
            ((Activity) context).startActivityForResult(Intent.createChooser(emailIntent, "Send mail..."), 1);
        else
            context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    ////////////////////////////////////////////////////////////////////////////////
    // email
    ////////////////////////////////////////////////////////////////////////////////

    public static String getUDID(Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @SuppressWarnings("deprecation")
    public static Point getDisplaySize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        size.x = display.getWidth();
        size.y = display.getHeight();

        return size;
    }

    public static boolean isTablet(Activity activity) {
        boolean bRet = false;

        // Method 1
//        DisplayMetrics dm = new DisplayMetrics();
//        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
//
//        double x = Math.pow(dm.widthPixels/dm.xdpi, 2);
//        double y = Math.pow(dm.heightPixels/dm.ydpi, 2);
//        double screenInches = Math.sqrt(x+y);
//
//        if (screenInches >= 7.0) {
//            bRet = true;
//        }

        // Method 2
//        int widthPixels = dm.widthPixels;
//        int heightPixels = dm.heightPixels;
//        float scaleFactor = dm.density;
//        float widthDp = widthPixels / scaleFactor;
//        float heightDp = heightPixels / scaleFactor;
//
//        float smallestWidth = Math.min(widthDp, heightDp);
//
//        if (smallestWidth >= 600) {
//            bRet = true;
//        }

        // Method 3
        String strType = activity.getString(R.string.screen_type);
        if (strType.equals("tablet")) {
            bRet = true;
        }

        return bRet;
    }

    public static void checkScreen(Activity activity) {
        if (isTablet(activity)) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    public static int dpToPx(int dp, Context ctx) {
        Resources r = ctx.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    //originally: http://stackoverflow.com/questions/5418510/disable-the-touch-events-for-all-the-views
    //modified for the needs here
    public static void enableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            if (view.isFocusable())
                view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableViewGroup((ViewGroup) view, enabled);
            } else if (view instanceof ListView) {
                if (view.isFocusable())
                    view.setEnabled(enabled);
                ListView listView = (ListView) view;
                int listChildCount = listView.getChildCount();
                for (int j = 0; j < listChildCount; j++) {
                    if (view.isFocusable())
                        listView.getChildAt(j).setEnabled(false);
                }
            }
        }
    }

    /**
     * Check whether file is exist or not
     */
    public static boolean checkFileExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Extract real file name from url string
     */
    public static String extractFileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * Get passed days from given date
     *
     * @param dateText
     * @return
     */
    public static int getPassedDays(String dateText) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date = new Date();
        try {
            date = formatter.parse(dateText);
        } catch (ParseException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(date);

        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis
        return (int) (diff / (24 * 60 * 60 * 1000));
    }

    /**
     * Get passed time from given date
     *
     * @param strDateTime
     * @return
     */
    public static String makeCommentTimeString(String strDateTime) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        Date date = new Date();
        try {
            date = formatter.parse(strDateTime);
        } catch (ParseException e) {
            if (Config.DEBUG) e.printStackTrace();
        }

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(date);

        Calendar today = Calendar.getInstance();

        long diff = today.getTimeInMillis() - thatDay.getTimeInMillis(); //result in millis

        long second = diff / 1000;
        int min = (int) second / 60;
        int hour = min / 60;
        int day = hour / 24;

        if (min < 0) {
            return "";
        } else if (min <= 1) {
            return String.format("%d min", min);
        } else if (min < 60) {
            return String.format("%d mins", min);
        } else if (min >= 60 && min < 60 * 24) {
            if (hour == 1) {
                return String.format("%d hour", hour);
            } else if (hour < 24) {
                return String.format("%d hours", hour);
            }
        } else {
            if (day == 1)
                return String.format("%d day", day);
            else
                return String.format("%d days", day);
        }

        return "";
    }

    /**
     * Get/Set image using remote URL string
     */
    public static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private RelativeLayout mImageFrame;
        private ImageView mImageView;
        private ProgressBar mProgressBar;

        public LoadImageTask(RelativeLayout view) {
            mImageFrame = view;
            mImageView = (ImageView) mImageFrame.getChildAt(0);
            mImageView.setBackgroundColor(Color.LTGRAY);
            mProgressBar = (ProgressBar) mImageFrame.getChildAt(1);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);

                if (map != null && url.contains("/userimage/")) {
                    Config.mUserProfile.imageProfile = map;
                    map = Utils.getCroppedBitmap(map, 200);
                }
            }

            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null)
                mImageView.setImageResource(R.drawable.unknown_character);
            else
                mImageView.setImageBitmap(result);
            mImageView.setBackgroundColor(Color.TRANSPARENT);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                if (stream != null) stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    /**
     * Get/Set image using remote URL string
     */
    public static class GetUserImageTask extends AsyncTask<String, Void, Bitmap> {

        private UserProfile mProfile;

        public GetUserImageTask(UserProfile profile) {
            mProfile = profile;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }

            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            mProfile.imageProfile = result;
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                if (stream != null) stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    public static void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(lpHeight,
                    View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;
        if (bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

}
