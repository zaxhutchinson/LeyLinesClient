package mycompany.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

/**
 * Created by tnash219 on 11/16/2014.
 */
public class SettingsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String mLatitude, mLongitude;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        buildGoogleApiClient();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int lifestyleSetting, sensitivitySetting;

        //lifestyle init
        Spinner lifestyleSpinner = (Spinner)findViewById(R.id.spinner);
        TextView lifestyleTextView = (TextView)findViewById(R.id.textView3);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> lifestyleAdapter = ArrayAdapter.createFromResource(this,
                R.array.lifestyle_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        lifestyleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        lifestyleSpinner.setAdapter(lifestyleAdapter);

        lifestyleSetting = sharedPreferences.getInt("pref_key_lifestyle_setting",0);
        lifestyleTextView.setText(getResources().getStringArray(R.array.lifestyle_summaries)[lifestyleSetting]);

        lifestyleSpinner.setSelection(lifestyleSetting);
        lifestyleSpinner.setOnItemSelectedListener(this);

        //sensitivity init
        Spinner sensitivitySpinner = (Spinner)findViewById(R.id.spinner2);
        TextView sensitivityTextView = (TextView)findViewById(R.id.textView4);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sensitivityAdapter = ArrayAdapter.createFromResource(this,
                R.array.sensitivity_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        sensitivityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        sensitivitySpinner.setAdapter(sensitivityAdapter);

        sensitivitySetting = sharedPreferences.getInt("pref_key_sensitivity_setting",0);
        sensitivityTextView.setText(getResources().getStringArray(R.array.sensitivity_summaries)[sensitivitySetting]);
        sensitivitySpinner.setSelection(sensitivitySetting);
        sensitivitySpinner.setOnItemSelectedListener(this);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.test);
        CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox);
        Button button = (Button)findViewById(R.id.button);

        checkBox.setChecked(sharedPreferences.getBoolean("pref_key_advanced_settings",false));

        if (!checkBox.isChecked()) {
            relativeLayout.setEnabled(true);
            lifestyleSpinner.setEnabled(true);
            sensitivitySpinner.setEnabled(true);
            button.setEnabled(false);
        }
        else {
            relativeLayout.setEnabled(false);
            lifestyleSpinner.setEnabled(false);
            sensitivitySpinner.setEnabled(false);
            button.setEnabled(true);
        }

        //disallows further editing after account is created and confirmed
        //TODO: implement disabling accounts at some point
        if(sharedPreferences.getBoolean("pref_key_account_setup",false)) {
            EditText accountField = (EditText)findViewById(R.id.editTextUid);
            accountField.setText("User ID: " + sharedPreferences.getString("pref_key_UID",""));
            accountField.setEnabled(false);
            accountField = (EditText)findViewById(R.id.editTextHost);
            accountField.setText("Host: " + sharedPreferences.getString("pref_key_HOST",""));
            accountField.setEnabled(false);
            accountField = (EditText)findViewById(R.id.editTextPort);
            accountField.setText("Port: " + sharedPreferences.getString("pref_key_PORT",""));
            accountField.setEnabled(false);
            Button submit = (Button)findViewById(R.id.button5);
            submit.setEnabled(false);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = adapterView.getId();
        if (id == R.id.spinner) {
            if (sharedPreferences.getBoolean("pref_key_advanced_settings",false) && sharedPreferences.getInt("pref_key_lifestyle_setting",-1) != i) {
                Toast.makeText(this, "Lifestyle set to " + getResources().getStringArray(R.array.lifestyle_choices)[i], Toast.LENGTH_SHORT).show();
            }
            editor.putInt("pref_key_lifestyle_setting",i);
            TextView textView = (TextView)findViewById(R.id.textView3);
            textView.setText(getResources().getStringArray(R.array.lifestyle_summaries)[i]);
            if (i == 0) {
                editor.putBoolean("pref_key_distance_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.adventurous_lifestyle)[0]));
                editor.putString("pref_key_distance_deviation_setting",getResources().getStringArray(R.array.adventurous_lifestyle)[1]);
                editor.putBoolean("pref_key_time_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.adventurous_lifestyle)[2]));
                editor.putString("pref_key_time_deviation_setting",getResources().getStringArray(R.array.adventurous_lifestyle)[3]);
                editor.putBoolean("pref_key_tracker_inactive_setting", Boolean.parseBoolean(getResources().getStringArray(R.array.adventurous_lifestyle)[4]));
                editor.putString("pref_key_tracker_inactive_duration_setting",getResources().getStringArray(R.array.adventurous_lifestyle)[5]);
            }
            else if (i == 1) {
                editor.putBoolean("pref_key_distance_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.regressive_lifestyle)[0]));
                editor.putString("pref_key_distance_deviation_setting",getResources().getStringArray(R.array.regressive_lifestyle)[1]);
                editor.putBoolean("pref_key_time_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.regressive_lifestyle)[2]));
                editor.putString("pref_key_time_deviation_setting",getResources().getStringArray(R.array.regressive_lifestyle)[3]);
                editor.putBoolean("pref_key_tracker_inactive_setting", Boolean.parseBoolean(getResources().getStringArray(R.array.regressive_lifestyle)[4]));
                editor.putString("pref_key_tracker_inactive_duration_setting",getResources().getStringArray(R.array.regressive_lifestyle)[5]);
            }
            else if (i == 2) {
                editor.putBoolean("pref_key_distance_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.child_lifestyle)[0]));
                editor.putString("pref_key_distance_deviation_setting",getResources().getStringArray(R.array.child_lifestyle)[1]);
                editor.putBoolean("pref_key_time_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.child_lifestyle)[2]));
                editor.putString("pref_key_time_deviation_setting",getResources().getStringArray(R.array.child_lifestyle)[3]);
                editor.putBoolean("pref_key_tracker_inactive_setting", Boolean.parseBoolean(getResources().getStringArray(R.array.child_lifestyle)[4]));
                editor.putString("pref_key_tracker_inactive_duration_setting",getResources().getStringArray(R.array.child_lifestyle)[5]);
            }
            else if (i == 3) {
                editor.putBoolean("pref_key_distance_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.adventurous_lifestyle)[0]));
                editor.putString("pref_key_distance_deviation_setting",getResources().getStringArray(R.array.adventurous_lifestyle)[1]);
                editor.putBoolean("pref_key_time_settings", Boolean.parseBoolean(getResources().getStringArray(R.array.adventurous_lifestyle)[2]));
                editor.putString("pref_key_time_deviation_setting",getResources().getStringArray(R.array.adventurous_lifestyle)[3]);
                editor.putBoolean("pref_key_tracker_inactive_setting", Boolean.parseBoolean(getResources().getStringArray(R.array.adventurous_lifestyle)[4]));
                editor.putString("pref_key_tracker_inactive_duration_setting",getResources().getStringArray(R.array.adventurous_lifestyle)[5]);
            }

        }
        else if (id == R.id.spinner2) {
            if (sharedPreferences.getBoolean("pref_key_advanced_settings",false) && sharedPreferences.getInt("pref_key_sensitivity_setting",-1) != i) {
                Toast.makeText(this, "Sensitivity set to " + getResources().getStringArray(R.array.sensitivity_choices)[i], Toast.LENGTH_SHORT).show();
            }
            editor.putInt("pref_key_sensitivity_setting",i);
            TextView textView = (TextView)findViewById(R.id.textView4);
            textView.setText(getResources().getStringArray(R.array.sensitivity_summaries)[i]);
        }
        editor.commit();
    }

    public void onCheckboxClicked(View view) {
        int id = view.getId();
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.test);
        Spinner lifestyleSpinner = (Spinner)findViewById(R.id.spinner);
        Spinner sensitivitySpinner = (Spinner)findViewById(R.id.spinner2);
        Button button = (Button)findViewById(R.id.button);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (R.id.checkBox == id) {
            if (!((CheckBox) view).isChecked()) {
                relativeLayout.setEnabled(true);
                lifestyleSpinner.setEnabled(true);
                sensitivitySpinner.setEnabled(true);
                button.setEnabled(false);
                editor.putBoolean("pref_key_advanced_settings",true);
            }
            else {
                relativeLayout.setEnabled(false);
                lifestyleSpinner.setEnabled(false);
                sensitivitySpinner.setEnabled(false);
                button.setEnabled(true);
                editor.putBoolean("pref_key_advanced_settings",false);
            }
        }
        editor.commit();
    }

    public void onButtonClicked(View view) {
        int id = view.getId();

        if (R.id.button6 == id) {
            Toast.makeText(this, "Settings Synced", Toast.LENGTH_SHORT).show();
        }

        if (R.id.button == id) {
            Intent preferences = new Intent(this, AdvancedSettingsActivity.class);
            startActivity(preferences);
        }

        if (R.id.button5 == id) {
            TextView textView = (TextView)findViewById(R.id.editTextUid);
            String uid = textView.getText().toString();
            textView = (TextView)findViewById(R.id.editTextHost);
            String host = textView.getText().toString();
            textView = (TextView)findViewById(R.id.editTextPort);
            String port = textView.getText().toString();
            SendAccountFragment sendAccountFragment = new SendAccountFragment().newInstance(uid,host,port,mLatitude,mLongitude);
            sendAccountFragment.show(getSupportFragmentManager(),"sendAccountFragment");
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        //Toast.makeText(this, "Connected to api", Toast.LENGTH_LONG).show();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = String.valueOf(mLastLocation.getLatitude());
            mLongitude = String.valueOf(mLastLocation.getLongitude());
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static class SendAccountFragment extends DialogFragment {

        public SendAccountFragment newInstance(String requestedUid, String selectedHost, String selectedPort, String currentLatitude, String currentLongitude) {
            SendAccountFragment sendAccountFragment = new SendAccountFragment();
            Bundle args = new Bundle();
            args.putString("UID", requestedUid);
            args.putString("HOST", selectedHost);
            args.putString("PORT", selectedPort);
            args.putString("LATITUDE",currentLatitude);
            args.putString("LONGITUDE",currentLongitude);
            sendAccountFragment.setArguments(args);
            return sendAccountFragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final String uid = getArguments().getString("UID");
            final String host = getArguments().getString("HOST");
            final String port = getArguments().getString("PORT");
            final String latitude = getArguments().getString("LATITUDE");
            final String longitude = getArguments().getString("LONGITUDE");
            String message = String.format("Desired UID: %s\nServer Location: %s:%s\nCurrent Location: (%s,%s)\nIs this correct?",uid,host,port,latitude,longitude);

            builder.setMessage(message)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            RequestAccountTask requestAccountTask = new RequestAccountTask(getActivity());
                            requestAccountTask.execute(uid, host, port, latitude, longitude, new String());

                            //temp stuff
//                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
//                            editor.putString("pref_key_UID",uid);
//                            editor.putString("pref_key_HOST",host);
//                            editor.putString("pref_key_PORT",port);
//                            editor.putBoolean("pref_key_account_setup",true);
//                            editor.commit();
//                            getActivity().finish();
//                            Toast.makeText(getActivity(), "Account created successfully", Toast.LENGTH_LONG).show();


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
}
