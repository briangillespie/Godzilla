import org.joda.time.DateTime;

public class Transaction {
	private int assetID;
	private int studentID;
	private DateTime checkOutDateTime;
	private DateTime checkInDateTime;
	
	public int getAssetID() {
		return assetID;
	}
	public void setAssetID(int assetID) {
		this.assetID = assetID;
	}
	public int getStudentID() {
		return studentID;
	}
	public void setStudentID(int studentID) {
		this.studentID = studentID;
	}
	public DateTime getCheckOutDateTime() {
		return checkOutDateTime;
	}
	public void setCheckOutDateTime(DateTime checkOutDateTime) {
		this.checkOutDateTime = checkOutDateTime;
	}
	public DateTime getCheckInDateTime() {
		return checkInDateTime;
	}
	public void setCheckInDateTime(DateTime checkInDateTime) {
		this.checkInDateTime = checkInDateTime;
	}
	
}
