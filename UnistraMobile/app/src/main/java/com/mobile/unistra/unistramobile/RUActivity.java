package com.mobile.unistra.unistramobile;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobile.unistra.unistramobile.annuaire.Wget;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class RUActivity extends ActionBarActivity {

    // Constantes
    private final static String[] jour = {"Lundi","Mardi","Mercredi","Jeudi","Vendredi"};
    private final static String[] ru_valide = {"Cronenbourg","Esplanade","Illkirch"};
    private final static String[] adresse_ru = {"23 rue du Loess 67200 Strasbourg",
            "32 boulevard de la Victoire 67000 Strasbourg","76 Route du Rhin 67400 Illkirch-Graffenstaden"};
    private final static int[] ru_id = {179,180,181};
    private final static String url_ru = "http://restos-u.crous-strasbourg.fr/fr/menu-de-la-semaine?restaurant=";
    private static Map<String,String> adress_rue_map = new Hashtable<>(3);
    // Les autres RU ne montrent pas leurs menus sur le site du Crous

    // Sélecteur
    private Spinner sp_ru;
    private Spinner sp_jr;
    private ListView lv;


    private int current_ru;
    // Il y a 5 jours dans la semaine avec chacun une liste de menu différent (list de taille 5)
    // Et pour chaque menu il y a plusieurs ingrédients (list de string)
    private List<List<String>> repas_semaine = new ArrayList<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ru);

        lv = (ListView)findViewById(R.id.listView_ru);
        lv.setClickable(false);
        lv.setActivated(false);
        lv.setFocusable(false);

        for (int i = 0 ; i < 3 ; i++)
            adress_rue_map.put(ru_valide[i],adresse_ru[i]);

        //Adapter RU
        sp_ru = (Spinner)findViewById(R.id.spinner_resto);
        List<String> spinnerArray_ru = new ArrayList<>();
        Collections.addAll(spinnerArray_ru, ru_valide);
        ArrayAdapter<String> adapter_ru = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,spinnerArray_ru);
        adapter_ru.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_ru.setAdapter(adapter_ru);

        //Adapter jour
        sp_jr = (Spinner)findViewById(R.id.spinner_jour_ru);
        List<String> spinnerArray_jr = new ArrayList<>();
        Collections.addAll(spinnerArray_jr, jour);
        ArrayAdapter<String> adapter_jr = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,spinnerArray_jr);
        adapter_jr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_jr.setAdapter(adapter_jr);

        sp_ru.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                current_ru = sp_ru.getSelectedItemPosition();
                int selected = 0;
                sp_jr.setSelection(selected);

                Wget wget = new Wget(url_ru + ru_id[current_ru]+ "&qt-field_collection_quicktabs="+ selected +"#qt-field_collection_quicktabs");
                wget.start();
                try {
                    wget.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Document doc = Jsoup.parse(wget.getHtml());
                // div du jour sélectionné
                Element div_du_jour = doc.getElementById("quicktabs-tabpage-field_collection_quicktabs-" + selected);
                if (div_du_jour != null) {
                    // div des tous descriptions des menus de ce même jour

                    Elements div_menus = div_du_jour.getElementsByClass("field-name-field-description");
                    if (div_menus != null) {
                        for (Element div_menu : div_menus) {
                            // Tag des description séparées
                            Elements texts = div_menu.getElementsByTag("h2");
                            if (texts != null) {
                                List<String> list = new ArrayList<String>();
                                for (Element text : texts) {
                                    String value = text.text();
                                    if(!list.contains(value))
                                        list.add(value);
                                }
                                repas_semaine.add(selected, list);
                            }
                        }
                    }
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
                else
                {
                    List<String> list = new ArrayList<String>();
                    list.add("Aucun menu pour " + jour[selected] + " à " + ru_valide[current_ru]);
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                current_ru = 0;
                int selected = 0;
                sp_jr.setSelection(selected);

                Wget wget = new Wget(url_ru + ru_id[current_ru]+ "&qt-field_collection_quicktabs="+ selected +"#qt-field_collection_quicktabs");
                wget.start();
                try {
                    wget.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Document doc = Jsoup.parse(wget.getHtml());
                // div du jour sélectionné
                Element div_du_jour = doc.getElementById("quicktabs-tabpage-field_collection_quicktabs-" + selected);
                if (div_du_jour != null) {
                    // div des tous descriptions des menus de ce même jour

                    Elements div_menus = div_du_jour.getElementsByClass("field-name-field-description");
                    if (div_menus != null) {
                        for (Element div_menu : div_menus) {
                            // Tag des description séparées
                            Elements texts = div_menu.getElementsByTag("h2");
                            if (texts != null) {
                                List<String> list = new ArrayList<String>();
                                for (Element text : texts) {
                                    String value = text.text();
                                    if(!list.contains(value))
                                        list.add(value);
                                }
                                repas_semaine.add(selected, list);
                            }
                        }
                    }
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
                else
                {
                    List<String> list = new ArrayList<String>();
                    list.add("Aucun menu pour " + jour[selected] + " à " + ru_valide[current_ru]);
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
            }
        });

        sp_jr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected = sp_jr.getSelectedItemPosition();

                Wget wget = new Wget(url_ru + ru_id[current_ru]+ "&qt-field_collection_quicktabs="+ selected +"#qt-field_collection_quicktabs");
                wget.start();
                try {
                    wget.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Document doc = Jsoup.parse(wget.getHtml());
                // div du jour sélectionné
                Element div_du_jour = doc.getElementById("quicktabs-tabpage-field_collection_quicktabs-" + selected);
                if (div_du_jour != null) {
                    // div des tous descriptions des menus de ce même jour

                    Elements div_menus = div_du_jour.getElementsByClass("field-name-field-description");
                    if (div_menus != null) {
                        for (Element div_menu : div_menus) {
                            // Tag des description séparées
                            Elements texts = div_menu.getElementsByTag("h2");
                            if (texts != null) {
                                List<String> list = new ArrayList<String>();
                                for (Element text : texts) {
                                    String value = text.text();
                                    if(!list.contains(value))
                                        list.add(value);
                                }
                                repas_semaine.add(selected, list);
                            }
                        }
                    }
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
                else
                {
                    List<String> list = new ArrayList<String>();
                    list.add("Aucun menu pour " + jour[selected] + " à " + ru_valide[current_ru]);
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                int selected = 0;

                Wget wget = new Wget(url_ru + ru_id[current_ru]+ "&qt-field_collection_quicktabs="+ selected +"#qt-field_collection_quicktabs");
                wget.start();
                try {
                    wget.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Document doc = Jsoup.parse(wget.getHtml());
                // div du jour sélectionné
                Element div_du_jour = doc.getElementById("quicktabs-tabpage-field_collection_quicktabs-" + selected);
                if (div_du_jour != null) {
                    // div des tous descriptions des menus de ce même jour

                    Elements div_menus = div_du_jour.getElementsByClass("field-name-field-description");
                    if (div_menus != null) {
                        for (Element div_menu : div_menus) {
                            // Tag des description séparées
                            Elements texts = div_menu.getElementsByTag("h2");
                            if (texts != null) {
                                List<String> list = new ArrayList<String>();
                                for (Element text : texts) {
                                    String value = text.text();
                                    if(!list.contains(value))
                                        list.add(value);
                                }
                                repas_semaine.add(selected, list);
                            }
                        }
                    }
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
                else
                {
                    List<String> list = new ArrayList<String>();
                    list.add("Aucun menu pour " + jour[selected] + " à " + ru_valide[current_ru]);
                    ArrayAdapter<String> test = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,repas_semaine.get(selected)){
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent) {
                            View view =super.getView(position, convertView, parent);

                            TextView textView=(TextView) view.findViewById(android.R.id.text1);

                            textView.setTextColor(Color.BLACK);

                            return view;
                        }
                    };
                    lv.setAdapter(test);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ru, menu);
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
