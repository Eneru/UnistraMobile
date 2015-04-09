package com.mobile.unistra.unistramobile.calendrier;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Alexandre on 31-03-15.
 */
public class LocalCal {
    private ArrayList<Event> agendaLocal;
    private String selectedCalendarId;
    private String ressource;

    public ArrayList<Event> getEvents(){
        return agendaLocal;
    }

    /**
     * Compare les événements de l'agenda local avec les événements chargés par l'ADE.
     */
    public void comparerAgendaEvent(Calendrier calendrier){
        if(agendaLocal != null && calendrier != null)
            calendrier.filtrerDoublons(agendaLocal);
    }

    /**
     * Méthode exportant la liste d'événements vers l'agenda par défaut du téléphone.
     */
    public void exportAgenda(Context context, Calendrier calendrier){
        if(agendaLocal != null && calendrier != null && calendrier.listeEvents != null) {
            for (Event event : calendrier.listeEvents) {
                if(!event.estDoublon()) {
                    ContentResolver cr = context.getContentResolver();
                    ContentValues values = new ContentValues();

                    values.put(CalendarContract.Events.DTSTART, event.getDebut().getTimeInMillis());
                    values.put(CalendarContract.Events.DTEND, event.getFin().getTimeInMillis());
                    values.put(CalendarContract.Events.TITLE, event.getTitre());
                    values.put(CalendarContract.Events.DESCRIPTION, event.getUid() + event.getDescription());
                    values.put(CalendarContract.Events.EVENT_LOCATION, event.getLieu());
                    TimeZone timeZone = event.getDebut().getTimeZone();
                    values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
                    values.put(CalendarContract.Events.CALENDAR_ID, selectedCalendarId);
                    values.put(CalendarContract.Events.HAS_ALARM, event.getAlarme()?1:0);

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

    public String getSelectedCalendarId(){return this.selectedCalendarId;}

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
        }catch (Exception e) {
            Log.e("chargerRessources", "Les ressources n'ont pas pu être chargées");
        }
        return data;
    }

    public String chargerCalendrier(Context context){
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = "";

        try{
            fIn = context.openFileInput("calendrier.csv");
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
            int lastInt = Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(Math.max(
                            data.lastIndexOf('0'),data.lastIndexOf('1')),data.lastIndexOf('2')),data.lastIndexOf('3')),data.lastIndexOf('4')),
                    data.lastIndexOf('5')),data.lastIndexOf('6')),data.lastIndexOf('7')),data.lastIndexOf('8')),data.lastIndexOf('9'));
            data = data.substring(0,lastInt+1);
        }catch (Exception e) {
            Log.e("chargerCalendrier", "Le calendrier par défaut n'a pas pu être chargé");
        }
        return data;
    }

    /**
     * Constructeur à utiliser pour la synchronisation uniquement !
     * <br>Il envoit directement la requête pour récupérer le calendrier "favori" sur l'agenda par défaut du téléphone, après vérification des doublons.
     * @param activity Activité appelante ; indispensable pour les "appels système"
     */
    public LocalCal(Activity activity) {
        this(activity, ""); //On devra enregistrer le dernier calendrier utilisé, aussi
        Calendrier calendrier = new Calendrier(this.ressource, "4"); //Ressource demandée, 4 semaines
        comparerAgendaEvent(calendrier);
        exportAgenda(activity, calendrier);
    }

    /**
     * Constructeur usuel.
     * @param activity
     * @param selectedCalendarId
     */
    public LocalCal(Activity activity, String selectedCalendarId) {
        this.ressource = chargerRessources(activity);
        if(!selectedCalendarId.equals(""))
            //Si on spécifie un identifiant de calendrier, alors on le met
            this.selectedCalendarId = selectedCalendarId;
        else {
            //Sinon on vérifie qu'on en a pas un dans les préférences
            this.selectedCalendarId = chargerCalendrier(activity);
            //Si on avait pas de préférence, alors on prend le default Cal
            if (this.selectedCalendarId.equals(""))
                this.selectedCalendarId = "1";
        }

        Uri l_eventUri;
        agendaLocal = new ArrayList<Event>();

        if (Build.VERSION.SDK_INT >= 8 ) {
            l_eventUri = Uri.parse("content://com.android.calendar/events");
        } else {
            l_eventUri = Uri.parse("content://calendar/events");
        }
        String[] l_projection = new String[]{"title", "dtstart", "dtend"};
        Cursor l_managedCursor = activity.managedQuery(l_eventUri, l_projection, "calendar_id=" + this.selectedCalendarId, null, "dtstart ASC, dtend ASC");
        if (l_managedCursor.moveToFirst()){
            String l_title;
            int l_colTitle = l_managedCursor.getColumnIndex(l_projection[0]);
            int l_colBegin = l_managedCursor.getColumnIndex(l_projection[1]);
            int l_colEnd = l_managedCursor.getColumnIndex(l_projection[2]);

            do {
                l_title = l_managedCursor.getString(l_colTitle);
                agendaLocal.add(new Event(l_title,l_managedCursor.getString(l_colBegin),l_managedCursor.getString(l_colEnd)));
            } while (l_managedCursor.moveToNext());
        }
    }

    public ArrayList<Event> listeEventsJour(GregorianCalendar date){
        ArrayList<Event> liste = new ArrayList<Event>();

        for(Event e: this.agendaLocal) {
            if(date.get(Calendar.DAY_OF_YEAR) == e.dateDebut.get(Calendar.DAY_OF_YEAR))
                liste.add(e);
        }
        return liste;
    }
}
