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

public class User {

    public static final String USER_ID = "user_id";
    public static final String LOGIN = "user_login";
    public static final String NAME = "user_name";
    public static final String EMAIL = "user_eamil";

    public static final String HOME_LOCATION_ID = "user_home_location_id";

    private String userId;
    private String login;
    private String name;
    private String email;
    private String homeLocationId;

    public User(String email, Location homeLocation, String login, String name, String userId) {
        this.email = email;
        this.homeLocationId = homeLocation==null?null:homeLocation.getLocationId();
        this.login = login;
        this.name = name;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", homeLocationId=" + homeLocationId +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomeLocationId() {
        return homeLocationId;
    }

    public void setHomeLocationId(String homeLocationId) {
        this.homeLocationId = homeLocationId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
