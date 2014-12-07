package ent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Annuaire extends Wget
{
	private ArrayList<Prof> annuaire;
	public Annuaire(String prenom, String nom, String structure, String fonction, String num) throws ClientProtocolException, URISyntaxException, IOException
	{
		super("http://annuaire.unistra.fr/chercher?n="+nom+"&p="+prenom+"&s="+structure+"&f="+fonction+"&t="+num);
		Document doc = Jsoup.parse(this.text);
		Element resultat = doc.getElementById("annuaire_resultats");
		Elements li = resultat.getElementsByTag("li");
		for(int i=0;i<li.size();i++)
		{
			Element identiteProf = li.get(i).getElementsByClass("a_lp").get(0);
			Element mailProf = li.get(i).getElementsByTag("a").get(1);
			Element orgProf = li.get(i).getElementsByClass("a_org").get(0);
			Element adresseProf = li.get(i).getElementsByClass("a_adresse a_icone").get(0);
			Element telephoneProf = li.get(i).getElementsByClass("a_telephone a_icone").get(0);
			Element faxProf = li.get(i).getElementsByClass("a_fax a_icone").get(0);
			annuaire.add(new Prof(identiteProf.html(),orgProf.html(),adresseProf.html(),telephoneProf.html(),mailProf.html(),faxProf.html()));
						
		}
	}
	
	public Prof getProf(int i)
	{
		return annuaire.get(i);
	}
	
	public ArrayList<Prof> getAnnuaire()
	{
		return this.annuaire;
	}
	
	public String getTableHtml()
	{
		String s="<table><tr><th>Identité</th><th>Organisation</th><th>Adresse</th><th>Téléphone</th><th>Mail</th><th>Fax</th></tr>";
		for(int i=0;i<annuaire.size();i++)
		{
			s+=annuaire.get(i).getTr();
		}
		s+="</table>";
		
		return s;
	}
}
