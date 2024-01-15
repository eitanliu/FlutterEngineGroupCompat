package io.flutter.embedding.engine;

import android.content.Intent;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;

public class FlutterShellArgs {
    public FlutterShellArgs(@NonNull String[] args) {
    }

    public FlutterShellArgs(@NonNull List<String> args) {
    }

    public FlutterShellArgs(@NonNull Set<String> args) {
    }

    public static FlutterShellArgs fromIntent(@NonNull Intent intent) {
        return new FlutterShellArgs(new String[]{});
    }

    public String[] toArray() {
        return new String[]{};
    }
}
