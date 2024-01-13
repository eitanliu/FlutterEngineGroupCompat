package io.flutter.embedding.utils;

public class InvokeUtils {
    public static void tryCall(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {}
    }
}
