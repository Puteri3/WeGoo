package com.example.wegoo;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateVehicleViewModel extends ViewModel {

    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private final MutableLiveData<Vehicle> vehicleData = new MutableLiveData<>();
    private final MutableLiveData<String> existingImageUrl = new MutableLiveData<>();
    private final MutableLiveData<List<Uri>> newImageUris = new MutableLiveData<>();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Event<String>> successEvent = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorEvent = new MutableLiveData<>();

    public LiveData<Vehicle> getVehicleData() {
        return vehicleData;
    }

    public LiveData<String> getExistingImageUrl() {
        return existingImageUrl;
    }

    public LiveData<List<Uri>> getNewImageUris() {
        return newImageUris;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Event<String>> getSuccessEvent() {
        return successEvent;
    }

    public LiveData<Event<String>> getErrorEvent() {
        return errorEvent;
    }

    public void loadVehicle(String vehicleId) {
        if (vehicleId == null || vehicleId.isEmpty()) {
            errorEvent.setValue(new Event<>("Vehicle ID is missing."));
            return;
        }

        isLoading.setValue(true);
        firestore.collection("vehicles").document(vehicleId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                        vehicleData.setValue(vehicle);
                        if (vehicle != null) {
                            existingImageUrl.setValue(vehicle.getImageUrl());
                        }
                    } else {
                        errorEvent.setValue(new Event<>("Vehicle not found."));
                    }
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorEvent.setValue(new Event<>("Failed to load vehicle: " + e.getMessage()));
                    isLoading.setValue(false);
                });
    }

    public void updateVehicle(String vehicleId, String name, String type, String price, String fuelType,
                              String engineCapacity, String seatingCapacity, String color) {

        isLoading.setValue(true);

        String currentImageUrl = existingImageUrl.getValue();
        List<Uri> imagesToUpload = newImageUris.getValue();

        if (imagesToUpload == null || imagesToUpload.isEmpty()) {
            saveDataToFirestore(vehicleId, name, type, price, fuelType, engineCapacity, seatingCapacity, color, currentImageUrl);
        } else {
            uploadImages(vehicleId, name, type, price, fuelType, engineCapacity, seatingCapacity, color, imagesToUpload);
        }
    }

    private void uploadImages(String vehicleId, String name, String type, String price, String fuelType, String engineCapacity, String seatingCapacity, String color, List<Uri> imagesToUpload) {
        final List<String> newUrls = new ArrayList<>();
        final AtomicInteger uploadCounter = new AtomicInteger(0);
        final AtomicBoolean errorOccurred = new AtomicBoolean(false);

        for (Uri uri : imagesToUpload) {
            MediaManager.get().upload(uri)
                    .option("folder", "vehicles/" + vehicleId)
                    .callback(new UploadCallback() {
                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            if (errorOccurred.get()) return;

                            String url = (String) resultData.get("secure_url");
                            if (url != null) {
                                newUrls.add(url);
                            }

                            if (uploadCounter.incrementAndGet() == imagesToUpload.size()) {
                                saveDataToFirestore(vehicleId, name, type, price, fuelType, engineCapacity, seatingCapacity, color, newUrls.get(0));
                            }
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            if (errorOccurred.compareAndSet(false, true)) {
                                errorEvent.postValue(new Event<>("Image upload failed: " + error.getDescription()));
                                isLoading.postValue(false);
                            }
                        }
                        @Override public void onStart(String requestId) { }
                        @Override public void onProgress(String requestId, long bytes, long totalBytes) { }
                        @Override public void onReschedule(String requestId, ErrorInfo error) { }
                    }).dispatch();
        }
    }


    private void saveDataToFirestore(String vehicleId, String name, String type, String price, String fuelType, String engineCapacity, String seatingCapacity, String color, String imageUrl) {
        Map<String, Object> vehicleUpdates = new HashMap<>();
        vehicleUpdates.put("name", name);
        vehicleUpdates.put("type", type);
        vehicleUpdates.put("price", price);
        vehicleUpdates.put("fuelType", fuelType);
        vehicleUpdates.put("engineCapacity", engineCapacity);
        vehicleUpdates.put("seatingCapacity", seatingCapacity);
        vehicleUpdates.put("color", color);
        vehicleUpdates.put("imageUrl", imageUrl);

        firestore.collection("vehicles").document(vehicleId).update(vehicleUpdates)
                .addOnSuccessListener(aVoid -> {
                    successEvent.setValue(new Event<>("Vehicle updated successfully!"));
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorEvent.setValue(new Event<>("Failed to update vehicle: " + e.getMessage()));
                    isLoading.setValue(false);
                });
    }

    public void setNewImageUris(List<Uri> uris) {
        newImageUris.setValue(uris);
    }

    /**
     * Used as a wrapper for data that is exposed via a LiveData that represents an event.
     */
    public static class Event<T> {

        private final T content;
        private boolean hasBeenHandled = false;

        public Event(T content) {
            this.content = content;
        }

        /**
         * Returns the content and prevents its use again.
         */
        public T getContentIfNotHandled() {
            if (hasBeenHandled) {
                return null;
            } else {
                hasBeenHandled = true;
                return content;
            }
        }

        /**
         * Returns the content, even if it's already been handled.
         */
        public T peekContent() {
            return content;
        }
    }
}
