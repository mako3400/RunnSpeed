package com.example.runnspeed;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    //Tej zmiennej użyjemy do przechowywania referencji do usługi OdometerService
    private OdometerService odometer;
    //Tej zmiennej użyjemy do przechowywania informacji o tym,
    //czy aktywnośc została powiązana z usługa czy nie
    private boolean bound = false;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            OdometerService.OdometerBinder odometerBinder =
                    (OdometerService.OdometerBinder) binder;
            //Rzutujemy przekazany obiekt Binder na typ OdometerBinder, dzieki czemu
            //będziemy mogli pobrać referencję do usługi OdometerService
            odometer = odometerBinder.getOdometer();
            //Kiedy usługa zostanie powiązana,przypisujemy zmiennej bound wartość true
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
         //Kiedy powiązanie zostanie zakończone,przypisujemy zmiennej wartośc false
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Podczas tworzenia aktywności wywołujemy metode watchMilege().
        watchMilege();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Kiedy aktywnośc zostaje uruchomiona próbujemy ją powiązać z usługą
        Intent intent = new Intent(this, OdometerService.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Powiązanie przerywamy w momencie zatrzymania aktywności
    if (bound){
        unbindService(connection);
        bound = false;
    }
    }
    //Metoda aktualizuje wyświetlony dystans jaki pokonało urządzenie
    private  void watchMilege(){
        final TextView distanceView = (TextView) findViewById(R.id.distance);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                double distance = 0.0;
                if (odometer != null){
                    //Jeśli dysponujemy referencją do usługi OdometerService,
                    // to wywołujemy jej metodę getDistance()
                    distance = odometer.getDistance();
                }
                String distanceStr = String.format("%1$,.2f kilometra",distance);
                distanceView.setText(distanceStr);
                //Informacje o pokonanym dystansie aktualizujemy co sekunde
                handler.postDelayed(this,1000);
            }
        });
    }

}
