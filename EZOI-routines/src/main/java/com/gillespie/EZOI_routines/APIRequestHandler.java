package com.gillespie.EZOI_routines;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class APIRequestHandler {

	private static final String GET = "GET";
	private static final String PUT = "PUT";


	/**
	 * 
	 * @param apiCallTargetURL
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */

	public String getAPIResponse(String apiCallTargetURL) throws MalformedURLException, IOException{
		HttpURLConnection connection = null;
		connection = configureGETRequest(new URL(apiCallTargetURL));
		return getJSONResponseAsString(connection);
	}

	public OutputStream getOutputStreamFromPUTRequest(String apiCallTargetURL) throws MalformedURLException, IOException{
		HttpURLConnection connection = configurePUTRequest(new URL(apiCallTargetURL));
		//System.out.println(connection.getResponseCode());
		return connection.getOutputStream();
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
			connection.setRequestMethod(GET);

			// request headers
			connection.setRequestProperty("token", Constants.USER_TOKEN);
			connection.setRequestProperty("User-Agent", Constants.USER_AGENT);
		}catch (IOException e) {
			throw new IOException("Cannot connect to the given URL.", e);
		}
		return connection;
	}

	private static HttpURLConnection configurePUTRequest(URL apiCallTargetURL) throws IOException{
		HttpURLConnection connection = null;
		try{
			connection = (HttpURLConnection) apiCallTargetURL.openConnection();
			connection.setDoOutput(true);
			// request method
			connection.setRequestMethod(PUT);

			// request headers
			connection.setRequestProperty("token", Constants.USER_TOKEN);
			connection.setRequestProperty("Content-Type", Constants.CONTENT_TYPE);
		}catch (IOException e) {
			throw new IOException("Cannot connect to the given URL.", e);
		}
		return connection;
	}

	//	private static getOutputStreamFromConnection(HttpURLConnection) throws IOException{
	//		
	//	}

	/**
	 * 
	 * @param connection - An HttpURLConnection to the EZOI API
	 * @return response - the String representation of the API response
	 * @throws IOException
	 */
	private String getJSONResponseAsString(HttpURLConnection connection) throws IOException{
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
			if (connection != null) connection.disconnect();
		}
		return responseString;
	}


	public static void main(String[] args) throws MalformedURLException, IOException{
		APIRequestHandler req = new APIRequestHandler();
		OutputStream os = req.getOutputStreamFromPUTRequest("https://northeasternuniversitysea.ezofficeinventory.com/assets/13/extend_checkout.api");
		System.out.println(os.hashCode());
	}
}