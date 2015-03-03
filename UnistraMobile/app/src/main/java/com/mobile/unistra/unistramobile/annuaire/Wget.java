package com.mobile.unistra.unistramobile.annuaire;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by mataniere on 22/02/2015.
 */
public class Wget extends Thread
{
    private String url;
    protected String html;

    public Wget(String url)
    {
        this.url=url;
    }

    @Override
    public void run()
    {
        AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
        HttpPost req= new HttpPost(url);
        HttpResponse response=null;
        try {
           response=  client.execute(req);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(response!=null)
        {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }


            String s = new String();
            html = new String();
            try {
                while(( s = reader.readLine())!=null)
                {
                    //html+=s;
                    concatHtml(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    public String getHtml()
    {
        return html;
    }

        protected void concatHtml(String s){
            html += s;
        }


}
