import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.HttpURLConnection;

public class HttpURLConnectionExample {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		HttpURLConnectionExample http = new HttpURLConnectionExample();

		System.out.println("Testing 1 - Update Item Name");
		//http.getAssetDetails(3);
		http.updateAssetName(16, "iPad No.2");
	}

	// Function to get Asset details
	private void getAssetDetails(int assetID) throws Exception {
		
//		curl Command:
//		curl  -H "token:<COMPANY_TOKEN>" -X GET \
//	          https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>.api

		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "assets/"+assetID+".api";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("token", "7a38ee6c5faf1e2756799fcc71e6d805");
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		//StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
		}
		in.close();
	}
	
	// Function to get Asset details
	private void updateAssetName(int assetID,String newName) throws Exception {

//		curl Command:
//		curl  -H "token:<COMPANY_TOKEN>" -X PUT \
//	          -d "fixed_asset[name]=<ASSET_NAME>" \
//	          https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>.api

		String url = "https://northeasternuniversitysea.ezofficeinventory.com/"
				+ "assets/"+assetID+".api";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setDoOutput(true);
		con.setRequestMethod("PUT");

		//add request header
		con.setRequestProperty("token", "7a38ee6c5faf1e2756799fcc71e6d805");
		//con.setRequestProperty("User-Agent", USER_AGENT);
		
		String data =  "\"fixed_asset[name]=iPad2Test\"";
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

        writer.write(data);
        writer.close();

		int responseCode = con.getResponseCode();
		//System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		//StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			System.out.println(inputLine);
		}
		in.close();
	}	
}
