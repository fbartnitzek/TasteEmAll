package com.example.fbartnitzek.tasteemall.test.data;

import com.example.fbartnitzek.tasteemall.data.csv.CsvFileReader;

import org.junit.Test;



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
    public void testCsvReader() {
        String fileName = System.getProperty("user.home") + "/beer.csv";

        System.out.println("reading csv file:");
        System.out.println(CsvFileReader.readCsvFile(fileName, "frank", '§', System.getProperty("user.home") + "/beer.json"));
        System.out.println();
        System.out.println("finished csv file reading");

    }
}