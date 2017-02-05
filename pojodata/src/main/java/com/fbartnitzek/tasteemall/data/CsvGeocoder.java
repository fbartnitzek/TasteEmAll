package com.fbartnitzek.tasteemall.data;

import com.fbartnitzek.tasteemall.data.csv.CsvFileReader;
import com.fbartnitzek.tasteemall.data.csv.CsvFileWriter;
import com.fbartnitzek.tasteemall.data.pojo.Location;
import com.fbartnitzek.tasteemall.data.pojo.Producer;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Copyright 2017.  Frank Bartnitzek
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


public class CsvGeocoder {

    private static final String PRODUCER_ALTERNATIVE_ADDRESSES = "producer_alternative_addresses";
    private static final String LOCATION_ALTERNATIVE_ADDRESSES = "location_alternative_addresses";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usasge: jar inputCsvFile outputCsvFile");
            return;
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        if (!inputFile.exists() || !inputFile.canRead()) {
            System.out.println("inputFile '" + inputFile.getAbsolutePath() + "' cannot be read!");
            return;
        }

        List<String> dataColumns = new ArrayList<>();
        List<List<String>> dataEntries = CsvFileReader.readCsvFileHeadingAndData(inputFile, dataColumns);
        System.out.println("columns: " + dataColumns);

        Map<String, Integer> locationColumns = new HashMap<>();
        locationColumns.put(Producer.INPUT, -1);
        locationColumns.put(Producer.LATITUDE, -1);
        locationColumns.put(Producer.LONGITUDE, -1);
        locationColumns.put(Producer.COUNTRY, -1);
        locationColumns.put(Producer.FORMATTED_ADDRESS, -1);
        locationColumns.put(PRODUCER_ALTERNATIVE_ADDRESSES, -1);
        locationColumns.put(Location.INPUT, -1);
        locationColumns.put(Location.LATITUDE, -1);
        locationColumns.put(Location.LONGITUDE, -1);
        locationColumns.put(Location.COUNTRY, -1);
        locationColumns.put(Location.FORMATTED_ADDRESS, -1);
        locationColumns.put(LOCATION_ALTERNATIVE_ADDRESSES, -1);

        for (int i = 0; i < dataColumns.size(); i++) {
            String columnName = dataColumns.get(i);
            if (locationColumns.containsKey(columnName)) {
                locationColumns.put(columnName, i);
            }
        }

        if (locationColumns.get(Producer.INPUT) + locationColumns.get(Location.INPUT) == -2) {
            System.out.println("no matching column '" + Producer.INPUT + "' or '" + Location.INPUT + "' found!");
            System.out.println("found the columns: " + dataColumns);
            return;
        }

        if (locationColumns.get(Producer.INPUT) > 0) {
            appendIfNotFound(dataColumns, locationColumns, Producer.FORMATTED_ADDRESS);
            appendIfNotFound(dataColumns, locationColumns, Producer.LATITUDE);
            appendIfNotFound(dataColumns, locationColumns, Producer.LONGITUDE);
            appendIfNotFound(dataColumns, locationColumns, Producer.COUNTRY);
            appendIfNotFound(dataColumns, locationColumns, PRODUCER_ALTERNATIVE_ADDRESSES);
        }

        if (locationColumns.get(Location.INPUT) > 0) {
            appendIfNotFound(dataColumns, locationColumns, Location.FORMATTED_ADDRESS);
            appendIfNotFound(dataColumns, locationColumns, Location.LATITUDE);
            appendIfNotFound(dataColumns, locationColumns, Location.LONGITUDE);
            appendIfNotFound(dataColumns, locationColumns, Location.COUNTRY);
            appendIfNotFound(dataColumns, locationColumns, LOCATION_ALTERNATIVE_ADDRESSES);
        }

        GeoApiContext context = new GeoApiContext().setApiKey(readApiKey());
        int maxFailures = 5;
        List<List<String>> outEntries = new ArrayList<>();

        for (List<String> dataEntry : dataEntries) {
            boolean changed = false;
            if (locationColumns.get(Producer.INPUT) >= 0) {
                String input = dataEntry.get(locationColumns.get(Producer.INPUT));

                if (input != null && input.length() > 0) {
                    GeocodingResult[] results = null;
                    try {
                        results = GeocodingApi.geocode(context, input).await();
                    } catch (Exception e) {
                        e.printStackTrace();
                        maxFailures--;
                        if (maxFailures <= 0) {
                            throw new RuntimeException("geocoding seems impossible... dataEntry: " + dataEntry, e);
                        }
                    }

                    changed = true;
                    if (results == null || results.length == 0) {
                        updateOrAppend(dataEntry, locationColumns.get(Producer.FORMATTED_ADDRESS), "nothing found for input...");
                    } else {
                        updateOrAppend(dataEntry, locationColumns.get(Producer.FORMATTED_ADDRESS), results[0].formattedAddress);
                        updateOrAppend(dataEntry, locationColumns.get(Producer.LATITUDE), Double.toString(results[0].geometry.location.lat));
                        updateOrAppend(dataEntry, locationColumns.get(Producer.LONGITUDE), Double.toString(results[0].geometry.location.lng));
                        updateOrAppend(dataEntry, locationColumns.get(Producer.COUNTRY), getCountry(results[0].addressComponents));

                        if (results.length > 1) {
                            for (int i = 1; i < results.length; i++) {
                                GeocodingResult otherResult = results[i];
                                appendTextOrColumn(dataEntry, locationColumns.get(PRODUCER_ALTERNATIVE_ADDRESSES), otherResult.formattedAddress);
                            }
                        }
                    }
                }
            }

            if (locationColumns.get(Location.INPUT) >= 0) {
                String input = dataEntry.get(locationColumns.get(Location.INPUT));

                if (input != null && input.length() > 0) {
                    GeocodingResult[] results = null;
                    try {
                        results = GeocodingApi.geocode(context, input).await();
                    } catch (Exception e) {
                        e.printStackTrace();
                        maxFailures--;
                        if (maxFailures <= 0) {
                            throw new RuntimeException("geocoding seems impossible... dataEntry: " + dataEntry, e);
                        }
                    }

                    changed = true;
                    if (results == null || results.length == 0) {
                        updateOrAppend(dataEntry, locationColumns.get(Location.FORMATTED_ADDRESS), "nothing found for input...");
                    } else {
                        updateOrAppend(dataEntry, locationColumns.get(Location.FORMATTED_ADDRESS), results[0].formattedAddress);
                        updateOrAppend(dataEntry, locationColumns.get(Location.LATITUDE), Double.toString(results[0].geometry.location.lat));
                        updateOrAppend(dataEntry, locationColumns.get(Location.LONGITUDE), Double.toString(results[0].geometry.location.lng));
                        updateOrAppend(dataEntry, locationColumns.get(Location.COUNTRY), getCountry(results[0].addressComponents));

                        if (results.length > 1) {
                            for (int i = 1; i < results.length; i++) {
                                GeocodingResult otherResult = results[i];
                                appendTextOrColumn(dataEntry, locationColumns.get(LOCATION_ALTERNATIVE_ADDRESSES), otherResult.formattedAddress);
                            }
                        }
                    }
                }
            }

            if (changed) {
                System.out.println(dataEntry);
            }
            outEntries.add(dataEntry);
        }


        String[] extendedColumns = extendColumns(dataColumns, locationColumns);

        String error = CsvFileWriter.writeFile(extendedColumns, outEntries, outputFile);
        if (error != null) {
            System.out.println("error while saving " + outputFile);
        }

    }

    private static String[] extendColumns(List<String> dataColumns, Map<String, Integer> locationColumns) {
        List<String> columns = new ArrayList<>();
        columns.addAll(dataColumns);

        for (Map.Entry<String, Integer> e : locationColumns.entrySet()) {
            if (e.getValue() >= 0) {
                System.out.println("appending " + e.getKey() + ", gap: " + (e.getValue() - (columns.size() - 1)));
                updateOrAppend(columns, e.getValue(), e.getKey());
            }
        }

        return columns.toArray(new String[columns.size()]);
    }

    private static void updateOrAppend(List<String> dataEntry, Integer index, String text) {
        if (index >= dataEntry.size()) {
            int neededEmptyEntries = index - (dataEntry.size() - 1);
            for (int i = 0; i < neededEmptyEntries; ++i) {
                dataEntry.add("");
            }
        }
        dataEntry.set(index, text);
    }


    private static void appendTextOrColumn(List<String> dataEntry, Integer index, String text) {
        if (index >= dataEntry.size()) {
            int neededEmptyEntries = index - (dataEntry.size() - 1);
            for (int i = 0; i < neededEmptyEntries; ++i) {
                dataEntry.add("");
            }
        }
        String prev = dataEntry.get(index);
        if (prev != null && !prev.isEmpty()) {
            dataEntry.set(index, prev + "; " + text);
        } else {
            dataEntry.set(index, text);
        }
    }

    private static String getCountry(AddressComponent[] addressComponents) {
        for (AddressComponent a: addressComponents){
            for (AddressComponentType type : a.types) {
                if ("country".equals(type.name().toLowerCase())) {
                    return a.longName;
                }
            }
        }
        return "NONE FOUND";
    }

    private static void appendIfNotFound(List<String> dataColumns, Map<String, Integer> locationColumns, String columnName) {
        if (locationColumns.get(columnName) < 0) {
            int max = Collections.max(locationColumns.values());
            int size = dataColumns.size() -1;
            locationColumns.put(columnName, (size > max ? size : max) + 1);
        }
    }

    private static String readApiKey() {
        Properties properties = new Properties();
        String path = System.getProperty("user.home") + "/.gradle/gradle.properties";
        try {
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String value = properties.getProperty("TasteEmAllGoogleMapsGeocodeApiKey");
        return unquote(value);
    }

    public static String unquote(String s) {
        if (s != null && (
                (s.startsWith("'") && s.endsWith("'")) || (s.startsWith("\"") && s.endsWith("\"")) )) {
            s = s.substring(1, s.length() - 1);
        }
        return s;
    }
}
