package com.gillespie.EZOI_routines;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.TypeFactory;
import org.joda.time.DateTime;
import org.joda.time.LocalTime;

public class PenalizeLateCheckIns {
  private static final String ASSETS = "assets/";
  private static final String MEMBERS = "members/";
  private static final String MASTER_URL =
      "https://northeasternuniversitysea.ezofficeinventory.com/";
  private final String USER_AGENT = "Google Chrome/45.0.2454.85";
  private final String USER_TOKEN = "7a38ee6c5faf1e2756799fcc71e6d805";
  // private final static DateTimeFormatter FORMAT =
  // DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
  private final int MAX_HOURS = 5;
  private final int GRACE_TIME_MINUTES = 15;
  private final LocalTime END_TIME = new LocalTime(22, 0);
  // private final int FAC_STAFF_GROUPID = 32954;
  private final String CHECKED_OUT = "checked_out";

  public static void main(String[] args) throws Exception {
    PenalizeLateCheckIns p = new PenalizeLateCheckIns();
    p.penalizeLateCheckInAssets();
//    RestrictCheckIn restrict = new RestrictCheckIn();
//    ArrayList<Integer> allAssetIDs = restrict.getAllActiveAssetIDs();
//    ArrayList<Transaction> transactionsOfTheDay = new ArrayList<Transaction>();
//    Transaction t = p.getTransactionsOfTheDay(13).get(0);
//    System.out.println(t);
//    p.sendLateEmail(t);
  }

  public void penalizeLateCheckInAssets() throws Exception {
    RestrictCheckIn restrict = new RestrictCheckIn();
    ArrayList<Integer> allAssetIDs = restrict.getAllActiveAssetIDs();
    int assetListSize = allAssetIDs.size();

    for (int i = 0; i < assetListSize; i++) {
      int assetID = allAssetIDs.get(i);
      ArrayList<Transaction> transactionsOfTheDay = getTransactionsOfTheDay(assetID);
      int numberOfTransactions = transactionsOfTheDay.size();

      // being dealt separately because of date conflict
      if (isAssetBeingHeld(assetID)) {
        Transaction thisTransaction = getLatestTransaction(assetID);
        System.out.print("Asset being held: ");
        System.out.println(thisTransaction);
//        sendLateEmail(thisTransaction);
//        revokeUserLoginAndUpdateNotes(thisTransaction.getStudentID());
      }

      for (int j = 0; j < numberOfTransactions; j++) {
        Transaction thisTransaction = transactionsOfTheDay.get(j);

        if (thisTransaction.is_checkout()) {
          DateTime checkOutDateTime = thisTransaction.getCheckOutDateTime();
          DateTime studentCheckInDateTime =
              getTransactionCheckInDateTime(thisTransaction, transactionsOfTheDay);
          DateTime currentCheckInDateTime = thisTransaction.getCheckInOn();
          DateTime maxCheckInDateTime = checkOutDateTime.plusHours(MAX_HOURS);
          DateTime endDateTime =
              checkOutDateTime.withTime(END_TIME.getHourOfDay(), END_TIME.getMinuteOfHour(),
                  END_TIME.getSecondOfMinute(), END_TIME.getMillisOfSecond());

          DateTime checkInDateTime =
              getEarliestDateTime(currentCheckInDateTime, maxCheckInDateTime, endDateTime);
          checkInDateTime = checkInDateTime.plusMinutes(GRACE_TIME_MINUTES);

          if (studentCheckInDateTime == null) {
            continue;
          }

          if (studentCheckInDateTime.compareTo(checkInDateTime) == 1) {
            System.out.print("Checked out item: ");
            System.out.println(thisTransaction);
//            sendLateEmail(thisTransaction);
//            revokeUserLoginAndUpdateNotes(thisTransaction.getStudentID());
          }
        }
      }
    }
  }

  private DateTime getEarliestDateTime(DateTime date1, DateTime date2, DateTime date3) {
    DateTime earliest;// = new DateTime();
    if ((date1.compareTo(date2) == -1) && (date1.compareTo(date3) == -1)) {
      earliest = date1;
    } else if ((date2.compareTo(date1) == -1) && (date2.compareTo(date3) == -1)) {
      earliest = date2;
    } else
      earliest = date3;
    return earliest;
  }

  private Transaction getLatestTransaction(int assetID) throws Exception {
    ArrayList<Transaction> currentTransactions = new ArrayList<Transaction>();

    String url = MASTER_URL + ASSETS + assetID + "/history_paginate.api?page=1";

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    // request method
    con.setRequestMethod("GET");

    // request headers
    con.setRequestProperty("token", USER_TOKEN);
    con.setRequestProperty("User-Agent", USER_AGENT);

    // reading input stream
    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    String response = "";
    while ((inputLine = in.readLine()) != null) {
      response += inputLine;
    }
    in.close();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    String arrayResponse = response.substring(11);
    CollectionType constructCollectionType =
        TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, Transaction.class);
    currentTransactions = mapper.readValue(arrayResponse, constructCollectionType);

    return currentTransactions.get(0);
  }


  private boolean isAssetBeingHeld(int assetID) throws Exception {
    RestrictCheckIn r = new RestrictCheckIn();
    String state = r.getAssetState(assetID);
    return (state.equals(CHECKED_OUT));
  }

  private DateTime getTransactionCheckInDateTime(Transaction thisTransaction,
      ArrayList<Transaction> transactionsOfTheDay) {
    int size = transactionsOfTheDay.size();
    DateTime checkInDateTime = new DateTime();
    for (int i = 0; i < size; i++) {
      if (thisTransaction.equals(transactionsOfTheDay.get(i))) {
        if (i == 0)
          return null;
        Transaction checkIn = transactionsOfTheDay.get(i - 1);
        checkInDateTime = checkIn.getCheckInDateTime();
      }
    }
    return checkInDateTime;
  }

  // curl -H "token:<COMPANY_TOKEN>" -X GET \
  // https://<SUBDOMAIN>.ezofficeinventory.com/assets/<ASSET#>/history_paginate.api? \
  // page=<PAGE_NUM | DEFAULT = 1>
  private ArrayList<Transaction> getTransactionsOfTheDay(int assetID) throws Exception {
    int i = 1;
    ArrayList<Transaction> currentTransactions = new ArrayList<Transaction>();
    ArrayList<Transaction> allTransactions = new ArrayList<Transaction>();

    while (true) {
      String url = MASTER_URL + ASSETS + assetID + "/history_paginate.api?page=" + i;

      URL obj = new URL(url);
      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
      // request method
      con.setRequestMethod("GET");

      // request headers
      con.setRequestProperty("token", USER_TOKEN);
      con.setRequestProperty("User-Agent", USER_AGENT);

      // reading input stream
      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      String inputLine;
      String response = "";
      while ((inputLine = in.readLine()) != null) {
        response += inputLine;
      }
      in.close();

      DateTime assetActiveDateTime = new DateTime().minusDays(1);
//      System.out.println(assetActiveDateTime);
      int day = assetActiveDateTime.getDayOfMonth();
      int month = assetActiveDateTime.getMonthOfYear();
      int year = assetActiveDateTime.getYear();
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      String emptyHistory = "{\"history\":[]";
      if (!response.substring(0,13).equals(emptyHistory)) {
        String arrayResponse = response.substring(11, response.length() - 1);
        CollectionType constructCollectionType =
                TypeFactory.defaultInstance().constructCollectionType(ArrayList.class,
                        Transaction.class);
        currentTransactions = mapper.readValue(arrayResponse, constructCollectionType);
        for (Transaction t : currentTransactions){
          DateTime checkoutDateTime = t.getCheckOutDateTime();
          if (day == checkoutDateTime.getDayOfMonth() &&
                  month == checkoutDateTime.getMonthOfYear() &&
                  year == checkoutDateTime.getYear()) {
            allTransactions.add(t);
          }
        }
//        System.out.println(allTransactions);
        currentTransactions.clear();
      } else {
        break;
      }
      i++;
    }
    return allTransactions;
  }
//  private ArrayList<Transaction> getTransactionsOfTheDay(int assetID) throws Exception {
//    int i = 1;
//    ArrayList<Transaction> currentTransactions = new ArrayList<Transaction>();
//    ArrayList<Transaction> allTransactions = new ArrayList<Transaction>();
//
//    while (true) {
//      String url = MASTER_URL + ASSETS + assetID + "/history_paginate.api?page=" + i;
//
//      URL obj = new URL(url);
//      HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//      // request method
//      con.setRequestMethod("GET");
//
//      // request headers
//      con.setRequestProperty("token", USER_TOKEN);
//      con.setRequestProperty("User-Agent", USER_AGENT);
//
//      // reading input stream
//      BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//      String inputLine;
//      String response = "";
//      while ((inputLine = in.readLine()) != null) {
//        response += inputLine;
//      }
//      in.close();
//
//      ObjectMapper mapper = new ObjectMapper();
//      mapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//      String emptyHistory = "{\"history\":[]}";
//      if (!response.equals(emptyHistory)) {
//        String arrayResponse = response.substring(11, response.length() - 1);
//        CollectionType constructCollectionType =
//            TypeFactory.defaultInstance().constructCollectionType(ArrayList.class,
//                Transaction.class);
//        currentTransactions = mapper.readValue(arrayResponse, constructCollectionType);
////        for (Transaction t : currentTransactions){
////          System.out.println(t.toString());
////        }
//        allTransactions.addAll(currentTransactions);
//        System.out.println(allTransactions);
//        DateTime currentDateTime = new DateTime();
//
//        DateTime assetActiveDateTime = currentDateTime.minusDays(1);
//        // DateTime assetActiveDateTime = currentDateTime;
//        int day = assetActiveDateTime.getDayOfMonth();
//        int month = assetActiveDateTime.getMonthOfYear();
//        int year = assetActiveDateTime.getYear();
//
//        DateTime lastTransactionDateTime = currentTransactions.get(4).getCheckOutDateTime();
//        System.out.println(lastTransactionDateTime);
//        int lastTransactionDay = lastTransactionDateTime.getDayOfMonth();
//        int lastTransactionMonth = lastTransactionDateTime.getMonthOfYear();
//        int lastTransactionYear = lastTransactionDateTime.getYear();
//        System.out.println(allTransactions);
//        if (!(day == lastTransactionDay && month == lastTransactionMonth && year == lastTransactionYear)) {
//          removePreviousDayTransactions(allTransactions, day, month, year);
//          System.out.println(allTransactions);
//          return allTransactions;
//        }
//      } else {
//        break;
//      }
//      i++;
//    }
//    return allTransactions;
//  }

  private void removePreviousDayTransactions(ArrayList<Transaction> allTransactions, int day,
      int month, int year) {
    int size = allTransactions.size();
    for (int i = size - 1; i >= 0; i--) {
      DateTime individualDateTime = allTransactions.get(i).getCheckOutDateTime();
      int individualDay = individualDateTime.getDayOfMonth();
      int individualMonth = individualDateTime.getMonthOfYear();
      int individualYear = individualDateTime.getYear();
      if (!(day == individualDay && month == individualMonth && year == individualYear))
        allTransactions.remove(i);
    }
  }

  // FIXME: change function to revoke login not deactivate user
  // TODO: test function to see if it works
  // curl -H "token:<COMPANY_TOKEN>" -X PUT \
  // https://<SUBDOMAIN>.ezofficeinventory.com/members/<USER_ID>/deactivate.api
  private void revokeLogin(long studentID) throws Exception {
    String url = MASTER_URL + MEMBERS + studentID + ".api";

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setDoOutput(true);
    // request method
    con.setRequestMethod("PUT");

    // request headers
    con.setRequestProperty("token", USER_TOKEN);
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    String data = "user[login_enabled]=0";

    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

    writer.write(data);
    writer.close();

    con.getResponseCode();
  }

  // curl -H "token:<COMPANY_TOKEN>" -X PUT \
  // -d "user[description]=<Late Fee>" \
  // https://<SUBDOMAIN>.ezofficeinventory.com/members/<USER_ID>.api
  private void revokeUserLoginAndUpdateNotes(long studentID) throws Exception {
    int fee = getPendingFee(studentID);
    fee += 25;

    String url = MASTER_URL + MEMBERS + studentID + ".api";

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    con.setDoOutput(true);
    // request method
    con.setRequestMethod("PUT");

    // request headers
    con.setRequestProperty("token", USER_TOKEN);
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    String data = "user[description]=Owes $" + fee;

    OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

    writer.write(data);
    writer.close();

    con.getResponseCode();

    revokeLogin(studentID);

  }

  private int getPendingFee(long studentID) throws Exception {

    String url = MASTER_URL + MEMBERS + studentID + ".api";

    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    // request method
    con.setRequestMethod("GET");

    // request headers
    con.setRequestProperty("token", USER_TOKEN);
    con.setRequestProperty("User-Agent", USER_AGENT);

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
    String description = "";
    while (parser.nextToken() != JsonToken.END_OBJECT) {
      String token = parser.getText();

      if ("description".equals(token)) {
        parser.nextToken();
        description = parser.getText();
      }
    }

    if (description.equals(""))
      return 0;

    description = description.substring(6, description.length());
    return Integer.parseInt(description);
  }

//  private void sendLateEmail(Transaction thisTransaction) {
//    long studentID = thisTransaction.getStudentID();
//    String studentEmail;
//    APIRequestHandler req = new APIRequestHandler();
//    try {
////      studentEmail = req.getAPIResponse("https://northeasternuniversitysea.ezofficeinventory.com/members/" + studentID + ".api");
//      studentEmail = thisTransaction.getStudentEmail();
//      System.out.println(studentEmail);
//      LateFeeEmailer lfe = new LateFeeEmailer();
//      lfe.sendMail(studentEmail);
//    }catch(Exception e){
//      e.printStackTrace();
//      System.out.println("Unable to send email to " + studentID);
//    }
//  }
}
