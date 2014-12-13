package mycompany.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by tnash219 on 11/16/2014.
 */
public class SettingsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
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
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = adapterView.getId();
        if (id == R.id.spinner) {
            editor.putInt("pref_key_lifestyle_setting",i);
            TextView textView = (TextView)findViewById(R.id.textView3);
            textView.setText(getResources().getStringArray(R.array.lifestyle_summaries)[i]);
        }
        else if (id == R.id.spinner2) {
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

        if (R.id.button == id) {
            Intent preferences = new Intent(this, AdvancedSettingsActivity.class);
            startActivity(preferences);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
