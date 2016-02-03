package CheckoutCode.EZOIRoutinesMaven;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Transaction {

	private final static DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private long id;
	private long assigned_to_id;
	private long created_by_id;
	private String created_at;
	private String updated_at;
	private boolean is_checkout;
	private long location_id;
	private long comments_count;
	private long package_id;
	private long basket_id;
	private boolean is_transfer;
	private String checkin_on;
	private String rent_collected;
	private String assigned_to_name;
	private String assigned_asset;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getStudentID() {
		return assigned_to_id;
	}
	public void setAssigned_to_id(long assigned_to_id) {
		this.assigned_to_id = assigned_to_id;
	}
	public long getCreated_by_id() {
		return created_by_id;
	}
	public void setCreated_by_id(long created_by_id) {
		this.created_by_id = created_by_id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public boolean is_checkout() {
		return is_checkout;
	}
	public void setIs_checkout(boolean is_checkout) {
		this.is_checkout = is_checkout;
	}
	public long getLocation_id() {
		return location_id;
	}
	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}
	public long getComments_count() {
		return comments_count;
	}
	public void setComments_count(long comments_count) {
		this.comments_count = comments_count;
	}
	public long getPackage_id() {
		return package_id;
	}
	public void setPackage_id(long package_id) {
		this.package_id = package_id;
	}
	public long getBasket_id() {
		return basket_id;
	}
	public void setBasket_id(long basket_id) {
		this.basket_id = basket_id;
	}
	public boolean is_transfer() {
		return is_transfer;
	}
	public void setIs_transfer(boolean is_transfer) {
		this.is_transfer = is_transfer;
	}
	public String getCheckin_on() {
		return checkin_on;
	}
	public void setCheckin_on(String checkin_on) {
		this.checkin_on = checkin_on;
	}
	public String getRent_collected() {
		return rent_collected;
	}
	public void setRent_collected(String rent_collected) {
		this.rent_collected = rent_collected;
	}
	public String getAssigned_to_name() {
		return assigned_to_name;
	}
	public void setAssigned_to_name(String assigned_to_name) {
		this.assigned_to_name = assigned_to_name;
	}
	public String getAssigned_asset() {
		return assigned_asset;
	}
	public void setAssigned_asset(String assigned_asset) {
		this.assigned_asset = assigned_asset;
	}
	
	public DateTime getCheckOutDateTime(){
		String checkOutDateTimeStr = created_at;
		DateTime checkOutDateTime = FORMAT.parseDateTime(checkOutDateTimeStr);
		return checkOutDateTime;
	}
	
	public DateTime getCheckInDateTime(){
		String checkInDateTimeStr = checkin_on;
		DateTime checkInDateTime = FORMAT.parseDateTime(checkInDateTimeStr);
		return checkInDateTime;
	}
	
}
