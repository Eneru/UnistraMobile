package com.mobile.unistra.unistramobile.calendrier;

import com.mobile.unistra.unistramobile.annuaire.Wget;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserImpl;
import net.fortuna.ical4j.data.ContentHandler;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;


import org.apache.commons.logging.*;


import java.util.Iterator;
import java.io.*;
import java.net.*;

/**
 * Created by Alexandre on 22-02-15.
 */
public class Calendrier extends Wget {
    private Calendar calendar;

    /**
     *
     * @param ressource    La ressource voulue (exemple : "4312" ou "4312,4311")
     * @param semaines
     */
    public Calendrier(String ressource, int semaines){
        super("https://adewebcons.unistra.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources="+ressource+"&projectId=5&calType=ical&nbWeeks="+semaines);
        this.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //CalendarBuilder builder = new CalendarBuilder();

    }

    /**
     * Cherche un calendrier sur l'ADE et en fait un objet <b>Calendar</b>
     * @param ressource La ressource voulue (exemple : "4312" ou "4312,4311")
     * @param nb_semaines
     * @return Un calendrier, si ça a fonctionné, <b>null</b> sinon.
     */
    public static Calendar fetch(String ressource, int nb_semaines){
        try{
            CalendarBuilder builder = new CalendarBuilder();
            URL url = new URL("https://adewebcons.unistra.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?"
                    + "resources="+ ressource
                    +"&projectId=5&calType=ical&nbWeeks="+nb_semaines);
            //URL url = new URL("https://adewebcons.unistra.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=4312,4311&projectId=5&calType=ical&nbWeeks=4");
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            return builder.build(reader);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * Méthode de test qui permet d'itérer sur le calendrier
     * @param calendar Calendrier à itérer
     */
    public static void blabla(Calendar calendar){
        if(calendar != null){
            for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
                Component component = (Component) i.next();
                //System.out.println("Component [" + component.getName() + "]");

                //component.getProperty("DESCRIPTION");
                System.out.println(component.getProperty("DESCRIPTION").getValue());//+ "\n");
       /*     	for (Iterator j = component.getProperties().iterator(); j.hasNext();) {
                	Property property = (Property) j.next();
                	System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
            	}*/
            }
        }
    }

    public static String aaa(Calendar calendar){
        if(calendar != null){
            return "Calendar = not NULL";
        }else return "Calendar = NULL";
    }
}
