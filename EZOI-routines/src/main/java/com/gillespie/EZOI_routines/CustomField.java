package com.gillespie.EZOI_routines;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Brian on 5/9/2016.
 */
public class CustomField {

    private final static DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    private String value;
    private String created_at;
    private String updated_at;
    private long number_value;
    private String date_value;
    private String options_value;
    private long line_item_id;
    private long company_id;

    // GETTERS
    public String getValue() { return this.value; }

    public DateTime getCreated_at() {
        String createdAtStr = this.created_at;
        DateTime createdAtDateTime = FORMAT.parseDateTime(createdAtStr);
        return createdAtDateTime;
    }

    public DateTime getUpdated_at() {
        String updatedAtStr = this.updated_at;
        DateTime updatedAtDateTime = FORMAT.parseDateTime(updatedAtStr);
        return updatedAtDateTime;
    }

    public long getNumber_value() { return this.number_value; }

    public String getDate_value() { return this.date_value; }

    public String getOptions_value() { return this.options_value; }

    public long getLine_item_id() { return this.line_item_id; }

    public long getCompany_id() { return this.company_id; }

    // SETTERS
    public void setValue(String value) { this.value = value; }

    //OTHER
    public String toString(){
        return "{ value:" + this.value + ", created_at:" + this.created_at + ", updated_at:" + this.updated_at
                + ", number_value:" + this.number_value + ", date_value:" + this.date_value + ", options_value:"
                + this.options_value + ", line_item_id:" + this.line_item_id + ", company_id:" + this.company_id + " }";
    }
}
