package com.mobile.unistra.unistramobile.calendrier;

import com.mobile.unistra.unistramobile.annuaire.Wget;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * Created by Alexandre on 22-02-15.
 */
public class Calendrier extends Wget {
    TimeZone fuseauHoraire;
    ArrayList<Event> listeEvents;

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
        fuseauHoraire = TimeZone.getTimeZone("Europe/Paris");
        this.listeEvents = listeEvents();

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

    public Date dateFin(String entree){
        int annee = anneeFin(entree);
        int mois = moisFin(entree);
        int jour = jourFin(entree);
        int heure = heureFin(entree);
        int minutes = minutesFin(entree);

        Date dateFin = new Date(annee-1900,mois,jour,heure,minutes);
        if (fuseauHoraire.inDaylightTime(dateFin)) dateFin.setHours(heure++);
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

    public Date dateDebut(String entree) {
        int annee = anneeDebut(entree);
        int mois = moisDebut(entree);
        int jour = jourDebut(entree);
        int heure = heureDebut(entree);
        int minutes = minutesDebut(entree);

        Date dateDebut = new Date(annee - 1900, mois, jour, heure, minutes);
        if (fuseauHoraire.inDaylightTime(dateDebut)) dateDebut.setHours(heure++);
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

    public String description(String entree){
        /*int debut = entree.indexOf("DESCRIPTION:")+12;
        int fin = entree.indexOf("\n",debut);

        return entree.substring(debut,fin);*/
        return "";
    }

    public String Uid(String entree){
        /*int debut = entree.indexOf("UID:")+4;
        int fin = entree.indexOf("\n");
        return entree.substring(debut, fin);*/
        return "";
    }

    public String afficherEvent(){
        String affichage="";
        for(Event event:listeEvents){
            affichage += event.titreCours + " : "+ event.salle + "\n\tà "+ event.getDebut().getTimeInMillis() +"\n";//event.getHeureDebut()+"h"+event.getMinuteDebut()  + " le "+ event.dateDebut.get(GregorianCalendar.DAY_OF_MONTH) +"/"+ event.dateDebut.get(GregorianCalendar.MONTH)+"/"+ event.dateDebut.get(GregorianCalendar.YEAR)+"\n";
        }
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

    public ArrayList<Event> listeEvents(){
        ArrayList<Event> liste = new ArrayList<Event>();
        for(String entree: donnerEvents())
            liste.add(genererEvent(entree));
        return liste;
    }

    private Event genererEvent(String entree){
        return new Event(Uid(entree), nomMatiere(entree),nomLieu(entree),description(entree),dateDebut(entree),dateFin(entree));
    }
}