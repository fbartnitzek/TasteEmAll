package com.fbartnitzek.tasteemall.test.data;

import com.fbartnitzek.tasteemall.data.csv.CsvFileReader;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

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

public class CsvFileReaderTest {

    @Test
    public void testReadCsvFileHeadingAndData() throws Exception {

        //        String fileName = System.getProperty("user.home") + "/beer.csv";
        String fileName = System.getProperty("user.home")
                + "/prog/Udacity_Android/TasteEmAll/csvData/export_Reviews_20160514_021338.csv";

        File file = new File(fileName);
        assertTrue("File " + file.getAbsolutePath() + " can not be found", file.exists() && file.canRead());

        List<String> headers = new ArrayList<>();
        List<List<String>> data = CsvFileReader.readCsvFileHeadingAndData (file, headers);

        assertTrue("invalid csv file", !data.isEmpty());
        assertTrue("number of headerColumns != number of dataColumns", headers.size() == data.get(0).size());

        // print file
        String line = "";
        for (String header : headers) {
            line += header + " §§ ";
        }
        System.out.println(line);

        for (List<String> dataEntry : data) {
            line = "";
            for (String attribute : dataEntry) {
                line += attribute + " §§ ";
            }
            System.out.println(line);
        }
    }
}