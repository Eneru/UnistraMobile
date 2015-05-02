package com.mobile.unistra.unistramobile.calendrier;

import java.util.ArrayList;

/**
 * Created by Alexandre on 26-04-15.
 */
public class Ressource {
    String code = null;
    String name = null;
    boolean selected = false;

    /**
     * Représente une ressource avec son titre
     * @param code Identifiant, servant à être envoyé à l'ADE pour récupérer l'agenda en ligne
     * @param name Titre de la ressource. Rend la liste de ressources plus lisible pour l'utilisateur.
     * @param selected Indique s'il doit être sélectionné par défaut par l'interface graphique.
     */
    public Ressource(String code, String name, boolean selected) {
        super();
        this.code = code;
        this.name = name;
        this.selected = selected;
    }

    //Getters, setters...
    public String getCode() {return code;}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Génère une liste de ressources.
     * <br>Elle est "hard-coded", mais pourra être dynamique et être basée sur un fichier texte, par exemple.
     * @return Une ArrayList de ressources, utilisable par la ListView de CalendarActivity.
     */
    public static ArrayList<Ressource> getRessourceList() {
        ArrayList<Ressource> ressourceList = new ArrayList<Ressource>();
        Ressource res = new Ressource("3877", "M1 ILC", false);
        ressourceList.add(res);
        res = new Ressource("3823", "M1 ISI", false);
        ressourceList.add(res);
        res = new Ressource("4044", "M1 RISE", false);
        ressourceList.add(res);
        res = new Ressource("4100", "M2 ILC", false);
        ressourceList.add(res);
        res = new Ressource("19949", "M2 ISI", false);
        ressourceList.add(res);
        res = new Ressource("4094", "M2 RISE", false);
        ressourceList.add(res);
        res = new Ressource("5319", "Salle J0a", false);
        ressourceList.add(res);
        res = new Ressource("5251", "Salle J1", false);
        ressourceList.add(res);
        res = new Ressource("5250", "Salle J2", false);
        ressourceList.add(res);
        res = new Ressource("5249", "Salle J3", false);
        ressourceList.add(res);
        res = new Ressource("5317", "Salle J4", false);
        ressourceList.add(res);
        res = new Ressource("5316", "Salle J5", false);
        ressourceList.add(res);
        return ressourceList;
    }
}
