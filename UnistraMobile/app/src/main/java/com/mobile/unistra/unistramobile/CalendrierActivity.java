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
import com.mobile.unistra.unistramobile.calendrier.Calendrier;

import java.util.ArrayList;
import java.util.GregorianCalendar;


public class CalendrierActivity extends ActionBarActivity {
    Calendrier calendrier;
    EditText txtRessource;
    EditText txtSemaines;
    TextView txt;
    TextView result;
    ArrayList<String> entrees;

    public void exportVersAgenda(String titre, String salle, String description){
       /* Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        startActivity(intent);*/
        Intent intent = new Intent(Intent.ACTION_INSERT);

        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.Events.TITLE, titre);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, salle);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, description);

        // Setting dates
        GregorianCalendar calDate = new GregorianCalendar(2015, 03, 01);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                calDate.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                calDate.getTimeInMillis());

        // make it a full day event
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

        // make it a recurring Event
        //intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=11;WKST=SU;BYDAY=TU,TH");

        // Making it private and shown as busy
        //intent.putExtra(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_PRIVATE);
        //intent.putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);

        this.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendrier);

        entrees = new ArrayList<String>();
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
                       entrees = calendrier.donnerEvents();//entrees.add(calendrier.premierEvent());
                       String resulta="";
                        for(String p : entrees){
                            resulta += calendrier.afficherEvent(p) + '\n';
                        }
                       result.setText(resulta);
                        //exportVersAgenda("On travaille sur le projet","à la maison","");
                   }else{
                       result.setText("Erreur au chargement de l'ics");
                   }
                }
                else txt.setText("FAILURE");
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
