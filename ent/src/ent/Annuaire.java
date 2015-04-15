package ent;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Annuaire extends Wget
{
	private ArrayList<Prof> annuaire;
	private boolean trouver;
	public Annuaire(String prenom, String nom, String structure, String fonction, String num) throws ClientProtocolException, URISyntaxException, IOException
	{
		super("http://annuaire.unistra.fr/chercher?n="+nom+"&p="+prenom+"&s="+structure+"&f="+fonction+"&t="+num);
		System.out.println(this.text);
		annuaire = new ArrayList<Prof>();
		Document doc = Jsoup.parse(this.text);
		
		Element divNbresultat = doc.getElementById("a_resultats");
		
		Elements nbresultat = divNbresultat.getElementsByTag("p");
		
		String phrasenbresultat = nbresultat.get(0).html();
		
		Pattern p = Pattern.compile("[0-9]+ .+");
		
		Matcher m = p.matcher(phrasenbresultat);
		boolean result = m.matches();
		if(result)
		{
			Element resultat = doc.getElementById("annuaire_resultats");
			Elements li = resultat.getElementsByTag("li");
			for(int i=0;i<li.size();i++)
			{
				Element identiteProf = li.get(i).getElementsByClass("a_lp").get(0);
				Elements groupmailProf = li.get(i).getElementsByTag("a");
				String mailProf="";
				if(!(groupmailProf.size()==0))
				{
					mailProf=groupmailProf.get(1).html();
				}
				
				Elements grouporgProf = li.get(i).getElementsByClass("a_org");
				String orgProf="";
				if(!(grouporgProf.size()==0))
				{
					orgProf=grouporgProf.get(0).html();
				}
				
				Elements groupadresseProf = li.get(i).getElementsByClass("a_adresse");
				String adresseProf="";
				if(!(groupadresseProf.size()==0))
				{
					adresseProf=groupadresseProf.get(0).html();
				}
				
				Elements grouptelephoneProf = li.get(i).getElementsByClass("a_telephone");
				String telephoneProf="";
				if(!(grouptelephoneProf.size()==0))
				{
					telephoneProf=grouptelephoneProf.get(0).html();
				}
				
				Elements groupfaxProf = li.get(i).getElementsByClass("a_fax");
				String faxProf;
				if(groupfaxProf.size()==0)
				{
					faxProf="";
				}
				else
				{
					faxProf=groupfaxProf.get(0).html();
				}
				annuaire.add(new Prof(identiteProf.html(),orgProf,adresseProf,telephoneProf,mailProf,faxProf));
				trouver=true;
			}
		}
		else
		{
			trouver=false;
		}
	}
	
	public Prof getProf(int i) throws NoResultException
	{
		if(trouver)
		{
			return annuaire.get(i);
		}
		else
		{
			throw new NoResultException(); 
		}
		
	}
	
	public ArrayList<Prof> getAnnuaire() throws NoResultException
	{
		if(trouver)
		{
			return this.annuaire;
		}
		else
		{
			throw new NoResultException();
		}
	}
	
	public String getTableHtml() throws NoResultException
	{
		if(trouver)
		{
			String s="<table><tr><th>Identité</th><th>Organisation</th><th>Adresse</th><th>Téléphone</th><th>Mail</th><th>Fax</th></tr>";
			for(int i=0;i<annuaire.size();i++)
			{
				s+=annuaire.get(i).getTr();
			}
			s+="</table>";
		
			return s;
		}
		else
		{
			throw new NoResultException();
		}
		
	}
}
