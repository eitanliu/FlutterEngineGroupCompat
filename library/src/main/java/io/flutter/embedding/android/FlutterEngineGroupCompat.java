package io.flutter.embedding.android;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineGroup;
import io.flutter.embedding.engine.dart.DartExecutor;

public class FlutterEngineGroupCompat {

    FlutterEngineGroup group;

    public FlutterEngineGroupCompat(FlutterEngineGroup group) {
        this.group = group;
    }

    public FlutterEngine createAndRunEngine(Options options) {
        Context context = options.getContext();
        DartExecutor.DartEntrypoint dartEntrypoint = options.getDartEntrypoint();
        String initialRoute = options.getInitialRoute();
        List<String> dartEntrypointArgs = options.getDartEntrypointArgs();
        return group.createAndRunEngine(context, dartEntrypoint, initialRoute);
    }

    /**
     * Options that control how a FlutterEngine should be created.
     */
    public static class Options {
        @NonNull
        private Context context;
        @Nullable
        private DartExecutor.DartEntrypoint dartEntrypoint;
        @Nullable
        private String initialRoute;
        @Nullable
        private List<String> dartEntrypointArgs;

        public Options(@NonNull Context context) {
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        /**
         * dartEntrypoint specifies the {@link DartExecutor.DartEntrypoint} the new engine should run. It doesn't
         * need to be the same entrypoint as the current engine but must be built in the same AOT or
         * snapshot.
         */
        public DartExecutor.DartEntrypoint getDartEntrypoint() {
            return dartEntrypoint;
        }

        /**
         * The name of the initial Flutter `Navigator` `Route` to load. If this is null, it will default
         * to the "/" route.
         */
        public String getInitialRoute() {
            return initialRoute;
        }

        /**
         * Arguments passed as a list of string to Dart's entrypoint function.
         */
        public List<String> getDartEntrypointArgs() {
            return dartEntrypointArgs;
        }

        /**
         * Setter for `dartEntrypoint` property.
         *
         * @param dartEntrypoint specifies the {@link DartExecutor.DartEntrypoint} the new engine should run. It
         *                       doesn't need to be the same entrypoint as the current engine but must be built in the
         *                       same AOT or snapshot.
         */
        public Options setDartEntrypoint(DartExecutor.DartEntrypoint dartEntrypoint) {
            this.dartEntrypoint = dartEntrypoint;
            return this;
        }

        /**
         * Setter for `initialRoute` property.
         *
         * @param initialRoute The name of the initial Flutter `Navigator` `Route` to load. If this is
         *                     null, it will default to the "/" route.
         */
        public Options setInitialRoute(String initialRoute) {
            this.initialRoute = initialRoute;
            return this;
        }

        /**
         * Setter for `dartEntrypointArgs` property.
         *
         * @param dartEntrypointArgs Arguments passed as a list of string to Dart's entrypoint function.
         */
        public Options setDartEntrypointArgs(List<String> dartEntrypointArgs) {
            this.dartEntrypointArgs = dartEntrypointArgs;
            return this;
        }
    }
}
