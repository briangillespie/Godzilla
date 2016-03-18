package com.gillespie.EZOI_routines;
import java.io.IOException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.joda.time.DateTime;


public class Asset {
	private int assetID;
	private double groupID;
	private String state;
	private DateTime checkInDateTime;
	private DateTime checkOutDateTime;

	public Asset(){
		assetID=-1;
		groupID=-1;
		state="";
		checkInDateTime=null;
		checkOutDateTime=null;
	}

	public int getAssetID() {
		return assetID;
	}

	public void setAssetID(int assetID) {
		this.assetID = assetID;
	}

	public double getGroupID() {
		return groupID;
	}

	public void setGroupID(double groupID) {
		this.groupID = groupID;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public DateTime getCheckInDateTime() {
		return checkInDateTime;
	}

	public void setCheckInDateTime(DateTime checkInDateTime) {
		this.checkInDateTime = checkInDateTime;
	}

	public DateTime getCheckOutDateTime() {
		return checkOutDateTime;
	}

	public void setCheckOutDateTime(DateTime checkOutDateTime) {
		this.checkOutDateTime = checkOutDateTime;
	}

	public String toString(){
		String checkOut = (this.checkOutDateTime == null) ? "null" : this.checkOutDateTime.toString();
		String checkIn = (this.checkInDateTime == null) ? "null" : this.checkInDateTime.toString();
		return "Asset: " + this.assetID + "\nGroup: " + this.groupID +
				"\nStatus: " + this.state + "\nCheckOut: " + checkOut + 
				"\nCheckIn: " + checkIn;
	}

	public boolean equals(Object anObject){
		Boolean isEqual;
		if (anObject instanceof Asset){
			Asset anAsset = (Asset) anObject;
			isEqual = (assetID == anAsset.getAssetID()) && (groupID == anAsset.getGroupID()) && (state.equals(anAsset.getState()));
			if (checkInDateTime == null && anAsset.getCheckInDateTime() == null){
				isEqual = isEqual && true;
			}else if (checkInDateTime != null && anAsset.getCheckInDateTime() != null){
				isEqual = isEqual && checkInDateTime.equals(anAsset.getCheckInDateTime());
			}else{
				isEqual = isEqual && false;
			}
			if (checkOutDateTime == null && anAsset.getCheckOutDateTime() == null){
				isEqual = isEqual && true;
			}else if (checkOutDateTime != null && anAsset.getCheckOutDateTime() != null){
				isEqual = isEqual && checkOutDateTime.equals(anAsset.getCheckOutDateTime());
			}else{
				isEqual = isEqual && false;
			}
			return isEqual;
		}
		return false;

	}

	public void setAssetValuesFromAPIResponseForID(String apiResponse, int assetID) throws JsonParseException, IOException{
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createJsonParser(apiResponse);
		this.assetID = assetID;
		while (parser.nextToken() != JsonToken.END_OBJECT) { 
			String token = parser.getText();

			if (Constants.STATE.equals(token)) { 
				parser.nextToken();
				this.state = parser.getText();
			}else if(Constants.GROUP_ID.equals(token)) { 
				parser.nextToken();
				this.groupID = parser.getValueAsInt();
			} else if(Constants.CHECKIN_ON.equals(token)) { 
				parser.nextToken();
				String checkInDateTimeStr = parser.getText();
				if(!(checkInDateTimeStr == null || checkInDateTimeStr.equalsIgnoreCase("null")))
				{
					DateTime checkInDateTime = Constants.FORMAT.parseDateTime(checkInDateTimeStr);
					this.checkInDateTime = checkInDateTime;
				}
			} else if(Constants.CHECKOUT_ON.equals(token)) { 
				parser.nextToken();
				String checkOutDateTimeStr = parser.getText();
				if(!(checkOutDateTimeStr == null || checkOutDateTimeStr.equalsIgnoreCase("null"))){
					DateTime checkOutDateTime = Constants.FORMAT.parseDateTime(checkOutDateTimeStr);
					this.checkOutDateTime = checkOutDateTime;
				}
			}
		}
	}
}