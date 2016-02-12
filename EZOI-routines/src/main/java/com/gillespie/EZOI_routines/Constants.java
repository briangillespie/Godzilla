package com.gillespie.EZOI_routines;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;


public class Constants {
  public static final String ASSETS = "assets/";
  public static final String MEMBERS = "members/";
  public static final String MASTER_URL =
      "https://northeasternuniversitysea.ezofficeinventory.com/";
  public static final String USER_AGENT = "Google Chrome/45.0.2454.85";
  public static final String USER_TOKEN = "7a38ee6c5faf1e2756799fcc71e6d805";
  public static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
  public static final String STATE = "state";
  public static final String SEQUENCE_NUM = "sequence_num";
  public static final String AVAILABLE = "available";
  public static final String CHECKED_OUT = "checked_out";
  public static final String RETIRED = "retired";
  public static final String MAINTENANCE = "maintenance";
  public static final String GET = "GET";
  public static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
  public static final String GROUP_ID = "group_id";
  public static final String CHECKIN_ON = "checkin_due_on";
  public static final String CHECKOUT_ON = "checkout_on";
  public static final String ASSET_ID = "asset_id";
  public static final int FAC_STAFF_GROUPID = 32954;
}
