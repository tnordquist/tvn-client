package edu.cnm.deepdive.tvnclient.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.location.Location;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LocationRepository {

  private static Application context;

  private final FusedLocationProviderClient locationClient;
  private final LocationRequest request;
  private final MutableLiveData<Location> location;

  private final Callback callback;

  private LocationRepository() {
    locationClient = LocationServices.getFusedLocationProviderClient(context);
    location = new MutableLiveData<>();
    request = LocationRequest.create();
    request.setInterval(15000); // FIXME Read from constants or preferences.
    request.setFastestInterval(5000); // FIXME Read from constants or preferences.
    request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    callback = new Callback();
  }

  public static void setContext(Application context) {
    LocationRepository.context = context;
  }

  public static LocationRepository getInstance() {
    return InstanceHolder.INSTANCE;
  }

  @SuppressLint("MissingPermission")
  public void start() {
    locationClient.requestLocationUpdates(request, callback, Looper.myLooper());
  }

  public void stop() {
    locationClient.removeLocationUpdates(callback);
  }

  public LiveData<Location> getLocation() {
    return location;
  }

  private class Callback extends LocationCallback {

    @Override
    public void onLocationResult(LocationResult locationResult) {
      super.onLocationResult(locationResult);
      location.postValue(locationResult.getLastLocation());
    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
      super.onLocationAvailability(locationAvailability);
    }
  }

  private static class InstanceHolder {

    private static final LocationRepository INSTANCE = new LocationRepository();
  }
}
