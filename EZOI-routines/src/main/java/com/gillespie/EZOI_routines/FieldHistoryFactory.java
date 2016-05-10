package com.gillespie.EZOI_routines;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by Brian on 5/9/2016.
 */
public class FieldHistoryFactory {

    private FieldHistory fieldHistory;

    public FieldHistoryFactory(String fieldHistoryString){
        this.setFieldHistory(fieldHistoryString);
    }

    public FieldHistory getFieldHistory(){ return this.fieldHistory; }

    public void setFieldHistory(String fieldHistoryString){
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            // Convert JSON string to Object
            this.fieldHistory = mapper.readValue(fieldHistoryString, FieldHistory.class);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        APIRequestHandler req = new APIRequestHandler();
        String assetID = "120";
        String fieldID = "10080";
        String apiURL = "https://northeasternuniversitysea.ezofficeinventory.com/assets/" +
                assetID +
                "/custom_attribute_history.api?custom_attribute_id=" + fieldID;
        try {
            String response = req.getAPIResponse(apiURL);
            FieldHistoryFactory fhc = new FieldHistoryFactory(response);
            FieldHistory fh = fhc.getFieldHistory();
            System.out.println(fh.tallyUsersAndAmountsOwed().toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
