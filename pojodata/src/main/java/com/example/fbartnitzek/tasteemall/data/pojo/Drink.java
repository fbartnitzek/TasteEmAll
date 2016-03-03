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

public class Drink {

    public static final String DRINK_ID = "drink_id";
    public static final String NAME = "drink_name";
    public static final String SPECIFICS = "drink_specificcs"; //alcohol by volume, ...
    public static final String STYLE = "beer_style";
    public static final String TYPE = "drink_style";
    // TODO: add ingredients...

    public static final String PRODUCER_ID = "drink_producer_id";

    private String drinkId;
    private String name;
    private String specifics;
    private String style;
    private String type;

    private String producerId;

    public Drink(String drinkId, Producer producer, String name, String style, String specifics, String type) {

        this.drinkId = drinkId;
        this.producerId = producer.getProducerId();
        this.name = name;
        this.style = style;
        this.specifics = specifics;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Drink{" +
                "drinkId='" + drinkId + '\'' +
                ", name='" + name + '\'' +
                ", specifics='" + specifics + '\'' +
                ", style='" + style + '\'' +
                ", type='" + type + '\'' +
                ", producerId='" + producerId + '\'' +
                '}';
    }

    public String getDrinkId() {
        return drinkId;
    }

    public void setDrinkId(String drinkId) {
        this.drinkId = drinkId;
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

    public String getSpecifics() {
        return specifics;
    }

    public void setSpecifics(String specifics) {
        this.specifics = specifics;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
