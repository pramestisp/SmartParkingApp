package id.ac.ugm.smartparking.smartparkingapp.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CheckSlotService extends Service {
    public CheckSlotService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
