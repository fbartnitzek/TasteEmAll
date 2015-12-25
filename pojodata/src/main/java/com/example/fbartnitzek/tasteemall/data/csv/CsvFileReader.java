package com.example.fbartnitzek.tasteemall.data.csv;


import com.example.fbartnitzek.tasteemall.data.Util;
import com.example.fbartnitzek.tasteemall.data.pojo.Beer;
import com.example.fbartnitzek.tasteemall.data.pojo.Brewery;
import com.example.fbartnitzek.tasteemall.data.pojo.Data;
import com.example.fbartnitzek.tasteemall.data.pojo.Location;
import com.example.fbartnitzek.tasteemall.data.pojo.Review;
import com.example.fbartnitzek.tasteemall.data.pojo.User;
import com.google.gson.Gson;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class CsvFileReader {

    private static final String BREWERY_LOCATION = "brewery_location";
    private static final String REVIEW_LOCATION = "review_location";

    /**
     * reads csv file (header, custom delimiter, standard-attributes for brewery, beer and review and 2 location-strings)
     * and returns a "db-ready" json-file
     * @param filePath
     * @param userName
     * @param delimiter
     * @param targetFile
     * @return
     */
    public static String readCsvFile(String filePath, String userName, char delimiter, String targetFile) {

        CSVParser csvParser = null;

        try {
            HashMap<String, Location> locations = new HashMap<>();
            HashMap<String, Brewery> breweries = new HashMap<>();
            HashMap<String, Beer> beers = new HashMap<>();
            List <Review> reviews = new ArrayList<>();
            Data data;
            User user = new User("", null, userName, userName, "user_" + userName);

            int empty = 0;

            // finally the right way: http://www.journaldev.com/2544/java-csv-parserwriter-example-using-opencsv-apache-commons-csv-and-supercsv
            //Create the CSVFormat object
            CSVFormat format = CSVFormat.RFC4180.withHeader().withDelimiter(delimiter);

            //initialize the MyCSVParser object
            csvParser= new CSVParser(new FileReader(filePath), format);

            int reviewId = 0;
            for (CSVRecord record : csvParser) {
                String breweryName = record.get(Brewery.NAME);
                String beerName = record.get(Beer.NAME);
                if (breweryName != null && !breweryName.isEmpty()
                        && beerName != null && !beerName.isEmpty()) {
//                    System.out.println("brewery: " + breweryName + ", beer: " + beerName);

                    // build all objects ...
                    String breweryLocString = record.get(BREWERY_LOCATION);
                    Location breweryLocation = null;
                    if (breweryLocString!= null && !breweryLocString.isEmpty()) {
                        if (!locations.containsKey(breweryLocString)) {
                            breweryLocation = Util.queryLocation(breweryLocString);
                            locations.put(breweryLocString, breweryLocation);
                        } else {
                            breweryLocation = locations.get(breweryLocString);
                        }
                    }
                    String reviewLocString = record.get(REVIEW_LOCATION);
                    Location reviewLocation = null;
                    if (reviewLocString != null && !reviewLocString.isEmpty()) {

                        if (!locations.containsKey(reviewLocString)) {
                            reviewLocation = Util.queryLocation(reviewLocString);
                            locations.put(reviewLocString, reviewLocation);
                        } else {
                            reviewLocation = locations.get(reviewLocString);
                        }
                    }

                    Brewery brewery = null;
                    if (breweries.containsKey(breweryName)) {
                        // TODO: try update
                        brewery = breweries.get(breweryName);
                    } else {
                        brewery = new Brewery(
                                "brewery_" + breweryName,
                                Util.getRecord(record, Brewery.INTRODUCED),
                                        breweryLocation,
                                        breweryName,
                                Util.getRecord(record, Brewery.WEBSITE));
                        breweries.put(breweryName, brewery);
                    }

                    Beer beer = null;
                    String beerFullName = breweryName + "/" + beerName;
                    if (beers.containsKey(beerFullName)) {
                        // TODO: try update
                        beer = beers.get(beerFullName);
                    } else {
                        beer = new Beer(
                                Util.getRecord(record, Beer.ABV),
                                beerFullName,
                                brewery,
                                Util.getRecord(record, Beer.DEGREES_PLATO),
                                Util.getRecord(record, Beer.IBU),
                                beerName,
                                Util.getRecord(record, Beer.STAMMWUERZE),
                                Util.getRecord(record, Beer.STYLE));
                        beers.put(beerFullName, beer);
                    }

                    reviews.add(new Review(
                            beer,
                            Util.getRecord(record, Review.DESCRIPTION),
                            reviewLocation,
                            Util.getRecord(record, Review.LOOK),
                            Util.getRecord(record, Review.RATING),
                            "Review_" + userName + "_" + ++reviewId,
                            Util.getRecord(record, Review.SMELL),
                            Util.getRecord(record, Review.TASTE),
                            Util.readTimestamp(Util.getRecord(record, Review.TIMESTAMP)),
                            user
                    ));

                } else {
                    empty++;
                }
            }

            // TODO: return json map of list of object

//            System.out.println("empty lines: " + empty);
//            System.out.println("reviews: " + reviews.size());
            String result = "";
            data = new Data(
                    Util.map2List(beers),
                    Util.map2List(breweries),
                    Util.map2List(locations),
                    reviews, user);

            Gson gson = new Gson();

            // convert java object to JSON format,
            // and returned as JSON formatted string
            String json = gson.toJson(data);

            try {
                //write converted json data to a file named "file.json"
                // TODO: stream-writer...

//                OutputStream os = new FileOutputStream("turnip");
//                Writer writer = new OutputStreamWriter(os,"UTF-8");
//                writer=new BufferedWriter(writer);
//                writer.write("Another string in UTF-8");


                FileWriter writer = new FileWriter(targetFile);
                writer.write(json);
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Review review: reviews) {
                result += System.lineSeparator() + review.toString();
//                System.out.println(review.toString());
            }
            return result;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                csvParser.close();  //close the parser
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;

    }
}
