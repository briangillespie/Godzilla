package com.gillespie.EZOI_routines;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.codehaus.jackson.JsonParseException;
import org.mockito.Matchers;

public class RestrictCheckInTest extends TestCase {

	public static RestrictCheckIn mockRestrict;
	public static RestrictCheckIn restrict;
	public static APIRequestHandler mockRequestHandler;


	
	protected void setUp() throws Exception {
		mockRequestHandler = mock(APIRequestHandler.class);
		mockRestrict = mock(RestrictCheckIn.class);
		restrict = new RestrictCheckIn(mockRequestHandler);

		doReturn(RestrictCheckInTestMockVals.EXPECTED_JSON_FOR_ASSET_5)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets/5.api");
		doReturn(RestrictCheckInTestMockVals.EXPECTED_JSON_FOR_ASSET_1)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets/1.api");
		doReturn(RestrictCheckInTestMockVals.EXPECTED_JSON_FOR_ASSET_13)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets/13.api");
		doReturn(RestrictCheckInTestMockVals.EXPECTED_JSON_FOR_ASSET_4)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets/4.api");
		
		doReturn(RestrictCheckInTestMockVals.EXPECTED_ACTIVE_ID_LIST_P1)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=1");
		doReturn(RestrictCheckInTestMockVals.EXPECTED_ACTIVE_ID_LIST_P2)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=2");
		doReturn(RestrictCheckInTestMockVals.EXPECTED_ACTIVE_ID_LIST_P3)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=3");
		doReturn(RestrictCheckInTestMockVals.EXPECTED_ACTIVE_ID_LIST_P4)
			.when(mockRequestHandler)
			.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=4");
		
		//doReturn().when(mockRequestHandler).getOutputStreamFromPUTRequest("https://northeasternuniversitysea.ezofficeinventory.com/assets/13/extend_checkout.api");
	}	

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetAllActiveAssetIDs() throws Exception {
		assertEquals(restrict.getAllActiveAssetIDs(), RestrictCheckInTestMockVals.ALL_ASSET_IDS_LIST); 
	}

	public void testGetAssetStateShouldReturnAvailable() throws Exception {
		assertEquals(restrict.getAssetState(5), Constants.AVAILABLE);
	}
	
	public void testGetAssetStateShouldReturnRetired() throws Exception {
		assertEquals(restrict.getAssetState(1), Constants.RETIRED);
	}
	
	public void testGetAssetStateShouldReturnCheckedOut() throws Exception {
		assertEquals(restrict.getAssetState(13), Constants.CHECKED_OUT);
	}
	
	public void testGetAssetStateShouldReturnMaintenance() throws Exception {
		assertEquals(restrict.getAssetState(4), Constants.MAINTENANCE);
	}
	
//	public void testGetAssetForIDShouldReturnAsset1(){
//		assertEquals(restrict.getAsssetForID)
//	}
}
