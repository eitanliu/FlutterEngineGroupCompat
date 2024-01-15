package io.flutter;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.loader.FlutterLoader;

public class FlutterInjector {
    public static FlutterInjector instance() {
        return new FlutterInjector();
    }

    @NonNull
    public FlutterLoader flutterLoader() {
        return new FlutterLoader();
    }
}
