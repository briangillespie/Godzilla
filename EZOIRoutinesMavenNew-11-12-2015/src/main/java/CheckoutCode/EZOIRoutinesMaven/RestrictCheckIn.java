package CheckoutCode.EZOIRoutinesMaven;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

public class RestrictCheckIn {
  private final int PAGES = 3;

  public static void main(String[] args) throws Exception {

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

}
