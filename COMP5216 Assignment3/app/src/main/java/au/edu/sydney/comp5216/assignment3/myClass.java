package au.edu.sydney.comp5216.assignment3;

import android.app.Application;
import android.util.Log;

/**
 * The type My class.
 * This class will only called once when the app is installed to the device.
 * Adding dummy data into database.
 */
public class myClass extends Application {
    /**
     * The My db.
     */
    DatabaseHelper myDB;

    /**
     * My app function only run once when the app is installed.
     * Manifests android:name=".myClass"
     */
    public void MyApp() {
        // this method fires only once per application start.
        // getApplicationContext returns null here

    }

    @Override
    public void onCreate() {
        super.onCreate();

        // this method fires once as well as constructor
        // but also application has context here
        myDB = new DatabaseHelper(this);
        String tempDate = "01-10-2019";
        String tempDistance = "3.6";
        String tempRunTime = "5:27";
        String tempPace = "00 h 01 m 30.8 s /km";
        String tempSpeed = "26.8";

        String tempDate2 = "05-10-2019";
        String tempDistance2 = "5.6";
        String tempRunTime2 = "6:27";
        String tempPace2 = "00h 01 m 09.1 s /km";
        String tempSpeed2 = "34.7";
        myDB.addData(tempDate, tempDistance, tempRunTime, tempPace, tempSpeed);
        myDB.addData(tempDate2, tempDistance2, tempRunTime2, tempPace2, tempSpeed2);
        Log.i("main", "Constructor fired");
        Log.i("main", "onCreate fired");
    }
}