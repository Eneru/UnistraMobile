package com.mobile.unistra.unistramobile.calendrier;

import android.content.Context;

import com.mobile.unistra.unistramobile.annuaire.Wget;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Created by Alexandre on 22-02-15.
 */
public class Calendrier extends Wget {
    TimeZone fuseauHoraire;
    public ArrayList<Event> listeEvents;
    String ressources;

    /**
     * Surcharge de concatHtml, servant à avoir un format qui m'arrange.
     * @see com.mobile.unistra.unistramobile.annuaire.Wget#run()
     * @param s ligne lue.
     */
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
        // Le fuseau horaire permet d'adapter heure d'hiver/été
        fuseauHoraire = TimeZone.getTimeZone("Europe/Paris");

        this.listeEvents = listeEvents();
        this.ressources = ressource;
    }

    public void refresh(){
        listeEvents = listeEvents();
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

    /**
     * Renvoit le lieu qui figure dans <emph>entree</emph>.
     * @param entree Chaîne de caractère à analyser ; si possible un seul événement à la fois.
     * @return Le lieu, sous forme de <b>String</b>.
     */
    public String nomLieu(String entree){
        return entree.substring(entree.indexOf("LOCATION:") + 9, entree.indexOf('\n', entree.indexOf("LOCATION:") + 9));
    }

    /**
     * Appelle les méthodes de parsage pour générer une <b>Date</b> complète, faisant figurer heure, minutes, jour, mois, année.
     * <br>Il s'adapte au changement d'heure hiver/été.
     * @param entree <b>String</b> correspondant à un événement d'un VCALENDAR
     * @return Une <b>Date</b>, corresondant à la date (et heure) de fin d'un événement.
     */
    public Date dateFin(String entree){
        int annee = anneeFin(entree);
        int mois = moisFin(entree);
        int jour = jourFin(entree);
        int heure = heureFin(entree);
        int minutes = minutesFin(entree);

        Date dateFin = new Date(annee-1900,mois,jour,heure,minutes);

        if (fuseauHoraire.inDaylightTime(dateFin)) dateFin.setHours(heure+1);
        return dateFin;
    }

    public int minutesFin(String entree){
        int offset = entree.indexOf("DTEND:") + 17;
        return Integer.parseInt(entree.substring(offset, offset + 2));
    }

    public int heureFin(String entree) {
        int offset = entree.indexOf("DTEND:") + 15;
        return Integer.parseInt(entree.substring(offset, offset + 2)) + (fuseauHoraire.getRawOffset() / 3600000);
    }

    public int jourFin(String entree){
        int offset = entree.indexOf("DTEND:")+12;
        return Integer.parseInt(entree.substring(offset, offset + 2));
    }

    public int moisFin(String entree){
        int offset = entree.indexOf("DTEND:")+10;
        return Integer.parseInt(entree.substring(offset, offset + 2))-1;
    }

    public int anneeFin(String entree){
        int offset = entree.indexOf("DTEND:")+6;
        return Integer.parseInt(entree.substring(offset, offset + 4));
    }

    /**
     * Appelle les méthodes de parsage pour générer une <b>Date</b> complète, faisant figurer heure, minutes, jour, mois, année.
     * <br>Il s'adapte au changement d'heure hiver/été.
     * @param entree <b>String</b> correspondant à un événement d'un VCALENDAR
     * @return Une <b>Date</b>, corresondant à la date (et heure) de début d'un événement.
     */
    public Date dateDebut(String entree) {
        int annee = anneeDebut(entree);
        int mois = moisDebut(entree);
        int jour = jourDebut(entree);
        int heure = heureDebut(entree);
        int minutes = minutesDebut(entree);

        Date dateDebut = new Date(annee - 1900, mois, jour, heure, minutes);
        if(fuseauHoraire.inDaylightTime(dateDebut)) dateDebut.setHours(heure+1);
        return dateDebut;
    }

    public int minutesDebut(String entree){
        int offset = entree.indexOf("DTSTART:") + 19;
        return Integer.parseInt(entree.substring(offset, offset + 2));
    }

    public int heureDebut(String entree) {
        int offset = entree.indexOf("DTSTART:") + 17;
        return Integer.parseInt(entree.substring(offset, offset + 2)) + (fuseauHoraire.getRawOffset() / 3600000);
    }

    public int jourDebut(String entree){
        int offset = entree.indexOf("DTSTART:")+14;
        return Integer.parseInt(entree.substring(offset, offset + 2));
    }

    public int moisDebut(String entree){
        int offset = entree.indexOf("DTSTART:")+12;
        return Integer.parseInt(entree.substring(offset, offset + 2))-1;
    }

    public int anneeDebut(String entree){
        int offset = entree.indexOf("DTSTART:")+8;
        return Integer.parseInt(entree.substring(offset, offset + 4));
    }

    /**
     * Parse un <b>String</b> pour y trouver la description.
     * @param entree String correspondant à un événement d'un VCALENDAR
     * @return String correspondant à la description
     */
    public String description(String entree){
        int debut = entree.indexOf("DESCRIPTION:")+12;
        int fin = entree.indexOf("\n",debut);
        String retour = entree.substring(debut,fin);
        retour = retour.replaceAll("\\\\n","\n");//Pour échapper un backslash il en faut 4 d'affilée.
        return retour;
    }

    /**
     * Parse un <b>String</b> pour y trouver l'UID.
     * @param entree String correspondant à un événement d'un VCALENDAR
     * @return String correspondant à l'UID
     */
    public String Uid(String entree){
        int debut = entree.indexOf("UID:")+4;
        int fin = entree.indexOf("\n",debut);
        return entree.substring(debut, fin);
    }

    /**
     * Retourne le <b>String</b> qui pourra être affiché.
     * <br>On ajoute tous les événements.
     * @return Chaîne de caractère représentant la liste des événements.
     */
    public String afficherEvent(){
        String affichage="";
        for(Event event:listeEvents)
            affichage += event.titreCours + " : "
                      + event.salle + (event.doublon?"EST UN DOUBLON":"isok") + "\n\tà "
                      + event.getDebut().getTimeInMillis() +"\n";
        return affichage;
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

    /**
     * Crée une liste de <b>Event</b> à partir des événements donnés en format <b>String</b>.
     * <b>Ne doit être utilisée qu'au tout début du programme ! Ce n'est pas un "get"</b>
     * @return Une liste de <b>Event</b> non ordonnée.
     */
    private ArrayList<Event> listeEvents(){
        ArrayList<Event> liste = new ArrayList<Event>();
        for(String entree: donnerEvents())
            liste.add(genererEvent(entree));
        return liste;
    }

    /**
     * Supprime un événement de la liste d'événements.
     * @param aSupprimer élément à supprimer
     */
    public void remove(Event aSupprimer){
        if(this.listeEvents.contains(aSupprimer))
            this.listeEvents.remove(aSupprimer);
    }

    /**
     * Appelle le constructeur de <b>Event</b> avec comme paramètre les méthodes de parsage correspondants aux champs.
     * @param entree <b>String</b> représentant un événement au format brut.
     * @return Un <b>Event</b> initialisé et prêt à l'emploi.
     */
    private Event genererEvent(String entree){
        return new Event(Uid(entree), nomMatiere(entree),nomLieu(entree),description(entree),dateDebut(entree),dateFin(entree));
    }

    /**
     * Renvoit la liste des ressources sous format <b>String</b>.
     * <br>On aura donc un <b>String</b> de la forme : "4208" ou "3012,123,123"
     * @return Une chaîne de caractère contenant des nombres, séparés par des virgules (pas d'espace)
     */
    public String getRessources(){return this.ressources;}

    public void filtrerDoublons(ArrayList<Event> agendaLocal){
        for(Event local : agendaLocal){
            for(Event recu : this.listeEvents){
                if(!recu.estDoublon() && recu.equals(local)){
                    recu.setDoublon(true);
                    local.setDoublon(true);
                }
            }
        }
    }
}