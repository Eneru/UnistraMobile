package com.mobile.unistra.unistramobile.calendrier;

/**
 * Created by Alexandre on 09-04-15.
 */
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.unistra.unistramobile.R;

public class EventAdapter extends ArrayAdapter {

    List   data;
    Context context;
    int layoutResID;

    public EventAdapter(Context context, int layoutResourceId,List data) {
        super(context, layoutResourceId, data);

        this.data=data;
        this.context=context;
        this.layoutResID=layoutResourceId;

        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsHolder holder = null;
        View row = convertView;
        //holder = null;

        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResID, parent, false);

            holder = new NewsHolder();

            holder.eventName = (TextView)row.findViewById(R.id.example_itemname);
            //holder.icon=(ImageView)row.findViewById(R.id.example_image);
            holder.horaires = (TextView)row.findViewById(R.id.example_text);
            //holder.button1=(Button)row.findViewById(R.id.swipe_button1);
            //holder.button2=(Button)row.findViewById(R.id.swipe_button2);
            //holder.button3=(Button)row.findViewById(R.id.swipe_button3);
            row.setTag(holder);
        }else{
            holder = (NewsHolder)row.getTag();
        }

        Event event = (Event) data.get(position);
        holder.eventName.setText(event.getTitre());
        //holder.icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
        //holder.icon=(ImageView)row.findViewById(R.id.example_image);
        holder.horaires.setText(event.hourToString());

        /*holder.button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 1 Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        holder.button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 2 Clicked",Toast.LENGTH_SHORT).show();
            }
        });

        holder.button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(context, "Button 3 Clicked",Toast.LENGTH_SHORT);
            }
        });*/

        return row;

    }

    static class NewsHolder{

        TextView eventName;
        //ImageView icon;
        TextView horaires;
        //Button button1;
        //Button button2;
        //Button button3;
    }

}