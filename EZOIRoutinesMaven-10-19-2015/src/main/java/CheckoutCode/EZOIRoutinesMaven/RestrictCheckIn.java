package CheckoutCode.EZOIRoutinesMaven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class RestrictCheckIn {
	private static final int PAGES = 3;

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
	
	public String getAssetState2(int assetID) throws IOException{
		String urlState =
				"https://northeasternuniversitysea.ezofficeinventory.com/" + "assets/" + assetID + ".api";
		String response = this.getAPIResponseFromURL(urlState);
		return this.getStateFromJSONString(response);
	}

	/**
	 * 
	 * @param targetURL - The complete URL for a given API call
	 * @return response - A string representation of a JSON response from the API
	 * @throws IOException
	 */
	public String getAPIResponseFromURL(String targetURL) throws IOException{
		URL obj;
		String response = "";
		try {
			obj = new URL(targetURL);
			HttpURLConnection con = configureGETRequest(obj);
			response = getInputStreamAsString(con);
		}catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * 
	 * @param apiCallTargetURL - URL Object representing the URL for an API call to EZOI
	 * @return connection - An HttpURLConnection that allows access to the JSON response from EZOI
	 * @throws IOException
	 */
	private static HttpURLConnection configureGETRequest(URL apiCallTargetURL) throws IOException{
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) apiCallTargetURL.openConnection();

			// request method
			connection.setRequestMethod("GET");

			// request headers
			connection.setRequestProperty("token", Constants.USER_TOKEN);
			connection.setRequestProperty("User-Agent", Constants.USER_AGENT);
		}catch (IOException e) {
			throw new IOException("Cannot connect to the given URL.", e);
		}finally{
			if (connection != null) connection.disconnect();
		}
		return connection;
	}

	/**
	 * 
	 * @param connection - An HttpURLConnection to the EZOI API
	 * @return response - the String representation of the API response
	 * @throws IOException
	 */
	private static String getInputStreamAsString(HttpURLConnection connection) throws IOException{
		BufferedReader in = null;
		String responseString = "";
		try{
			in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				responseString += inputLine;
			}
			in.close();
		}catch (IOException e){
			throw new IOException("Cannot interpret input stream from the given URL", e);
		}finally {
			if (in != null) in.close();
		}
		return responseString;
	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public String getStateFromJSONString(String response) throws JsonParseException, IOException{
		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createJsonParser(response);
		String state = "";
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
	 * @param response
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	public static ArrayList<Integer> getActiveAssetsFromPage(String response) throws JsonParseException, IOException{
		ArrayList<Integer> assetIDs = new ArrayList<Integer>();

		JsonFactory jsonFactory = new JsonFactory();
		JsonParser parser = jsonFactory.createJsonParser(response);

		while (parser.nextToken() != JsonToken.END_ARRAY) {
			String token = parser.getText();
			if ("sequence_num".equals(token)) {
				parser.nextToken();
				assetIDs.add(parser.getValueAsInt());
			}
		}
		return assetIDs;
	}


	// Currently being used for ad-hoc testing
	public static void main(String[] args) throws Exception {
		RestrictCheckIn r = new RestrictCheckIn();



		// Begin ad-hoc testing for getAssetState
		//System.out.println(r.getAssetState(assetID));

		int assetID = 2;
		String urlState =
				"https://northeasternuniversitysea.ezofficeinventory.com/" + "assets/" + assetID + ".api";
		String response = r.getAPIResponseFromURL(urlState);
		System.out.println(response);
		String state = r.getStateFromJSONString(response);
		System.out.println(state);



		// Begin ad-hoc testing for getAllActiveAssetIDs
		//System.out.println(r.getAllActiveAssetIDs().toString());

		ArrayList<Integer> allAssetIDs = new ArrayList<Integer>();
		for (int i = 1; i <= PAGES; i++){
			String urlIDs = "https://northeasternuniversitysea.ezofficeinventory.com/assets.api?page=" + i;
			String responseTwo = r.getAPIResponseFromURL(urlIDs);

			allAssetIDs.addAll(getActiveAssetsFromPage(responseTwo));
		}

		System.out.println(allAssetIDs.toString());
	}
}
