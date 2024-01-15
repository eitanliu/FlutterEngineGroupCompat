package io.flutter.embedding.engine.dart;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DartExecutor {


    public static class DartEntrypoint {
        @NonNull
        public static DartEntrypoint createDefault() {
            return new DartEntrypoint("flutterLoader.findAppBundlePath()", "main");
        }

        /**
         * The path within the AssetManager where the app will look for assets.
         */
        @NonNull
        public final String pathToBundle;

        /**
         * The library or file location that contains the Dart entrypoint function.
         */
        @Nullable
        public final String dartEntrypointLibrary;

        /**
         * The name of a Dart function to execute.
         */
        @NonNull
        public final String dartEntrypointFunctionName;

        public DartEntrypoint(
                @NonNull String pathToBundle, @NonNull String dartEntrypointFunctionName) {
            this.pathToBundle = pathToBundle;
            dartEntrypointLibrary = null;
            this.dartEntrypointFunctionName = dartEntrypointFunctionName;
        }

        public DartEntrypoint(
                @NonNull String pathToBundle,
                @NonNull String dartEntrypointLibrary,
                @NonNull String dartEntrypointFunctionName) {
            this.pathToBundle = pathToBundle;
            this.dartEntrypointLibrary = dartEntrypointLibrary;
            this.dartEntrypointFunctionName = dartEntrypointFunctionName;
        }

        @Override
        @NonNull
        public String toString() {
            return "DartEntrypoint( bundle path: "
                    + pathToBundle
                    + ", function: "
                    + dartEntrypointFunctionName
                    + " )";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            DartEntrypoint that = (DartEntrypoint) o;

            if (!pathToBundle.equals(that.pathToBundle)) return false;
            return dartEntrypointFunctionName.equals(that.dartEntrypointFunctionName);
        }

        @Override
        public int hashCode() {
            int result = pathToBundle.hashCode();
            result = 31 * result + dartEntrypointFunctionName.hashCode();
            return result;
        }
    }
}
