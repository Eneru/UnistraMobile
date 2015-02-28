package com.mobile.unistra.unistramobile.calendrier;

import com.mobile.unistra.unistramobile.annuaire.Wget;
import net.fortuna.ical4j.model.Calendar;

/**
 * Created by Alexandre on 22-02-15.
 */
public class Calendrier extends Wget {
    private Calendar calendar;

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
        if(this.getHtml().substring(0,15).equalsIgnoreCase("BEGIN:VCALENDAR")
                && this.getHtml().substring(this.getHtml().length() -13).equalsIgnoreCase("END:VCALENDAR"))
            return true;
        else return false;
    }
}
