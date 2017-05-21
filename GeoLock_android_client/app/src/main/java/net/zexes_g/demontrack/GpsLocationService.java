package net.zexes_g.demontrack;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Button;

import java.util.Iterator;

/**
 * Created by zexes-g on 06/04/17.
 */

public class GpsLocationService extends Service implements LocationListener ,GpsStatus.Listener{



      ///////////////
     // Attribute //
    ///////////////

    /* Refresh params */
    private final int TIME_REFRESH = 1000 * 3; /* 1000 ms = 1s */
    private final int DISTANCE_REFRESH = 0;    /* 0 m */

    /* GPS_MANAGER */
    private LocationManager gps_locationManager;
    private final String GPS_PROVIDER = LocationManager.GPS_PROVIDER;

    /* Location data */
    String data;
    String lat, lon, alt;

    /* Intents */
    Intent networkLocationService;
    //----------------------------------------------------------------------------------------------


      ////////////////////////
     // Service Life Cycle //
    ////////////////////////

    /* Create Service */
    @Override
    public void onCreate() {

        Log.d("AABBCC","Service Create " + GPS_PROVIDER);
        /* Initialise components */
        init();
    }


    /** The service is starting, due to a call to startService() */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("AABBCC","Service Start command " + GPS_PROVIDER);
        /* Activate the listeners */
        //network_locationManager.requestLocationUpdates(NETWORK_PROVIDER,TIME_REFRESH,DISTANCE_REFRESH,this);
        gps_locationManager.requestLocationUpdates(GPS_PROVIDER,TIME_REFRESH,DISTANCE_REFRESH,this);
        return startId;
    }

    /* When a client Rebinf to the service */
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }


    /* When a client bind to the service */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /* when all clients unbind from the service */
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    /* Service is about to be destroyed */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("AABBCC","Service DESTROTY " + GPS_PROVIDER);
        /* Desactivate the listener if is activated */
        if (gps_locationManager != null){
            gps_locationManager.removeUpdates(this);
        }
    }
    //----------------------------------------------------------------------------------------------


      ///////////////
     // Listeners //
    ///////////////

    /* Location listener */
    @Override
    public void onLocationChanged(Location location) {

        /* Check Network location service state */
        if (networkLocationService != null){
            stopService(networkLocationService);
            networkLocationService = null;
        }

        /* Get data */
        lat = "" + location.getLatitude();
        lon = "" + location.getLongitude();
        alt = "" + location.getAltitude();
        data = lat + "\n" + lon + "\n" + alt;

        /* Send data */
        Log.d("AABBCC",location.getProvider() + " : (lat, lon, alt) : \n" + data);
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    @Override
    public void onProviderEnabled(String provider) {
    }
    @Override
    public void onProviderDisabled(String provider) {
        // TODO popup notification in time of work
    }

    /* Gps status listener */
    @Override
    public void onGpsStatusChanged(int event) {
        GpsStatus gpsStatus = gps_locationManager.getGpsStatus(null);

        /* If gps is on */
        if(gpsStatus != null) {

            /* From Gps satellite get Iterable Satellites Objects */
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> sat = satellites.iterator();

            /* Fhe satellites list */
            String lSatellites = null;

            /* Counters */
            int nb_sat_found = 0;
            int nb_sat_used = 0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                lSatellites = "Satellite : ";
                if(satellite.usedInFix())
                    lSatellites += (nb_sat_used++);
                else
                    lSatellites += (nb_sat_used);
                lSatellites += "/"+(nb_sat_found++) ;

            }
            if(nb_sat_used == 0 && networkLocationService == null){
                networkLocationService = new Intent(this, NetworkLocationService.class);
                startService(networkLocationService);
            }
            Log.d("AABBCC",GPS_PROVIDER + " : "+lSatellites);
        }
    }
    //----------------------------------------------------------------------------------------------


      //////////////
     // Methodes //
    //////////////

    /* Initialise the components */
    private void init() {
        Log.d("AABBCC","Service init");

        /* Get the network Location Service */
        networkLocationService = null;

        /* Initialise the managers */
        gps_locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        gps_locationManager.addGpsStatusListener(this);
    }
    //----------------------------------------------------------------------------------------------


}