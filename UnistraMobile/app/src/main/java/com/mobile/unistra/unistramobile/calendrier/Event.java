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
    public boolean doublon;

    public Event(String titre, String dateDebut, String dateFin){
        this.titreCours = titre;
        this.dateDebut =  new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        this.dateFin = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));

        this.dateDebut.setTimeInMillis(Long.parseLong(dateDebut));
        if(dateFin!=null)
            this.dateFin.setTimeInMillis(Long.parseLong(dateFin));
        this.doublon = false;
    }

    public Event(String uid, String titreCours, String salle, String description, Date dateDebut, Date dateFin){
        this.titreCours = titreCours;
        this.salle = salle;
        this.uid = uid;
        this.description = description;
        this.dateDebut =  new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        this.dateFin = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));

        this.dateDebut.setTime(dateDebut);
        this.dateFin.setTime(dateFin);
        this.doublon = false;
    }

    public void setDoublon(Boolean doublon){this.doublon = doublon;}

    public GregorianCalendar getDebut(){return dateDebut;}
    public GregorianCalendar getFin(){return dateFin;}

    public String getTitre(){return titreCours;}
    public String getLieu(){return salle;}
    public String getDescription(){return description;}
    public String getUid(){return uid;}

    public String estDoublon(){return doublon?"true":"false";}

    public int getHeureDebut(){return dateDebut.get(GregorianCalendar.HOUR_OF_DAY);}
    public int getMinuteDebut(){return dateDebut.get(GregorianCalendar.MINUTE);}

    public int getHeureFin(){return dateFin.get(GregorianCalendar.HOUR_OF_DAY);}
    public int getMinuteFin(){return dateFin.get(GregorianCalendar.MINUTE);}
}