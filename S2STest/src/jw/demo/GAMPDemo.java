/*
 * 		GA Measurement Protocol demo
 * 		Read CRM data from a csv file and send hits using measurement protocol
 * 		by JeeWook Kim
 *
 * 		Samples are offered on as-is basis, and designed only to provide you with certain examples of how such code samples could be utilized.
 *      By implementing any of Samples, you agree to solely assume all responsibility for any consequences that arise from such implementation.
 * 
 */
package jw.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.IOUtils;

public class GAMPDemo {
	public static void main(String[] args) throws Exception {
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

		// initialize the CSVParser object
		CSVParser parser;
		URI uri;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		// read CRM data from a CSV file
		String homepath = System.getProperty("user.home");
		try {
			parser = new CSVParser(
					new FileReader(
							homepath+"/cid-ltv-sessions-20170916.csv"),
					format); // need to change to your own CRM csv file
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			throw e1;
		} catch (IOException e1) {
			e1.printStackTrace();
			throw e1;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (CSVRecord record : parser) {
			// prepare name value parameters to send using http post
			params.clear();
			params.add(new BasicNameValuePair("v", "1"));
			params.add(new BasicNameValuePair("tid", "UA-54388314-3")); // need to change to your tracking id
			params.add(new BasicNameValuePair("cid", record.get("cid")));
			params.add(new BasicNameValuePair("t", "event"));
			params.add(new BasicNameValuePair("ec", "crm"));
			params.add(new BasicNameValuePair("ea", "update"));
			params.add(new BasicNameValuePair("cd6", record.get("ltv"))); // set value to custom dimension 6
			params.add(new BasicNameValuePair("ni", "1"));
			// send GA hits using measurement protocol
			try {
				uri = new URIBuilder().setScheme("https")
						.setHost("www.google-analytics.com")
						.setPath("/collect").build(); 
				HttpPost httppost = new HttpPost(uri);
				System.out.println(httppost.getURI());
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
				System.out.println(entity.toString());
				System.out.println(IOUtils.toString(entity.getContent(),"UTF-8")); 
				httppost.setEntity(entity);
				
				CloseableHttpResponse response = httpclient.execute(httppost);
				System.out.println(response.getStatusLine());
				response.close();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		// close the parser
		parser.close();

	}
}
