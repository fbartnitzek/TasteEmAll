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

public class Beer{

    public static final String BEER_ID = "beer_id";
    public static final String NAME = "beer_name";
    public static final String ABV = "beer_abv"; //alcohol by volume
    // https://de.wikipedia.org/wiki/Stammw%C3%BCrze#Grad_Plato
    public static final String DEGREES_PLATO = "beer_plato"; //formula between plato and Stammwuerze exists
    public static final String STAMMWUERZE = "beer_stammwuerze";// but confusing - for now 2 optional values...
    // Stammwuerze / EBC / plato / SRM ...
    //https://en.wikipedia.org/wiki/Beer_measurement
    public static final String STYLE = "beer_style";
    public static final String IBU = "beer_ibu";    // international bitterness unit

    public static final String BREWERY_ID = "beer_brewery_id";

    private String beerId;
    private String name;
    private String abv;
    private String degreesPlato;
    private String stammwuerze;
    private String style;
    private String ibu;
//    private Brewery brewery;
    private String breweryId;

    public Beer(String abv, String beerId, Brewery brewery, String degreesPlato, String ibu, String name, String stammwuerze, String style) {
        this.abv = abv;
        this.beerId = beerId;
//        this.brewery = brewery;
        this.breweryId = brewery.getBreweryId();
        this.degreesPlato = degreesPlato;
        this.ibu = ibu;
        this.name = name;
        this.stammwuerze = stammwuerze;
        this.style = style;
    }

    @Override
    public String toString() {
        return "Beer{" +
                "abv='" + abv + '\'' +
                ", beerId='" + beerId + '\'' +
                ", name='" + name + '\'' +
                ", degreesPlato='" + degreesPlato + '\'' +
                ", stammwuerze='" + stammwuerze + '\'' +
                ", style='" + style + '\'' +
                ", ibu='" + ibu + '\'' +
                ", breweryId=" + breweryId +
                '}';
    }

    public String getAbv() {
        return abv;
    }

    public void setAbv(String abv) {
        this.abv = abv;
    }

    public String getBeerId() {
        return beerId;
    }

    public void setBeerId(String beerId) {
        this.beerId = beerId;
    }

    public String getBreweryId() {
        return breweryId;
    }

    public void setBreweryId(String breweryId) {
        this.breweryId = breweryId;
    }

    public String getDegreesPlato() {
        return degreesPlato;
    }

    public void setDegreesPlato(String degreesPlato) {
        this.degreesPlato = degreesPlato;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStammwuerze() {
        return stammwuerze;
    }

    public void setStammwuerze(String stammwuerze) {
        this.stammwuerze = stammwuerze;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
