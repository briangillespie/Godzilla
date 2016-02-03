package CheckoutCode.EZOIRoutinesMaven;
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

	public boolean equals(Object anObject){
		if (anObject instanceof Asset){
			Asset anAsset = (Asset) anObject;
			return (assetID == anAsset.getAssetID() &&
					groupID == anAsset.getGroupID() &&
					state.equals(anAsset.getState()) &&
					checkInDateTime.equals(anAsset.getCheckInDateTime()) &&
					checkOutDateTime.equals(anAsset.getCheckOutDateTime()));
		}
		return false;

	}
}
