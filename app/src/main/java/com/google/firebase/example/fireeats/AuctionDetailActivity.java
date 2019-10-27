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
 package com.google.firebase.example.fireeats;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.example.fireeats.adapter.BidAdapter;
import com.google.firebase.example.fireeats.model.Bid;
import com.google.firebase.example.fireeats.model.TossItem;
import com.google.firebase.example.fireeats.util.TossItemUtil;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;



public class AuctionDetailActivity extends AppCompatActivity implements
        View.OnClickListener,
        EventListener<DocumentSnapshot>,
        RatingDialogFragment.RatingListener {

    private static final String TAG = "RestaurantDetail";

    public static final String ITEM_ID = "toss_item_id";

    private ImageView mImageView;
    private TextView mNameView;
    private MaterialRatingBar mRatingIndicator;
    private TextView mNumRatingsView;
    private TextView mCityView;
    private TextView mCategoryView;
    private TextView mPriceView;
    private ViewGroup mEmptyView;
    private RecyclerView mRatingsRecycler;
    private Button mBuyButton;

    private RatingDialogFragment mRatingDialog;

    private FirebaseFirestore mFirestore;
    private DocumentReference mTossItemRef;
    private ListenerRegistration mRestaurantRegistration;

    private BidAdapter mBidAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        
        mImageView = findViewById(R.id.restaurant_image);
        mNameView = findViewById(R.id.restaurant_name);
        mRatingIndicator = findViewById(R.id.restaurant_rating);
        mNumRatingsView = findViewById(R.id.restaurant_num_ratings);
        mCityView = findViewById(R.id.restaurant_city);
        mCategoryView = findViewById(R.id.restaurant_category);
        mPriceView = findViewById(R.id.restaurant_price);
        mEmptyView = findViewById(R.id.view_empty_ratings);
        mRatingsRecycler = findViewById(R.id.recycler_ratings);

        findViewById(R.id.restaurant_button_back).setOnClickListener(this);
        findViewById(R.id.fab_show_rating_dialog).setOnClickListener(this);
        findViewById(R.id.buy_button).setOnClickListener(this);

        // Get restaurant ID from extras
        String restaurantId = getIntent().getExtras().getString(ITEM_ID);
        if (restaurantId == null) {
            throw new IllegalArgumentException("Must pass extra " + ITEM_ID);
        }

        // Initialize Firestore
        mFirestore = FirebaseFirestore.getInstance();

        // Get reference to the restaurant
        mTossItemRef = mFirestore.collection("testitem").document(restaurantId);

        // Get ratings
        Query ratingsQuery = mTossItemRef
                .collection("ratings")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50);

        // RecyclerView
        mBidAdapter = new BidAdapter(ratingsQuery) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    mRatingsRecycler.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    mRatingsRecycler.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        mRatingsRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRatingsRecycler.setAdapter(mBidAdapter);

        mRatingDialog = new RatingDialogFragment();
    }

    @Override
    public void onStart() {
        super.onStart();

        mBidAdapter.startListening();
        mRestaurantRegistration = mTossItemRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        mBidAdapter.stopListening();

        if (mRestaurantRegistration != null) {
            mRestaurantRegistration.remove();
            mRestaurantRegistration = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.restaurant_button_back:
                onBackArrowClicked(v);
                break;
            case R.id.fab_show_rating_dialog:
                onAddRatingClicked(v);
                break;
            case R.id.buy_button:
                onBuyClicked(v);
                break;
        }
    }

    private void onBuyClicked(View v) {
        Log.d("onBuyClicked", "called buy");
        // Instantiate the RequestQueue.
        JSONObject params = new JSONObject();
        try {
            params.put("medium", "balance");
            params.put("payee_id", "5db48daf3c8c2216c9fcb63e");
            params.put("transaction_date", "2019-10-26");
            params.put("status", "pending");
            params.put("amount", 100);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://api.reimaginebanking.com/accounts/5db48c813c8c2216c9fcb63d/transfers?key=ba3960564eccc01469a256e4fec3af3d";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, params,
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        Log.d("VolleyResponse" ,response.toString());
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("errResponse", "That didn't work! "+ error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Accept", "application/json");

                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

        AlertDialog alertDialog = new AlertDialog.Builder(AuctionDetailActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("You have bought this item!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.show();
    }

    private Task<Void> addRating(final DocumentReference restaurantRef,
                                 final Bid bid) {
        // Create reference for new bid, for use inside the transaction
        final DocumentReference ratingRef = restaurantRef.collection("ratings")
                .document();

        // In a transaction, add the new bid and update the aggregate totals
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction)
                    throws FirebaseFirestoreException {

                TossItem tossItem = transaction.get(restaurantRef)
                        .toObject(TossItem.class);

                // Compute new number of ratings
                int newNumRatings = tossItem.getNumRatings() + 1;

                // Compute new average bid
                double oldRatingTotal = tossItem.getAvgRating() *
                        tossItem.getNumRatings();
                double newAvgRating = (oldRatingTotal + bid.getRating()) /
                        newNumRatings;

                // Set new tossItem info
                tossItem.setNumRatings(newNumRatings);
                tossItem.setAvgRating(newAvgRating);

                // Commit to Firestore
                transaction.set(restaurantRef, tossItem);
                transaction.set(ratingRef, bid);

                return null;
            }
        });
    }

    /**
     * Listener for the TossItem document ({@link #mTossItemRef}).
     */
    @Override
    public void onEvent(DocumentSnapshot snapshot, FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "restaurant:onEvent", e);
            return;
        }
        Log.d("onEvent", snapshot.toString());

        onTossItemLoaded(snapshot.toObject(TossItem.class));
    }

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

    private void onTossItemLoaded(TossItem tossItem) {
        Log.d("onTossItemLoaded", "called method");
        Log.d("onTossItemLoaded", tossItem.toString());
        mNameView.setText(tossItem.getName());
        mRatingIndicator.setRating((float) tossItem.getAvgRating());
        mNumRatingsView.setText(getString(R.string.fmt_num_ratings, tossItem.getNumRatings()));
        mCityView.setText(tossItem.getAddress());
        mCategoryView.setText(tossItem.getCategory());
        mPriceView.setText(TossItemUtil.getPriceString(tossItem.getStartPrice()));
        mImageView.setImageBitmap(StringToBitMap(tossItem.getPhoto()));

//        // Background image
//        if (!TextUtils.isEmpty(tossItem.getPhoto())) {
//            Glide.with(mImageView.getContext())
//                    .load(tossItem.getPhoto())
//                    .into(mImageView);
//        }


    }

    public void onBackArrowClicked(View view) {
        onBackPressed();
    }

    public void onAddRatingClicked(View view) {
        mRatingDialog.show(getSupportFragmentManager(), RatingDialogFragment.TAG);
    }

    @Override
    public void onRating(Bid bid) {
        // In a transaction, add the new bid and update the aggregate totals
        addRating(mTossItemRef, bid)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Bid added");

                        // Hide keyboard and scroll to top
                        hideKeyboard();
                        mRatingsRecycler.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Add bid failed", e);

                        // Show failure message and hide keyboard
                        hideKeyboard();
                        Snackbar.make(findViewById(android.R.id.content), "Failed to add bid",
                                Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
