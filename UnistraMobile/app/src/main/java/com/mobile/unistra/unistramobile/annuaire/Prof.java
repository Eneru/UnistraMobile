package com.mobile.unistra.unistramobile.annuaire;

/**
 * Created by mataniere on 22/02/2015.
 */

import java.util.HashMap;

public class Prof
{
    private String identite;
    private String adresse;
    private String organisation;
    private String telephone;
    private String mail;
    private String fax;
    public Prof(String identite,String org,String adresse, String tel,String mail, String fax)
    {
        this.setIdentite(identite);
        this.adresse=adresse;
        this.organisation=org;
        this.telephone=tel;
        this.mail=mail;
        this.fax=fax;
    }

    public HashMap<String, String> getTable()
    {
        HashMap<String, String> table = new HashMap<String,String>();
        table.put("identite", identite);
        table.put("adresse",adresse);
        table.put("organisation",organisation);
        table.put("telephone",telephone);
        table.put("mail",mail);
        table.put("fax",fax);

        return table;
    }

    public String getTr()
    {
        return "<tr><td>"+identite+"</td><td>"+organisation+"</td><td>"+adresse+"</td><td>"+telephone+"</td><td>"+mail+"</td><td>"+fax+"</td></tr>";
    }

    public String getAdresse() {
        return adresse;
    }
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }
    public String getOrganisation() {
        return organisation;
    }
    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }
    public String getTelephone() {
        return telephone;
    }
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
    public String getMail() {
        return mail;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public String getFax() {
        return fax;
    }
    public void setFax(String fax) {
        this.fax = fax;
    }
    public String getIdentite() {
        return identite;
    }
    public void setIdentite(String identite) {
        this.identite = identite;
    }
}
