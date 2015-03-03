package com.mobile.unistra.unistramobile.calendrier;

import com.mobile.unistra.unistramobile.annuaire.Wget;
import net.fortuna.ical4j.model.Calendar;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Alexandre on 22-02-15.
 */
public class Calendrier extends Wget {
    TimeZone fuseauHoraire = TimeZone.getTimeZone("Europe/Paris");

    @Override
    protected void concatHtml(String s) {
        html += s + '\n';
    }


    /**
     * Constructeur de Calendrier. Il utilise la classe <b>Wget</b> de Nicolas.
     * <br>À la création, cette classe exécute un thread pour aller chercher des données au format <emph>.ics</emph> sur le site de l'Unistra.
     * @param ressource     La ressource voulue (exemple : "4312" ou "4312,4311"). 4308 = M1S2 ILC
     * @param semaines      Le nombre de semaines voulues
     */
    public Calendrier(String ressource, String semaines){
        super("https://adewebcons.unistra.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources="+ressource+"&projectId=5&calType=ical&nbWeeks="+semaines);
        this.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Le tri des informations reçues aura probablement lieu ici
        this.interrupt();
    }

    /**
     * Permet de vérifier si le fichier téléchargé ressemble bien à un fichier <emph>.ics</emph>.
     * @return <b>true</b>, si le fichier commence bien par <emph>BEGIN:VCALENDAR</emph> et termine par <emph>END:VCALENDAR</emph>.
     */
    public boolean estValide(){
        if(this.getHtml().substring(0,15).equalsIgnoreCase("BEGIN:VCALENDAR"))
            return true;
        else return false;
    }

    /**
     * Renvoit le premier nom de matière qui figure dans <emph>entree</emph>.
     * @param entree Chaîne de caractère à analyser ; si possible un seul événement à la fois.
     * @return Le nom de la matière seulement, sous forme de <b>String</b>.
     */
    public String nomMatiere(String entree){
        return entree.substring(entree.indexOf("SUMMARY:") + 8, entree.indexOf('\n', entree.indexOf("SUMMARY:") + 8));
    }


    public String nomLieu(String entree){
        return entree.substring(entree.indexOf("LOCATION:") + 9, entree.indexOf('\n', entree.indexOf("LOCATION:") + 9));
    }

    /**
     * L'heure de début.
     * @param entree
     * @param date      La date à laquelle l'heure s'applique : permet d'adapter l'heure au fuseau horaire et à l'heure d'hiver/été
     * @return          Un String contenant l'heure et les minutes, séparés d'un 'h'
     */
    public String heureDebut(String entree, Date date){
        int offset = entree.indexOf("DTSTART:")+17;
        int heure = Integer.parseInt(entree.substring(offset, offset + 2)) + (fuseauHoraire.getRawOffset() / 3600000);

        //Si on est en heure d'été, on rajoute une heure
        if(fuseauHoraire.inDaylightTime(date)) heure ++;

        return heure + "h"+ entree.substring(offset +2, offset + 4);
    }

    /**
     * L'heure de fin.
     * @param entree
     * @param date      La date à laquelle l'heure s'applique : permet d'adapter l'heure au fuseau horaire et à l'heure d'hiver/été
     * @return          Un String contenant l'heure et les minutes, séparés d'un 'h'
     */
    public String heureFin(String entree, Date date){
        int offset = entree.indexOf("DTEND:")+15;
        int heure = Integer.parseInt(entree.substring(offset, offset + 2)) + (fuseauHoraire.getRawOffset() / 3600000);

        //Si on est en heure d'été, on rajoute une heure
        if(fuseauHoraire.inDaylightTime(date)) heure ++;

        return heure + "h"+ entree.substring(offset +2, offset + 4);
    }

    public String jourDebut(String entree){
        int offset = entree.indexOf("DTSTART:")+14;
        return entree.substring(offset, offset + 2);
    }

    public String moisDebut(String entree){
        int offset = entree.indexOf("DTSTART:")+12;
        return entree.substring(offset, offset + 2);
    }

    public String anneeDebut(String entree){
        int offset = entree.indexOf("DTSTART:")+8;
        return entree.substring(offset, offset + 4);
    }


    public String afficherEvent(String entree){
        int jour = Integer.parseInt(jourDebut(entree));
        int mois = Integer.parseInt(moisDebut(entree));
        int annee = Integer.parseInt(anneeDebut(entree));
        String heureDebut = heureDebut(entree, new Date(annee,mois,jour));
        String heureFin = heureFin(entree, new Date(annee,mois,jour));

        return nomMatiere(entree) + " : de "+ heureDebut + " à " + heureFin + " le " + jourDebut(entree) + "/" + moisDebut(entree) + "/" + anneeDebut(entree) + ", en salle : "+ nomLieu(entree);
    }

    /**
     * Donne la liste de tous les événements en format brut : <emph>BEGIN:VEVENT ... END:VEVENT</emph>.
     * <br>On pourra utiliser afficherEvent(p) sur chaque String p de ce tableau, ou n'importe quelle autre méthode de parsage.
     * @return Un tableau de String, non ordonné.
     */
    public ArrayList<String> donnerEvents(){
        String parse = this.getHtml();//.substring(this.getHtml().indexOf("BEGIN:VCALENDAR")+16);
        ArrayList<String> resultat = new ArrayList<String>();

        //Les virgules sont représentées par "\,". On va donc remplacer ça par ","
        parse = parse.replaceAll("\\\\,",",");//Pour échapper un backslash il en faut 4 d'affilée.

        while(parse.indexOf("END:VCALENDAR") != 0){
            resultat.add(parse.substring(
                    parse.indexOf("BEGIN:VEVENT")+13,
                    parse.indexOf("END:VEVENT")
            ));
            parse = parse.substring(parse.indexOf("END:VEVENT")+11);
        }
        return resultat;
    }
}

class Event {
    String titreCours;
    String salle;
    String description;
    Date dateDebut;
    Date dateFin;
    int heureDebut;
    int minutesDebut;
    int heureFin;
    int minutesFin;

    public Event(){

    }
}