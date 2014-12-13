package mycompany.myapplication;

import android.app.Activity;

/**
 * Created by tnash219 on 10/22/2014.
 */
public class UserStatus {

    //Represents user activity measured against an algorithm used for determining
    //to send an alert
    private enum AlertLevel { NONE, MODERATE, HIGH };
    AlertLevel alertLevel;

    //These strings correspond to alert level text
    final private String alertNone = "Ok";
    final private String alertModerate = "Caution";
    final private String alertHigh = "Alert";

    //Indicates if user is currently sending their position and timestamps to the server
    private boolean trackerEnabled;

    //These strings correspond to the tracker status
    final private String trackerOn = "Enabled";
    final private String trackerOff = "Disabled";

    //Indicates which type of map the user is to use
    private enum DisplayType { NONE, ZONE, PATH, BOTH };
    DisplayType displayType;

    //These strings correspond to map display type text
    final private String displayNone = "None";
    final private String displayZone = "Zone";
    final private String displayPath = "Path";
    final private String displayBoth = "Both";

    //Indicates whether the algorithm should be looser in the case the user is
    //allowed to automatically create paths or something like that
    private boolean automaticPathCreation;

    //These strings correspond to how paths can be generated/created
    final private String pathAutomatic = "Auto";
    final private String pathManual = "Manual";

    //TODO Variable/object that stores map building data goes here... maybe

    //Constructor that takes in a string array and initializes variables
    //but the string is expected to be formatted correctly and should
    //only be provided directly from a file read from off the server
    public UserStatus(String parseString) {
        String[] initialize = parseString.split("[ ]+");
        if (initialize.length == 4) {
            alertLevel = AlertLevel.valueOf(initialize[0]);
            trackerEnabled = Boolean.valueOf(initialize[1]);
            displayType = DisplayType.valueOf(initialize[2]);
            automaticPathCreation = Boolean.valueOf(initialize[3]);
        }
    }


    public AlertLevel getAlertLevel() {
        return alertLevel;
    }

    public String getAlertText() {
        String level = new String();
        switch (getAlertLevel()) {
            case NONE:
                level = alertNone;
                break;
            case MODERATE:
                level = alertModerate;
                break;
            case HIGH:
                level = alertHigh;
                break;
        }
        return level;
    }

    //note: this returns the color id and not the actual color
    //you will need getResources().getColor(COLOR_ID) to retrieve the color
    public int getAlertColor() {
        int color = 0;
        switch (getAlertLevel()) {
            case NONE:
                color = R.color.green;
                break;
            case MODERATE:
                color = R.color.yellow;
                break;
            case HIGH:
                color = R.color.red;
                break;
        }
        return color;
    }

    public boolean isTrackerEnabled() {
        return trackerEnabled;
    }

    public String getTrackerText() {
        return isTrackerEnabled() ? trackerOn : trackerOff;
    }

    //note: this returns the color id and not the actual color
    //you will need getResources().getColor(COLOR_ID) to retrieve the color
    public int getTrackerColor() {
        return isTrackerEnabled() ? R.color.green : R.color.yellow;
    }

    public void setTrackerEnabled(boolean trackerEnabled) {
        this.trackerEnabled = trackerEnabled;
    }


    public void setAlertLevel(AlertLevel alertLevel) {
        this.alertLevel = alertLevel;
    }

    public DisplayType getDisplayType() {
        return displayType;
    }

    public String getDisplayText() {
        String type = new String();
        switch (getDisplayType()) {
            case NONE:
                type = displayNone;
                break;
            case ZONE:
                type = displayZone;
                break;
            case PATH:
                type = displayPath;
                break;
            case BOTH:
                type = displayBoth;
                break;
        }
        return type;
    }


    public void setDisplayType(DisplayType displayType) {
        this.displayType = displayType;
    }

    public boolean isAutomaticPathCreation() {
        return automaticPathCreation;
    }

    public String getPathCreationText() {
        return isAutomaticPathCreation() ? pathAutomatic : pathManual;
    }

    public void setAutomaticPathCreation(boolean automaticPathCreation) {
        this.automaticPathCreation = automaticPathCreation;
    }

}
