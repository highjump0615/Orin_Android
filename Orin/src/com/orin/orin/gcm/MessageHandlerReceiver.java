package com.orin.orin.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MessageHandlerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
        Log.i("message", newMessage);
    }

}
