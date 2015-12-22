package com.example.fbartnitzek.tasteemall.data;


import com.example.fbartnitzek.tasteemall.data.pojo.Location;

import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Map;

/**
 * Copyright 2015.  Frank Bartnitzek
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

public class Util {
    public static Location queryLocation(String locationString) {
        // TODO: google maps ...?
        return new Location("", "", "location_" + locationString, "", locationString, "");

    }

    public static String getRecord(CSVRecord record, String attribute) {
        try{
            return record.get(attribute);
        } catch (IllegalArgumentException e){
            return null;
        }
    }

    public static String readTimestamp(String record) {
        // TODO ...
        return record;
    }

    public static <V> ArrayList<V> map2List(Map<String, V> map) {
        ArrayList<V> valuesList = new ArrayList<V>(map.values());
        return valuesList;
    }

}
