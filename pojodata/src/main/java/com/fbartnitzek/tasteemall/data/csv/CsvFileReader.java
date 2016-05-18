package com.fbartnitzek.tasteemall.data.csv;


import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
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

public class CsvFileReader {

    private static final String PRODUCER_LOCATION = "producer_location";
    private static final String REVIEW_LOCATION = "review_location";


    /**
     * reads CSV file in data and headers with same count, uses CSV_Format RFC4180 (, and "")
     * @param file file to read
     * @param headers expected columns
     * @return data
     */
    public static List<List<String>> readCsvFileHeadingAndData(File file, List<String> headers) {

        List<List<String>> data = new ArrayList<>();

        CSVParser csvParser = null;
        Reader csvReader = null;
        try {
            csvReader = new FileReader(file);
            csvParser = new CSVParser(csvReader, CsvFileWriter.CSV_FORMAT_RFC4180.withHeader());
            Map<String, Integer> headerMap = csvParser.getHeaderMap();

            // 0 columns seems impossible, but valid

            // ordered columns instead unordered set (for insert)!
            headers.addAll(headerMap.keySet());
            for (CSVRecord record : csvParser) {
                List<String> dataEntry = new ArrayList<>();
                for (int i=0 ; i < headers.size(); ++i) {
                    dataEntry.add(record.get(headers.get(i)));
                }
                data.add(dataEntry);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                csvReader.close();
                csvParser.close();
            } catch (IOException e) {
//                System.out.println("Error while closing fileReader/csvFileParser !!!");
                e.printStackTrace();
            }
        }

        return data;
    }


}
