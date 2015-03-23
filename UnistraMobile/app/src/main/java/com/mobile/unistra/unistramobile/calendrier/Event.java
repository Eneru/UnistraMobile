package com.mobile.unistra.unistramobile.calendrier;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class Event {
    GregorianCalendar dateDebut;
    GregorianCalendar dateFin;
    String uid;
    String titreCours;
    String salle;
    String description;

    public Event(String uid, String titreCours, String salle, String description, Date dateDebut, Date dateFin){
        this.titreCours = titreCours;
        this.salle = salle;
        this.uid = uid;
        this.description = description;
        this.dateDebut =  new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        this.dateFin = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));


        this.dateDebut.setTime(dateDebut);
        this.dateFin.setTime(dateFin);
    }

    public GregorianCalendar getDebut(){return dateDebut;}
    public GregorianCalendar getFin(){return dateFin;}

    public String getTitre(){return titreCours;}
    public String getLieu(){return salle;}
    public String getDescription(){return description;}
    public String getUid(){return uid;}

    public int getHeureDebut(){return dateDebut.get(GregorianCalendar.HOUR_OF_DAY);}
    public int getMinuteDebut(){return dateDebut.get(GregorianCalendar.MINUTE);}

    public int getHeureFin(){return dateFin.get(GregorianCalendar.HOUR_OF_DAY);}
    public int getMinuteFin(){return dateFin.get(GregorianCalendar.MINUTE);}
}