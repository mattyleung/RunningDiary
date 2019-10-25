package au.edu.sydney.comp5216.assignment3;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The type Tracking.
 */
public class tracking extends FragmentActivity implements Runnable, OnMapReadyCallback {
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 2000;
    /**
     * The constant RUNTIME_PERMISSION_CODE.
     */
    public static final int RUNTIME_PERMISSION_CODE = 8;
    /**
     * The Location manager.
     */
    protected LocationManager locationManager;
    /**
     * The N.
     */
    static double n = 0;
    private GoogleMap mMap;
    /**
     * The Millisecond time.
     */
    long MillisecondTime, /**
     * The Start time.
     */
    StartTime, /**
     * The Time buff.
     */
    TimeBuff, /**
     * The Update time.
     */
    UpdateTime = 0L;
    /**
     * The S 1.
     */
    Long s1, /**
     * The R 1.
     */
    r1;
    /**
     * The Plat.
     */
    double plat, /**
     * The Plon.
     */
    plon, /**
     * The Clat.
     */
    clat, /**
     * The Clon.
     */
    clon, /**
     * The Dis.
     */
    dis, /**
     * The Chei.
     */
    chei;
    /**
     * The Total distance.
     */
    double totalDistance, /**
     * The Total time in hour.
     */
    totalTimeInHour = 0, /**
     * The Pace.
     */
    pace;
    /**
     * The Seconds.
     */
    int Seconds, /**
     * The Minutes.
     */
    Minutes, /**
     * The Milli seconds.
     */
    MilliSeconds, /**
     * The Pace hour.
     */
    paceHour, /**
     * The Pace minutes.
     */
    paceMinutes;
    /**
     * The Bool.
     */
    boolean bool = true;
    /**
     * The Counter.
     */
    MyCount counter;
    /**
     * The E 1.
     */
    EditText e1;
    /**
     * The Start.
     */
    Button start, /**
     * The Pause.
     */
    pause, /**
     * The End.
     */
    end, /**
     * The Home.
     */
    home;
    /**
     * The Location.
     */
    Location location;
    /**
     * The Stop watch time.
     */
    TextView stopWatchTime;
    /**
     * The List elements.
     */
    String[] ListElements = new String[]{};
    /**
     * The Location list.
     */
    List<List> locationList;
    /**
     * The Adapter.
     */
    ArrayAdapter<String> adapter;
    /**
     * The My db.
     */
    DatabaseHelper myDB;
    /**
     * The List elements array list.
     */
    List<String> ListElementsArrayList;
    /**
     * The Handler.
     */
    Handler handler, /**
     * The M handler.
     */
    mHandler;
    /**
     * The Pace to save.
     */
    String paceToSave, /**
     * The Speed to save.
     */
    speedToSave, /**
     * The Total run time.
     */
    totalRunTime, /**
     * The Total distance str.
     */
    totalDistanceStr;
    /**
     * The Map fragment.
     */
    SupportMapFragment mapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_map);

        start = (Button) findViewById(R.id.button1);//current position
        pause = (Button) findViewById(R.id.button2);
        end = (Button) findViewById(R.id.button3);
        home = (Button) findViewById(R.id.button4);

        ListElementsArrayList = new ArrayList<String>(Arrays.asList(ListElements));
        adapter = new ArrayAdapter<String>(tracking.this,
                android.R.layout.simple_list_item_1,
                ListElementsArrayList
        );
        locationList = new ArrayList<>();
        stopWatchTime = (TextView) findViewById(R.id.timeTextView);
        handler = new Handler();
        mHandler = new Handler();
        myDB = new DatabaseHelper(this);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        AndroidRuntimePermission();
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);
                end.setEnabled(false);
                mToastRunnable.run();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                end.setEnabled(true);
                mHandler.removeCallbacks(mToastRunnable);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(tracking.this, MainActivity.class);
                startActivity(intent);
            }
        });

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalDistance = 0;
                totalRunTime = stopWatchTime.getText().toString();
                Toast.makeText(tracking.this, "The total time is: " + totalRunTime,
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(tracking.this, "The length of gpsList is: " + locationList.size(),
                        Toast.LENGTH_SHORT).show();
                totalDistance = 0;
                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                MilliSeconds = 0;
                stopWatchTime.setText("00:00:00");
                ListElementsArrayList.clear();
                adapter.notifyDataSetChanged();
                if (locationList.size() > 1) {
                    for (int j = 0; j < locationList.size(); j++) {
                        if (j != locationList.size() && j + 1 < locationList.size()) {
                            List<Double> tempStartList = new ArrayList<Double>();
                            tempStartList = locationList.get(j);
                            double tempLatitudeStart = tempStartList.get(0);
                            double tempLongtitudeStart = tempStartList.get(1);
                            double tempAltitudeStart = tempStartList.get(2);

                            List<Double> tempEndList = new ArrayList<Double>();
                            tempEndList = locationList.get(j + 1);
                            double tempLatitudeEnd = tempEndList.get(0);
                            double tempLongtitudeEnd = tempEndList.get(1);
                            double tempAltitudeEnd = tempEndList.get(2);

                            totalDistance = totalDistance + distance(mMap, tempLatitudeStart, tempLatitudeEnd,
                                    tempLongtitudeStart,
                                    tempLongtitudeEnd, tempAltitudeStart, tempAltitudeEnd);
                            //TODO: DRAW LINE

                            mMap.addPolyline(new PolylineOptions()
                                    .add(new LatLng(tempStartList.get(0), tempStartList.get(1)),
                                            new LatLng(tempStartList.get(0), tempStartList.get(1)))
                                    .width(5)
                                    .color(Color.RED));
                            Log.i("Calculate distance", "Distance is: " + totalDistance);
                        } else {

                        }
                    }
                    if (Build.VERSION.SDK_INT >= 23 &&
                            ContextCompat.checkSelfPermission(tracking.this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(tracking.this,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    Double tempLat = location.getLatitude();
                    Double tempLong = location.getLongitude();
                    LatLng temp = new LatLng(tempLat, tempLong);
                    mMap.addMarker(new MarkerOptions().position(temp).title("END"));
//                    mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp,18));
                } else {
                    totalDistance = 0;
                    Log.i("Calculate distance", "Distance is: 0, you didn't move " + totalDistance);
                }
                double totalDistanceInKm = totalDistance / 1000;
                String[] timeSplit = totalRunTime.split(":");
                double hourDouble = Double.parseDouble(timeSplit[0]);
                double minutesDouble = Double.parseDouble(timeSplit[1]);
                double secondsDouble = Double.parseDouble(timeSplit[2]);
                totalTimeInHour = totalTimeInHour + hourDouble;
                minutesDouble = minutesDouble / 60;
                totalTimeInHour = totalTimeInHour + minutesDouble;
                secondsDouble = secondsDouble / 3600;
                totalTimeInHour = totalTimeInHour + secondsDouble;
                Toast.makeText(tracking.this, "The total distance in meters is: " + totalDistance,
                        Toast.LENGTH_SHORT).show();
                Toast.makeText(tracking.this, "The total distance in km is: " + totalDistanceInKm,
                        Toast.LENGTH_SHORT).show();
                if (totalDistanceInKm != 0.0) {
                    totalDistanceStr = String.valueOf(totalDistanceInKm).substring(0, 4);
                } else {
                    totalDistanceStr = String.valueOf(totalDistanceInKm);
                }
                Date date = new Date(); // this object contains the current date value
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = formatter.format(date);
                paceToSave = calculatePace(totalDistanceInKm, totalTimeInHour);
                double tempSpeedToSave = totalDistanceInKm / totalTimeInHour;
                speedToSave = String.valueOf(tempSpeedToSave).substring(0, 3);
                myDB.addData(formattedDate, totalDistanceStr, totalRunTime.substring(0, 4), paceToSave, speedToSave);
                Log.i("DB actions", "Date: " + formattedDate);
                Log.i("DB actions", "Distance: " + totalDistanceStr);
                Log.i("DB actions", "RunTime: " + totalRunTime.subSequence(0, 4));
                Log.i("DB actions", "Pace: " + paceToSave);
                Log.i("DB actions", "Speed: " + speedToSave);

            }
        });

    }


    /**
     * Show current location.
     */
    protected void showCurrentLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(tracking.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    tracking.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    RUNTIME_PERMISSION_CODE
                            );
                        }
                    });
                    alert_builder.setNeutralButton("Cancel", null);
                    AlertDialog dialog = alert_builder.create();
                    dialog.show();
                } else {
                    ActivityCompat.requestPermissions(
                            tracking.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            } else {

            }

        }
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            clat = location.getLatitude();
            clon = location.getLongitude();
            chei = location.getAltitude();
            List<Double> tempList = new ArrayList<Double>();

            tempList.add(clat);
            tempList.add(clon);
            tempList.add(chei);
            locationList.add(tempList);
            Toast.makeText(tracking.this, message,
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(tracking.this, "null location",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            String message = String.format(
                    "New Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );

            Toast.makeText(tracking.this, message, Toast.LENGTH_LONG).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(tracking.this, "Provider status changed",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(tracking.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(tracking.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_LONG).show();
        }

    }

    /**
     * The type My count.
     */
    public class MyCount extends CountDownTimer {
        /**
         * Instantiates a new My count.
         *
         * @param millisInFuture    the millis in future
         * @param countDownInterval the count down interval
         */
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            counter = new MyCount(30000, 1000);
            counter.start();
            n = n + 1;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            s1 = millisUntilFinished;
            r1 = (30000 - s1) / 1000;
            e1.setText(String.valueOf(r1));
        }
    }

    @Override
    public void run() {
        while (bool) {
            clat = location.getLatitude();
            clon = location.getLongitude();
            if (clat != plat || clon != plon) {
                dis += getDistance(plat, plon, clat, clon);
                plat = clat;
                plon = clon;
            }
        }
    }

    /**
     * Gets distance.
     * Calculate distance by using the ‘haversine’ formula.
     *
     * @param lat1 the lat 1
     * @param lon1 the lon 1
     * @param lat2 the lat 2
     * @param lon2 the lon 2
     * @return the distance
     */
    public double getDistance(double lat1, double lon1, double lat2, double lon2) {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB - lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang * 6371;
        return dist;
    }


    /**
     * Distance double.
     * Calculate distance by using the ‘haversine’ formula.
     *
     * @param googleMap the google map
     * @param lat1      the lat 1
     * @param lat2      the lat 2
     * @param lon1      the lon 1
     * @param lon2      the lon 2
     * @param el1       the el 1
     * @param el2       the el 2
     * @return the double
     */
    public double distance(GoogleMap googleMap, double lat1, double lat2, double lon1,
                           double lon2, double el1, double el2) {
        mMap = googleMap;
        final int R = 6371; // Radius of the earth
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        double height = el1 - el2;
        distance = Math.pow(distance, 2) + Math.pow(height, 2);
        drawlineMap(mMap, lat1, lat2, lon1, lon2);
        return Math.sqrt(distance);
    }


    /**
     * Drawline map.
     * For every two coordinates, draw a polyline on the mapView.
     *
     * @param map  the map
     * @param lat1 the lat 1
     * @param lat2 the lat 2
     * @param lon1 the lon 1
     * @param lon2 the lon 2
     */
    public void drawlineMap(GoogleMap map, double lat1, double lat2, double lon1, double lon2) {
        map.addPolyline(new PolylineOptions()
                .add(new LatLng(lat1, lon1), new LatLng(lat2, lon2))
                .width(10)
                .color(Color.RED));
    }

    /**
     * The Runnable.
     * Get the time when the start button is clicked.
     */
    public Runnable runnable = new Runnable() {

        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            MilliSeconds = (int) (UpdateTime % 1000);
            stopWatchTime.setText("" + Minutes + ":"
                    + String.format("%02d", Seconds) + ":"
                    + String.format("%03d", MilliSeconds));
            handler.postDelayed(this, 0);
        }
    };

    private Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            showCurrentLocation();
            mHandler.postDelayed(this, 5000);
        }
    };

    /**
     * When google map is ready in the xml file, current location marker will be added in the mapView.
     *
     * @param googleMap
     */
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) != null) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Double tempLat = location.getLatitude();
            Double tempLong = location.getLongitude();
            LatLng temp = new LatLng(tempLat, tempLong);
            mMap.addMarker(new MarkerOptions().position(temp).title("Start"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(temp, 18));
        }

    }


    /**
     * Calculate pace string.
     *
     * @param distance the distance
     * @param time     the time
     * @return the string
     */
    public String calculatePace(double distance, double time) {
        Log.i("In calculate pace", "distance: " + distance);
        Log.i("In calculate pace", "time: " + time);
        if (distance != 0) {
            pace = time / distance;
            paceHour = (int) pace;

            double hoursDeciamlPart = pace - paceHour;

            double minutesTemp = 60 * hoursDeciamlPart;
            paceMinutes = (int) minutesTemp;
            double secondsDecimalPart = minutesTemp - paceMinutes;
            double secondTemp = secondsDecimalPart * 60;
            Log.i("In calculate pace", ": " + secondTemp);
            BigDecimal bd = new BigDecimal(secondTemp).setScale(2, RoundingMode.HALF_UP);
            double secondInFourDigit = bd.doubleValue();
            String paceString = paceHour + " h " + paceMinutes + " m " + secondInFourDigit +
                    " s " + "/km";
            return paceString;
        } else {
            return "N/A";
        }
    }

    /**
     * Android runtime permission.
     */
    public void AndroidRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(tracking.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    tracking.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    RUNTIME_PERMISSION_CODE

                            );
                            ActivityCompat.requestPermissions(
                                    tracking.this,
                                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                } else {

                    ActivityCompat.requestPermissions(
                            tracking.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            RUNTIME_PERMISSION_CODE
                    );
                    ActivityCompat.requestPermissions(
                            tracking.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            RUNTIME_PERMISSION_CODE

                    );
                }
            }
        }
    }

    /**
     * Android runtime permission.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case RUNTIME_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
            }
        }
    }

}
