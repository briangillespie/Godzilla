package CheckoutCode.EZOIRoutinesMaven;

import junit.framework.TestCase;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.mockito.Matchers;

public class RestrictCheckInTest extends TestCase {

	public static RestrictCheckIn mockRestrict;
	public static RestrictCheckIn restrict;
	
	protected void setUp() throws Exception {
		mockRestrict = mock(RestrictCheckIn.class);
		restrict = new RestrictCheckIn();
		
		when(mockRestrict.getAssetState(2)).thenReturn("available");
		when(mockRestrict.getAssetState2(2)).thenReturn("available");
		when(mockRestrict.getAssetState2(1)).thenReturn("checked_out");
		
		when(mockRestrict.getAPIResponseFromURL("https://northeasternuniversitysea.ezofficeinventory.com/assets/1.api")).thenReturn("{\"asset\":{\"state\":\"available\"}}");
		doThrow(IllegalArgumentException.class).when(mockRestrict).getAssetState(-1);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

//	public void testGetAllActiveAssetIDs() {
//		fail("Not yet implemented"); // TODO
//	}

	public void testGetAssetState() throws Exception {
		assertEquals("available", mockRestrict.getAssetState2(2));
		verify(mockRestrict).getAssetState2(Matchers.eq(2));
		assertEquals("checked_out", mockRestrict.getAssetState2(1));
		verify(mockRestrict).getAssetState2(Matchers.eq(1));
		verify(mockRestrict, times(2)).getAssetState2(anyInt());
	}

	public void testGetAPIResponseFromURL() throws JsonParseException, IOException {
		assertEquals(mockRestrict.getAPIResponseFromURL("https://northeasternuniversitysea.ezofficeinventory.com/assets/1.api"),"{\"asset\":{\"state\":\"available\"}}");
	}

	public void testGetStateFromJSONString() throws JsonParseException, IOException {
		assertEquals("available", restrict.getStateFromJSONString(mockRestrict.getAPIResponseFromURL("https://northeasternuniversitysea.ezofficeinventory.com/assets/1.api")));
	}
//
//	public void testGetActiveAssetsFromPage() {
//		fail("Not yet implemented"); // TODO
//	}

}
