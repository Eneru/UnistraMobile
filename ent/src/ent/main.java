package ent;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub,
		try {
			//new Ent("buckenmeier","ckcompoat@24");
			//Wget wget=new Wget("https://adewebcons.unistra.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=17765,27693,22056,21994,4307,22022,22002,4312,4311,4101,30935,26993&projectId=5&calType=ical&nbWeeks=4");
			//System.out.println(wget.getText());
			Annuaire annu = new Annuaire("", "N", "", "", "");
			
			System.out.println(annu.getTableHtml());
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
