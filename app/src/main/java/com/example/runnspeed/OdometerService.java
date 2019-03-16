package com.example.runnspeed;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

//Klasa naszej usługi dziedziczy po klasie Service
public class OdometerService extends Service {
    //Prywatne zmienne statyczne
    private final IBinder binder = (IBinder) new OdometerService();
    private static double distanceInMeters;// Przebyty dystans
    private static Location lastLocation = null; //Ostatnie położenie urządzenia

    public class OdometerBinder extends Binder{
        OdometerService getOdometer(){
            return OdometerService.this;
        }
    }
    //Metoda onBind służy do wiązania komponentów z daną usługą
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        //Tworzymy obiekt nasłuchujący
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Jeśli to pierwsze informacje o położeniu, to zapisujemy je w zmiennej lastLocation
                if (lastLocation == null){
                    lastLocation = location;
                }
                //Do pokonanego dystansu zapisanego w zmiennej distanceInMeters,
                //dodajemy odległość między bieżącym i ostatnim położeniem
                 distanceInMeters += location.distanceTo(lastLocation);
                lastLocation = location;
            }
            @Override
            public void onProviderEnabled(String arg0) {

            }

            @Override
            public void onProviderDisabled(String arg0) {

            }
            @Override
            public void onStatusChanged(String arg0, int arg1, Bundle bundle) { }
        };
        //Rejstrujemy obiekt nasłuchujący  w systemie w systemowej usłudze lokalizacyjnej
        LocationManager locationManager =
                (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,1,listener);
    }
    //Konwertujemy przbyty dystans na kilometry
    public double getDistance(){
        return this.distanceInMeters / 1000;
    }

    public OdometerService() {
    }

}
