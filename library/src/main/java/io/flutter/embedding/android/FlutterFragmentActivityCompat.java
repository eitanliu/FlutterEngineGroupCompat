package io.flutter.embedding.android;

import static io.flutter.embedding.android.FlutterActivityLaunchConfigsCompat.DART_ENTRYPOINT_URI_META_DATA_KEY;
import static io.flutter.embedding.android.FlutterActivityLaunchConfigsCompat.EXTRA_CACHED_ENGINE_GROUP_ID;
import static io.flutter.embedding.android.FlutterActivityLaunchConfigsCompat.EXTRA_DART_ENTRYPOINT_ARGS;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngineGroupCacheCompat;
import io.flutter.embedding.engine.FlutterShellArgs;
import io.flutter.embedding.utils.InvokeUtils;

public class FlutterFragmentActivityCompat extends FlutterFragmentActivity
        implements FlutterActivityAndFragmentDelegateCompat.HostCompat {
    private static final String TAG = "FlutterFragmentActivityCompat";
    private FlutterFragment flutterFragment;

    @Override
    FlutterFragment retrieveExistingFlutterFragmentIfPossible() {
        return flutterFragment = super.retrieveExistingFlutterFragmentIfPossible();
    }

    @NonNull
    @Override
    protected FlutterFragment createFlutterFragment() {
        return flutterFragment = createFlutterFragmentCompat();
    }

    FlutterFragmentCompat createFlutterFragmentCompat() {
        final FlutterActivityLaunchConfigs.BackgroundMode backgroundMode = getBackgroundMode();
        final RenderMode renderMode = getRenderMode();
        final TransparencyMode transparencyMode =
                backgroundMode == FlutterActivityLaunchConfigs.BackgroundMode.opaque
                        ? TransparencyMode.opaque
                        : TransparencyMode.transparent;
        final boolean shouldDelayFirstAndroidViewDraw = renderMode == RenderMode.surface;

        if (getCachedEngineId() != null) {
            Log.v(
                    TAG,
                    "Creating FlutterFragment with cached engine:\n"
                            + "Cached engine ID: "
                            + getCachedEngineId()
                            + "\n"
                            + "Will destroy engine when Activity is destroyed: "
                            + shouldDestroyEngineWithHost()
                            + "\n"
                            + "Background transparency mode: "
                            + backgroundMode
                            + "\n"
                            + "Will attach FlutterEngine to Activity: "
                            + shouldAttachEngineToActivity());

            return new FlutterFragment.CachedEngineFragmentBuilder(FlutterFragmentCompat.class, getCachedEngineId())
                    .renderMode(renderMode)
                    .transparencyMode(transparencyMode)
                    .handleDeeplinking(shouldHandleDeeplinking())
                    .shouldAttachEngineToActivity(shouldAttachEngineToActivity())
                    .destroyEngineWithFragment(shouldDestroyEngineWithHost())
                    .shouldDelayFirstAndroidViewDraw(shouldDelayFirstAndroidViewDraw)
                    .build();
        } else {
            Log.v(
                    TAG,
                    "Creating FlutterFragment with new engine:\n"
                            + "Cached engine group ID: "
                            + getCachedEngineGroupId()
                            + "\n"
                            + "Background transparency mode: "
                            + backgroundMode
                            + "\n"
                            + "Dart entrypoint: "
                            + getDartEntrypointFunctionName()
                            + "\n"
                            + "Dart entrypoint library uri: "
                            + (getDartEntrypointLibraryUri() != null ? getDartEntrypointLibraryUri() : "\"\"")
                            + "\n"
                            + "Initial route: "
                            + getInitialRoute()
                            + "\n"
                            + "App bundle path: "
                            + getAppBundlePath()
                            + "\n"
                            + "Will attach FlutterEngine to Activity: "
                            + shouldAttachEngineToActivity());

            if (getCachedEngineGroupId() != null) {
                return new FlutterFragmentCompat.NewEngineInGroupFragmentBuilder(getCachedEngineGroupId())
                        .dartEntrypoint(getDartEntrypointFunctionName())
                        .initialRoute(getInitialRoute())
                        .handleDeeplinking(shouldHandleDeeplinking())
                        .renderMode(renderMode)
                        .transparencyMode(transparencyMode)
                        .shouldAttachEngineToActivity(shouldAttachEngineToActivity())
                        .shouldDelayFirstAndroidViewDraw(shouldDelayFirstAndroidViewDraw)
                        .build();
            }

            FlutterFragment.NewEngineFragmentBuilder builder = new FlutterFragment.NewEngineFragmentBuilder(FlutterFragmentCompat.class)
                    .dartEntrypoint(getDartEntrypointFunctionName())
                    .initialRoute(getInitialRoute())
                    .appBundlePath(getAppBundlePath())
                    .flutterShellArgs(FlutterShellArgs.fromIntent(getIntent()))
                    .handleDeeplinking(shouldHandleDeeplinking())
                    .renderMode(renderMode)
                    .transparencyMode(transparencyMode)
                    .shouldAttachEngineToActivity(shouldAttachEngineToActivity())
                    .shouldDelayFirstAndroidViewDraw(shouldDelayFirstAndroidViewDraw);
            InvokeUtils.tryCall(() -> builder.dartLibraryUri(getDartEntrypointLibraryUri()));
            InvokeUtils.tryCall(() -> builder.dartEntrypointArgs(getDartEntrypointArgs()));

            return builder.build();
        }
    }

    @Override
    public ExclusiveAppComponent<Activity> getExclusiveAppComponent() {
        return ((FlutterFragmentCompat) flutterFragment).getExclusiveAppComponent();
    }

    @Nullable
    @Override
    public String getCachedEngineGroupId() {
        String groupId = getIntent().getStringExtra(EXTRA_CACHED_ENGINE_GROUP_ID);
        if (groupId == null || groupId.isEmpty()) {
            groupId = FlutterEngineGroupCacheCompat.Global.GROUP_NAME;
        }
        return groupId;
    }

    /**
     * The Dart entrypoint arguments will be passed as a list of string to Dart's entrypoint function.
     *
     * <p>A value of null means do not pass any arguments to Dart's entrypoint function.
     *
     * <p>Subclasses may override this method to directly control the Dart entrypoint arguments.
     */
    @Nullable
    public List<String> getDartEntrypointArgs() {
        //noinspection unchecked
        return (List<String>) getIntent().getSerializableExtra(EXTRA_DART_ENTRYPOINT_ARGS);
    }

    /**
     * The Dart library URI for the entrypoint that will be executed as soon as the Dart snapshot is
     * loaded.
     *
     * <p>Example value: "package:foo/bar.dart"
     *
     * <p>This preference can be controlled by setting a {@code <meta-data>} called {@link
     * FlutterActivityLaunchConfigsCompat#DART_ENTRYPOINT_URI_META_DATA_KEY} within the Android manifest
     * definition for this {@code FlutterFragmentActivity}.
     *
     * <p>A value of null means use the default root library.
     *
     * <p>Subclasses may override this method to directly control the Dart entrypoint uri.
     */
    @Nullable
    public String getDartEntrypointLibraryUri() {
        try {
            Bundle metaData = getMetaData();
            return metaData != null ? metaData.getString(DART_ENTRYPOINT_URI_META_DATA_KEY) : null;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @Override
    public boolean shouldDispatchAppLifecycleState() {
        return true;
    }
}
