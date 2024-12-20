package com.fbartnitzek.tasteemall;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

public class TestHelper {

    @Test
    public void testAddressHelper() {
        String address = "geocode_me_lat_49.9922412_long_8.660975";

        assertTrue(Utils.checkGeocodeAddressFormat(address));
        try {
            double lat = Utils.getLatitude(address);
            double lon = Utils.getLongitude(address);
            System.out.println("lat: " + lat + ", long: " + lon);
        } catch (NumberFormatException e) {
            fail("Utils.getLat/Long with Exception");
        }

        assertFalse(Utils.checkGeocodeAddressFormat("geocode_me_lat49.9922412_long_8.660975"));

    }

    @Test
    public void testTimeHelper() throws Exception {
        String iso= "2016-05-12 22:05:59";

        Date date = Utils.getDate(iso);
        assertNotNull(date);
        assertEquals("12.05.2016", Utils.getFormattedDate(date, "dd.MM.yyyy"));
        assertEquals("22:05:59", Utils.getFormattedDate(date, "HH:mm:ss"));
        assertEquals("12.05.16 22:05", Utils.getFormattedDate(date, "dd.MM.yy HH:mm"));

    }
}
