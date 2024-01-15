package io.flutter.embedding.engine.loader;

import androidx.annotation.NonNull;

public class FlutterLoader {
    @NonNull
    public String findAppBundlePath() {
        return "flutterApplicationInfo.flutterAssetsDir";
    }
}
