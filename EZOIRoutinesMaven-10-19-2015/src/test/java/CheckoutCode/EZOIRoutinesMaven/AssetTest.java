/**
 * 
 */
package CheckoutCode.EZOIRoutinesMaven;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author brian.g
 *
 */
public class AssetTest extends TestCase {

	private int STD_ASSET_ID = 1;
	private int STD_GROUP_ID = 1;
	private int NO_ASSET_ID = -1;
	private int NO_GROUP_ID = -1;
	private String CHECKED_OUT = "checked out";
	private String EMPTY_STRING = "";
	private String AVAILABLE = "available";

	private final static DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

	private Asset emptyAsset;
	private Asset availableAsset;
	private Asset checkedOutAsset;

	/**
	 * @param name
	 */
	public AssetTest(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		emptyAsset = new Asset();
		
		availableAsset = new Asset();
		availableAsset.setAssetID(STD_ASSET_ID);
		availableAsset.setGroupID(STD_GROUP_ID);
		availableAsset.setState(AVAILABLE);
		availableAsset.setCheckInDateTime(FORMAT.parseDateTime("2016-01-01 12:00:00"));
		availableAsset.setCheckOutDateTime(FORMAT.parseDateTime("2016-01-01 12:00:00"));
		
		checkedOutAsset = new Asset();
		checkedOutAsset.setAssetID(STD_ASSET_ID);
		checkedOutAsset.setGroupID(STD_GROUP_ID);
		checkedOutAsset.setState(CHECKED_OUT);
		checkedOutAsset.setCheckInDateTime(FORMAT.parseDateTime("2016-01-01 12:00:00"));
		checkedOutAsset.setCheckOutDateTime(FORMAT.parseDateTime("2016-01-01 12:01:00"));

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testEquals() {
		Asset availableAssetCopy = new Asset();
		availableAssetCopy.setAssetID(1);
		availableAssetCopy.setGroupID(1);
		availableAssetCopy.setState(AVAILABLE);
		availableAssetCopy.setCheckInDateTime(FORMAT.parseDateTime("2016-01-01 12:00:00"));
		availableAssetCopy.setCheckOutDateTime(FORMAT.parseDateTime("2016-01-01 12:00:00"));
		
	    Assert.assertTrue(!availableAsset.equals(null));
	    Assert.assertEquals(availableAsset, availableAsset);
	    Assert.assertEquals(availableAsset, availableAssetCopy); // (1)
	    Assert.assertTrue(!availableAsset.equals(emptyAsset));
	}
	
	/**
	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#getAssetID()}.
	 */
	public void testGetAssetID() {
		Assert.assertEquals(emptyAsset.getAssetID(), NO_ASSET_ID);
		Assert.assertEquals(availableAsset.getAssetID(), STD_ASSET_ID);
	}

	/**
	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#setAssetID(int)}.
	 */
	public void testSetAssetID() {
		Assert.assertEquals(availableAsset.getAssetID(), STD_ASSET_ID);
		availableAsset.setAssetID(STD_ASSET_ID + 1);
		Assert.assertEquals(availableAsset.getAssetID(), STD_ASSET_ID + 1);
	}

	/**
	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#getGroupID()}.
	 */
	public void testGetGroupID() {
		Assert.assertEquals(emptyAsset.getAssetID(), NO_GROUP_ID);
		Assert.assertEquals(availableAsset.getAssetID(), STD_GROUP_ID);
	}

	/**
	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#setGroupID(double)}.
	 */
	public void testSetGroupID() {
		Assert.assertEquals(availableAsset.getAssetID(), STD_GROUP_ID);
		availableAsset.setAssetID(STD_GROUP_ID + 1);
		Assert.assertEquals(availableAsset.getAssetID(), STD_GROUP_ID + 1);
	}

	/**
	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#getState()}.
	 */
	public void testGetState() {
		Assert.assertEquals(emptyAsset.getState(), EMPTY_STRING);
		Assert.assertEquals(availableAsset.getState(), AVAILABLE);
		Assert.assertEquals(checkedOutAsset.getState(), CHECKED_OUT);
	}

	/**
	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#setState(java.lang.String)}.
	 */
	public void testSetState() {
		Assert.assertEquals(emptyAsset.getState(), EMPTY_STRING);
		emptyAsset.setState(AVAILABLE);
		Assert.assertEquals(emptyAsset.getState(), AVAILABLE);
		emptyAsset.setState(CHECKED_OUT);
		Assert.assertEquals(emptyAsset.getState(), CHECKED_OUT);
	}

//	/**
//	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#getCheckInDateTime()}.
//	 */
//	public void testGetCheckInDateTime() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#setCheckInDateTime(org.joda.time.DateTime)}.
//	 */
//	public void testSetCheckInDateTime() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#getCheckOutDateTime()}.
//	 */
//	public void testGetCheckOutDateTime() {
//		fail("Not yet implemented");
//	}
//
//	/**
//	 * Test method for {@link CheckoutCode.EZOIRoutinesMaven.Asset#setCheckOutDateTime(org.joda.time.DateTime)}.
//	 */
//	public void testSetCheckOutDateTime() {
//		fail("Not yet implemented");
//	}

	public static void main(String[] args){
		TestCase test= new AssetTest("check setters") 
		{
			public void runTest() {
				testGetAssetID();
			}
		};
	}

}
