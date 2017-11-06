/*
 * 		Server2Server test
 * 		by JeeWook
 */
package com.soyoyoo.s2s;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
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

import javax.xml.bind.DatatypeConverter;

public class S2SiOSTest {
	public static void main(String[] args) throws Exception {
		

		CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

		// initialize the CSVParser object
		CSVParser parser;
		URI uri;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			parser = new CSVParser(
					new FileReader(
							"/Users/jeewook/Development/apache-jmeter-2.12/bin/remarketing-i2.txt"),
					format);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (CSVRecord record : parser) {
			params.clear();
			String muid = DatatypeConverter.printHexBinary( 
			           MessageDigest.getInstance("MD5").digest(record.get("muid").getBytes("UTF-8")));
			params.add(new BasicNameValuePair("muid", muid));
			params.add(new BasicNameValuePair("bundleid", record
					.get("bundleid")));
			params.add(new BasicNameValuePair("idtype", record.get("idtype")));
			params.add(new BasicNameValuePair("remarketing_only", record
					.get("remarketing_only")));
			params.add(new BasicNameValuePair("appversion", record
					.get("appversion")));
			params.add(new BasicNameValuePair("usage_tracking_enabled", record
					.get("usage_tracking_enabled")));
			params.add(new BasicNameValuePair("osversion", record
					.get("osversion")));
			params.add(new BasicNameValuePair("lat", record.get("lat")));
			params.add(new BasicNameValuePair("data.product_id", record
					.get("data.product_id")));
			params.add(new BasicNameValuePair("data.product_category", record
					.get("data.product_category")));
			params.add(new BasicNameValuePair("data.action_type", record
					.get("data.action_type")));

			try {
				uri = new URIBuilder().setScheme("https")
						.setHost("www.googleadservices.com")
						.setPath("/pagead/conversion/969704640/")
						.setParameters(params).build();

				HttpGet httpget = new HttpGet(uri);
				System.out.println(httpget.getURI());
				CloseableHttpResponse response = httpclient.execute(httpget);
				System.out.println(response.getStatusLine());
				response.close();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// close the parser
		parser.close();

	}
}
