/*
 * 		GA Measurement Protocol demo
 * 		by JeeWook Kim
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
import org.apache.commons.cli.*;
import org.apache.commons.io.IOUtils;

public class GAMPCSV {
	public static void main(String[] args) throws Exception {
		// create the command line parser
		CommandLineParser clparser = new DefaultParser();

		// create the Options
		Options options = new Options();
		options.addOption("i", "input", true, "csv input file path");
		options.addOption("c", "column", true, "cid column name");
		options.addOption("p", "property", true, "GA property tracking ID");
		options.addOption("ec", "category", false, "event category");
		options.addOption("ea", "action", false, "event action");
		options.addOption("cd", "dimension", false, "custom dimension index");
		options.addOption("v", "value", false, "custom dimension value");
		
        HelpFormatter formatter = new HelpFormatter();
        String filepath = "";
        String cidcolumn = "";
        String property = "";
        String eventCategory = null;
        String eventAction = null;
        String cdindex = null;
        String cdvalue = null;
        
        try {
        	CommandLine line = clparser.parse( options, args );
            filepath = line.getOptionValue("input");
            cidcolumn = line.getOptionValue("column");
            property = line.getOptionValue("property");
            if( line.hasOption( "category" ) ) {
            	eventCategory = line.getOptionValue("category"); 
            }
            if( line.hasOption( "action" ) ) {
            	eventAction = line.getOptionValue("action"); 
            }
            if( line.hasOption( "dimension" ) ) {
            	cdindex = line.getOptionValue("dimension"); 
            }
            if( line.hasOption( "value" ) ) {
            	cdvalue = line.getOptionValue("value"); 
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("GAMPCSV", options);
            System.exit(1);
        }

        
        System.out.println(filepath);
        System.out.println(cidcolumn);
        System.out.println(property);

        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');

		// initialize the CSVParser object
		CSVParser parser;
		URI uri;
		CloseableHttpClient httpclient = HttpClients.createDefault();

		try {
			
			parser = new CSVParser(
					new FileReader(
							filepath),
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
			params.add(new BasicNameValuePair("v", "1"));
			params.add(new BasicNameValuePair("tid", property));
			params.add(new BasicNameValuePair("cid", record.get(cidcolumn)));
			params.add(new BasicNameValuePair("t", "event"));
			if (eventCategory != null)
				params.add(new BasicNameValuePair("ec", eventCategory));
			if (eventAction != null)
				params.add(new BasicNameValuePair("ea", eventAction));
			if (cdindex != null && cdvalue != null)
				params.add(new BasicNameValuePair("cd"+cdindex, cdvalue));			
			params.add(new BasicNameValuePair("ni", "1"));

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
