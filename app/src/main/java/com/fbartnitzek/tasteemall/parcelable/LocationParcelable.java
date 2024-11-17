package com.fbartnitzek.tasteemall.parcelable;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.fbartnitzek.tasteemall.Utils;

import org.jetbrains.annotations.NotNull;

/**
 * Copyright 2016.  Frank Bartnitzek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class LocationParcelable implements Parcelable {

    public static final int INVALID_ID = -1;
    private final int id;
    private final String locationId;
    private final double latitude;
    private final double longitude;
    private final String country;
    private final String input;
    private final String formattedAddress;
    private final String description;

    public LocationParcelable(int id, String country, String locationId, double latitude, double longitude, String input, String formattedAddress, String description) {
        this.id = id;
        this.country = country;
        this.locationId = locationId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.input = input;
        this.formattedAddress = formattedAddress;
        this.description = description;
    }

    protected LocationParcelable(Parcel in) {
        id = in.readInt();
        locationId = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        country = in.readString();
        input = in.readString();
        formattedAddress = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(locationId);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(country);
        dest.writeString(input);
        dest.writeString(formattedAddress);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LocationParcelable> CREATOR = new Creator<LocationParcelable>() {
        @Override
        public LocationParcelable createFromParcel(Parcel in) {
            return new LocationParcelable(in);
        }

        @Override
        public LocationParcelable[] newArray(int size) {
            return new LocationParcelable[size];
        }
    };

    public double getLongitude() {
        return longitude;
    }

    public String getLocationId() {
        return locationId;
    }

    public double getLatitude() {
        return latitude;
    }

    public int getId() {
        return id;
    }

    public String getInput() {
        return input;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getDescription() {
        return description;
    }

    public String getCountry() {
        return country;
    }

    public boolean isGeocodeable() {
        return Utils.isValidLatLong(latitude, longitude) || !TextUtils.isEmpty(input);
    }

    @NotNull
    @Override
    public String toString() {
        return "LocationParcelable{" +
                "country='" + country + '\'' +
                ", locationId='" + locationId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", id=" + id +
                ", input='" + input + '\'' +
                ", formattedAddress='" + formattedAddress + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
