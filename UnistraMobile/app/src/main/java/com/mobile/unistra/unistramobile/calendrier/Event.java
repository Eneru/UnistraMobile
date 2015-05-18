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
    public String nomRessource;

    /**
     * Constructeur à utiliser pour les événements de l'agenda local.
     * @param titreCours Le titre de l'événement (pas forcément un cours)
     * @param dateDebut Date de début (en format "getTimeInMillis()")
     * @param dateFin Date de fin (en format "getTimeInMillis()")
     */
    public Event(String titreCours, String dateDebut, String dateFin){
        this.titreCours = titreCours;
        this.salle = "";
        this.uid = "";
        this.description = "";
        this.dateDebut =  new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        this.dateFin = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        this.alarme = false;

        Date dtDebut = new Date(Long.parseLong(dateDebut));
        Date dtFin;
        try{
            dtFin = new Date(Long.parseLong(dateFin));
        }catch(Exception e){
            dtFin = dtDebut;
        }
        this.dateDebut.setTime(dtDebut);
        this.dateFin.setTime(dtFin);

        this.doublon = false;
    }

    /**
     * Constructeur à utiliser pour les événements de l'ADE.
     * @param uid L'UID, tel qu'il est écrit dans l'ADE
     * @param titreCours Le titre de l'événement (pas forcément un cours)
     * @param salle Salle, telle qu'elle est écrte dans l'ADE
     * @param description Description de l'événement.
     * @param dateDebut Date de début (en format "getTimeInMillis()")
     * @param dateFin Date de fin (en format "getTimeInMillis()")
     */
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

    public int getHeureFin(){return dateFin.get(GregorianCalendar.HOUR_OF_DAY);}
    public int getMinuteFin(){return dateFin.get(GregorianCalendar.MINUTE);}

    public boolean estDoublon(){
        return doublon;
    }
    public void setDoublon(boolean etat){this.doublon = etat;}

    public boolean getAlarme(){return alarme;}
    public void setAlarme(boolean etat){this.alarme = etat;}
    public boolean invertAlarme(){this.alarme = !this.alarme;return this.alarme;}

    /**
     * Teste si deux événements ont suffisamment de points communs pour être potentiellement le même.
     * @param aTester Event à vérifier si c'est le même que <b>this</b>.
     * @return <b>true</b> si on dirait le même Event, <b>false</b> sinon.
     */
    public boolean equals(Event aTester){
        if((this.titreCours.equals(aTester.titreCours)) &&
                (this.getDebut().equals(aTester.getDebut())) &&
                (this.getFin().equals(aTester.getFin())))
            return true;
        else return false;
    }

    /*
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
    }*/

    /**
     * Sort un String à partir des horaires de l'événement.
     * <br>Sert à afficher les horaires (dans la swipeList, par exemple)
     * @return Un String du style : "10h00 - 12h30"
     */
    public String hourToString(){
        String heureFin = String.valueOf(this.getHeureFin());
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