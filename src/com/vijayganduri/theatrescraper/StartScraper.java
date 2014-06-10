package com.vijayganduri.theatrescraper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class StartScraper {

	private static final String TAG = StartScraper.class.getName();

	private static final String URL = "http://movies.sulekha.com/movie-theaters";

	private static final int LAST_PAGE_NO = 450;//TODO make this dynamic

	private static final String START_TAG = "<ul class=\"twocol theatrelistli floatbugfix\">";
	private static final String END_TAG = "</ul><br clear=\"all\"><br><div id=\"page_navi\">";

	public static void main(String []args){

		println("Start extracting...");

		ReadWriteExcelFile exlFile = new ReadWriteExcelFile();
		int lastPosition = exlFile.getLastSavedPosition();

		println("Last saved position : "+lastPosition);

		pullContent(lastPosition+1, URL);
		println("Operation finished!!");
	}


	private static void pullContent(int i, String url){
		if(i>LAST_PAGE_NO){
			println("All Pages Extracted.");
			return;
		}

		if(i!=1){//exception for first page
			url = String.format("%s_%s", url,i);
		}

		println("   Extracting page "+i+" of "+LAST_PAGE_NO);

		HttpClient httpclient = new DefaultHttpClient();
		HttpContext httpcontext = new BasicHttpContext();
		HttpGet httpget = new HttpGet(url);

		try {
			HttpResponse response = httpclient.execute(httpget, httpcontext);

			BufferedReader b_reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuffer sb = new StringBuffer();

			String line = null;
			while((line = b_reader.readLine()) != null){
				sb.append(line + "\n");
			}

			String matchFound = StringUtils.substringBetween(sb.toString(), START_TAG, END_TAG);

			if(matchFound!=null){

				Document doc = Jsoup.parse(matchFound);

				Elements elements = doc.select("li");
				ArrayList<Theatre> theatres = new ArrayList<Theatre>();

				for(Element element : elements){
					try{
						Theatre theatre = new Theatre();
						//----Theatre ----
						for(Element e : element.getElementsByClass("subhead1")){
							theatre.setTheatre(e.text());
							break;
						}
						//----Address----;
						for(Element el : element.getElementsByClass("postedtime")){
							theatre.setAddress(el.text());						
							Elements sel = el.select("a[href]");
							theatre.setPlace(sel.get(0).text());
							if(sel.size()>1)
								theatre.setCountry(sel.get(1).text());
							break;
						}
						//----Phone----;
						for(Element e : element.getElementsByClass("smalltxt")){
							if(e.text().contains("Phone: ")){						
								theatre.setPhone(e.text().substring(7));
								break;
							}
						}
						theatres.add(theatre);
					}catch(Exception e){
						errprintln(TAG+" exception in single item e "+e);
						errprintln(TAG+" Skipped a row in page "+i+" with name : "+element.getElementsByClass("subhead1").get(0).text());
					}
				}

				ReadWriteExcelFile exlFile = new ReadWriteExcelFile();
				exlFile.createFile(i, theatres);
			}

		} catch (Exception e) {
			errprintln(TAG+";  Exception : "+e);
			errprintln(TAG+";  Fail!! Abort operation");
			return;
		}

		i++;
		pullContent(i, URL);		
	}


	private static void println(String msg){
		System.out.println(msg);
	}

	private static void errprintln(String msg){
		System.err.println(msg);
	}

}
