package CheckoutCode.EZOIRoutinesMaven;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PenalizeLateCheckIns {
	//Constants
	private final String USER_AGENT = "Google Chrome/45.0.2454.85";
	private final String USER_TOKEN = "7a38ee6c5faf1e2756799fcc71e6d805";
	private final static DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private final int MAX_HOURS=5;
	private final int GRACE_TIME_MINUTES=15;
	private final LocalTime END_TIME=new LocalTime(22,0);
	private final int FAC_STAFF_GROUPID=32954;
	private final String CHECKED_OUT = "checked_out";
		
    public static void main( String[] args ) throws Exception {	
    	PenalizeLateCheckIns p= new PenalizeLateCheckIns();
		//p.penalizeLateCheckInAssets();
    	
		ArrayList<Transaction> transactionsOfTheDay=p.getTransactionsOfTheDay(3);
    }
    
    public void penalizeLateCheckInAssets() throws Exception {
		RestrictCheckIn restrict = new RestrictCheckIn();
		ArrayList<Integer> allAssetIDs = restrict.getAllActiveAssetIDs();
		int assetListSize=allAssetIDs.size();
		
		for(int i=0; i<assetListSize; i++){
			int assetID=allAssetIDs.get(i);			
			ArrayList<Transaction> transactionsOfTheDay=getTransactionsOfTheDay(assetID);
			int numberOfTransactions=transactionsOfTheDay.size();
			
			if(isAssetBeingHeld(assetID)){
				Transaction thisTransaction = getLatestTransaction(assetID);
				sendLateEmail(thisTransaction);
				deActivateUserLoginAndUpdateNotes(thisTransaction.getStudentID());
			}
		
			for(int j=0;j<numberOfTransactions;j++){
				Transaction thisTransaction=transactionsOfTheDay.get(j);
				
				if(thisTransaction.is_checkout()){
					DateTime checkOutDateTime=thisTransaction.getCheckOutDateTime();
					DateTime actualCheckInDateTime=getTransactionCheckInDateTime(thisTransaction,transactionsOfTheDay);
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
						continue;
					}
					
					if(actualCheckInDateTime.compareTo(checkInDateTime)==1){
						sendLateEmail(thisTransaction);
						deActivateUserLoginAndUpdateNotes(thisTransaction.getStudentID());
					}
				}
					
			}
		}
		
	}
    
    private Transaction getLatestTransaction(int assetID) throws Exception {
    	ArrayList<Transaction> currentTransactions=new ArrayList<Transaction>();
		
		String url = "https://northeasternuniversitysea.ezofficeinventory.com/assets/"+assetID+
				"/history_paginate.api?page=0";
		
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
		String arrayResponse=response.substring(11);
		CollectionType constructCollectionType = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Transaction.class);
		currentTransactions=mapper.readValue(arrayResponse,constructCollectionType); 
		
    	return currentTransactions.get(0);
    }
    
    
    private boolean isAssetBeingHeld(int assetID) throws Exception{
    	RestrictCheckIn r = new RestrictCheckIn();
    	String state = r.getAssetState(assetID);
    	return (state == CHECKED_OUT);
    }
    
    private DateTime getTransactionCheckInDateTime(Transaction thisTransaction,ArrayList<Transaction> transactionsOfTheDay){
    	int size = transactionsOfTheDay.size();
    	DateTime checkInDateTime = new DateTime();
    	for(int i = 0;i < size;i++){
    		if(thisTransaction.equals(transactionsOfTheDay.get(i))){
    			if(i == 0)
    				return null;
    			Transaction checkIn = transactionsOfTheDay.get(i-1);
    			checkInDateTime = checkIn.getCheckInDateTime();
    		}
    	}
    	return checkInDateTime;
    }
	
	//curl  -H "token:<COMPANY_TOKEN>" -X GET \
    //https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>/history_paginate.api? \
    //page=<PAGE_NUM | DEFAULT = 1>
	private ArrayList<Transaction> getTransactionsOfTheDay(int assetID) throws Exception{
		int i=1;
		ArrayList<Transaction> currentTransactions=new ArrayList<Transaction>();
		ArrayList<Transaction> allTransactions=new ArrayList<Transaction>();
		
		while(true){
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
	    	String emptyHistory="{\"history\":[]}";
	    	if(!response.equals(emptyHistory)){
	    		String arrayResponse=response.substring(11);
	    		CollectionType constructCollectionType = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Transaction.class);
	    		currentTransactions=mapper.readValue(arrayResponse,constructCollectionType); 
	    		allTransactions.addAll(currentTransactions);
	    		DateTime currentDateTime = new DateTime();
	    		
	    		DateTime assetActiveDateTime=currentDateTime.minusDays(1);
	    		int day=assetActiveDateTime.getDayOfMonth();
	    		int month=assetActiveDateTime.getMonthOfYear();
	    		int year=assetActiveDateTime.getYear();
	    		
	    		DateTime lastTransactionDateTime=currentTransactions.get(4).getCheckOutDateTime();
	    		int lastTransactionDay=lastTransactionDateTime.getDayOfMonth();
	    		int lastTransactionMonth=lastTransactionDateTime.getMonthOfYear();
	    		int lastTransactionYear=lastTransactionDateTime.getYear();
	    		
	    		if(day > lastTransactionDay || month > lastTransactionMonth || year > lastTransactionYear){
	    			removePreviousDayTransactions(allTransactions,day,month,year);
	    			return allTransactions;
	    		}
	    	}
	    	else break;
	    	i++;
		}
		return allTransactions;
	}
	
	private void removePreviousDayTransactions(ArrayList<Transaction> allTransactions,int day,int month,int year){
		int size=allTransactions.size();
		for(int i=size-1;i>=0;i--){
			DateTime individualDateTime=allTransactions.get(i).getCheckOutDateTime();
			int individualDay=individualDateTime.getDayOfMonth();
			int individualMonth=individualDateTime.getMonthOfYear();
			int individualYear=individualDateTime.getYear();
			if(day > individualDay || month > individualMonth || year > individualYear)
				allTransactions.remove(i);
		}
	}
	
	//curl  -H "token:<COMPANY_TOKEN>" -X PUT \
    //https://<SUBDOMAIN>.ezofficeinventory.com/members/<USER_ID>/deactivate.api
	private void deActivateUser(long studentID) throws Exception{
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
	private void deActivateUserLoginAndUpdateNotes(long studentID) throws Exception {
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
	
	private void sendLateEmail(Transaction thisTransaction){
		
	}
}
