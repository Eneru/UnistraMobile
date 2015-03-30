package com.mobile.unistra.unistramobile;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.unistra.unistramobile.calendrier.Calendrier;
import com.mobile.unistra.unistramobile.calendrier.Event;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendrierActivity extends FragmentActivity implements OnItemSelectedListener {
    CaldroidFragment caldroidFragment;
    CaldroidListener listener;
    Spinner spinner;
    Calendrier calendrier;
    EditText txtRessource;
    EditText txtSemaines;

    public ArrayList<Event> agendaLocal;

    //private MyCalendar m_calendars[];
    String calendriers[];
    String selectedCalendarId = "1";

    /**
     * Méthode permettant de récupérer tous les événements sur l'agenda du téléphone.
     */
    private void getLocalEvents() {
        Uri l_eventUri;
        agendaLocal = new ArrayList<Event>();

        if (Build.VERSION.SDK_INT >= 8 ) {
            l_eventUri = Uri.parse("content://com.android.calendar/events");
        } else {
            l_eventUri = Uri.parse("content://calendar/events");
        }
        String[] l_projection = new String[]{"title", "dtstart", "dtend"};
        Cursor l_managedCursor = this.managedQuery(l_eventUri, l_projection, "calendar_id=" + selectedCalendarId, null, "dtstart ASC, dtend ASC");
        if (l_managedCursor.moveToFirst()){//.moveToFirst()) {
            int l_cnt = 0;
            String l_title;
            String l_begin;
            String l_end;
            int l_colTitle = l_managedCursor.getColumnIndex(l_projection[0]);
            int l_colBegin = l_managedCursor.getColumnIndex(l_projection[1]);
            int l_colEnd = l_managedCursor.getColumnIndex(l_projection[1]);
            do {
                l_title = l_managedCursor.getString(l_colTitle);
                l_begin = getDateTimeStr(l_managedCursor.getString(l_colBegin));
                l_end = getDateTimeStr(l_managedCursor.getString(l_colEnd));
                ++l_cnt;
                agendaLocal.add(new Event(l_title,l_managedCursor.getString(l_colBegin),l_managedCursor.getString(l_colEnd)));

            } while (l_managedCursor.moveToNext());
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
        if(agendaLocal != null && calendrier != null && calendrier.listeEvents != null) {
            for (Event event : calendrier.listeEvents) {
                if(!event.estDoublon()) {
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
                    values.put(CalendarContract.Events.CALENDAR_ID, selectedCalendarId);

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        getCalendar(this);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, calendriers);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        selectedCalendarId = String.valueOf(spinner.getSelectedItemId() + 1);

        // Initialisation des widgets
        txtRessource= (EditText) findViewById(R.id.ressourceEditText);
        txtSemaines= (EditText) findViewById(R.id.weekEditText);

        // Initialisation du widget Caldroid
        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();


        // Setup listener
        final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");
        listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                toasterNotif("Clic sur la date");
                // On affichera la liste des cours sur la date donnée
            }
            @Override
            public void onChangeMonth(int month, int year) {
            }
            @Override
            public void onLongClickDate(Date date, View view) {
                toasterNotif("Clic long");
                // On proposera de supprimer la date donnée ?
            }
            @Override
            public void onCaldroidViewCreated() {
            }
        };
        caldroidFragment.setCaldroidListener(listener);

        // Chargement du calendrier local
        getLocalEvents();

        // Affichage du calendrier local
        colorCalendrierLocal();

        // Chargement des ressources
        String ressources = chargerRessources(this);
        if(!ressources.equals(""))txtRessource.setText(ressources);

        // Actions du bouton Recherche
        Button btn_search = (Button) findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    calendrier = new Calendrier(txtRessource.getText().toString(),txtSemaines.getText().toString());
                    sauvegarderRessources(getBaseContext(), calendrier.getRessources());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(calendrier != null){
                    comparerAgendaEvent();
                    colorCalendrier();
                }
            }
        });


        // Actions du bouton Exporter
        Button btn_export = (Button) findViewById(R.id.exportButton);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendrier!=null && !calendrier.listeEvents.isEmpty()) {
                    exportAgenda();
                    toasterNotif("Événements ajoutés à l'agenda");
                    comparerAgendaEvent();
                    colorCalendrier();
                }
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
        if(agendaLocal != null && calendrier != null)
            calendrier.filtrerDoublons(agendaLocal);
    }

    /**
     * Sauvegarde les ressources entrées en recherche dans un fichier sur le téléphone.
     */
    public void sauvegarderRessources(Context context, String data){
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try{
            fOut = context.openFileOutput("ressources.csv",MODE_PRIVATE);//MODE_APPEND);
            osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            //popup surgissant pour le résultat
            Toast.makeText(context, "Ressource sauvegardée",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Toast.makeText(context, "Ressource non sauvegardée",Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                osw.close();
                fOut.close();
            } catch (IOException e) {
                Toast.makeText(context, "Ressource non sauvegardée",Toast.LENGTH_SHORT).show();
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
            int lastInt = Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(
                    data.lastIndexOf('0'),data.lastIndexOf('1')),data.lastIndexOf('2')),data.lastIndexOf('3')),data.lastIndexOf('4')),
                    data.lastIndexOf('5')),data.lastIndexOf('6')),data.lastIndexOf('7')),data.lastIndexOf('8')),data.lastIndexOf('9'));
            data = data.substring(0,lastInt+1);
            toasterNotif("Préférences chargées");
        }
        catch (Exception e) {
            Log.e("chargerRessources","Les ressources n'ont pas pu être chargées");
        }
        return data;
    }

    /**
     * Affiche un toast : le message sur fond noir en bas, qui disparaît au bout de quelques secondes.
     * @param text Texte à afficher en toast
     */
    private void toasterNotif(CharSequence text){
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * Colore les événements trouvés
     */
    private void colorCalendrier(){
        for(Event e:calendrier.listeEvents){
            if(e.doublon)
                caldroidFragment.setBackgroundResourceForDate(R.color.fuchsia,new Date(e.getDebut().getTimeInMillis()));
            else
                caldroidFragment.setBackgroundResourceForDate(R.color.red,new Date(e.getDebut().getTimeInMillis()));
            caldroidFragment.refreshView();
        }
    }

    /**
     * Colore les événements trouvés sur l'agenda local
     */
    private void colorCalendrierLocal(){
        int couleur = R.color.caldroid_gray;
        switch(Integer.parseInt(selectedCalendarId)-1){
            case 0 :
                couleur = R.color.caldroid_lighter_gray;
                break;
            case 1 :
                couleur = R.color.caldroid_gray;
                break;
            case 2 :
                couleur = R.color.caldroid_darker_gray;
                break;
            case 3 :
                couleur = R.color.caldroid_sky_blue;
                break;
            case 4 :
                couleur = R.color.caldroid_holo_blue_light;
                break;
            case 5 :
                couleur = R.color.caldroid_holo_blue_dark;
                break;
            case 6 :
                couleur = R.color.blue;
                break;
            default :
                couleur = R.color.black;
        }

        for(Event event:agendaLocal){
            caldroidFragment.setBackgroundResourceForDate(couleur, new Date(event.getDebut().getTimeInMillis()));
            caldroidFragment.refreshView();
        }
    }

    public void getCalendar(Context c) {
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendars;
        calendars = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = c.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendars, projection, null, null, null);

        if (managedCursor.moveToFirst()){
            //m_calendars = new MyCalendar[managedCursor.getCount()];
            calendriers = new String[managedCursor.getCount()]; //AJOUT RECENT
            String calName;
            String calID;
            int cont= 0;
            int nameCol = managedCursor.getColumnIndex(projection[1]);
            int idCol = managedCursor.getColumnIndex(projection[0]);
            do {
                calName = managedCursor.getString(nameCol);
                calID = managedCursor.getString(idCol);
                //m_calendars[cont] = new MyCalendar(calName, calID);
                calendriers[cont] = new String(calName);
                cont++;
            } while(managedCursor.moveToNext());
            managedCursor.close();
        }
    }

    /**
     * Ce qu'il se passe quand on appuie sur un élément de la liste déroulante
     * @param parent
     * @param view
     * @param pos
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        selectedCalendarId = String.valueOf(spinner.getSelectedItemId() + 1);

        // Chargement du calendrier local
        getLocalEvents();

        caldroidFragment = new CaldroidFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
        caldroidFragment.setArguments(args);

        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        caldroidFragment.setCaldroidListener(listener);

        caldroidFragment.clearSelectedDates();
        colorCalendrierLocal();

        if(calendrier != null) {
            calendrier.refresh();
            comparerAgendaEvent();
            colorCalendrier();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
/*
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
}*/