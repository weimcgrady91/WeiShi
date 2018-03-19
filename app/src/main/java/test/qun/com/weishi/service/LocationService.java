package test.qun.com.weishi.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import test.qun.com.weishi.App;
import test.qun.com.weishi.ConstantValue;
import test.qun.com.weishi.util.LogUtil;
import test.qun.com.weishi.util.PreferencesUtil;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        super.onCreate();
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setCostAllowed(true);
//        String bestProvider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LogUtil.i("location=" + location.toString());
                String safeNumber = (String) PreferencesUtil.getData(App.sContext, ConstantValue.KEY_PHONE_SAFE_NUMBER, "");
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(safeNumber, null, "location[ latitude=" + location.getLatitude() + ",longitude=" + location.getLongitude() + "]", null, null);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }
}
