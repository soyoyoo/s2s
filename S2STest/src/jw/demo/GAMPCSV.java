/*
 * 		GA Measurement Protocol demo
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
		options.addOption("file", true, "csv input file path");
		options.addOption("cid", true, "cid field name in a csv input file");
		options.addOption("tid", true, "GA property tracking ID");
		options.addOption("ec", true, "event category");
		options.addOption("ea", true, "event action");
		options.addOption("dh", true, "document host name");
		options.addOption("dp", true, "document path");
		for (int i=1; i<= 200; i++) {
			options.addOption("cd"+i, "dimension"+i, true, "custom dimension index "+i);
		}
        HelpFormatter formatter = new HelpFormatter();
        String file = null;
        String cid = null;
        String tid = null;
        String ec = null;
        String ea = null;
        String [] cd = new String[201];
        String dh = null;
        String dp = null;
        int count =1;
        try {
        	CommandLine line = clparser.parse( options, args );
        	if( line.hasOption( "file" ) ) {
        		file = line.getOptionValue("file");
        		System.out.println("file="+file);        		
        	} else throw new Exception("no value for argument -file");
        	if( line.hasOption( "cid" ) ) {
        		cid = line.getOptionValue("cid");
        		System.out.println("cid="+cid);
        	} else throw new Exception("no value for argument -field");
        	if( line.hasOption( "tid" ) ) {
        		tid = line.getOptionValue("tid");
        		System.out.println("tid="+tid);
        	} else throw new Exception("no value for argument -property");
            if( line.hasOption( "ec" ) ) {
            	ec = line.getOptionValue("ec"); 
            	System.out.println("ec="+ec);
            } else throw new Exception("no value for argument -ec");
            if( line.hasOption( "ea" ) ) {
            	ea = line.getOptionValue("ea"); 
            	System.out.println("ea="+ea);
            } else throw new Exception("no value for argument -ea");
            for (int i=1; i<= 200; i++) {
            	if( line.hasOption( "cd"+i ) ) {
            		cd[i] = line.getOptionValue("cd"+i); 
            		System.out.println("cd"+i+"="+cd[i]);
            	}
            }  
            if( line.hasOption( "dh" ) ) {
            	dh = line.getOptionValue("dh"); 
            	System.out.println("dh="+dh);
            }
            if( line.hasOption( "dp" ) ) {
            	dp = line.getOptionValue("dp"); 
            	System.out.println("dp="+dp);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            formatter.printHelp("java GAMPCSV", options);
            System.exit(1);
        }

        CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(',');
		CSVParser parser;
		URI uri;
		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
		CloseableHttpClient httpclient = HttpClients.custom()
                .setUserAgent(userAgent)
                .build();
			
		try {
			parser = new CSVParser(new FileReader(file), format);
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
			params.add(new BasicNameValuePair("tid", tid));
			params.add(new BasicNameValuePair("cid", record.get(cid)));
			params.add(new BasicNameValuePair("t", "event"));
			params.add(new BasicNameValuePair("ec", ec));
			params.add(new BasicNameValuePair("ea", ea));
			for (int i=1; i<= 200; i++) {
				if (cd[i] != null)
					params.add(new BasicNameValuePair("cd"+i, cd[i]));	
			}
			params.add(new BasicNameValuePair("ni", "1"));
			if (dh != null) params.add(new BasicNameValuePair("dh", dh));
			if (dp != null) params.add(new BasicNameValuePair("dp", dp));
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
		if (seconds == 0) seconds = 1;
		System.out.println("duration(sec)="+seconds);
		System.out.println("requests/sec="+(count-1)/seconds);
		// close the parser
		parser.close();

	}
}
