package com.mobile.unistra.unistramobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobile.unistra.unistramobile.annuaire.Annuaire;
import com.mobile.unistra.unistramobile.annuaire.NoResultException;
import com.mobile.unistra.unistramobile.annuaire.Prof;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


public class AnnuaireActivity extends ActionBarActivity {

    private String prefix;
    private AtomContactListAdapter adapter;

    private ListView listView;
    private LinearLayout layout;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annuaire);
        _init_view();
    }

    private void _init_view()
    {
        adapter = new AtomContactListAdapter
                (AnnuaireActivity.this,R.layout.list_annuaire_item,new ArrayList<AtomContact>());
        editText = (EditText)findViewById(R.id.nameEditText);
        listView = (ListView)findViewById(R.id.list_recherche);
        listView.setAdapter(adapter);
        layout = (LinearLayout)findViewById(R.id.progressbar_view);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()>=4)
                {
                    new Task().execute();
                }
                else
                {
                    prefix = null;
                    adapter.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            layout.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            layout.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                adapter.clear();
                prefix = editText.getText().toString();
                try
                {
                    String[] recherche = prefix.split(" ");
                    Annuaire annuaire;
                    if (recherche.length >= 2)
                        annuaire = new Annuaire(recherche[1],recherche[0],"","","");
                    else
                        annuaire = new Annuaire("",recherche[0],"","","");
                    ArrayList<Prof> profs = annuaire.getAnnuaire();
                    for (Prof p : profs)
                        adapter.add
                                (new AtomContact(p.getIdentite(),p.getTelephone(),p.getMail()));
                }
                catch (URISyntaxException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }catch (NoResultException e) {
                    e.printStackTrace();
                    editText.setText(editText.getText().toString().substring(0,editText.getText().toString().length()-2));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_annuaire, menu);
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

    /*
    private void dialog_mail(final String mail)
    {
        final String sujet= "Ajouter un sujet";
        final String corps= "Ajouter un corps au courriel";
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_mail, null))
                // Add action buttons
                .setPositiveButton(R.string.button_sendto_str, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText subject = (EditText) findViewById(R.id.subject_mail);
                        EditText body = (EditText) findViewById(R.id.body_mail);

                        if (subject.getText().length() == 0)
                            subject.setText(sujet);
                        if (body.getText().length() == 0)
                            body.setText(corps);

                        if (subject.getText().length() != 0
                                && body.getText().length() != 0
                                && !body.getText().toString().equals(corps)
                                && !subject.getText().toString().equals(sujet)) {
                            //mail_teacher(mail, subject.getText().toString(), body.getText().toString());
                            dialog.dismiss();
                        }

                    }
                })
                .setNegativeButton(R.string.button_cancel_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    */
}

class AtomContact implements Serializable {
    private static final long serialVersionUID = -5435670920302756945L;

    private String name = "";
    private String num = "";
    private String mail ="";

    public AtomContact(String name, String num, String mail) {
        this.setName(name);
        this.setNum(num);
        this.setMail(mail);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getMail ()
    {
        return mail;
    }

    public void setMail (String mail)
    {
        this.mail = mail;
    }
}

class AtomContactListAdapter extends ArrayAdapter<AtomContact> {

    protected static final String LOG_TAG = AtomContactListAdapter.class.getSimpleName();

    private List<AtomContact> items;
    private int layoutResourceId;
    private Context context;

    public AtomContactListAdapter(Context context, int layoutResourceId, List<AtomContact> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AtomContactHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new AtomContactHolder();
        holder.atomContact = items.get(position);
        holder.call = (ImageButton)row.findViewById(R.id.image_button_appeler);
        final AtomContactHolder finalHolder = holder;
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number= finalHolder.atomContact.getNum();
                call_teacher(number);
            }
        });
        holder.call.setTag(holder.atomContact);
        holder.sendto = (ImageButton)row.findViewById(R.id.image_button_mailer);
        holder.sendto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = finalHolder.atomContact.getMail();
                mail_teacher(email);
            }
        });
        holder.sendto.setTag(holder.atomContact);
        holder.name = (TextView)row.findViewById(R.id.identity_text);
        row.setTag(holder);

        setupItem(holder);
        return row;
    }

    private void setupItem(AtomContactHolder holder) {
        holder.name.setText(holder.atomContact.getName());
    }

    public void call_teacher (String number)
    {
        String nb = "tel:" + number.replace(".","");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(nb));
        getContext().startActivity(intent);
    }

    private void mail_teacher (String mail)
    {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String uritext = "mailto:" + Uri.encode(mail)
                +"?subject=&body=";
        Uri uri = Uri.parse(uritext);

        intent.setData(uri);
        getContext().startActivity(Intent.createChooser(intent, "Envoi du courriel..."));
    }

    public static class AtomContactHolder {
        AtomContact atomContact;
        TextView name;
        ImageButton call;
        ImageButton sendto;
    }
}
