package io.flutter.embedding.utils;

import java.lang.reflect.Method;

public class InvokeUtils {
    public static void tryCall(Runnable runnable) {
        try {
            runnable.run();
        } catch (Throwable ignored) {
        }
    }

    public static boolean hasMethod(Method[] methods, String name) {
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
