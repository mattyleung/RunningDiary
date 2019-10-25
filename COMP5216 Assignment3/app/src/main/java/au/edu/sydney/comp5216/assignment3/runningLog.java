package au.edu.sydney.comp5216.assignment3;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * The type Running log.
 */
public class runningLog extends Activity {
    /**
     * The My db.
     */
    DatabaseHelper myDB;
    /**
     * The Weekly array list.
     */
    ArrayList<HashMap<String, Double>> weeklyArrayList;
    /**
     * The Weekly hash.
     */
    HashMap<String, ArrayList<HashMap<String, Double>>> weeklyHash;
    /**
     * The Temp start date.
     */
    Date tempStartDate;
    /**
     * The Temp string.
     */
    String tempString;
    /**
     * The Distance.
     */
    TextView distance, /**
     * The Time.
     */
    time, /**
     * The Pace.
     */
    pace, /**
     * The Speed.
     */
    speed, /**
     * The Date picker.
     */
    datePicker;
    /**
     * The Simple list view.
     */
    ListView simpleListView;
    /**
     * The Home button.
     */
    Button homeButton;
    /**
     * The Picker.
     */
    DatePickerDialog picker;
    /**
     * The Picked date.
     */
    String pickedDate;
    /**
     * The Saved week of year.
     */
    int savedWeekOfYear = -1;

    /**
     * This function will be called once the emulator access runningLog class.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.running_log);
        simpleListView = (ListView) findViewById(R.id.list);
        myDB = new DatabaseHelper(this);
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        int totalNumberOfWeeks = Calendar.getInstance().getActualMaximum(Calendar.WEEK_OF_YEAR);
        ArrayList<HashMap> hashmapTemp = new ArrayList<HashMap>(totalNumberOfWeeks);
        datePicker = (TextView) findViewById(R.id.datePicker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(runningLog.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                pickedDate =
                                        Integer.toString(dayOfMonth) + "-" + Integer.toString(monthOfYear + 1) + "-" +
                                                Integer.toString(year);
                                datePicker.setText(pickedDate);
                                Calendar currentCalendar = Calendar.getInstance();
                                try {

                                    currentCalendar.setTime(formatter.parse(pickedDate));
                                    int week = currentCalendar.get(Calendar.WEEK_OF_YEAR);
                                    Log.i("DatePicker: ", "week of year is: " + week);
                                    calculateAverageDistance(weeklyHash, week);
                                    calculateAverageTime(weeklyHash, week);
                                    calculateAveragePace(weeklyHash, week);
                                    calculateAverageSpeed(weeklyHash, week);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, year, month, day);
                picker.show();
            }
        });
        homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(runningLog.this, MainActivity.class);
                startActivity(intent);
            }
        });
        ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
        weeklyArrayList = new ArrayList<>();

        weeklyHash = new HashMap<>();

        Cursor data = myDB.getListContents();
        if (data.getCount() == 0) {
            Log.i("Cursor", "Cursor has: " + data.getCount());
            Toast.makeText(getApplicationContext(), "NO DATA", Toast.LENGTH_SHORT).show();
        } else {
            Log.i("Cursor", "Cursor has: " + data.getCount());
            while (data.moveToNext()) {
                Toast.makeText(getApplicationContext(), "Date: " + data.getString(1), Toast.LENGTH_SHORT).show();
                tempString = data.getString(1);
                Calendar tempCal = Calendar.getInstance();
                try {
                    tempStartDate = formatter.parse(tempString);
                    tempCal.setTime(tempStartDate);

                    int weekOfYear = tempCal.get(Calendar.WEEK_OF_YEAR);
                    if (weekOfYear != savedWeekOfYear) {
                        savedWeekOfYear = weekOfYear;
                        weeklyArrayList = new ArrayList<>();
                    }
//                    if(isDateInCurrentWeek(tempStartDate)==true){
                    HashMap<String, Double> hashMap = new HashMap<>();//create a hashmap to store the data in key
                    if (data.getString(2) == null) {
                        hashMap.put("distance", 0.0);
                    } else {
                        hashMap.put("distance", Double.parseDouble(data.getString(2).substring(0, 3)));
                    }
                    String[] split = data.getString(3).split(":");
                    int min = Integer.parseInt(split[0]);
                    int sec = Integer.parseInt(split[1]);
                    double totalTimeInSec = min * 60 + sec;
                    hashMap.put("time", totalTimeInSec);
                    if (data.getString(4).equals("N/A")) {
                        hashMap.put("pace", 0.0);
                    } else {
                        String tempString = data.getString(4).replace("hours", "");
                        tempString = tempString.replace("h", "");
                        tempString = tempString.replace("Minutes", "");
                        tempString = tempString.replace("m", "");
                        tempString = tempString.replace("Seconds", "");
                        tempString = tempString.replace("s", "");
                        tempString = tempString.replace("/km", "");
                        tempString = tempString.replace("/k", "");
                        tempString = tempString.replace("\\s+", ".");
                        Log.i("New Pace: ", "is: " + tempString);
                        String[] tempDoubleList = tempString.split("\\s+");
                        String tempPace = "";
                        Double tempPace2 = 0.0;
                        for (int i = 0; i < tempDoubleList.length; i++) {
                            if (i == 0) {
                                tempPace2 = tempPace2 + Double.parseDouble(tempDoubleList[i]) * 3600;
                            } else if (i == 1) {
                                tempPace2 = tempPace2 + Double.parseDouble(tempDoubleList[i]) * 60;
                            } else {
                                tempPace2 = tempPace2 + Double.parseDouble(tempDoubleList[i]);
                            }
                        }
                        Log.i("Counting: ", "final is: " + tempPace);
                        Log.i("Counting: ", "final2 is: " + tempPace2);
                        hashMap.put("pace", tempPace2);
                    }

                    hashMap.put("speed", Double.parseDouble(data.getString(5)));
                    weeklyArrayList.add(hashMap);
                    String week = Integer.toString(weekOfYear);
                    weeklyHash.put(week, weeklyArrayList);
                    if (weeklyArrayList.size() == 7) {
                        weeklyArrayList.clear();
                    }
                    Log.i("Checking", "weeklyHash size is: " + weeklyHash.size());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                HashMap<String, String> hashMap = new HashMap<>();//create a hashmap to store the data in key value pair
                hashMap.put("date", data.getString(1));
                if (data.getString(2) == null) {
                    hashMap.put("distance", "0.0");
                } else {
                    hashMap.put("distance", data.getString(2).substring(0, 3));
                }
                hashMap.put("time", data.getString(3));
                hashMap.put("pace", data.getString(4));
                hashMap.put("speed", data.getString(5));
                arrayList.add(hashMap);
            }
        }
        String[] from = {"date", "distance", "time", "pace", "speed"};//string array
        int[] to = {R.id.date, R.id.distance, R.id.time, R.id.pace, R.id.speed};//int array of views id's
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, arrayList, R.layout.mylist, from, to);//Create object and set the
        simpleListView.setAdapter(simpleAdapter);//sets the adapter for listView
    }

    /**
     * Calculate average distance.
     *
     * @param hashmap the hashmap
     * @param week    the week
     */
    public void calculateAverageDistance(HashMap hashmap, int week) {
        HashMap<String, ArrayList<HashMap<String, Double>>> tempHash = hashmap;

        if (tempHash.get(Integer.toString(week)) == null) {
            distance = (TextView) findViewById(R.id.distance);
            distance.setText("0.0km");
            Toast.makeText(runningLog.this, "There is no record in week " + week,
                    Toast.LENGTH_SHORT).show();
        } else {
            weeklyArrayList = tempHash.get(Integer.toString(week));
            Double totalDistance = 0.0;
            int numberOfDaysInList = weeklyArrayList.size();
            for (int i = 0; i < weeklyArrayList.size(); i++) {
                totalDistance = totalDistance + weeklyArrayList.get(i).get("distance");
            }
            Double averageDistance = totalDistance / numberOfDaysInList;
            Log.i("AverageDis", "is: " + averageDistance);
            Log.i("ToatalDis", "is: " + totalDistance);
            Log.i("NumOfDays", "is: " + numberOfDaysInList);
            int subStringEnd = 4;
            if (averageDistance.toString().length() < subStringEnd) {
                distance = (TextView) findViewById(R.id.distance);
                distance.setText(averageDistance.toString().substring(0, 3) + "km");
            } else {
                distance = (TextView) findViewById(R.id.distance);
                distance.setText(averageDistance.toString().substring(0, subStringEnd) + "km");
            }
        }
    }

    /**
     * Calculate average time.
     *
     * @param hashmap the hashmap
     * @param week    the week
     */
    public void calculateAverageTime(HashMap hashmap, int week) {
        HashMap<String, ArrayList<HashMap<String, Double>>> tempHash = hashmap;
        if (tempHash.get(Integer.toString(week)) == null) {
            time = (TextView) findViewById(R.id.time);
            time.setText("0:00");
            Toast.makeText(runningLog.this, "There is no record in week " + week,
                    Toast.LENGTH_SHORT).show();
        } else {
            weeklyArrayList = tempHash.get(Integer.toString(week));
            Double totalTime = 0.0;
            int numberOfDaysInList = weeklyArrayList.size();
            for (int i = 0; i < weeklyArrayList.size(); i++) {
                totalTime = totalTime + weeklyArrayList.get(i).get("time");
            }
            Double averageTime = totalTime / numberOfDaysInList;
            Double tempAverageTime = averageTime / 60;
            int value = tempAverageTime.intValue();
            Double decimalPart = tempAverageTime - value;
            Double seconds = decimalPart * 60;
            int secondsInt = seconds.intValue();
            String tempString = Integer.toString(value) + ":" + Integer.toString(secondsInt);
            time = (TextView) findViewById(R.id.time);
            Log.i("AverageTime", "is: " + tempString);
            time.setText(tempString);
        }
    }

    /**
     * Calculate average pace.
     *
     * @param hashmap the hashmap
     * @param week    the week
     */
    public void calculateAveragePace(HashMap hashmap, int week) {
        HashMap<String, ArrayList<HashMap<String, Double>>> tempHash = hashmap;
        if (tempHash.get(Integer.toString(week)) == null) {
            pace = (TextView) findViewById(R.id.pace);
            pace.setText("0:00");
            Toast.makeText(runningLog.this, "There is no record in week " + week,
                    Toast.LENGTH_SHORT).show();
        } else {
            weeklyArrayList = tempHash.get(Integer.toString(week));
            Double totalPace = 0.0;
            int numberOfDaysInList = weeklyArrayList.size();
            for (int i = 0; i < weeklyArrayList.size(); i++) {
                totalPace = totalPace + weeklyArrayList.get(i).get("pace");
            }

            //Converting time in seconds to hourly format and return the integer of hour.
            Double averagePace = totalPace / numberOfDaysInList;
            Double tempHour = averagePace / 3600;
            int intPartHour = tempHour.intValue();
            Double decimalPart = tempHour - intPartHour;

            //Gathering minutes component, multiply 60 as to convert it to percentage of basis 60.
            Double decimalPartMin = decimalPart * 60;
            int intPartMin = decimalPartMin.intValue();
            Double deicmalPartSec = decimalPartMin - intPartMin;


            //Gathering seconds component, mutiply 60 as to convert it to percentage of basis 60.
            Double deciamlPartSecAsTime = deicmalPartSec * 60;
            int intPartSec = deciamlPartSecAsTime.intValue();
            String paceTempString =
                    Integer.toString(intPartHour) + "h" + Integer.toString(intPartMin) + "m" + Integer.toString(intPartSec) + "s" +
                            "/km";
            pace = (TextView) findViewById(R.id.pace);
            pace.setText(paceTempString);
        }
    }


    /**
     * Calculate average speed.
     *
     * @param hashmap the hashmap
     * @param week    the week
     */
    public void calculateAverageSpeed(HashMap hashmap, int week) {
        HashMap<String, ArrayList<HashMap<String, Double>>> tempHash = hashmap;
        if (tempHash.get(Integer.toString(week)) == null) {
            speed = (TextView) findViewById(R.id.speed);
            speed.setText("0:00");
            Toast.makeText(runningLog.this, "There is no record in week " + week,
                    Toast.LENGTH_SHORT).show();
        } else {
            Double totalSpeed = 0.0;
            int numberOfDaysInList = weeklyArrayList.size();
            for (int i = 0; i < weeklyArrayList.size(); i++) {
                totalSpeed = totalSpeed + weeklyArrayList.get(i).get("speed");
            }
            Double averageSpeed = totalSpeed / numberOfDaysInList;
            Log.i("AverageSpeed", "is: " + averageSpeed);
            Log.i("ToatalSpeed", "is: " + totalSpeed);
            Log.i("NumOfDays", "is: " + numberOfDaysInList);
            int subStringEnd = 4;
            if (averageSpeed.toString().length() < subStringEnd) {
                speed = (TextView) findViewById(R.id.speed);
                speed.setText(averageSpeed.toString().substring(0, 3) + "km/h");
            } else {
                speed = (TextView) findViewById(R.id.speed);
                speed.setText(averageSpeed.toString().substring(0, subStringEnd) + "km/h");
            }
        }

    }
}

