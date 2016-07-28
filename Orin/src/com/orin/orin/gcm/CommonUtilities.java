package com.orin.orin.gcm;

import android.content.Context;
import android.content.Intent;

public final class CommonUtilities {

    // Google Console Project ID
    public static final String SENDER_ID = "164195409804";

    /**
     * Action for GCM messages.
     */
    public static final String DISPLAY_MESSAGE_ACTION = "com.orin.orin.gcm.DISPLAY_MESSAGE";

    /**
     * Intent's extra that contains the message to be displayed.
     */
    public static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p/>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    public static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }

}