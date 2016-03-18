package com.gillespie.EZOI_routines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class RestrictCheckIn {

	// TODO:
	// Fix this so that we can just grab page number from JSON when needed
	private static final int PAGES = 4;

	private final String USER_AGENT = "Google Chrome/45.0.2454.85";
	private final String USER_TOKEN = "7a38ee6c5faf1e2756799fcc71e6d805";
	private final static DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private final int MAX_HOURS=5;
	private final int STAFF_MAX_HOURS=24;
	private final LocalTime END_TIME=new LocalTime(22,0);


	private APIRequestHandler handler;

	public RestrictCheckIn(APIRequestHandler handler){
		this.handler = handler;
	}

	public RestrictCheckIn(){
		this.handler = null;
	}

	public void setAPIRequestHandler(APIRequestHandler handler){
		this.handler = handler;
	}
	
	private DateTime setToEndOfDay(DateTime anyDateTime){
		return anyDateTime.withTime(
				END_TIME.getHourOfDay(), 
				END_TIME.getMinuteOfHour(),
				END_TIME.getSecondOfMinute(),
				END_TIME.getMillisOfSecond());
	}

	private void restrictAllToFiveHours2() throws Exception {
		ArrayList<Integer> allAssetIds = getAllActiveAssetIDs();

		for(Integer assetID : allAssetIds){
			Asset thisAsset = getAssetForID(assetID);
			DateTime currentCheckInDateTime = thisAsset.getCheckInDateTime();
			DateTime checkOutDateTime = thisAsset.getCheckOutDateTime();
			if(thisAsset.getState().equals(Constants.CHECKED_OUT) && thisAsset.getGroupID() != Constants.FAC_STAFF_GROUPID)
			{
				DateTime maxCheckInDateTime = checkOutDateTime.plusHours(MAX_HOURS);
				DateTime endDateTime = setToEndOfDay(checkOutDateTime);
				DateTime checkInDateTime = (endDateTime.compareTo(maxCheckInDateTime) == 1) ? maxCheckInDateTime : endDateTime;
				
				if(	currentCheckInDateTime == null || 									//if currentCheckInDateTime==null then the second clause crashes
					currentCheckInDateTime.compareTo(checkInDateTime) == 1 || 			// set check-in time only if the current check-in time is more than 5 hrs of the checkout time
					checkOutDateTime.compareTo(currentCheckInDateTime) == 1)
				{ 	
					setCheckOutDateTime(thisAsset.getAssetID(), checkInDateTime);		//TODO: Instead of calling this fxn, can we just write the new val, then update the whole asset?
				}
			}
			else if(thisAsset.getState().equals(Constants.CHECKED_OUT) && thisAsset.getGroupID() == Constants.FAC_STAFF_GROUPID) 
			{
				DateTime staffCheckOutDateTime = checkOutDateTime.plusHours(STAFF_MAX_HOURS);
				if(currentCheckInDateTime == null || currentCheckInDateTime.compareTo(staffCheckOutDateTime) == 1)
				{
					setCheckOutDateTime(thisAsset.getAssetID(), staffCheckOutDateTime);
				}
			}
		}
	}

	private Asset getAssetForID(Integer assetID) throws MalformedURLException, IOException{
		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "assets/"+assetID+".api";

		String response = handler.getAPIResponse(url);
		Asset thisAsset = new Asset();
		thisAsset.setAssetValuesFromAPIResponseForID(response, assetID);
		return thisAsset;
	}

	// curl command for extending checkout:
	// curl  -H "token:<COMPANY_TOKEN>" -X PUT \
	// -d "till=<TILL_DATE AND TIME>" \
	// https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>/extend_checkout.api
	/**
	 * TODO: Would it maybe just be easier to grab the entire asset, update its checkout time, then update the entire asset
	 * 		 Something like 
	 * 		 restrict = new RestrictCheckIn(APIhandler)
	 * 		 asset = getAssetForID(assetID)
	 * 		 asset.setCheckOutDateTime(newCheckOutTime)
	 * 		 restrict.updateAsset(asset)
	 * @param assetID
	 * @param checkInDateTime
	 * @throws Exception
	 */
	private void setCheckOutDateTime(int assetID, DateTime checkInDateTime) throws Exception {

		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "assets/"+assetID+"/extend_checkout.api";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setDoOutput(true);
		// request method
		con.setRequestMethod("PUT");

		// request headers
		con.setRequestProperty("token", Constants.USER_TOKEN);
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String checkInDateTimeStr = "";
		String data = "";

		checkInDateTimeStr = checkInDateTime.toString(FORMAT);
		data = "till=" + checkInDateTimeStr;

		OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

		writer.write(data);
		writer.close();

		con.getResponseCode();

	}

	/**
	 * 
	 * @param assetID
	 * @param checkInDateTime
	 * @throws Exception
	 */
	private void setCheckOutDateTime2(int assetID, DateTime checkInDateTime) throws Exception{
		String url = "https://northeasternuniversitysea.ezofficeinventory.com/" 
				+ "assets/" + assetID + "/extend_checkout.api";
		OutputStreamWriter writer = new OutputStreamWriter(handler.getOutputStreamFromPUTRequest(url));
		String checkInDateTimeStr = checkInDateTime.toString(FORMAT);
		String data = "till=" + checkInDateTimeStr;
		writer.write(data);
		writer.close();
	}

	/**
	 * 
	 * @return allAssetIDs - ArrayList<Integer>
	 * @throws Exception
	 */
	public ArrayList<Integer> getAllActiveAssetIDs() throws Exception {
		ArrayList<Integer> allAssetIDs = new ArrayList<Integer>();
		for (int i = 1; i <= PAGES; i++) {
			String url = "https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=" + i;
			String response = handler.getAPIResponse(url);			
			allAssetIDs.addAll(this.getActiveAssetsFromPage(response));
		}
		return allAssetIDs;
	}

	/**
	 * 
	 * @param assetID
	 * @return
	 * @throws IOException
	 */
	public String getAssetState(int assetID) throws IOException{
		String urlState =
				"https://northeasternuniversitysea.ezofficeinventory.com/assets/" + assetID + ".api";
		String response = handler.getAPIResponse(urlState);
		return this.getStateFromJSONString(response);
	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public String getStateFromJSONString(String response) throws JsonParseException, IOException{
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createJsonParser(response);
		String state = "";
		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String token = (String) parser.getText();

			if (Constants.STATE.equals(token)) {
				parser.nextToken();
				state = parser.getText();
			}
		}
		return state;
	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public ArrayList<Integer> getActiveAssetsFromPage(String response) throws JsonParseException, IOException{
		ArrayList<Integer> assetIDs = new ArrayList<Integer>();
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createJsonParser(response);

		while (parser.nextToken() != JsonToken.END_ARRAY) {
			String token = parser.getText();
			if (Constants.SEQUENCE_NUM.equals(token)) {
				parser.nextToken();
				assetIDs.add(parser.getValueAsInt());
			}
		}
		return assetIDs;
	}

	// Currently being used for ad-hoc testing
	public static void main(String[] args) throws Exception {
		RestrictCheckIn r = new RestrictCheckIn(new APIRequestHandler());

		// Begin ad-hoc testing for getAssetState
		System.out.println(r.getAssetState(5));

		// Begin ad-hoc testing for getAllActiveAssetIDs
		System.out.println(r.getAllActiveAssetIDs());

		RestrictCheckIn restrict = new RestrictCheckIn(new APIRequestHandler());
		restrict.restrictAllToFiveHours2();

	}
}

//// API Utilities
//// curl command for getting asset details:
//// curl  -H "token:<COMPANY_TOKEN>" -X GET \
//// https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>.api
//public void getAndUpdateAssetDetails(Asset thisAsset) throws Exception {
//	int assetID = thisAsset.getAssetID();
//
//	String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
//			+ "assets/"+assetID+".api";
//
//	URL obj = new URL(url);
//	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//	// request method
//	con.setRequestMethod("GET");
//
//	// request headers
//	con.setRequestProperty("token", USER_TOKEN);
//	con.setRequestProperty("User-Agent", USER_AGENT);
//
//	// reading input stream
//	BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//	String inputLine;
//	String response="";
//	while ((inputLine = in.readLine()) != null) {
//		response+=inputLine;
//	}
//	in.close();
//
//	// Iterating different fields of the JSON
//	JsonFactory jsonFactory = new JsonFactory();
//	JsonParser parser = jsonFactory.createJsonParser(response);
//
//	while (parser.nextToken() != JsonToken.END_OBJECT) { 
//		String token = parser.getText();
//
//		if ("state".equals(token)) { 
//			parser.nextToken();
//			thisAsset.setState(parser.getText());
//		} else if("group_id".equals(token)) { 
//			parser.nextToken();
//			thisAsset.setGroupID(parser.getValueAsInt());
//		} else if("checkin_due_on".equals(token)) { 
//			parser.nextToken();
//			String checkInDateTimeStr=parser.getText();
//			if(checkInDateTimeStr!=null){
//				DateTime checkInDateTime=FORMAT.parseDateTime(checkInDateTimeStr);
//				thisAsset.setCheckInDateTime(checkInDateTime);
//			}
//		} else if("checkout_on".equals(token)) { 
//			parser.nextToken();
//			String checkOutDateTimeStr=parser.getText();
//			if(checkOutDateTimeStr!=null){
//				DateTime checkOutDateTime=FORMAT.parseDateTime(checkOutDateTimeStr);
//				thisAsset.setCheckOutDateTime(checkOutDateTime);
//			}
//		}
//	}
//}

