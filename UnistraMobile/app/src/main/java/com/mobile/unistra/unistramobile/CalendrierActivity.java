package com.mobile.unistra.unistramobile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.unistra.unistramobile.calendrier.Calendrier;
import com.mobile.unistra.unistramobile.calendrier.Event;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    public static String getDateTimeStr(String p_time_in_millis) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date l_time = new Date(Long.parseLong(p_time_in_millis));
        return sdf.format(l_time);
    }

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
                values.put(CalendarContract.Events.DESCRIPTION, event.getUid() + event.getDescription());
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

                /*if (hasAlert == true) {
                    long eventId = Long.parseLong(uri.getLastPathSegment());
                    calendarEventContentValues.clear();
                    calendarEventContentValues.put("event_id", eventId);
                    calendarEventContentValues.put("method", 1);
                    calendarEventContentValues.put("minutes", alertTime);
                    contentResolver.insert(Uri.parse(reminderProviderName), calendarEventContentValues);
                }*/

                // insert event to calendar
                Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        // Initialisation des widgets
        txtRessource= (EditText) findViewById(R.id.ressourceEditText);
        txtSemaines= (EditText) findViewById(R.id.weekEditText);
        txt = (TextView) findViewById(R.id.result_txt);
        result = (TextView) findViewById(R.id.reslutTextView);

        String ressources = chargerRessources(this);
        if(!ressources.equals(""))txtRessource.setText(ressources);

        // Actions du bouton Exporter
        Button btn_export = (Button) findViewById(R.id.exportButton);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendrier!=null && !calendrier.listeEvents().isEmpty()) {
                    comparerAgendaEvent();
                    exportAgenda();
                    result.setText(calendrier.afficherEvent());

                    toasterNotif("Événements ajoutés à l'agenda");
                }
            }

        });

        // Actions du bouton Recherche
        Button btn_search = (Button) findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocalEvents();

                try {
                    calendrier = new Calendrier(txtRessource.getText().toString(),txtSemaines.getText().toString());
                    sauvegarderRessources();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(calendrier != null){
                    if(calendrier.estValide()) {
                        result.setText("");
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
        //getMenuInflater().inflate(R.menu.menu_calendrier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings)
            return true;
        return super.onOptionsItemSelected(item);
    }

    /**
     * Compare les événements de l'agenda local avec les événements chargés par l'ADE.
     */
    public void comparerAgendaEvent(){
        for(AgendaLocal a : agendaLocal){
            for(Event e : calendrier.listeEvents()){
                if(e.getTitre().equals(a.titre) && e.getDebut().getTimeInMillis() == Long.parseLong(a.dtstart) && e.getFin().getTimeInMillis() == Long.parseLong(a.dtend)){
                   calendrier.listeEvents().remove(e); //il faudra probablement faire autrement
                }
            }
        }
    }

    /**
     * Sauvegarde les ressources entrées en recherche dans un fichier sur le téléphone.
     */
    public void sauvegarderRessources(){
        if(calendrier != null && calendrier.getRessources() != null) {
            try {
                FileOutputStream fos = openFileOutput("ressources.csv", Context.MODE_PRIVATE);
                fos.write(calendrier.getRessources().getBytes());
                fos.close();
                Log.w("sauvegarderRessources","Réussite de la sauvegarde : "+calendrier.getRessources());
            } catch (Exception e) {
                //Fichier non trouvé
                Log.e("sauvegarderRessources","Erreur à la sauvegarde");
            }
        }
    }

    /**
     * Charge les ressources du fichier sur le téléphone.
     * @return Un <b>String</b> contenant les ressources  enregistrées.
     */
    public String chargerRessources(Context context){
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;

        try{
            fIn = context.openFileInput("ressources.csv");
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            //affiche le contenu de mon fichier dans un popup surgissant
            //Toast.makeText(context, " "+data,Toast.LENGTH_SHORT).show();
            toasterNotif("Préférences chargées");

        }
        catch (Exception e) {
            Log.e("chargerRessources","Les ressources n'ont pas pu être chargées");
        }
        return data;
    }

    private void toasterNotif(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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