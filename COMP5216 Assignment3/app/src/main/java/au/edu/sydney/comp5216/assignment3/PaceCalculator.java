package au.edu.sydney.comp5216.assignment3;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * The type Pace calculator.
 */
public class PaceCalculator extends AppCompatActivity {
    /**
     * The Calculate button.
     */
    Button calculateButton;
    /**
     * The Distance spinner.
     */
    Spinner distanceSpinner;
    /**
     * The Hour box.
     */
    EditText hourBox, /**
     * The Min box.
     */
    minBox, /**
     * The Sec box.
     */
    secBox, /**
     * The Distance box.
     */
    distanceBox;
    /**
     * The Result view.
     */
    TextView resultView;
    /**
     * The Pace minutes.
     */
    int paceMinutes;
    /**
     * The Hour.
     */
    String hour, /**
     * The Minutes.
     */
    minutes, /**
     * The Seconds.
     */
    seconds, /**
     * The Distance string.
     */
    distanceString, /**
     * The Distance unit.
     */
    distanceUnit, /**
     * The Append string.
     */
    appendString;
    /**
     * The Minutes double.
     */
    double minutesDouble, /**
     * The Hour double.
     */
    hourDouble, /**
     * The Seconds double.
     */
    secondsDouble, /**
     * The Total time in hour.
     */
    totalTimeInHour, /**
     * The Pace.
     */
    pace, /**
     * The Distance double.
     */
    distanceDouble, /**
     * The Pace hour.
     */
    paceHour;

    /**
     * This function will be called once the emulator access PaceCalculator class.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pacecalculator);
        addItemsOnSpinner2();
        hourBox = findViewById(R.id.hour);
        minBox = findViewById(R.id.minutes);
        secBox = findViewById(R.id.second);
        distanceBox = findViewById(R.id.distance);
        resultView = findViewById(R.id.resultView);
        distanceDouble = 0;
        calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalTimeInHour = 0;
                hour = hourBox.getText().toString();
                minutes = minBox.getText().toString();
                seconds = secBox.getText().toString();
                distanceString = distanceBox.getText().toString();
                distanceUnit = distanceSpinner.getSelectedItem().toString();
                if (TextUtils.isEmpty(hour) && (TextUtils.isEmpty(minutes) && TextUtils.isEmpty(seconds))) {
                    Toast.makeText(PaceCalculator.this, "Please enter the time",
                            Toast.LENGTH_LONG).show();

                } else {
                    if (!TextUtils.isEmpty(hour)) {
                        hourDouble = Double.parseDouble(hour);
                        totalTimeInHour = totalTimeInHour + hourDouble;
                        Log.i("Time gathering", "The total time in hour is: " + totalTimeInHour);
                    }
                    if (!TextUtils.isEmpty(minutes)) {
                        minutesDouble = Double.parseDouble(minutes);
                        minutesDouble = minutesDouble / 60;
                        totalTimeInHour = totalTimeInHour + minutesDouble;
                        Log.i("Time gathering", "The total time in hour is: " + totalTimeInHour);
                    }
                    if (!TextUtils.isEmpty(seconds)) {
                        secondsDouble = Double.parseDouble(seconds);
                        secondsDouble = secondsDouble / 3600;
                        totalTimeInHour = totalTimeInHour + secondsDouble;
                        Log.i("Time gathering", "The total time in hour is: " + totalTimeInHour);
                    }
                }
                if (TextUtils.isEmpty(distanceString)) {
                    Toast.makeText(PaceCalculator.this, "Please enter the distance",
                            Toast.LENGTH_LONG).show();
                    return;
                } else {
                    distanceDouble = Double.parseDouble(distanceString);
                    Log.i("Distance", "We get distance in Int: " + distanceDouble);
                }
                calculatePace(distanceDouble, totalTimeInHour, distanceUnit);
            }
        });
    }


    /**
     * Calculate pace.
     *
     * @param distance     the distance
     * @param time         the time
     * @param distanceUnit the distance unit
     */
    public void calculatePace(double distance, double time, String distanceUnit) {
        if (distanceUnit.equals("Meters")) {
            appendString = "per meters";
        } else if (distanceUnit.equals("Miles")) {
            appendString = "per miles";
        } else if (distanceUnit.equals("Yards")) {
            appendString = "per yards";
        } else {
            appendString = "per kilometers";
        }
        pace = time / distance;
        paceHour = (int) pace;

        double hoursDeciamlPart = pace - paceHour;

        double minutesTemp = 60 * hoursDeciamlPart;
        paceMinutes = (int) minutesTemp;
        double secondsDecimalPart = minutesTemp - paceMinutes;
        double secondTemp = secondsDecimalPart * 60;
        BigDecimal bd = new BigDecimal(secondTemp).setScale(2, RoundingMode.HALF_UP);
        double secondInFourDigit = bd.doubleValue();
        String paceString = paceHour + " hours " + paceMinutes + " Minutes " + secondInFourDigit +
                " Seconds " + appendString;
        resultView.setText(paceString);
    }


    /**
     * Add items on spinner 2, which allow users to pick distance unit.
     */
    public void addItemsOnSpinner2() {

        distanceSpinner = (Spinner) findViewById(R.id.spinnerDistance);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.distance_unit, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSpinner.setAdapter(adapter);
    }

}
