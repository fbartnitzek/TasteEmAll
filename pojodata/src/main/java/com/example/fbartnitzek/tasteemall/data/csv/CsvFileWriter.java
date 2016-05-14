package com.example.fbartnitzek.tasteemall.data.csv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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

public class CsvFileWriter {

    //based on: https://examples.javacodegeeks.com/core-java/apache/commons/csv-commons/writeread-csv-files-with-apache-commons-csv-example/

    public static final char DELIMITER = ';';
    public static final char QUOTE_CHAR = '"';

    // Umlaute working - just not in excel android app :-p
    public static CSVFormat mFormat = CSVFormat.RFC4180.withDelimiter(DELIMITER).withQuote(QUOTE_CHAR);

    public static String writeFile(String[] headers, List<List<String>> entries, File file) {

        FileWriter fileWriter = null;
        CSVPrinter csvFilePrinter = null;
        String msg = null;
        try {
            fileWriter = new FileWriter(file);  //initialize FileWriter object
            csvFilePrinter = new CSVPrinter(fileWriter, mFormat);   //initialize CSVPrinter object
            csvFilePrinter.printRecord(headers);    //Create CSV file header

            //Write a new student object list to the CSV file
            for (List<String> dataEntry : entries) {
                csvFilePrinter.printRecord(dataEntry);
            }

        } catch (Exception e) {
            e.printStackTrace();
            msg = "Error in CsvFileWriter !!!";
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.flush();
                    fileWriter.close();
                }
                if (csvFilePrinter != null) {
                    csvFilePrinter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                msg = "Error while flushing/closing fileWriter/csvPrinter !!!";

            }
        }
        return msg;
    }
}
