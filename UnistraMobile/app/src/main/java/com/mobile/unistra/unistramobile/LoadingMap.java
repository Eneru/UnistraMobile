package com.mobile.unistra.unistramobile;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class LoadingMap extends Activity {

    //Introduce an delay
    private final int WAIT_TIME = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);
        findViewById(R.id.mainSpinner1).setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                //Simulating a long running task
	  /* Create an Intent that will start the ProfileData-Activity. */
                Intent mainIntent = new Intent(LoadingMap.this,MapsActivity.class);
                LoadingMap.this.startActivity(mainIntent);
                LoadingMap.this.finish();
            }
        }, WAIT_TIME);
    }
}
