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
    public boolean alarme;

    public Event(String titreCours, String dateDebut, String dateFin){
        this("",titreCours, "","",new Date(Long.parseLong(dateDebut)), new Date(Long.parseLong(dateFin)));
    }

    public Event(String uid, String titreCours, String salle, String description, Date dateDebut, Date dateFin){
        this.titreCours = titreCours;
        this.salle = salle;
        this.uid = uid;
        this.description = description;
        this.dateDebut =  new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        this.dateFin = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        this.alarme = false;

        this.dateDebut.setTime(dateDebut);
        this.dateFin.setTime(dateFin);

        this.doublon = false;
    }

    public GregorianCalendar getDebut(){return dateDebut;}
    public GregorianCalendar getFin(){return dateFin;}

    public String getTitre(){return titreCours;}
    public String getLieu(){return salle;}
    public String getDescription(){return description;}
    public String getUid(){return uid;}

    public int getHeureDebut(){return dateDebut.get(GregorianCalendar.HOUR_OF_DAY);}
    public int getMinuteDebut(){return dateDebut.get(GregorianCalendar.MINUTE);}
    public int getJourDebut(){return dateDebut.get(GregorianCalendar.DAY_OF_MONTH);}
    public int getMoisDebut(){return dateDebut.get(GregorianCalendar.MONTH);}
    public int getAnneeDebut(){return dateDebut.get(GregorianCalendar.YEAR);}

    public int getHeureFin(){return dateFin.get(GregorianCalendar.HOUR_OF_DAY);}
    public int getMinuteFin(){return dateFin.get(GregorianCalendar.MINUTE);}


    public boolean estDoublon(){
        return doublon;
    }


    public void setDoublon(boolean etat){this.doublon = etat;}
    public boolean getAlarme(){return alarme;}
    public void setAlarme(boolean etat){this.alarme = etat;}
    public boolean invertAlarme(){this.alarme = !this.alarme;return this.alarme;}

    public boolean equals(Event aTester){
        if((this.titreCours.equals(aTester.titreCours)) &&
                (this.getDebut().equals(aTester.getDebut())))// &&
                //(this.getFin().equals(aTester.getFin())))
            return true;
        else return false;
    }
    public String toString(){
        String heureDebut = String.valueOf(this.getHeureDebut());
        if(this.getHeureDebut() < 10) heureDebut = " " + heureDebut;
        String heureFin = String.valueOf(this.getHeureFin());
        if(this.getHeureFin() < 10) heureFin = " " + heureFin;

        String minutesDebut = String.valueOf(this.getMinuteDebut());
        if(this.getMinuteDebut() < 10) minutesDebut += "0";
        String minutesFin = String.valueOf(this.getMinuteFin());
        if(this.getMinuteFin() < 10) minutesFin += "0";

        return heureDebut +"h"+ minutesDebut +" - "
                + heureFin +"h"+minutesFin + " : "
                + this.titreCours;
    }

    public String hourToString(){
        String heureFin = String.valueOf(this.getHeureFin());
        //if(this.getHeureFin() < 10) heureFin = " " + heureFin;
        String heureDebut = String.valueOf(this.getHeureDebut());
        if((this.getHeureFin() > 9)&&(this.getHeureDebut() < 10)) heureDebut = " " + heureDebut;

        String minutesDebut = String.valueOf(this.getMinuteDebut());
        if(this.getMinuteDebut() < 10) minutesDebut += "0";
        String minutesFin = String.valueOf(this.getMinuteFin());
        if(this.getMinuteFin() < 10) minutesFin += "0";

        return heureDebut +"h"+ minutesDebut +" - "
                + heureFin +"h"+minutesFin;
    }
}