package CheckoutCode.EZOIRoutinesMaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class RestrictCheckIn {
	private static final int PAGES = 3;


	// Currently being used for ad-hoc testing
	public static void main(String[] args) throws Exception {
		RestrictCheckIn r = new RestrictCheckIn();
		
		// Begin ad-hoc testing for getAssetState
		int assetID = 2;
		System.out.println(r.getAssetState(assetID));

		String urlState =
				"https://northeasternuniversitysea.ezofficeinventory.com/" + "assets/" + assetID + ".api";
		String response = r.getAPIResponseFromURL(urlState);
		String state = "";

		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createJsonParser(response);

		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String token = parser.getText();

			if ("state".equals(token)) {
				parser.nextToken();
				state = parser.getText();
			}
		}
		System.out.println(state);

		// Begin ad-hoc testing for getAllActiveAssetIDs
		ArrayList<Integer> allAssetIDs = new ArrayList<Integer>();

		for (int i = 1; i <= PAGES; i++){
			String urlIDs = "https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=" + i;
			String responseTwo = r.getAPIResponseFromURL(urlIDs);

			JsonFactory jsonFactoryTwo = new JsonFactory();
			JsonParser parserTwo = jsonFactoryTwo.createJsonParser(responseTwo);

			while (parserTwo.nextToken() != JsonToken.END_ARRAY) {
				String token = parserTwo.getText();
				if ("sequence_num".equals(token)) {
					parserTwo.nextToken();
					allAssetIDs.add(parserTwo.getValueAsInt());
				}
			}
		}
		System.out.println(r.getAllActiveAssetIDs().toString());
		System.out.println(allAssetIDs.toString());
	}

	// curl command for getting all asset details:
	// curl -H "token:<COMPANY_TOKEN>" -X GET \
	// https://<SUBDOMAIN>.ezofficeinventory.com/assets.api?page=<PAGE_NUM | DEFAULT = 1>
	public ArrayList<Integer> getAllActiveAssetIDs() throws Exception {
		ArrayList<Integer> allAssetIDs = new ArrayList<Integer>();
		for (int i = 1; i <= PAGES; i++) {
			String url = "https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=" + i;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			// request method
			con.setRequestMethod("GET");

			// request headers
			con.setRequestProperty("token", Constants.USER_TOKEN);
			con.setRequestProperty("User-Agent", Constants.USER_AGENT);

			// reading input stream
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			String response = "";
			while ((inputLine = in.readLine()) != null) {
				response += inputLine;
			}
			in.close();

			JsonFactory jsonFactory = new JsonFactory();
			JsonParser parser = jsonFactory.createJsonParser(response);

			while (parser.nextToken() != JsonToken.END_ARRAY) {
				String token = parser.getText();
				if ("sequence_num".equals(token)) {
					parser.nextToken();
					allAssetIDs.add(parser.getValueAsInt());
				}
			}
		}
		return allAssetIDs;
	}

	/**
	 * 
	 * @param assetID an integer representing the unique ID of an asset
	 * @return state - A string representing the status of an asset. One of: "available" or "checked_out"
	 * @throws Exception
	 */
	public String getAssetState(int assetID) throws Exception {

		String state = "";
		String url =
				"https://northeasternuniversitysea.ezofficeinventory.com/" + "assets/" + assetID + ".api";

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// request method
		con.setRequestMethod("GET");

		// request headers
		con.setRequestProperty("token", Constants.USER_TOKEN);
		con.setRequestProperty("User-Agent", Constants.USER_AGENT);

		// reading input stream
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		String response = "";
		while ((inputLine = in.readLine()) != null) {
			response += inputLine;
		}
		in.close();

		// Iterating different fields of the JSON
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createJsonParser(response);

		while (parser.nextToken() != JsonToken.END_OBJECT) {
			String token = parser.getText();

			if ("state".equals(token)) {
				parser.nextToken();
				state = parser.getText();
			}
		}
		return state;
	}

	/**
	 * 
	 * @param targetURL - The complete URL for a given API call
	 * @return response - A string representation of a JSON response from the API
	 * @throws IOException
	 */
	protected String getAPIResponseFromURL(String targetURL) throws IOException{

		URL obj;
		String response = "";
		try {
			obj = new URL(targetURL);
			HttpURLConnection con = null;
			try {
				con = (HttpURLConnection) obj.openConnection();
				
				// request method
				con.setRequestMethod("GET");

				// request headers
				con.setRequestProperty("token", Constants.USER_TOKEN);
				con.setRequestProperty("User-Agent", Constants.USER_AGENT);
			}catch (IOException e) {
				throw new IOException("Cannot connect to the given URL.", e);
			}finally{
				if (con != null) con.disconnect();
			}
			// reading input stream
			BufferedReader in = null;
			try{
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response += inputLine;
				}
				in.close();
			}catch (IOException e){
				throw new IOException("Cannot interpret input stream from the given URL", e);
			}finally {
				if (in != null) in.close();
			}
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return response;
	}
}
