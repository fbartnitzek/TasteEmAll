package com.fbartnitzek.tasteemall.location;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressData implements Parcelable {
    private double latitude;
    private double longitude;
    private String countryCode;
    private String countryName;
    private String formatted;
    private String origInput;

    public AddressData(double latitude, double longitude, String countryCode, String countryName, String formatted, String origInput) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.formatted = formatted;
        this.origInput = origInput;
    }

    protected AddressData(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        countryCode = in.readString();
        countryName = in.readString();
        formatted = in.readString();
        origInput = in.readString();
    }

    @Override
    public String toString() {
        return "AddressData{" +
                "formatted='" + formatted + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(countryCode);
        parcel.writeString(countryName);
        parcel.writeString(formatted);
        parcel.writeString(origInput);
    }

    public static final Creator<AddressData> CREATOR = new Creator<AddressData>() {
        @Override
        public AddressData createFromParcel(Parcel in) {
            return new AddressData(in);
        }

        @Override
        public AddressData[] newArray(int size) {
            return new AddressData[size];
        }
    };

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public String getOrigInput() {
        return origInput;
    }

    public void setOrigInput(String origInput) {
        this.origInput = origInput;
    }
}
