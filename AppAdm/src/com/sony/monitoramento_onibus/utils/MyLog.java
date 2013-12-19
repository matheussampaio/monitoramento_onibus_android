package com.sony.monitoramento_onibus.utils;

import android.util.Log;

public class MyLog {
    private static final String ERROR = "ERROR";
    private static final String WARNING = "WARNING";
    private static final String INFO = "INFO";
    private static final String DEBUG = "DEBUG";

    /*
     * None: 0; ERROR: 1; WARN: 2; INFO: 3; DEBUG: 4;
     */
    private static final int LEVEL = 4;

    public static void Error(String msg) {
        if (LEVEL >= 1) {
            Log.d(ERROR, msg);
        }
    }

    public static void Warning(String msg) {
        if (LEVEL >= 2) {
            Log.d(WARNING, msg);
        }
    }

    public static void Info(String msg) {
        if (LEVEL >= 3) {
            Log.d(INFO, msg);
        }
    }

    public static void Debug(String msg) {
        if (LEVEL >= 4) {
            Log.d(DEBUG, msg);
        }
    }
}
