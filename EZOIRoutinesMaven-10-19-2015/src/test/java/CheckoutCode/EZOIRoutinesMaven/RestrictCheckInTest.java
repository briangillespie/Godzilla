package CheckoutCode.EZOIRoutinesMaven;

import java.net.HttpURLConnection;
import java.net.URL;

import junit.framework.TestCase;

public class RestrictCheckInTest extends TestCase {

	protected void setUp() throws Exception {
		int assetIDNormal = 1;
		int assetIDNotSet = -1;
		int assetIDNulll = (Integer) null;
		int assetIdZero = 0;
		double assetIDNonInt = 1.1;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetAllAssetIDs(){
		
	}
	
	public void testGetAssetState(){
		
	}
}
