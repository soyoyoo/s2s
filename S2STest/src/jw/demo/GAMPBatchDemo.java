/*  
 *  	GA Measurement Protocol demo
 * 		By JeeWook Kim
 * 
 * 
 * 		Samples are offered on as-is basis, and designed only to provide you with certain examples of how such code samples could be utilized.
 *      By implementing any of Samples, you agree to solely assume all responsibility for any consequences that arise from such implementation.
 *
 *	 	POST /batch HTTP/1.1
 * 		Host: www.google-analytics.com
 *	
 *   	send GA hits in batch using measurement protocol, for example (need to change the tid, cid, cd6, etc)
 *		v=1&tid=UA-54388314-3&cid=014951771.1499372497&t=event&ec=crm&ea=update&cd6=100&ni=1
 *		v=1&tid=UA-54388314-3&cid=103460135.1499090342&t=event&ec=crm&ea=update&cd6=90&ni=1
 *		v=1&tid=UA-54388314-3&cid=103579386.1499068372&t=event&ec=crm&ea=update&cd6=80&ni=1
 */

package jw.demo;

import java.net.*;

public class GAMPBatchDemo {

	public static void main(String[] args) {
		try {
			GAMPBatchDemo test = new GAMPBatchDemo();
		    test.sendSingleHit();
			test.sendBatchHits();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendBatchHits(){
		try {
			// need to change tid, cid, cdX on your own configuration
			StringBuffer sb = new StringBuffer("v=1&tid=UA-54388314-3&cid=014951771.1499372497&t=event&ec=crm&ea=update&cd6=100&ni=1\n");
			sb = sb.append(new StringBuffer("v=1&tid=UA-54388314-3&cid=103460135.1499090342&t=event&ec=crm&ea=update&cd6=90&ni=1\n"));
			sb = sb.append(new StringBuffer("v=1&tid=UA-54388314-3&cid=103579386.1499068372&t=event&ec=crm&ea=update&cd6=80&ni=1\n"));
			sb = sb.append(new StringBuffer("v=1&tid=UA-54388314-3&cid=1054710447.1498867245&t=event&ec=crm&ea=update&cd6=70&ni=1\n"));
			sb = sb.append(new StringBuffer("v=1&tid=UA-54388314-3&cid=1055526594.1499084365&t=event&ec=crm&ea=update&cd6=60&ni=1\n"));
			
			String hits = sb.toString();
			System.out.println("hits to send:\n" + hits);
			URL batchUrl = new URL(
					"https://www.google-analytics.com/batch");
			HttpURLConnection con = (HttpURLConnection) batchUrl.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.getOutputStream().write(hits.getBytes("UTF-8"));
	        int httpResult = con.getResponseCode();
			System.out.println("Http Result:" + httpResult);
			con.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendSingleHit() {
		try {
			// need to change tid, cid, cdX on your own configuration
		String hit = new String("v=1&tid=UA-54388314-3&cid=014951771.1499372497&t=event&ec=crm&ea=update&cd6=100&ni=1");
		
		System.out.println("hit to send:\n" + hit);
		URL collectUrl = new URL(
				"https://www.google-analytics.com/collect");
		HttpURLConnection con2 = (HttpURLConnection) collectUrl.openConnection();
		con2.setRequestMethod("POST");
		con2.setDoOutput(true);
		con2.getOutputStream().write(hit.getBytes("UTF-8"));
        int httpResult2 = con2.getResponseCode();
		System.out.println("Http Result:" + httpResult2);
		con2.disconnect();
	} catch (Exception e) {
		e.printStackTrace();
	}
	}
}
