/**
 * Copyright 2017 Google Inc. All Rights Reserved.
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
 package com.google.firebase.example.fireeats.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * TossItem POJO.
 */
@IgnoreExtraProperties
public class TossItem {

    public static final String FIELD_CITY = "city";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_POPULARITY = "numRatings";
    public static final String FIELD_AVG_RATING = "avgRating";

    private String name;
    private String address;
    private String category;
    private String photo;
    private int price;
    private int numRatings;
    private String description;
    private double avgRating;
    // Need to add string to date formatter
    private String startDate;
    private String endDate;
    // Start price and End price
    private long startPrice;
    private long endPrice;

    public TossItem() {}

    public TossItem(String name, String address, String description, String category, String photo,
                    String startDate, String endDate, long startPrice, long endPrice) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.photo = photo;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startPrice = startPrice;
        this.endPrice = endPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNumRatings() {
        return numRatings;
    }

    public void setNumRatings(int numRatings) {
        this.numRatings = numRatings;
    }

    public double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(double avgRating) {
        this.avgRating = avgRating;
    }

    public String getStartDate() {return startDate;}
    public void setStartDate(String startDate) {this.startDate = startDate;}

    public String getEndDate() {return endDate;}
    public void setEndDate(String endDate) {this.endDate = endDate;}

    public long getStartPrice() {return startPrice;}
    public void setStartPrice (int startPrice) {this.startPrice = startPrice;}

    public long getEndPrice() {return endPrice;}
    public void setEndPrice(int endPrice) {this.endPrice = endPrice;}

}
