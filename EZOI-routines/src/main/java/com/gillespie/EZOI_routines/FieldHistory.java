package com.gillespie.EZOI_routines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
/**
 * Created by Brian on 5/9/2016.
 */
public class FieldHistory {

    private static final String CHARGE = "owe";
//    private static final String PAYMENT = "payment";

    private List<CustomField> custom_attribute;

    public List<CustomField> getCustom_attribute(){
        return this.custom_attribute;
    }

    public void setCustom_attribute(List<CustomField> fieldHistoryList){
        this.custom_attribute = fieldHistoryList;
    }

    public String toString(){
        String str = "{field history: [\n\t";
        for(CustomField field : this.custom_attribute){
            str += field.toString() + "\n\t";
        }
        str = str.substring(0,str.length()-2) + "\n]}";
        return str;
    }

    public Collection<Entry<String, Double>> tallyUsersAndAmountsOwed(){
        HashMap<String, Double> usersAndCharges = new HashMap<String, Double>();
        String[] userItemState;
        String userID;
        String groupID;
        String state;
        for(CustomField entry : this.custom_attribute){
            userItemState = entry.getValue().split(",");
            userID = userItemState[0];
            groupID = userItemState[1];
            state = userItemState[2];

            // TODO: Get charge price from groupID
            Double charge = state.equals(CHARGE) ? 25.0 : -1.0 * 25.0;
            if (usersAndCharges.get(userID) != null){
                usersAndCharges.put(userID, usersAndCharges.get(userID) + charge);
            }
            else{
                usersAndCharges.put(userID, charge);
            }
        }
        return usersAndCharges.entrySet();
    }
}