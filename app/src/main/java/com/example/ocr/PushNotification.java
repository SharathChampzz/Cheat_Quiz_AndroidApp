package com.example.ocr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class PushNotification extends FirebaseMessagingService {
    private static final String TAG = "0";

    public PushNotification() {
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From : " + remoteMessage.getFrom());
        Log.d(TAG, "From : " + remoteMessage.getFrom());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}
