package com.example.bookedup.utils;

import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;

import com.example.bookedup.model.Accommodation;

import java.util.List;
import java.util.Map;

public class SharedViewModel extends ViewModel {
    private List<Accommodation> mostPopularAccommodations;
    private Map<Long, List<Bitmap>> accommodationImageMap;

    public List<Accommodation> getMostPopularAccommodations() {
        return mostPopularAccommodations;
    }

    public void setMostPopularAccommodations(List<Accommodation> mostPopularAccommodations) {
        this.mostPopularAccommodations = mostPopularAccommodations;
    }

    public Map<Long, List<Bitmap>> getAccommodationImageMap() {
        return accommodationImageMap;
    }

    public void setAccommodationImageMap(Map<Long, List<Bitmap>> accommodationImageMap) {
        this.accommodationImageMap = accommodationImageMap;
    }
}
