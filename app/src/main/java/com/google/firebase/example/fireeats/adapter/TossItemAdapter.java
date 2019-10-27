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
 package com.google.firebase.example.fireeats.adapter;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.example.fireeats.R;
import com.google.firebase.example.fireeats.model.TossItem;
import com.google.firebase.example.fireeats.util.TossItemUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

/**
 * RecyclerView adapter for a list of Restaurants.
 */
public class TossItemAdapter extends FirestoreAdapter<TossItemAdapter.ViewHolder> {

    public interface OnRestaurantSelectedListener {

        void onRestaurantSelected(DocumentSnapshot restaurant);

    }

    private OnRestaurantSelectedListener mListener;

    public TossItemAdapter(Query query, OnRestaurantSelectedListener listener) {
        super(query);
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.item_restaurant, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getSnapshot(position), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView nameView;
        MaterialRatingBar ratingBar;
        TextView numRatingsView;
        TextView priceView;
        TextView categoryView;
        TextView cityView;
        TextView currentDateView;

        public Bitmap StringToBitMap(String encodedString){
            try {
                byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch(Exception e) {
                e.getMessage();
                return null;
            }
        }

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.restaurant_item_image);
            nameView = itemView.findViewById(R.id.restaurant_item_name);
//            ratingBar = itemView.findViewById(R.id.restaurant_item_rating);
//            numRatingsView = itemView.findViewById(R.id.restaurant_item_num_ratings);
            priceView = itemView.findViewById(R.id.restaurant_item_price);
//            categoryView = itemView.findViewById(R.id.restaurant_item_category);
//            cityView = itemView.findViewById(R.id.restaurant_item_city);
            currentDateView = itemView.findViewById(R.id.startdate);
        }

        public void bind(final DocumentSnapshot snapshot,
                         final OnRestaurantSelectedListener listener) {

            TossItem tossItem = snapshot.toObject(TossItem.class);
            Resources resources = itemView.getResources();

            imageView.setImageBitmap(StringToBitMap(tossItem.getPhoto()));
            nameView.setText(tossItem.getName());
//            ratingBar.setRating((float) tossItem.getAvgRating());
//            cityView.setText(tossItem.getAddress());
//            categoryView.setText(tossItem.getCategory());
//            numRatingsView.setText(resources.getString(R.string.fmt_num_ratings,
//                    tossItem.getNumRatings()));
            if (tossItem.getCurrentPrice() == 0)
            {
                priceView.setText(TossItemUtil.getPriceString(tossItem.getStartPrice()));
            } else
            {
                priceView.setText(TossItemUtil.getPriceString(tossItem.getCurrentPrice()));
            }

            String startDate = tossItem.getStartDate();
            startDate = startDate.substring(0,10);
            currentDateView.setText("02w 05d");

            Log.d("toss item", startDate);

            // Click listener
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("toss item", "null");
                    if (listener != null) {
                        Log.d("toss item", "not null");
                        listener.onRestaurantSelected(snapshot);
                    }
                }
            });
        }

    }
}
