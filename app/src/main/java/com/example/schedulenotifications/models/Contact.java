package com.example.schedulenotifications.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.example.schedulenotifications.AppDatabase;
import java.io.Serializable;

// model class for contacts with properties, Room annotations, and getters & setters
@Entity(tableName = AppDatabase.TABLE_NAME_CONTACTS)
public class Contact implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int contact_id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "country_code")
    public String countryCode;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "frequency")
    public int frequency;

    @ColumnInfo(name = "start_date")
    public long startDate;

    @ColumnInfo(name = "status")
    public int status;

    public Contact() {
    }

    // getters and setters
    public Integer getId() {
        return contact_id;
    }

    public void setId(int id) {
        this.contact_id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setFrequency(int frequency) {this.frequency = frequency; }

    public Integer getFrequency() { return this.frequency; }

    public void setStartDate(long startDate) {this.startDate = startDate; }

    public long getStartDate() {return this.startDate; }

    public void setStatus(int status) {this.status = status; }

    public Integer getStatus() { return this.status; }

    public void setCountryCode(String countryCode) {this.countryCode = countryCode; }

    public String getCountryCode() { return this.countryCode; }

}
