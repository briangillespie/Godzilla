import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;





import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class PenalizeLateCheckIn {
	
	//Constants
	private final String USER_AGENT = "Google Chrome/45.0.2454.85";
	private final String USER_TOKEN = "7a38ee6c5faf1e2756799fcc71e6d805";
	private final static DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private final int MAX_HOURS=5;
	private final int GRACE_TIME_MINUTES=15;
	private final LocalTime END_TIME=new LocalTime(22,0);
	private final int FAC_STAFF_GROUPID=32954;
	
	public static void main(String[] args) throws Exception {
		PenalizeLateCheckIn p= new PenalizeLateCheckIn();
		//p.deActivateUserAndUpdateNotes(56587);
	}
	
	

	public void penalizeLateCheckInAssets(int studentID) throws Exception {
		RestrictCheckIn restrict = new RestrictCheckIn();
		ArrayList<Integer> allAssetIDs = restrict.getAllActiveAssetIDs();
		int assetListSize=allAssetIDs.size();
		
		for(int i=0; i<assetListSize; i++){
			int assetID=allAssetIDs.get(i);			
			ArrayList<Transaction> transactionsOfTheDay=getTransactionsOfTheDay(assetID);
			int numberOfTransactions;
			
			for(int j=0;j<numberOfTransactions;j++){
				Transaction thisTransaction=transactionsOfTheDay.get(j);
				
				DateTime checkOutDateTime=thisTransaction.getCheckOutDateTime();
				DateTime actualCheckInDateTime=thisTransaction.getCheckInDateTime();
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
				checkInDateTime=checkInDateTime.plusMinutes(GRACE_TIME_MINUTES);
				
				if(actualCheckInDateTime==null){
					sendAdminEmail(thisTransaction.getStudentID());
				}
				
				if(actualCheckInDateTime.compareTo(checkInDateTime)==1){
					sendLateEmail(thisTransaction);
					deActivateUserAndUpdateNotes(thisTransaction.getStudentID());
				}
					
			}
		}
		
	}
	
	//curl  -H "token:<COMPANY_TOKEN>" -X GET \
    //https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>/history_paginate.api? \
    //page=<PAGE_NUM | DEFAULT = 1>
	private ArrayList<Transaction> getTransactionsOfTheDay(int assetID) throws Exception{
		int i=1;
		boolean allTransDone=false;
		ArrayList<Transaction> allTransactions=new ArrayList<Transaction>();
		
		while(!allTransDone){
			String url = "https://northeasternuniversitysea.ezofficeinventory.com/assets/"+assetID+
					"/history_paginate.api?page="+i;
			
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
			
	    	ObjectMapper mapper = new ObjectMapper();
	    	//List<Transaction> navigation = 
	    	JavaType type = mapper.getNodeFactory().constructCollectionType(ArrayList.class, Transaction.class);
	    	ArrayList<Transaction> friendsList = mapper.readValue(response, type);
	    	
	    	CollectionType constructCollectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, Transaction.class);
	    	List<Transaction> test=mapper.readValue(response,constructCollectionType); 

			JsonFactory jsonFactory = new JsonFactory();
		    JsonParser parser = jsonFactory.createParser(response);

		    while (parser.nextToken() != JsonToken.END_ARRAY) { 
		    	String token = parser.getText();
		    	if ("sequence_num".equals(token)) { 
		    		parser.nextToken();
		    	}
		    }
		    i++;
		}
		return allTransactions;
	}
	
	//curl  -H "token:<COMPANY_TOKEN>" -X PUT \
    //https://<SUBDOMAIN>.ezofficeinventory.com/members/<USER_ID>/deactivate.api
	private void deActivateUser(int studentID) throws Exception{
		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "members/"+studentID+"/deactivate.api";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// request method
		con.setRequestMethod("PUT");

		// request headers
		con.setRequestProperty("token", USER_TOKEN);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		       
        con.getResponseCode();
	}
	
	//curl  -H "token:<COMPANY_TOKEN>" -X PUT \
    //-d "user[description]=<Late Fee>" \
    //https://<SUBDOMAIN>.ezofficeinventory.com/members/<USER_ID>.api
	private void deActivateUserAndUpdateNotes(int studentID) throws Exception {
		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "members/"+studentID+".api";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setDoOutput(true);
		// request method
		con.setRequestMethod("PUT");

		// request headers
		con.setRequestProperty("token", USER_TOKEN);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		String data= "user[description]=Owes $25";
		 
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

        writer.write(data);
        writer.close();
        
        con.getResponseCode();
        
        deActivateUser(studentID);

	}
}
