package com.mobile.unistra.unistramobile.calendrier;

import com.mobile.unistra.unistramobile.calendrier.Wget;
import net.fortuna.ical4j.model.Calendar;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alexandre on 22-02-15.
 */
public class Calendrier extends Wget {
    private Calendar calendar;
    private Date date;

    /**
     * Constructeur de Calendrier. Il utilise la classe <b>Wget</b> de Nicolas.
     * <br>À la création, cette classe exécute un thread pour aller chercher des données au format <emph>.ics</emph> sur le site de l'Unistra.
     * @param ressource     La ressource voulue (exemple : "4312" ou "4312,4311"). 4308 = M1S2 ILC
     * @param semaines      Je sais pas ce que c'est, ça, probablement la semaine voulue, mais va falloir vérifier...
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
        //if(entree.indexOf("TDLOCATION") < entree.indexOf("LOCATION"))
        return entree.substring(entree.indexOf("LOCATION:") + 9, entree.indexOf('\n', entree.indexOf("LOCATION:") + 9));
    }

    public String jourDebut(String entree){
        int truc = entree.indexOf("DTSTART:")+14;
        return entree.substring(truc, truc + 2);
    }

    public String moisDebut(String entree){
        int truc = entree.indexOf("DTSTART:")+12;
        return entree.substring(truc, truc + 2);
    }

    public String anneeDebut(String entree){
        int truc = entree.indexOf("DTSTART:")+8;
        return entree.substring(truc, truc + 4);
    }

    public String afficherEvent(String entree){
        return nomMatiere(entree) + " : " + jourDebut(entree) + "/" + moisDebut(entree) + "/" + anneeDebut(entree) + ", en salle : "+ nomLieu(entree);
    }

    /**
     * Donne la liste de tous les événements en format brut : <emph>BEGIN:VEVENT ... END:VEVENT</emph>.
     * <br>On pourra utiliser afficherEvent(p) sur chaque String p de ce tableau, ou n'importe quelle autre méthode de parsage.
     * @return Un tableau de String, non ordonné.
     */
    public ArrayList<String> donnerEvents(){
        String parse = this.getHtml();//.substring(this.getHtml().indexOf("BEGIN:VCALENDAR")+16);

        ArrayList<String> resultat = new ArrayList<String>();
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
