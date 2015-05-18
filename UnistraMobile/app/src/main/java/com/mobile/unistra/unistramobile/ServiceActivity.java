package com.mobile.unistra.unistramobile;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.mobile.unistra.unistramobile.service.BackgroundReceiver;


public class ServiceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);

        Button btn_on = (Button) findViewById(R.id.on);
        Button btn_off = (Button) findViewById(R.id.off);

        btn_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                BackgroundReceiver br = new BackgroundReceiver();
                br.setUpdateCal(v.getContext());
            }
        });

        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                BackgroundReceiver br = new BackgroundReceiver();
                br.setCancelUpdateCal(v.getContext());
            }
        });
    }


}
