package com.example.fbartnitzek.tasteemall.data.pojo;

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

public class Producer {

    public static final String PRODUCER_ID = "producer_id";
    public static final String NAME = "producer_name";
    public static final String DESCRIPTION = "producer_description";
    //wikipedia-style for absolute age of a brewery => separate...?
    public static final String WEBSITE = "producer_website";
    public static final String LOCATION = "producer_location";

    private String producerId;
    private String name;
    private String description;
    private String website;
    private String location;

    public Producer(String producerId, String description, String location, String name, String website) {
        this.producerId = producerId;
        this.description= description;
        this.location = location;
        this.name = name;
        this.website = website;
    }

    @Override
    public String toString() {
        return "Producer{" +
                "description='" + description + '\'' +
                ", producerId='" + producerId + '\'' +
                ", name='" + name + '\'' +
                ", website='" + website + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
