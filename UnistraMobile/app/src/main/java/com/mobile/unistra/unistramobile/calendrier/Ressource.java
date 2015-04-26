package com.mobile.unistra.unistramobile.calendrier;

import java.util.ArrayList;

/**
 * Created by Alexandre on 26-04-15.
 */
public class Ressource {


    String code = null;
    String name = null;
    boolean selected = false;

    public Ressource(String code, String name, boolean selected) {
        super();
        this.code = code;
        this.name = name;
        this.selected = selected;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
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


    public static ArrayList<Ressource> getRessourceList() {
        ArrayList<Ressource> ressourceList = new ArrayList<Ressource>();
        Ressource res = new Ressource("3877", "M1 ILC", true);
        ressourceList.add(res);
        res = new Ressource("3822", "M1 ISI", false);
        ressourceList.add(res);
        res = new Ressource("4044", "M1 RISE", false);
        ressourceList.add(res);
        res = new Ressource("4100", "M2 ILC", false);
        ressourceList.add(res);
        res = new Ressource("19949", "M2 ISI", false);
        ressourceList.add(res);
        res = new Ressource("4094", "M2 RISE", false);
        ressourceList.add(res);
        res = new Ressource("5316", "J5", false);
        ressourceList.add(res);
        return ressourceList;
    }
}
