package ent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class Wget
{
	private PoolingHttpClientConnectionManager cm;
	private URIBuilder requestBuilder;
	private String text;

	public Wget(String url) throws ClientProtocolException, URISyntaxException, IOException
	{
		
		 cm = new PoolingHttpClientConnectionManager();
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
		requestBuilder= new URIBuilder();
		
		// protocole
		String temp[]=url.split("://");
		String protocole = temp[0];
		
		//host
		temp=temp[1].split("/");
				
		String host =temp[0];
		
		//path
		temp=temp[1].split("?");
		
		String path = temp[0];
		
		//param
		
		String params=temp[1];
		
		requestBuilder.setScheme("https");
		requestBuilder.setHost("adewebcons.unistra.fr");
		requestBuilder.setPath("/jsp/custom/modules/plannings/anonymous_cal.jsp");
		requestBuilder.addParameter("resources","17765,27693,22056,21994,4307,22022,22002,4312,4311,4102,30935,26993");
		requestBuilder.addParameter("projectId", "5");
		requestBuilder.addParameter("calType", "ical");
		requestBuilder.addParameter("nbWeeks", "4");
		text = this.execute(requestBuilder);
	}
	
	private String execute(URIBuilder urlbuilder) throws URISyntaxException, ClientProtocolException, IOException
	{
		URI url = urlbuilder.build();
		HttpUriRequest request = new HttpPost(url);
	//	request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
		CloseableHttpClient client = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();
		client.execute(request);
		HttpResponse response = client.execute(request);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String s = new String();
		String html = new String();
		  while(( s = reader.readLine())!=null)
		  {
			 html+=s;
		  }
		  
		  return html;
	}
	
	public String getText()
	{
		return text;
	}
}
