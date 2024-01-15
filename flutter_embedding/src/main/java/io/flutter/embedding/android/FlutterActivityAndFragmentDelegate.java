package io.flutter.embedding.android;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import java.util.List;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineGroup;
import io.flutter.embedding.engine.FlutterShellArgs;
import io.flutter.plugin.platform.PlatformPlugin;

public class FlutterActivityAndFragmentDelegate implements ExclusiveAppComponent<Activity> {

    FlutterActivityAndFragmentDelegate(@NonNull Host host) {
        this(host, null);
    }

    FlutterActivityAndFragmentDelegate(@NonNull Host host, @Nullable FlutterEngineGroup engineGroup) {
    }

    void setupFlutterEngine() {
    }

    void setUpFlutterEngine() {
    }

    void release() {
    }

    public boolean isAttached() {
        return true;
    }

    void onAttach(@NonNull Context context) {
    }

    void onDetach() {
    }

    @Override
    public void detachFromFlutterEngine() {

    }

    FlutterEngine getFlutterEngine() {
        return null;
    }

    @NonNull
    @Override
    public Activity getAppComponent() {
        return null;
    }

    interface Host
            extends FlutterEngineProvider,
            FlutterEngineConfigurator,
            PlatformPlugin.PlatformPluginDelegate {
        @NonNull
        Context getContext();

        @Nullable
        boolean shouldHandleDeeplinking();

        @Nullable
        Activity getActivity();

        @NonNull
        Lifecycle getLifecycle();

        @NonNull
        FlutterShellArgs getFlutterShellArgs();

        @Nullable
        String getCachedEngineId();

        @Nullable
        String getCachedEngineGroupId();

        boolean shouldDestroyEngineWithHost();

        void detachFromFlutterEngine();

        @NonNull
        String getDartEntrypointFunctionName();

        @Nullable
        String getDartEntrypointLibraryUri();

        @Nullable
        List<String> getDartEntrypointArgs();

        @NonNull
        String getAppBundlePath();

        @Nullable
        String getInitialRoute();

        @NonNull
        RenderMode getRenderMode();

        @NonNull
        TransparencyMode getTransparencyMode();


        ExclusiveAppComponent<Activity> getExclusiveAppComponent();

        @Nullable
        FlutterEngine provideFlutterEngine(@NonNull Context context);

        @Nullable
        PlatformPlugin providePlatformPlugin(
                @Nullable Activity activity, @NonNull FlutterEngine flutterEngine);

        void configureFlutterEngine(@NonNull FlutterEngine flutterEngine);

        void cleanUpFlutterEngine(@NonNull FlutterEngine flutterEngine);

        boolean shouldAttachEngineToActivity();

        void onFlutterTextureViewCreated(@NonNull FlutterTextureView flutterTextureView);

        void onFlutterUiDisplayed();

        void onFlutterUiNoLongerDisplayed();

        boolean shouldRestoreAndSaveState();

        void updateSystemUiOverlays();

        boolean shouldDispatchAppLifecycleState();

        boolean attachToEngineAutomatically();
    }
}
