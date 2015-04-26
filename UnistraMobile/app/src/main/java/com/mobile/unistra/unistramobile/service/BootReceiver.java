package com.mobile.unistra.unistramobile.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by nbuckenmeier on 15/04/2015.
 */
public class BootReceiver extends BroadcastReceiver
{
    BackgroundReceiver majCal = new BackgroundReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("bootreceiver", "Bootreceiver lancé");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            majCal.setUpdateCal(context);
        }
    }
}
