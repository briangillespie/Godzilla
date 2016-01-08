import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class RestrictCheckIn {
	
	// CONSTANTS
	private final String USER_AGENT = "Google Chrome/45.0.2454.85";
	private final String USER_TOKEN = "7a38ee6c5faf1e2756799fcc71e6d805";
	private final static DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private final int MAX_HOURS=5;
	private final int STAFF_MAX_HOURS=24;
	private final LocalTime END_TIME=new LocalTime(22,0);
	private final int PAGES=3;
	private final int FAC_STAFF_GROUPID=32954;

	public static void main(String[] args) throws Exception {

		RestrictCheckIn restrict = new RestrictCheckIn();
		restrict.restrictAllToFiveHours();
		//System.out.println(restrict.isAssetCheckedOut(10));
		//DateTime test=FORMAT.parseDateTime("2015-09-11 17:29:00");
		//restrict.setCheckOutDateTime(10,test);
		//String test=restrict.getAssetDetails(3,"checkin_due_on");
		//DateTime checkOutDateTime=FORMAT.parseDateTime(test);
//		DateTime currentCheckInDateTime=null;
//		DateTime checkInDateTime=FORMAT.parseDateTime("2015-08-28 17:31:51");
//		if(currentCheckInDateTime==null || currentCheckInDateTime.compareTo(checkInDateTime)==1){
//			int a=10;
//		}
		//int test=currentCheckInDateTime.compareTo(checkInDateTime);
		//ArrayList<Integer> test1=restrict.getAllActiveAssetIDs();
		//int s=10;
	}
	
	private void restrictAllToFiveHours() throws Exception {
		ArrayList<Integer> allAssetIDs=getAllActiveAssetIDs();
		int listSize=allAssetIDs.size();
		
		for(int i=0; i<listSize; i++){

			Asset thisAsset =new Asset();
			thisAsset.setAssetID(allAssetIDs.get(i));
			getAndUpdateAssetDetails(thisAsset);
			
			DateTime currentCheckInDateTime=thisAsset.getCheckInDateTime();
			if(thisAsset.getState().equals("checked_out") && // set check-in time only for assets checked out
					thisAsset.getGroupID()!=FAC_STAFF_GROUPID){ // do not set check-in time for fac-staff assets
				DateTime checkOutDateTime=thisAsset.getCheckOutDateTime();
				DateTime maxCheckInDateTime=checkOutDateTime.plusHours(MAX_HOURS);
				DateTime endDateTime=checkOutDateTime.withTime(END_TIME.getHourOfDay(),
						END_TIME.getMinuteOfHour(),END_TIME.getSecondOfMinute(),
						END_TIME.getMillisOfSecond());
				
				DateTime checkInDateTime=new DateTime();
				if(endDateTime.compareTo(maxCheckInDateTime)==1){
					checkInDateTime=maxCheckInDateTime;
				} else {
					checkInDateTime=endDateTime;
				}
				
				if(currentCheckInDateTime==null || //if currentCheckInDateTime==null then the second clause crashes
						currentCheckInDateTime.compareTo(checkInDateTime)==1 || // set check-in time only if the current check-in time is more than 5 hrs of the checkout time
						checkOutDateTime.compareTo(currentCheckInDateTime)==1){ // set check-in time if current check-in time is before the check-out time
						setCheckOutDateTime(thisAsset.getAssetID(),checkInDateTime);
				}
			} else if(thisAsset.getState().equals("checked_out") && 
					thisAsset.getGroupID()==FAC_STAFF_GROUPID) {
				DateTime checkOutDateTime=thisAsset.getCheckOutDateTime();
				DateTime staffCheckOutDateTime=checkOutDateTime.plusHours(STAFF_MAX_HOURS);
				if(currentCheckInDateTime==null || currentCheckInDateTime.compareTo(staffCheckOutDateTime)==1)
					setCheckOutDateTime(thisAsset.getAssetID(),staffCheckOutDateTime);
			}
		}
	}
	
	// API Utilities
	// curl command for getting asset details:
    // curl  -H "token:<COMPANY_TOKEN>" -X GET \
    // https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>.api
	public void getAndUpdateAssetDetails(Asset thisAsset) throws Exception {
		int assetID=thisAsset.getAssetID();
		
		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "assets/"+assetID+".api";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// request method
		con.setRequestMethod("GET");

		// request headers
		con.setRequestProperty("token", USER_TOKEN);
		con.setRequestProperty("User-Agent", USER_AGENT);

		// reading input stream
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		String response="";
		while ((inputLine = in.readLine()) != null) {
			response+=inputLine;
		}
		in.close();
		
		// Iterating different fields of the JSON
		JsonFactory jsonFactory = new JsonFactory();
	    JsonParser parser = jsonFactory.createParser(response);

	    while (parser.nextToken() != JsonToken.END_OBJECT) { 
	    	String token = parser.getText();
	    	
	    	if ("state".equals(token)) { 
	    		parser.nextToken();
	    		thisAsset.setState(parser.getValueAsString());
	    	} else if("group_id".equals(token)) { 
	    		parser.nextToken();
	    		thisAsset.setGroupID(parser.getValueAsInt());
	    	} else if("checkin_due_on".equals(token)) { 
	    		parser.nextToken();
	    		String checkInDateTimeStr=parser.getValueAsString();
	    		if(checkInDateTimeStr!=null){
	    			DateTime checkInDateTime=FORMAT.parseDateTime(checkInDateTimeStr);
	    			thisAsset.setCheckInDateTime(checkInDateTime);
	    		}
	    	} else if("checkout_on".equals(token)) { 
	    		parser.nextToken();
	    		String checkOutDateTimeStr=parser.getValueAsString();
	    		if(checkOutDateTimeStr!=null){
	    			DateTime checkOutDateTime=FORMAT.parseDateTime(checkOutDateTimeStr);
	    			thisAsset.setCheckOutDateTime(checkOutDateTime);
	    		}
	    	}
	    }
	}
	
	
	// curl command for extending checkout:
	// curl  -H "token:<COMPANY_TOKEN>" -X PUT \
    // -d "till=<TILL_DATE AND TIME>" \
    // https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>/extend_checkout.api
	private void setCheckOutDateTime(int assetID, DateTime checkInDateTime) throws Exception {
		
		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "assets/"+assetID+"/extend_checkout.api";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setDoOutput(true);
		// request method
		con.setRequestMethod("PUT");

		// request headers
		con.setRequestProperty("token", USER_TOKEN);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		String checkInDateTimeStr="";
		String data="";
		
		checkInDateTimeStr=checkInDateTime.toString(FORMAT);
		data= "till="+checkInDateTimeStr;
		 
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

        writer.write(data);
        writer.close();
        
        con.getResponseCode();

	}
	
	// curl command for getting all asset details:
	// curl  -H "token:<COMPANY_TOKEN>" -X GET \
    // https://<SUBDOMAIN>.ezofficeinventory.com/assets.api?page=<PAGE_NUM | DEFAULT = 1>
	public ArrayList<Integer> getAllActiveAssetIDs() throws Exception {
		ArrayList<Integer> allAssetIDs=new ArrayList<Integer>();
		for(int i=1;i<=PAGES;i++){
			String url = "https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page="+i;
			
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// request method
			con.setRequestMethod("GET");

			// request headers
			con.setRequestProperty("token", USER_TOKEN);
			con.setRequestProperty("User-Agent", USER_AGENT);

			// reading input stream
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			String response="";
			while ((inputLine = in.readLine()) != null) {
				response+=inputLine;
			}
			in.close();
			
			JsonFactory jsonFactory = new JsonFactory();
		    JsonParser parser = jsonFactory.createParser(response);

		    while (parser.nextToken() != JsonToken.END_ARRAY) { 
		    	String token = parser.getText();
		    	if ("sequence_num".equals(token)) { 
		    		parser.nextToken();
		    		allAssetIDs.add(parser.getValueAsInt());
		    	}
		    }
		}
	    return allAssetIDs;
	}
}
