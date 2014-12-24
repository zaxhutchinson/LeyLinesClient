package mycompany.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * not currently implemented
 * allows users to indicate destinations to the server
 */
public class DestinationActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.button2) {
            DialogFragment newFragment = new TimePickerFragment1();
            newFragment.show(getSupportFragmentManager(), "arrivalPicker");
        }

        else if (id == R.id.button3) {
            DialogFragment newFragment = new TimePickerFragment2();
            newFragment.show(getSupportFragmentManager(), "destinationPicker");
        }

        else if (id == R.id.button4) {

            boolean valid = true;

            //Parse GPS
            double latitude = 0, longitude = 0;

            EditText editText = (EditText)findViewById(R.id.editText);
            if (editText.length() > 0) {
                latitude = Double.parseDouble(editText.getText().toString());
                if (latitude > 90 || latitude < -90) {
                    Toast.makeText(this, "Cannot add location: invalid latitude", Toast.LENGTH_LONG).show();
                    valid = false;
                }
            }
            else {
                Toast.makeText(this, "Cannot add location: latitude missing", Toast.LENGTH_LONG).show();
                valid = false;
            }


            EditText editText2 = (EditText)findViewById(R.id.editText2);
            if (valid) {
                if (editText2.length() > 0) {
                    longitude = Double.parseDouble(editText2.getText().toString());
                    if (longitude > 180 || longitude < -180) {
                        Toast.makeText(this, "Cannot add location: invalid longitude", Toast.LENGTH_LONG).show();
                        valid = false;
                    }
                } else {
                    Toast.makeText(this, "Cannot add location: longitude missing", Toast.LENGTH_LONG).show();
                    valid = false;
                }
            }

            //Parse time frame
            int timeframe[] = { 0, 0, 0, 0 };

            if (valid) {

                SimpleDateFormat format = new SimpleDateFormat("H:m");
                TextView textView6 = (TextView) findViewById(R.id.textView6);
                Date arrival = new Date();
                TextView textView10 = (TextView) findViewById(R.id.textView10);
                Date departure = new Date();
                try {
                    arrival = format.parse(textView6.getText().toString());
                    departure = format.parse(textView10.getText().toString());
                } catch (ParseException e) {
                    Toast.makeText(this, "Cannot add location: invalid time format", Toast.LENGTH_LONG).show();
                    valid = false;
                    e.printStackTrace();
                }


                if (arrival.getTime() <= departure.getTime()) {
                    timeframe[0] = arrival.getHours();
                    timeframe[1] = arrival.getMinutes();
                    timeframe[2] = departure.getHours();
                    timeframe[3] = departure.getMinutes();
                }

                else {
                    Toast.makeText(this, "Cannot add location: invalid time range", Toast.LENGTH_LONG).show();
                    valid = false;
                }
            }

            //Parse days of week
            boolean week[];
            if (valid) {

                CheckBox sunday = (CheckBox) findViewById(R.id.checkBox2);
                CheckBox monday = (CheckBox) findViewById(R.id.checkBox3);
                CheckBox tuesday = (CheckBox) findViewById(R.id.checkBox4);
                CheckBox wednesday = (CheckBox) findViewById(R.id.checkBox5);
                CheckBox thursday = (CheckBox) findViewById(R.id.checkBox6);
                CheckBox friday = (CheckBox) findViewById(R.id.checkBox7);
                CheckBox saturday = (CheckBox) findViewById(R.id.checkBox8);

                week = new boolean[]{
                        sunday.isChecked(),
                        monday.isChecked(),
                        tuesday.isChecked(),
                        wednesday.isChecked(),
                        thursday.isChecked(),
                        friday.isChecked(),
                        saturday.isChecked()
                };

                UpdateDestinationFragment destination = new UpdateDestinationFragment().newInstance(latitude, longitude, week, timeframe);
                destination.show(getSupportFragmentManager(),"destination");
            }
        }
    }

    public static class UpdateDestinationFragment extends DialogFragment {

        public UpdateDestinationFragment newInstance(double latitude, double longitude, boolean[] week, int[] timeframe) {
            UpdateDestinationFragment updateDestinationFragment = new UpdateDestinationFragment();
            Bundle args = new Bundle();
            args.putDouble("LATITUDE",latitude);
            args.putDouble("LONGITUDE",longitude);
            args.putBooleanArray("WEEK",week);
            args.putIntArray("TIMEFRAME",timeframe);
            updateDestinationFragment.setArguments(args);
            return updateDestinationFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final double latitude = getArguments().getDouble("LATITUDE");
            final double longitude = getArguments().getDouble("LONGITUDE");
            final boolean week[] = getArguments().getBooleanArray("WEEK");
            final int timeframe[] = getArguments().getIntArray("TIMEFRAME");

            StringBuffer message = new StringBuffer();

            //add coordinates to message
            message.append("Coordinates: (" + latitude + "," + longitude + ")\n");

            //figure out which days of week are considered
            StringBuffer daysOfWeek = new StringBuffer();
            int count = 0;
            for (int i = 0; i < week.length; i++) {
                count += week[i] ? 1 : -1;
            }
            if (count == 7 || count == -7) {
                daysOfWeek.append("Daily");
            }
            else {
                String days[] = {"Sun, ", "Mon, ", "Tue, ", "Wed, ", "Thu, ", "Fri, ", "Sat, "};
                for (int i = 0; i < week.length; i++) {
                    if (week[i]) daysOfWeek.append(days[i]);
                }
                daysOfWeek.delete(daysOfWeek.length()-2,daysOfWeek.length()-1);
            }

            //add days of week to message
            message.append("Days: " + daysOfWeek + "\n");

            //add timeframe to message
            message.append(String.format("From: %d:%02d", timeframe[0], timeframe[1]));
            message.append(String.format(" To: %d:%02d\n", timeframe[2], timeframe[3]));

            //add final question to message
            message.append("\nIs this correct?\n(Cannot be undone)");

            builder.setMessage(message)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });

            // Create the AlertDialog object and return it
            return builder.create();
        }


    }

    public static class TimePickerFragment1 extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
                TextView arrivalTime = (TextView)getActivity().findViewById(R.id.textView6);
                arrivalTime.setText(String.format("%d:%02d", hourOfDay, minute));
        }
    }

    public static class TimePickerFragment2 extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user

                TextView departureTime = (TextView)getActivity().findViewById(R.id.textView10);
                departureTime.setText(String.format("%d:%02d", hourOfDay, minute));
        }
    }


}
