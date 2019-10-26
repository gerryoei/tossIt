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

import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.model.TossItem;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

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


//    private TextView dateTimeDisplay;
//    private Calendar calendar;
//    private SimpleDateFormat dateFormat;
//    private String date;

//    public static void getCurrentDate() {
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(dtf.format(now));
//    }

    public static String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        return currentDate;
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
        tossItem.setStartDate(getCurrentDate());

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
