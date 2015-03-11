package com.mobile.unistra.unistramobile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class CalendrierActivity extends ActionBarActivity {
    Calendrier calendrier;
    EditText txtRessource;
    EditText txtSemaines;
    TextView txt;
    TextView result;

    ArrayList<AgendaLocal> agendaLocal;


    private MyCalendar m_calendars[];
    private String m_selectedCalendarId = "1"; //2 = google, chez moi.

    /**
     * A faire proprement : permet de créer un nouveau calendrier et éviter de tout mettre dans le calendrier par défaut.
     * @deprecated ça marche pas...
     */
    public void nouveauCal(){
        Uri calUri = CalendarContract.Calendars.CONTENT_URI;
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, "UnistraMobile");
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        cv.put(CalendarContract.Calendars.NAME, "Name");
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "Display Name");
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, "red");
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, true);
        cv.put(CalendarContract.Calendars.VISIBLE, 1);
        cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);

        calUri = calUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "UNISTRAMOBILE")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();
        Uri result = this.getContentResolver().insert(calUri, cv);
    }


    /**
     * Méthode permettant de récupérer tous les événements sur l'agenda du téléphone.
     */
    private void getLocalEvents() {
        Uri l_eventUri;
        agendaLocal = new ArrayList<AgendaLocal>();

        if (Build.VERSION.SDK_INT >= 8 ) {
            l_eventUri = Uri.parse("content://com.android.calendar/events");
        } else {
            l_eventUri = Uri.parse("content://calendar/events");
        }
        String[] l_projection = new String[]{"title", "dtstart", "dtend"};
        Cursor l_managedCursor = this.managedQuery(l_eventUri, l_projection, "calendar_id=" + m_selectedCalendarId, null, "dtstart ASC, dtend ASC");//"dtstart DESC, dtend DESC");
        //Cursor l_managedCursor = this.managedQuery(l_eventUri, l_projection, null, null, null);
        if (l_managedCursor.moveToFirst()){//.moveToFirst()) {
            int l_cnt = 0;
            String l_title;
            String l_begin;
            String l_end;
            //StringBuilder l_displayText = new StringBuilder();
            int l_colTitle = l_managedCursor.getColumnIndex(l_projection[0]);
            int l_colBegin = l_managedCursor.getColumnIndex(l_projection[1]);
            int l_colEnd = l_managedCursor.getColumnIndex(l_projection[1]);
            do {
                l_title = l_managedCursor.getString(l_colTitle);
                l_begin = getDateTimeStr(l_managedCursor.getString(l_colBegin));
                l_end = getDateTimeStr(l_managedCursor.getString(l_colEnd));
                //l_displayText.append(l_title + "\n" + l_begin + "\n" + l_end + "\n----------------\n");
                ++l_cnt;
                //agendaLocal.add(new AgendaLocal(l_title,l_begin,l_end));
                agendaLocal.add(new AgendaLocal(l_title,l_managedCursor.getString(l_colBegin),l_managedCursor.getString(l_colEnd)));

            } while (l_managedCursor.moveToNext());// && l_cnt < 3);
            //result.setText(l_displayText.toString());
        }
    }

    private static final String DATE_TIME_FORMAT = "yyyy MMM dd, HH:mm:ss";
    public static String getDateTimeStr(int p_delay_min) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        if (p_delay_min == 0) {
            return sdf.format(cal.getTime());
        } else {
            Date l_time = cal.getTime();
            l_time.setMinutes(l_time.getMinutes() + p_delay_min);
            return sdf.format(l_time);
        }
    }

    public static String getDateTimeStr(String p_time_in_millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date l_time = new Date(Long.parseLong(p_time_in_millis));
        return sdf.format(l_time);
    }

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
        if(agendaLocal != null) {
            for (Event event : calendrier.listeEvents()) {
                ContentResolver cr = getContentResolver();
                ContentValues values = new ContentValues();

                values.put(CalendarContract.Events.DTSTART, event.getDebut().getTimeInMillis());
                values.put(CalendarContract.Events.DTEND, event.getFin().getTimeInMillis());
                values.put(CalendarContract.Events.TITLE, event.getTitre());
                values.put(CalendarContract.Events.DESCRIPTION, event.getDescription() + '\n' + event.getUid());
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
                    comparerAgendaEvent();
                    exportAgenda();
                //    exportAgendaOld();
                result.setText(calendrier.afficherEvent());
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


                getLocalEvents();

/*                for(AgendaLocal a : agendaLocal) {
                    result.append(a.toString());
                }*/
                //getCalendars();
                try {
                    calendrier = new Calendrier(txtRessource.getText().toString(),txtSemaines.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(calendrier != null){
                    if(calendrier.estValide()) {
                        result.setText("");
                        /*for(AgendaLocal a : agendaLocal) {
                            result.append(a.toString());
                        }*/
                        result.setText(calendrier.afficherEvent());
                    }else{
                        result.setText("Erreur au chargement de l'ics");
                    }
                }else txt.setText("Echec au chargement du calendrier en ligne");
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


    public void comparerAgendaEvent(){
        for(AgendaLocal a : agendaLocal){
            for(Event e : calendrier.listeEvents()){
                if(e.getTitre().equals(a.titre) && e.getDebut().getTimeInMillis() == Long.parseLong(a.dtstart) && e.getFin().getTimeInMillis() == Long.parseLong(a.dtend)){
                   calendrier.listeEvents().remove(e);
                }
            }
        }
    }
}

class AgendaLocal{
    public String titre;
    public String dtstart;
    public String dtend;

    public AgendaLocal(String titre, String dtstart, String dtend){
        this.titre = titre;
        this.dtstart = dtstart;
        this.dtend = dtend;
    }

    public String toString(){
        return titre +'\n' + dtstart +'\n' + dtend +'\n' + "---------------"  +'\n';
    }
}

class MyCalendar {
    public String name;
    public String id;
    public MyCalendar(String _name, String _id) {
        name = _name;
        id = _id;
    }
    @Override
    public String toString() {
        return name;
    }
}