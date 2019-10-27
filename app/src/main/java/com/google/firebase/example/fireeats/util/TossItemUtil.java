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
 package com.google.firebase.example.fireeats.util;

import android.content.Context;
import android.util.Log;

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.model.TossItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import static java.lang.Math.abs;

/**
 * Utilities for Restaurants.
 */
public class TossItemUtil {

    private static final String TAG = "TossItemUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final String ITEM_PNG = "https://storage.googleapis.com/firestorequickstarts.appspot.com/food_%d.png";

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAME_FIRST_WORDS = {
            "New",
            "Broken",
            "Lovely",
            "Beautiful",
            "Magda's",
            "Sam's",
            "World Famous",
            "Killer",
            "The Best",
    };

    private static final String[] NAME_SECOND_WORDS = {
            "Furniture",
            "Chair",
            "Laptop",
            "Tablet",
            "Table",
            "Handphone",
            "Bed",
    };

    private static final String[] IMG_LINKS = {
            "https://www.athome.com/on/demandware.static/-/Sites/default/dw9d07e9a6/2019/website-audit/Accent-Furniture-Banner.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRAvSMgw8Fvhhv_J3JuNf8kuGcTbXIDv1-LyMqF_BFkCpbwaZlB&s"
    };


    // Get current date
    public static String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(calendar.getTime());
        String corDate = "20" + currentDate.substring(6,8) + "/" + currentDate.substring(0,2) + "/" + currentDate.substring(3,5) ;
        Log.d("getCurrentDate", currentDate);
        Log.d("getCurrentDate", corDate);
        return corDate;
    }

    public static int daysBetween(int startyear,int startmonth, int startdate, int endyear, int endmonth, int enddate){
        int numofdays = 0;
        numofdays = abs((startdate - enddate)) + abs(startmonth-endmonth)*30 + abs(startyear-endyear)*365;
        return numofdays;
    }
    // Calculate current price
    public static long getCurrentPrice(String startDate, String endDate, String currentDate, int startPrice, int endPrice){
        long currentPrice;

        int yearStartDate = Integer.parseInt(startDate.substring(0,4));
        int yearEndDate = Integer.parseInt(endDate.substring(0,4));
        int yearCurrentDate = Integer.parseInt(currentDate.substring(0,4));
        Log.d("yearstartdate is = ", Integer.toString(yearStartDate));
        Log.d("yearenddate is = ", Integer.toString(yearEndDate));
        Log.d("yearcurrentdate is = ", Integer.toString(yearCurrentDate));
        int monthStartDate = Integer.parseInt(startDate.substring(5,7));
        int monthEndDate = Integer.parseInt(endDate.substring(5,7));
        int monthCurrentDate = Integer.parseInt(currentDate.substring(5,7));
        Log.d("monthstartdate is = ", Integer.toString(monthStartDate));
        Log.d("monthenddate is = ", Integer.toString(monthEndDate));
        Log.d("monthcurrentdate is = ", Integer.toString(monthCurrentDate));
        int dayStartDate = Integer.parseInt(startDate.substring(8,10));
        int dayEndDate = Integer.parseInt(endDate.substring(8,10));
        int dayCurrentDate = Integer.parseInt(currentDate.substring(8,10));
        Log.d("dayStartDate is = ", Integer.toString(dayStartDate));
        Log.d("dayEndDate is = ", Integer.toString(dayEndDate));
        Log.d("dayCurrentDate is = ", Integer.toString(dayCurrentDate));

        int numerator = daysBetween(yearCurrentDate,monthCurrentDate,dayCurrentDate,yearEndDate,monthEndDate,dayEndDate)*(startPrice-endPrice);
        Log.d("numerator = ", Integer.toString(numerator));
        int denominator = daysBetween(yearStartDate,monthStartDate,dayStartDate,yearEndDate,monthEndDate,dayEndDate);
        Log.d("denominator = ", Integer.toString(denominator));

        currentPrice = (numerator / denominator) + endPrice;
        Log.d("currentPrice = ", Long.toString(currentPrice));

        return currentPrice;

    }

    /**
     * Create a random TossItem POJO.
     */
    public static TossItem getRandom(Context context) {
        TossItem tossItem = new TossItem();
        Random random = new Random();

        // Cities (first elemnt is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        int[] prices = new int[]{1, 2, 3};

        tossItem.setName(getRandomName(random));
        tossItem.setCity(getRandomString(cities, random));
        tossItem.setCategory(getRandomString(categories, random));
        tossItem.setPhoto(getRandomString(IMG_LINKS, random));
        tossItem.setPrice(getRandomInt(prices, random));
        tossItem.setAvgRating(getRandomRating(random));
        tossItem.setNumRatings(random.nextInt(20));

        // Set the dates
        tossItem.setStartDate("2019/10/11");
        tossItem.setEndDate("2019/10/30");
        tossItem.setCurrentDate(getCurrentDate());

        if (tossItem.getCategory() == "Tech"){
            Log.d("TECH ITEM FOUND",tossItem.getStartDate());
        }
        // Set the prices
        tossItem.setStartPrice(950);
        tossItem.setEndPrice(50);
        tossItem.setCurrentPrice(getCurrentPrice(tossItem.getStartDate(),tossItem.getEndDate(),tossItem.getCurrentDate(), tossItem.getStartPrice(),tossItem.getEndPrice()));
        return tossItem;
    }


    /**
     * Get a random image.
     */
    private static String getRandomImageUrl(Random random) {
        // Integer between 1 and MAX_IMAGE_NUM (inclusive)
        int id = random.nextInt(MAX_IMAGE_NUM) + 1;

        return String.format(Locale.getDefault(), ITEM_PNG, id);
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(TossItem tossItem) {
        return getPriceString(tossItem.getPrice());
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(int priceInt) {
        switch (priceInt) {
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
            default:
                return "$$$";
        }
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    private static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " "
                + getRandomString(NAME_SECOND_WORDS, random);
    }

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

}
