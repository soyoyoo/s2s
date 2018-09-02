/*
 * 		GA Measurement Protocol demo
 * 		by JeeWook Kim
 */
package jw.demo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
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
		options.addOption("e", "category", true, "event category");
		options.addOption("a", "action", true, "event action");
		options.addOption("d", "dimension", true, "custom dimension index");
		options.addOption("v", "value", true, "custom dimension value");
		
        HelpFormatter formatter = new HelpFormatter();
        String filepath = null;
        String cidcolumn = null;
        String property = null;
        String eventCategory = null;
        String eventAction = null;
        String cdindex = null;
        String cdvalue = null;
        int count =1;
        try {
        	CommandLine line = clparser.parse( options, args );
        	if( line.hasOption( "input" ) ) {
        		filepath = line.getOptionValue("input");
        		System.out.println("filepath="+filepath);        		
        	} else throw new Exception("no value for argument --input");
        	if( line.hasOption( "column" ) ) {
        		cidcolumn = line.getOptionValue("column");
        		System.out.println("cidcolumn="+cidcolumn);
        		
        	} else throw new Exception("no value for argument --column");
        	if( line.hasOption( "property" ) ) {
        		property = line.getOptionValue("property");
        		System.out.println("property="+property);
        	} else throw new Exception("no value for argument --property");
            if( line.hasOption( "category" ) ) {
            	eventCategory = line.getOptionValue("category"); 
            	System.out.println("eventCategory="+eventCategory);
            } else throw new Exception("no value for argument --category");
            if( line.hasOption( "action" ) ) {
            	eventAction = line.getOptionValue("action"); 
            	System.out.println("eventAction="+eventAction);
            } else throw new Exception("no value for argument --action");
            if( line.hasOption( "dimension" ) ) {
            	cdindex = line.getOptionValue("dimension"); 
            	System.out.println("cdindex="+cdindex);
            }
            if( line.hasOption( "value" ) ) {
            	cdvalue = line.getOptionValue("value"); 
            	System.out.println("cdvalue="+cdvalue);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java GAMPCSV", options);
            System.exit(1);
        }

        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
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
		Date start = new Date();
		System.out.println("start time="+start.toString());
		for (CSVRecord record : parser) {
			params.clear();
			params.add(new BasicNameValuePair("v", "1"));
			params.add(new BasicNameValuePair("tid", property));
			params.add(new BasicNameValuePair("cid", record.get(cidcolumn)));
			params.add(new BasicNameValuePair("t", "event"));
			params.add(new BasicNameValuePair("ec", eventCategory));
			params.add(new BasicNameValuePair("ea", eventAction));
			if (cdindex != null && cdvalue != null)
				params.add(new BasicNameValuePair("cd"+cdindex, cdvalue));			
			params.add(new BasicNameValuePair("ni", "1"));

			try {
				uri = new URIBuilder().setScheme("https")
						.setHost("www.google-analytics.com")
						.setPath("/collect").build(); 
				HttpPost httppost = new HttpPost(uri);
				System.out.println("count="+count);
				System.out.println(httppost.getURI());
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
				System.out.println(entity.toString());
				System.out.println(IOUtils.toString(entity.getContent(),"UTF-8")); 
				httppost.setEntity(entity);
				
				CloseableHttpResponse response = httpclient.execute(httppost);
				System.out.println(response.getStatusLine());
				response.close();
				count ++;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} // for
		Date end = new Date();
		System.out.println("end time="+end.toString());
		long seconds = (end.getTime()-start.getTime())/1000;
		System.out.println("duration(sec)="+seconds);
		System.out.println("requests/sec="+count/seconds);
		// close the parser
		parser.close();

	}
}
