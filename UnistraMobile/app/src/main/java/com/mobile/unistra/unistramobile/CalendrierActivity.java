package com.mobile.unistra.unistramobile;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import com.mobile.unistra.unistramobile.annuaire.Annuaire;
import com.mobile.unistra.unistramobile.annuaire.NoResultException;
import com.mobile.unistra.unistramobile.calendrier.Calendrier;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.GregorianCalendar;


public class CalendrierActivity extends ActionBarActivity {

    public void test(){
       /* Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        startActivity(intent);*/
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, "Learn Android");
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Home suit home");
        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Download Examples");

        // Setting dates
        GregorianCalendar calDate = new GregorianCalendar(2012, 10, 02);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis());

        // make it a full day event
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        // make it a recurring Event
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

        // Making it private and shown as busy
        intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        TextView txt = (TextView) findViewById(R.id.result_txt);

        Calendrier calendrier = new Calendrier("4312,4311",4);
/*
        try{
            CalendarBuilder builder;
        }catch (Exception e){
        }

        txt.setText("TEST2");
*/
        Button btn_search = (Button) findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtName= (EditText) findViewById(R.id.nameEditText);
                try {
                    test();
                } catch (Exception e) {
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calendrier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
