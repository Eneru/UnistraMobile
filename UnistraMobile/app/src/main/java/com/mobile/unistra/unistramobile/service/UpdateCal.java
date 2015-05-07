package com.mobile.unistra.unistramobile.service;


import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mobile.unistra.unistramobile.MainActivity;
import com.mobile.unistra.unistramobile.R;
import com.mobile.unistra.unistramobile.calendrier.LocalCal;

/**
 * Created by nbuckenmeier on 15/04/2015.
 */
public class UpdateCal extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public UpdateCal()
    {
        super("Service Background UnistraMobile");
    }
    @Override
    protected void onHandleIntent(Intent intent)
    {
        new LocalCal(this); sendNotification("Calendrier mis à jour");
        Log.e("updatecal", "update cal lancé");
        BackgroundReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(msg)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

}
