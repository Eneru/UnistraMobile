package com.mobile.unistra.unistramobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton btn_annu = (ImageButton) findViewById(R.id.imageButton4);
        btn_annu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goAnnu= new Intent(MainActivity.this,AnnuaireActivity.class);
                startActivity(goAnnu);
            }
        });
        ImageButton btn_calen = (ImageButton) findViewById(R.id.imageButton);
        btn_calen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goCalen= new Intent(MainActivity.this,CalendrierActivity.class);
                startActivity(goCalen);
            }
        });

        ImageButton btn_map = (ImageButton) findViewById(R.id.imageButton3);
        btn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goMap= new Intent(MainActivity.this,LoadingMap.class);
                startActivity(goMap);
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
