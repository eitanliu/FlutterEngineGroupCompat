package io.flutter.embedding.engine;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.flutter.embedding.engine.dart.DartExecutor;
import io.flutter.plugin.platform.PlatformViewsController;

public class FlutterEngineGroup {
    public FlutterEngineGroup(@NonNull Context context) {
        this(context, null);
    }

    public FlutterEngineGroup(@NonNull Context context, @Nullable String[] dartVmArgs) {
    }

    public FlutterEngine createAndRunDefaultEngine(@NonNull Context context) {
        return createAndRunEngine(context, null);
    }

    public FlutterEngine createAndRunEngine(
            @NonNull Context context, @Nullable DartExecutor.DartEntrypoint dartEntrypoint) {
        return createAndRunEngine(context, dartEntrypoint, null);
    }

    public FlutterEngine createAndRunEngine(
            @NonNull Context context,
            @Nullable DartExecutor.DartEntrypoint dartEntrypoint,
            @Nullable String initialRoute) {
        return new FlutterEngine();
    }

    public FlutterEngine createAndRunEngine(@NonNull Options options) {
        return new FlutterEngine();
    }

    public static class Options {
        @NonNull
        private Context context;
        @Nullable
        private DartExecutor.DartEntrypoint dartEntrypoint;
        @Nullable
        private String initialRoute;
        @Nullable
        private List<String> dartEntrypointArgs;
        @NonNull
        private PlatformViewsController platformViewsController;
        private boolean automaticallyRegisterPlugins = true;
        private boolean waitForRestorationData = false;

        public Options(@NonNull Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public DartExecutor.DartEntrypoint getDartEntrypoint() {
            return dartEntrypoint;
        }

        public String getInitialRoute() {
            return initialRoute;
        }

        public List<String> getDartEntrypointArgs() {
            return dartEntrypointArgs;
        }

        public PlatformViewsController getPlatformViewsController() {
            return platformViewsController;
        }

        public boolean getAutomaticallyRegisterPlugins() {
            return automaticallyRegisterPlugins;
        }

        public boolean getWaitForRestorationData() {
            return waitForRestorationData;
        }

        public Options setDartEntrypoint(DartExecutor.DartEntrypoint dartEntrypoint) {
            this.dartEntrypoint = dartEntrypoint;
            return this;
        }

        public Options setInitialRoute(String initialRoute) {
            this.initialRoute = initialRoute;
            return this;
        }

        public Options setDartEntrypointArgs(List<String> dartEntrypointArgs) {
            this.dartEntrypointArgs = dartEntrypointArgs;
            return this;
        }

        public Options setPlatformViewsController(
                @NonNull PlatformViewsController platformViewsController) {
            this.platformViewsController = platformViewsController;
            return this;
        }

        public Options setAutomaticallyRegisterPlugins(boolean automaticallyRegisterPlugins) {
            this.automaticallyRegisterPlugins = automaticallyRegisterPlugins;
            return this;
        }

        public Options setWaitForRestorationData(boolean waitForRestorationData) {
            this.waitForRestorationData = waitForRestorationData;
            return this;
        }
    }
}
