/*
 * 		Server2Server demo
 * 		by JeeWook Kim
 */
package jw.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

public class S2SDemo {
	public static void main(String[] args) throws Exception {
		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

		// initialize the CSVParser object
		CSVParser parser;
		URI uri;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			String homepath = System.getProperty("user.home");
			parser = new CSVParser(
					new FileReader(
							homepath+"/Development/apache-jmeter-2.12/bin/remarketing-a3.txt"),
					format);
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
			throw e1;
		} catch (IOException e1) {
			
			e1.printStackTrace();
			throw e1;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (CSVRecord record : parser) {
			params.clear();
			params.add(new BasicNameValuePair("rdid", record.get("rdid")));
			params.add(new BasicNameValuePair("bundleid", record
					.get("bundleid")));
			params.add(new BasicNameValuePair("idtype", record.get("idtype")));
			params.add(new BasicNameValuePair("remarketing_only", record
					.get("remarketing_only")));
			params.add(new BasicNameValuePair("appversion", record
					.get("appversion")));
			params.add(new BasicNameValuePair("osversion", record
					.get("osversion")));
			params.add(new BasicNameValuePair("lat", record.get("lat")));
			params.add(new BasicNameValuePair("data.product_id", record
					.get("data.product_id")));
			params.add(new BasicNameValuePair("data.product_category", record
					.get("data.product_category")));
			params.add(new BasicNameValuePair("data.action_type", record
					.get("data.action_type")));
			params.add(new BasicNameValuePair("data.download_date", record
					.get("data.download_date")));
			params.add(new BasicNameValuePair("data.purchase_date", record
					.get("data.purchase_date")));
			params.add(new BasicNameValuePair("usage_tracking_enabled", "1"));

			try {
				uri = new URIBuilder().setScheme("https")
						.setHost("www.googleadservices.com")
						.setPath("/pagead/conversion/969704640/")
						.setParameters(params).build();  // need to change your Conversion ID of AdWords account
				HttpGet httpget = new HttpGet(uri);
				System.out.println(httpget.getURI());
				CloseableHttpResponse response = httpclient.execute(httpget);
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
