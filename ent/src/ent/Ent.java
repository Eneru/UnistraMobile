package ent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




public class Ent {
	
	private String password;
	private String username;
	private URI url;
	private String response;
	private HttpGet request;
	private URIBuilder requestBuilder;
	private HttpContext context;
	private PoolingHttpClientConnectionManager cm;
	
	public Ent(String username, String password) throws ClientProtocolException, URISyntaxException, IOException
	{
		this.password=password;
		this.username=username;
		 cm = new PoolingHttpClientConnectionManager();
		// Increase max total connection to 200
		cm.setMaxTotal(200);
		// Increase default max connection per route to 20
		cm.setDefaultMaxPerRoute(20);
		requestBuilder= new URIBuilder();
		
		this.connect(username,password);
		requestBuilder.setScheme("https");
		requestBuilder.setHost("ent.unistra.fr");
		System.out.println(this.execute(requestBuilder));
		
	}
	
	private String execute(URIBuilder urlbuilder) throws URISyntaxException, ClientProtocolException, IOException
	{
		url =  urlbuilder.build();
		HttpUriRequest request = new HttpPost(url);
		CloseableHttpClient client = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();
		client.execute(request, this.context);
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
	// authentification on ENT
	private boolean connect(String id, String passwors) throws URISyntaxException, ClientProtocolException, IOException
	{
		// get the authentification form
		requestBuilder.setScheme("https");
		requestBuilder.setHost("cas.unistra.fr");
		requestBuilder.setPath("/cas/login");
		this.url = this.requestBuilder.build();
		CloseableHttpClient httpclient = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();
		HttpUriRequest request = new HttpPost(url);
		HttpResponse response = httpclient.execute(request);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String s = new String();
		String html = new String();
		  while(( s = reader.readLine())!=null)
		  {
			 html+=s;
		  }
		  
		  // I search the session Id
		  String sessionId = response.getFirstHeader("Set-Cookie").getValue();
		 System.out.println(sessionId);
		 
		  //Now i have the origin form I build my post request
		  Document doc = Jsoup.parse(html);
		  Elements form = doc.getElementsByTag("form");
		  Element user= form.get(0).getElementById("username");
		  Element pass= form.get(0).getElementById("password");
		  user.attr("value","buckenmeier");
		  pass.attr("value","test");
		  Elements inputs = form.get(0).getElementsByTag("input");
		  URIBuilder localBuilder= this.requestBuilder;
		  for(int i =0;i<inputs.size();i++)
		  {
			  Element el = inputs.get(i);
			  localBuilder.addParameter(el.attr("name"),el.attr("value"));
		  }
		  // I send a cookie with the session id for persistant connection
		  
		  String numSession=sessionId.split(";")[0].split("=")[1];
		  CookieStore cookieStore = new BasicCookieStore();
		  Cookie session = new BasicClientCookie("JSESSIONID",numSession);
		  cookieStore.addCookie(session);
		  HttpContext context = new BasicHttpContext();
		  context.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		  
		  this.url= localBuilder.build();
		  request = new HttpPost(url);
		  response = httpclient.execute(request,context);
		  reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		  s = new String();
		  html = new String();
			  while(( s = reader.readLine())!=null)
			  {
				 html+=s;
			  }
			  doc = Jsoup.parse(html);
			  Element err = doc.getElementById("status");
			  Boolean connect = true;
			  
			  if(err!=null)
			  {
				  if(err.attr("class").equals("errors"))
				  {
					  connect = false;
				  }
			  }
			 
			  System.out.println(connect+" "+html);
			 
			 
		 return connect;
	}
	
	
	
	
}
