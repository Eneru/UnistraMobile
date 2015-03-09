package com.mobile.unistra.unistramobile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.mobile.unistra.unistramobile.calendrier.Calendrier;
import com.mobile.unistra.unistramobile.calendrier.Event;
import java.util.TimeZone;


public class CalendrierActivity extends ActionBarActivity {
    Calendrier calendrier;
    EditText txtRessource;
    EditText txtSemaines;
    TextView txt;
    TextView result;

    /*public void AddEvent(Context ctx, String title, Calendar start, Calendar end) {
        ContentResolver contentResolver = ctx.getContentResolver();

        ContentValues calendarEventContentValues = new ContentValues();
        calendarEventContentValues.put(CalendarContract.Events.CALENDAR_ID, 1); // Hard code to pick first one
        calendarEventContentValues.put(CalendarContract.Events.TITLE, title);
        calendarEventContentValues.put(CalendarContract.Events.DTSTART, start.getTimeInMillis());
        calendarEventContentValues.put(CalendarContract.Events.DTEND, end.getTimeInMillis());
        calendarEventContentValues.put(CalendarContract.Events.EVENT_TIMEZONE, start.getTimeZone().toString());
        Uri uri = contentResolver.insert(Uri.parse(eventsProviderName), calendarEventContentValues);

        if (hasAlert == true) {
            long eventId = Long.parseLong(uri.getLastPathSegment());
            calendarEventContentValues.clear();
            calendarEventContentValues.put("event_id", eventId);
            calendarEventContentValues.put("method", 1);
            calendarEventContentValues.put("minutes", alertTime);
            contentResolver.insert(Uri.parse(reminderProviderName), calendarEventContentValues);
        }

        int id = Integer.parseInt(uri.getLastPathSegment());
        Toast.makeText(ctx, "Created Calendar Event " + id,
                Toast.LENGTH_SHORT).show();
    }*/


    /**
     * Méthode exportant la liste d'événements vers l'agenda par défaut du téléphone.
     */
    public void exportAgenda(){
        for(Event event : calendrier.listeEvents()) {
            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();

            values.put(CalendarContract.Events.DTSTART, event.getDebut().getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, event.getFin().getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, event.getTitre());
            values.put(CalendarContract.Events.DESCRIPTION, event.getDescription());
            values.put(CalendarContract.Events.EVENT_LOCATION, event.getLieu());
            TimeZone timeZone = event.getDebut().getTimeZone();
            values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());

            // default calendar
            values.put(CalendarContract.Events.CALENDAR_ID, 1);

            //values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;UNTIL="
            //        + dtUntill);
            //for one hour
            //values.put(CalendarContract.Events.DURATION, "+P1H");

            //values.put(CalendarContract.Events.HAS_ALARM, 1);

            // insert event to calendar
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        }
    }

    public void exportAgendaOld(){
        for(Event event:calendrier.listeEvents()){
            Intent intent = new Intent(Intent.ACTION_INSERT);

            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.Events.TITLE, event.getTitre());
            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, event.getLieu());
            intent.putExtra(CalendarContract.Events.DESCRIPTION, event.getDescription());

            // Setting dates
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                    event.getDebut().getTimeInMillis());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                    event.getFin().getTimeInMillis());
            this.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        Button btn_export = (Button) findViewById(R.id.exportButton);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendrier!=null && !calendrier.listeEvents().isEmpty())
                    exportAgenda();
                //    exportAgendaOld();
            }

        });

        Button btn_search = (Button) findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtRessource= (EditText) findViewById(R.id.ressourceEditText);
                txtSemaines= (EditText) findViewById(R.id.weekEditText);
                txt = (TextView) findViewById(R.id.result_txt);
                result = (TextView) findViewById(R.id.reslutTextView);

                if(txtRessource.getText().toString().equalsIgnoreCase("ressource")) txtRessource.setText("4312");
                if(txtSemaines.getText().toString().equalsIgnoreCase("semaines")) txtSemaines.setText("4");

                try {
                    calendrier = new Calendrier(txtRessource.getText().toString(),txtSemaines.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(calendrier != null){
                    if(calendrier.estValide()) {
                        result.setText(calendrier.afficherEvent());
                        //exportVersAgenda("On travaille sur le projet","à la maison","");
                    }else{
                        result.setText("Erreur au chargement de l'ics");
                    }
                }else txt.setText("FAILURE");
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