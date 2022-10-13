/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package com.hcmut.admin.utrafficsystem.ui.searchplace.result;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.hcmut.admin.utrafficsystem.R;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
public class PlaceAutoCompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

    private ArrayList<AutocompletePrediction> mResultList;
    private PlacesClient placesClient;
    private SearchPlaceAdapter searchPlaceAdapter;

    public PlaceAutoCompleteAdapter(Context context, PlacesClient placesClient, @Nullable SearchPlaceAdapter searchPlaceAdapter) {
        super(context, R.layout.item_place_autocomplete, R.id.tvPlace);
        this.placesClient = placesClient;
        this.searchPlaceAdapter = searchPlaceAdapter;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View row = super.getView(position, convertView, parent);

        AutocompletePrediction item = getItem(position);

        TextView textView1 = row.findViewById(R.id.tvPlace);
        TextView textView2 = row.findViewById(R.id.tvPlaceDetail);
        if (item != null) {
            textView1.setText(item.getPrimaryText(null));
            textView2.setText(item.getSecondaryText(null));
        }

        return row;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults results = new FilterResults();

                // We need a separate list to store the results, since
                // this is run asynchronously.
                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();

                // Skip the autocomplete query if no constraints are given.
                if (charSequence != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    filterData = getAutocomplete(charSequence);
                }

                results.values = filterData;
                if (filterData != null) {
                    results.count = filterData.size();
                } else {
                    results.count = 0;
                }

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {

                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    mResultList = (ArrayList<AutocompletePrediction>) results.values;
                    if (searchPlaceAdapter != null) {
                        searchPlaceAdapter.setDataSet(mResultList);
                    } else {
                        notifyDataSetChanged();
                    }
                } else {
                    // The API did not return any results, invalidate the data set.
                    if (searchPlaceAdapter == null) {
                        notifyDataSetInvalidated();
                    }
                }
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                // Override this method to display a readable result in the AutocompleteTextView
                // when clicked.
                if (resultValue instanceof AutocompletePrediction) {
                    return ((AutocompletePrediction) resultValue).getFullText(null);
                } else {
                    return super.convertResultToString(resultValue);
                }
            }
        };
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {

        //Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(106.4568713, 11.1503533),
                new LatLng(106.9661283, 10.4072733));


        final FindAutocompletePredictionsRequest.Builder requestBuilder =
                FindAutocompletePredictionsRequest.builder()
                        .setLocationBias(bounds)
                        .setQuery(constraint.toString())
                        .setCountry("VN") //Use only in specific country
//                        .setLocationBias(bounds)
                        .setSessionToken(AutocompleteSessionToken.newInstance());

        Task<FindAutocompletePredictionsResponse> results =
                placesClient.findAutocompletePredictions(requestBuilder.build());


        //Wait to get results.
        try {
            Tasks.await(results, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }

        if (results.isSuccessful()) {
            if (results.getResult() != null) {
                return (ArrayList<AutocompletePrediction>) results.getResult().getAutocompletePredictions();
            }
            return null;
        } else {
            return null;
        }
    }
}
