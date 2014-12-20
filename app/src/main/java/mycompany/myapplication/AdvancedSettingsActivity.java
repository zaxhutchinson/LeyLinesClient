package mycompany.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.BaseAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;


/**
 * Created by tnash219 on 11/1/2014.
 */
public class AdvancedSettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        /*
        //reed dat file yo
        File myPath = new File(Environment.getExternalStorageDirectory().toString());
        File myFile = new File(myPath, "MySharedPreferences");

        try
        {
            FileWriter fw = new FileWriter(myFile);
            PrintWriter pw = new PrintWriter(fw);

            Map<String,?> prefsMap = sharedPreferences.getAll();

            for(Map.Entry<String,?> entry : prefsMap.entrySet())
            {
                pw.println(entry.getKey() + " : " + entry.getValue().toString());
            }

            pw.close();
            fw.close();
        }
        catch (Exception e)
        {
            System.out.println(e.toString());
        }
        */

        /*Contacts start*/
        //Contact 1
        String container = sharedPreferences.getString("pref_key_contact1_info_setting","");
        //if we go with default instead of a null string we could use something like this
        //getResources().getString(R.string.contact_type_default)
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact1_config");
            preference.setSummary(container);
            preference = findPreference("pref_key_contact1_info_setting");
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_contact1_config");
            preference.setSummary("None");
            preference = findPreference("pref_key_contact1_info_setting");
            preference.setSummary("None");
        }

        container = sharedPreferences.getString("pref_key_contact1_type_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact1_type_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = listPreference.getEntry().toString();
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_contact1_alert_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact1_alert_setting");
            preference.setSummary("Minimum alert level: Defcon " + container);
        }

        //Contact 2
        container = sharedPreferences.getString("pref_key_contact2_info_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact2_config");
            preference.setSummary(container);
            preference = findPreference("pref_key_contact2_info_setting");
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_contact2_config");
            preference.setSummary("None");
            preference = findPreference("pref_key_contact2_info_setting");
            preference.setSummary("None");
        }
        container = sharedPreferences.getString("pref_key_contact2_type_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact2_type_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = listPreference.getEntry().toString();
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_contact2_alert_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact2_alert_setting");
            preference.setSummary("Minimum alert level: Defcon " + container);
        }

        //Contact 3
        container = sharedPreferences.getString("pref_key_contact3_info_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact3_config");
            preference.setSummary(container);
            preference = findPreference("pref_key_contact3_info_setting");
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_contact3_config");
            preference.setSummary("None");
            preference = findPreference("pref_key_contact3_info_setting");
            preference.setSummary("None");
        }
        container = sharedPreferences.getString("pref_key_contact3_type_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact3_type_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = listPreference.getEntry().toString();
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_contact3_alert_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_contact3_alert_setting");
            preference.setSummary("Minimum alert level: Defcon " + container);
        }
        /*Contacts end*/

        /*Background Activity start*/
        //Alerts
        container = sharedPreferences.getString("pref_key_alert_frequency","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_alert_frequency");
            ListPreference listPreference = (ListPreference)preference;
            container = "Alerts can be sent " + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }

        //GPS Collecting
        container = sharedPreferences.getString("pref_key_gps_collect_frequency","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_gps_collect_frequency");
            ListPreference listPreference = (ListPreference)preference;
            container = "GPS coordinates are retrieved " + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }

        //GPS Sending
        container = sharedPreferences.getString("pref_key_gps_send_frequency","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_gps_send_frequency");
            ListPreference listPreference = (ListPreference)preference;
            container = "GPS coordinates sync " + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }
        /*Background Activity end*/

        /*Alert Configuration start*/
        //Distance Overall
        Boolean boolContainer = sharedPreferences.getBoolean("pref_key_distance_settings",false);
        if (boolContainer) {
            Preference preference = findPreference("pref_key_distance_config");
            container = "Alerts currently enabled";
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_distance_config");
            container = "Alerts currently disabled";
            preference.setSummary(container);
        }
        float floatContainer = sharedPreferences.getFloat("pref_key_distance_importance",0);

        if (floatContainer != 0) {
            //placeholder
        }
        container = sharedPreferences.getString("pref_key_distance_deviation_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_distance_deviation_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to remain within "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " of your normal path";
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_distance_deviation_alert","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_distance_deviation_alert");
            preference.setSummary(container);
        }

        /*Alert Configuration start*/
        // Distance Relative to previous paths
        boolContainer = sharedPreferences.getBoolean("pref_key_distance_total_settings",false);
        if(boolContainer) {
            Preference preference = findPreference("pref_key_distance_total_config");
            container = "Alerts currently enabled";
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_distance_total_config");
            container = "Alerts currently disabled";
            preference.setSummary(container);
        }
        floatContainer = sharedPreferences.getFloat("pref_key_distance_total_importance",0);

        if(floatContainer != 0) {
            //placeholder
        }
        container = sharedPreferences.getString("pref_key_distance_deviation_total_setting","");
        if(container.length() > 0) {
            Preference preference = findPreference("pref_key_distance_deviation_total_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "A previously unknown path cannot be longer than "
                    + listPreference.getEntry().toString().toLowerCase()
                    + ".";
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_distance_deviation_total_alert","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_distance_deviation_total_alert");
            preference.setSummary(container);
        }

        //Time
        boolContainer = sharedPreferences.getBoolean("pref_key_time_settings",false);
        if (boolContainer) {
            Preference preference = findPreference("pref_key_time_config");
            container = "Alerts currently enabled";
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_time_config");
            container = "Alerts currently disabled";
            preference.setSummary(container);
        }
        floatContainer = sharedPreferences.getFloat("pref_key_time_importance",0);

        if (floatContainer != 0) {
            //placeholder
        }
        container = sharedPreferences.getString("pref_key_time_deviation_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_time_deviation_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to be off your normal path for more than "
                    + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_time_deviation_alert","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_time_deviation_alert");
            preference.setSummary(container);
        }

        //Location
        boolContainer = sharedPreferences.getBoolean("pref_key_location_settings",false);
        if (boolContainer) {
            Preference preference = findPreference("pref_key_location_config");
            container = "Alerts currently enabled";
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_location_config");
            container = "Alerts currently disabled";
            preference.setSummary(container);
        }
        floatContainer = sharedPreferences.getFloat("pref_key_location_importance",0);

        if (floatContainer != 0) {
            //placeholder
        }
        container = sharedPreferences.getString("pref_key_location_distance_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_location_distance_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to be within "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " of certain locations around certain times";
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_location_time_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_location_time_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to be nearby certain locations within "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " of designated times";
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_location_settings_alert","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_location_settings_alert");
            preference.setSummary(container);
        }

        //Battery
        boolContainer = sharedPreferences.getBoolean("pref_key_battery_settings",false);
        if (boolContainer) {
            Preference preference = findPreference("pref_key_battery_config");
            container = "Alerts currently enabled";
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_battery_config");
            container = "Alerts currently disabled";
            preference.setSummary(container);
        }
        floatContainer = sharedPreferences.getFloat("pref_key_battery_importance",0);

        if (floatContainer != 0) {
            //placeholder
        }

        container = sharedPreferences.getString("pref_key_battery_level_settin","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_battery_level_settin");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to keep your battery above "
                    + listPreference.getValue()
                    + "%% charge remaining";
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_battery_level_alert","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_battery_level_alert");
            preference.setSummary(container);
        }

        //Tracker
        boolContainer = sharedPreferences.getBoolean("pref_key_tracker_settings",false);
        if (boolContainer) {
            Preference preference = findPreference("pref_key_tracker_config");
            container = "Alerts currently enabled";
            preference.setSummary(container);
        }
        else {
            Preference preference = findPreference("pref_key_tracker_config");
            container = "Alerts currently disabled";
            preference.setSummary(container);
        }
        floatContainer = sharedPreferences.getFloat("pref_key_tracker_importance",0);

        if (floatContainer != 0) {
            //placeholder
        }
        container = sharedPreferences.getString("pref_key_tracker_disabled_duration_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_tracker_disabled_duration_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to turn your tracker off for more than "
                    + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_tracker_inactive_duration_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_tracker_inactive_duration_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to remain in the same area more than "
                    + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_tracker_response_misses_setting","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_tracker_response_misses_setting");
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to miss more than "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " consecutive server updates";
            preference.setSummary(container);
        }
        container = sharedPreferences.getString("pref_key_tracker_settings_alert","");
        if (container.length() > 0) {
            Preference preference = findPreference("pref_key_tracker_settings_alert");
            preference.setSummary(container);
        }
        /*Alert Configuration end*/

        //set up a listener for changes in the setting menu
        SharedPreferences.OnSharedPreferenceChangeListener listener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        // listener implementation
                    }
                };

        //begin listening for any changes to the settings menu
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String container;
        Preference preference = findPreference(key);

        /*Contacts start*/
        //Contact 1
        if (key.equals("pref_key_contact1_info_setting")) {
            container = sharedPreferences.getString(key, "");
            if (container.length() == 0) container = "None";
            preference.setSummary(container);
            preference = findPreference("pref_key_contact1_config");
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_contact1_type_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = listPreference.getEntry().toString();
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_contact1_alert_setting")) {
            container = "Minimum alert level: Defcon " + sharedPreferences.getString(key, "");
            preference.setSummary(container);
        }

        //Contact 2
        else if (key.equals("pref_key_contact2_info_setting")) {
            container = sharedPreferences.getString(key, "");
            if (container.length() == 0) container = "None";
            preference.setSummary(container);
            preference = findPreference("pref_key_contact2_config");
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_contact2_type_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = listPreference.getEntry().toString();
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_contact2_alert_setting")) {
            container = "Minimum alert level: Defcon " + sharedPreferences.getString(key, "");
            preference.setSummary(container);
        }

        //Contact 3
        else if (key.equals("pref_key_contact3_info_setting")) {
            container = sharedPreferences.getString(key, "");
            if (container.length() == 0) container = "None";
            preference.setSummary(container);
            preference = findPreference("pref_key_contact3_config");
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_contact3_type_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = listPreference.getEntry().toString();
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_contact3_alert_setting")) {
            container = "Minimum alert level: Defcon " + sharedPreferences.getString(key, "");
            preference.setSummary(container);
        }

        //Self Notify
        else if (key.equals("pref_key_contact_self_setting")) {
            //static summary
        }
        /*Contacts end*/

        /*Background Activity start*/
        //Alerts
        else if (key.equals("pref_key_alert_frequency")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "Alerts can be sent " + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }

        //GPS Collecting
        else if (key.equals("pref_key_gps_collect_frequency")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "GPS coordinates are retrieved " + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }

        //GPS Sending
        else if (key.equals("pref_key_gps_send_frequency")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "GPS coordinates sync " + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }
        /*Background Activity end*/

        /*Alert Configuration start*/
        //Distance
        else if (key.equals("pref_key_distance_settings")) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)preference;
            preference = findPreference("pref_key_distance_config");
            preference.setSummary(checkBoxPreference.isChecked() ? "Alerts currently enabled" : "Alerts currently disabled");
        }
        else if (key.equals("pref_key_distance_importance")) {
            //placeholder
        }
        else if (key.equals("pref_key_distance_deviation_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to be off your normal path for more than "
                    + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }

        //Time
        else if (key.equals("pref_key_time_settings")) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)preference;
            preference = findPreference("pref_key_time_config");
            preference.setSummary(checkBoxPreference.isChecked() ? "Alerts currently enabled" : "Alerts currently disabled");
        }
        else if (key.equals("pref_key_time_importance")) {
            //placeholder
        }
        else if (key.equals("pref_key_time_deviation_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to remain within "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " of your normal path";
            preference.setSummary(container);
        }

        //Location
        else if (key.equals("pref_key_location_settings")) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)preference;
            preference = findPreference("pref_key_location_config");
            preference.setSummary(checkBoxPreference.isChecked() ? "Alerts currently enabled" : "Alerts currently disabled");
        }
        else if (key.equals("pref_key_location_importance")) {
            //placeholder
        }
        else if (key.equals("pref_key_location_distance_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to be within "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " of certain locations around certain times";
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_location_time_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to be nearby certain locations within "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " of designated times";
            preference.setSummary(container);
        }

        //Battery
        else if (key.equals("pref_key_battery_settings")) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)preference;
            preference = findPreference("pref_key_battery_config");
            preference.setSummary(checkBoxPreference.isChecked() ? "Alerts currently enabled" : "Alerts currently disabled");
        }
        else if (key.equals("pref_key_battery_importance")) {
            //placeholder
        }
        else if (key.equals("pref_key_battery_level_settin")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected to keep your battery above "
                    + listPreference.getValue()
                    + "%% charge remaining";
            preference.setSummary(container);
        }

        //Tracker
        else if (key.equals("pref_key_tracker_settings")) {
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference)preference;
            preference = findPreference("pref_key_tracker_config");
            preference.setSummary(checkBoxPreference.isChecked() ? "Alerts currently enabled" : "Alerts currently disabled");
        }
        else if (key.equals("pref_key_tracker_importance")) {
            //placeholder
        }
        else if (key.equals("pref_key_tracker_disabled_setting") ||
            key.equals("pref_key_tracker_inactive_setting") ||
            key.equals("pref_key_tracker_response_setting")) {
            //these have static summaries and don't affect any others
        }
        else if (key.equals("pref_key_tracker_disabled_duration_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to turn your tracker off for more than "
                    + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_tracker_inactive_duration_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to remain in the same area more than "
                    + listPreference.getEntry().toString().toLowerCase();
            preference.setSummary(container);
        }
        else if (key.equals("pref_key_tracker_response_misses_setting")) {
            ListPreference listPreference = (ListPreference)preference;
            container = "You're expected not to miss more than "
                    + listPreference.getEntry().toString().toLowerCase()
                    + " consecutive server updates";
            preference.setSummary(container);
        }
        /*Alert Configuration end*/

        //self explanatory stuff from all categories
        else  {
            container = sharedPreferences.getString(key, "");
            preference.setSummary(container);
        }

        //needed for finalizing updates to the ui
        ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
