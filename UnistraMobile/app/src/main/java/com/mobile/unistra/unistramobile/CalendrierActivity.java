package com.mobile.unistra.unistramobile;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.mobile.unistra.unistramobile.calendrier.Calendrier;
import com.mobile.unistra.unistramobile.calendrier.Event;
import com.mobile.unistra.unistramobile.calendrier.EventAdapter;
import com.mobile.unistra.unistramobile.calendrier.LocalCal;
import com.mobile.unistra.unistramobile.calendrier.Ressource;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class CalendrierActivity extends FragmentActivity implements OnItemSelectedListener{
    TextView selectedRes;
    ArrayList<Ressource> listeRessources;
    FrameLayout panneauDeBase;
    CaldroidFragment caldroidFragment;
    CaldroidListener listener;
    Spinner spinner;
    Spinner choixSemaines;
    public Calendrier calendrier;
    Button btnRessource;
    private PopupWindow pwindo;
    public LocalCal agendaLocal;
    SwipeListView swipelistview;
    EventAdapter adapter;
    MyCustomAdapter dataAdapter;
    String ressource="";
    String calendriers[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        listeRessources = Ressource.getRessourceList();

        panneauDeBase = (FrameLayout) findViewById( R.id.panneauDeBase);
        panneauDeBase.getForeground().setAlpha( 0);

        agendaLocal = new LocalCal(this,"");

        getCalendar(this);

        selectedRes = (TextView) findViewById(R.id.selectedRes);
        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, calendriers);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        agendaLocal.getSelectedCalendarId();
        spinner.setSelection(Integer.parseInt(agendaLocal.getSelectedCalendarId())-1);

        // Initialisation des widgets
        choixSemaines= (Spinner) findViewById(R.id.weekSpinner);
        Integer[] items = new Integer[]{1,2,3,4,5,6};
        ArrayAdapter<Integer> adaptSpinner = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, items);
        choixSemaines.setAdapter(adaptSpinner);
        choixSemaines.setSelection(3);

        // Initialisation du widget Caldroid
        caldroidFragment = new CaldroidFragment();
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
            caldroidFragment.setArguments(args);
        }
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        // Setup listener
        listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                initiatePopupWindow(date);
            }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
            }

            @Override
            public void onCaldroidViewCreated() {
            }
        };
        caldroidFragment.setCaldroidListener(listener);

        // Affichage du calendrier local
        colorCalendrierLocal();

        // Chargement des ressources
        //ressource = LocalCal.chargerRessources(this);
        //if(ressource.equals(""))ressource = "4308";

        //Actions du bouton Ressources
        btnRessource= (Button) findViewById(R.id.ressourceEditButton);
        btnRessource.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                displayListView();
            }
        });

        // Actions du bouton Recherche
        Button btn_search = (Button) findViewById(R.id.button_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                if (calendrier != null) {
                    for (Event e : calendrier.listeEvents)
                        caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_white, new Date(e.getDebut().getTimeInMillis()));
                    colorCalendrierLocal();
                }
                if(!ressource.equals("")) {
                    try {

                        calendrier = new Calendrier(ressource, String.valueOf((int) choixSemaines.getSelectedItem()));
                        sauvegarderRessources(getBaseContext(), calendrier.getRessources());
                        LocalCal.sauvegarderCalendrier(getBaseContext(), String.valueOf(spinner.getSelectedItemId() + 1));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (calendrier != null) {
                        agendaLocal.comparerAgendaEvent(calendrier);
                        colorCalendrierTelecharge();
                    } else toasterNotif("Calendrier introuvable");
                }else{
                    toasterNotif("Veuillez choisir au moins une ressource");
                }
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
                    colorCalendrierTelecharge();
                    LocalCal.sauvegarderCalendrier(getBaseContext(), String.valueOf(spinner.getSelectedItemId() + 1));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    public static void sauvegarderRessources(Context context, String data){
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
    private void colorCalendrierTelecharge(){
        for(Event e:calendrier.listeEvents){
            /*if(e.doublon)
                caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_holo_blue_light,new Date(e.getDebut().getTimeInMillis()));
            else*/
                caldroidFragment.setBackgroundResourceForDate(R.color.caldroid_holo_blue_light,new Date(e.getDebut().getTimeInMillis()));
            caldroidFragment.refreshView();
        }
    }

    /**
     * Colore les événements trouvés sur l'agenda local
     */
    private void colorCalendrierLocal(){
        //int couleur = R.color.caldroid_gray;
        int couleur = R.color.caldroid_transparent;
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
     * Ce qu'il se passe quand on appuie sur un élément de la liste de calendriers
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        // Chargement du calendrier local
        agendaLocal = new LocalCal(this, String.valueOf(spinner.getSelectedItemId() + 1));

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
            colorCalendrierTelecharge();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }

    private void initiatePopupWindow(Date date) {
        //On convertit la date en GregorianCalendar, car Date est deprecated
        GregorianCalendar dateVoulue =  new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        dateVoulue.setTime(date);

        //On met le tout sous forme de String[] pour pouvoir le mettre dans une liste.
        final ArrayList<Event> eventsDuJour;
        final ArrayList<Event> aEffacer = new ArrayList<Event>();
        if(calendrier != null)
            eventsDuJour = calendrier.listeEventsJour(dateVoulue);
        else
            eventsDuJour = agendaLocal.listeEventsJour(dateVoulue);

        //Si la date contient effectivement des événements, on les affiche
        if(eventsDuJour.size() >0) {
            //Initialisations pour la fenêtre popup
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.swipelist, null);

            //Formation de la liste
            swipelistview = (SwipeListView) layout.findViewById(R.id.example_swipe_lv_list);
            adapter=new EventAdapter(this,R.layout.custom_row,eventsDuJour);


            //Formation de la swipeList avec les Listeners
            swipelistview.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onOpened(int position, boolean toRight) {
                }

                @Override
                public void onClosed(int position, boolean fromRight) {
                }

                @Override
                public void onListChanged() {
                }

                @Override
                public void onMove(int position, float x) {
                }

                @Override
                public void onStartOpen(int position, int action, boolean right) {
                }

                @Override
                public void onStartClose(int position, boolean right) {
                }

                @Override
                public void onClickFrontView(int position) {
                }

                @Override
                public void onClickBackView(int position) {
                    Log.d("swipe", String.format("onClickBackView %d", position));
                    swipelistview.closeAnimate(position);//when you touch back view it will close
                }

                @Override
                public void onDismiss(int[] reverseSortedPositions) {
                    aEffacer.add(eventsDuJour.get(reverseSortedPositions[0]));
                    eventsDuJour.remove(reverseSortedPositions[0]);
                    adapter.notifyDataSetChanged();
                }
            });

            //These are the swipe listview settings. you can change these
            //setting as your requrement
            swipelistview.setSwipeMode(SwipeListView.SWIPE_MODE_BOTH);
            swipelistview.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_DISMISS);//.SWIPE_ACTION_REVEAL); //there are four swipe actions
            swipelistview.setSwipeActionRight(SwipeListView.SWIPE_ACTION_DISMISS);//SWIPE_ACTION_REVEAL);
            swipelistview.setOffsetLeft(convertDpToPixel(260f)); // left side offset
            swipelistview.setOffsetRight(convertDpToPixel(0f)); // right side offset
            swipelistview.setAnimationTime(50); // animarion time
            swipelistview.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress

            swipelistview.setAdapter(adapter);

            adapter.notifyDataSetChanged();


            //Titre de la liste
            TextView dateEvent= (TextView) layout.findViewById(R.id.dateEvent);
            dateEvent.setText(dateVoulue.get(GregorianCalendar.DAY_OF_MONTH) + "/"
                    + (dateVoulue.get(GregorianCalendar.MONTH)+1)+"/"
                    + dateVoulue.get(GregorianCalendar.YEAR));

            //Bouton OK
            Button btnOk = (Button) layout.findViewById(R.id.buttonAccepter);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Sauvegarde, puis quitter
                    if(calendrier != null)
                        for (Event e : aEffacer)
                            calendrier.remove(e);
                    else
                        for(Event e:aEffacer)
                            agendaLocal.remove(e);
                    pwindo.dismiss();
                }
            });

            //Bouton Annuler
            Button btnAnnuler = (Button) layout.findViewById(R.id.buttonAnnuler);
            btnAnnuler.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pwindo.dismiss();
                }
            });

            //Création et positionnement de la fenêtre popup
            pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            //On noircit l'arrière plan (en rendant opaque la plaque noire en premier plan de celui-ci)
            panneauDeBase.getForeground().setAlpha( 200);

            //Actions lorsque la fenêtre "popup" disparaît
            pwindo.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    //On retire le noircissement du fond
                    panneauDeBase.getForeground().setAlpha(0);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null)
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
    }

    private void displayListView() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.ressource_list, null);

        final ArrayList<Ressource> ressourceList = listeRessources;

        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(this,
                R.layout.custom_list, ressourceList);
        ListView listView = (ListView) layout.findViewById(R.id.listViewRessources);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        /**
         * On pourra corriger la ressource en cliquant dessus
         */
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                /*Ressource res = (Ressource) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Clicked on Row: " + res.getName(),
                        Toast.LENGTH_LONG).show();*/

            }
        });

        //Bouton Valider
        Button btnValider = (Button) layout.findViewById(R.id.btnValider);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sauvegarde, puis quitter
                checkButtonClick();
                pwindo.dismiss();
            }
        });

        //Création et positionnement de la fenêtre popup
        pwindo = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

        //On noircit l'arrière plan (en rendant opaque la plaque noire en premier plan de celui-ci)
        panneauDeBase.getForeground().setAlpha( 200);

        //Actions lorsque la fenêtre "popup" disparaît
        pwindo.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //On retire le noircissement du fond
                panneauDeBase.getForeground().setAlpha(0);
            }
        });
    }

    private class MyCustomAdapter extends ArrayAdapter<Ressource> {
        private ArrayList<Ressource> countryList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Ressource> countryList) {
            super(context, textViewResourceId, countryList);
            this.countryList = new ArrayList<Ressource>();
            this.countryList.addAll(countryList);
        }

        private class ViewHolder {
            TextView code;
            CheckBox name;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.custom_list, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.code);
                holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                convertView.setTag(holder);

                holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Ressource ressource = (Ressource) cb.getTag();
                        ressource.setSelected(cb.isChecked());
                    }
                });
            }
            else holder = (ViewHolder) convertView.getTag();

            Ressource ressource = countryList.get(position);
            holder.code.setText(" (" +  ressource.getCode() + ")");
            holder.name.setText(ressource.getName());
            holder.name.setChecked(ressource.isSelected());
            holder.name.setTag(ressource);

            return convertView;
        }
    }

    private void checkButtonClick() {
         ressource = "";
        String amettre="";
        ArrayList<Ressource> ressourceList = dataAdapter.countryList;
        for(int i=0;i<ressourceList.size();i++){
             Ressource res = ressourceList.get(i);
             if(res.isSelected()){
                 if(ressource.equals("")) {
                     ressource = res.getCode();
                     amettre = res.getName();
                 }else {
                     ressource += "," + res.getCode();
                     amettre += ", " + res.getName();
                 }
             }
        }
        selectedRes.setText(amettre);
        /*Toast.makeText(getApplicationContext(),
            ressource, Toast.LENGTH_LONG).show();*/
    }
}