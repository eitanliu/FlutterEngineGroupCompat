package io.flutter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Log {
    private static int logLevel = android.util.Log.DEBUG;

    public static int ASSERT = android.util.Log.ASSERT;
    public static int DEBUG = android.util.Log.DEBUG;
    public static int ERROR = android.util.Log.ERROR;
    public static int INFO = android.util.Log.INFO;
    public static int VERBOSE = android.util.Log.VERBOSE;
    public static int WARN = android.util.Log.WARN;

    /**
     * Sets a log cutoff such that a log level of lower priority than {@code logLevel} is filtered
     * out.
     *
     * <p>See {@link android.util.Log} for log level constants.
     */
    public static void setLogLevel(int logLevel) {
        Log.logLevel = logLevel;
    }

    public static void println(@NonNull int level, @NonNull String tag, @NonNull String message) {
    }

    public static void v(@NonNull String tag, @NonNull String message) {

    }

    public static void v(@NonNull String tag, @NonNull String message, @NonNull Throwable tr) {
    }

    public static void i(@NonNull String tag, @NonNull String message) {
    }

    public static void i(@NonNull String tag, @NonNull String message, @NonNull Throwable tr) {
    }

    public static void d(@NonNull String tag, @NonNull String message) {
    }

    public static void d(@NonNull String tag, @NonNull String message, @NonNull Throwable tr) {
    }

    public static void w(@NonNull String tag, @NonNull String message) {
    }

    public static void w(@NonNull String tag, @NonNull String message, @NonNull Throwable tr) {
    }

    public static void e(@NonNull String tag, @NonNull String message) {
    }

    public static void e(@NonNull String tag, @NonNull String message, @NonNull Throwable tr) {
    }

    public static void wtf(@NonNull String tag, @NonNull String message) {
    }

    public static void wtf(@NonNull String tag, @NonNull String message, @NonNull Throwable tr) {
    }

    @NonNull
    public static String getStackTraceString(@Nullable Throwable tr) {
        return "";
    }
}
