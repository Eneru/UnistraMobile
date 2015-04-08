package com.mobile.unistra.unistramobile;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.unistra.unistramobile.calendrier.Calendrier;
import com.mobile.unistra.unistramobile.calendrier.Event;
import com.mobile.unistra.unistramobile.calendrier.LocalCal;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendrierActivity extends FragmentActivity implements OnItemSelectedListener{
    FrameLayout panneauDeBase;
    CaldroidFragment caldroidFragment;
    CaldroidListener listener;
    Spinner spinner;
    public Calendrier calendrier;
    EditText txtRessource;
    EditText txtSemaines;
    private PopupWindow pwindo;
    public LocalCal agendaLocal;

    //private MyCalendar m_calendars[];
    String calendriers[];
    String selectedCalendarId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        panneauDeBase = (FrameLayout) findViewById( R.id.panneauDeBase);
        panneauDeBase.getForeground().setAlpha( 0);

        agendaLocal = new LocalCal(this, selectedCalendarId);

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
                initiatePopupWindow(date);
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
                    agendaLocal.comparerAgendaEvent(calendrier);
                    colorCalendrier();
                }else toasterNotif("Connexion impossible");
            }
        });


        // Actions du bouton Exporter
        Button btn_export = (Button) findViewById(R.id.exportButton);
        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calendrier!=null && !calendrier.listeEvents.isEmpty()) {
                    agendaLocal.exportAgenda(getApplicationContext() ,calendrier);
                    toasterNotif("Événements ajoutés à l'agenda");
                    agendaLocal.comparerAgendaEvent(calendrier);
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
                caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_darker_gray,new Date(e.getDebut().getTimeInMillis()));
            else
                caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_holo_blue_light,new Date(e.getDebut().getTimeInMillis()));
            caldroidFragment.refreshView();
        }
    }

    /**
     * Colore les événements trouvés sur l'agenda local
     */
    private void colorCalendrierLocal(){
        int couleur = R.color.caldroid_gray;
        for(Event event:agendaLocal.getEvents()){
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
        agendaLocal = new LocalCal(this, selectedCalendarId);

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

        if (calendrier != null) {
            calendrier.refresh();
            agendaLocal.comparerAgendaEvent(calendrier);
            colorCalendrier();
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void initiatePopupWindow(Date date) {
        if(calendrier != null) {
            GregorianCalendar dateVoulue =  new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
            dateVoulue.setTime(date);
            String[] listeAAfficher = calendrier.listEventString(dateVoulue);
            if(listeAAfficher.length >0) {
                try {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View layout = inflater.inflate(R.layout.popup, null);
                    ListView listView = (ListView) layout.findViewById(R.id.listView);
                    TextView titrePopup = (TextView) layout.findViewById(R.id.titrePopup);
                    titrePopup.setText(dateVoulue.get(GregorianCalendar.DAY_OF_MONTH) + "/" + (dateVoulue.get(GregorianCalendar.MONTH)+1)+"/"+dateVoulue.get(GregorianCalendar.YEAR));

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                            R.layout.support_simple_spinner_dropdown_item, listeAAfficher);
                    listView.setBackgroundColor(getResources().getColor(R.color.caldroid_holo_blue_light));
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            toasterNotif("Clic sur un objet");
                            pwindo.dismiss();
                            panneauDeBase.getForeground().setAlpha(0);
                        }
                    });

                    pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
                    pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
                    panneauDeBase.getForeground().setAlpha( 220);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}