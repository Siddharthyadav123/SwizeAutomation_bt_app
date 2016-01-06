package com.svizeautomation.app.util;


import android.app.Application;

import com.svizeautomation.app.application.BluetoothViewerFullApplication;

public class ApplicationUtils {
    private ApplicationUtils() {
        // utility class, forbidden constructor
    }

    public static boolean isLiteVersion(Application application) {
        return ((BluetoothViewerFullApplication) application).isLiteVersion();
    }
}
