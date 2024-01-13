package io.flutter.embedding.android;

import static io.flutter.embedding.android.FlutterActivityLaunchConfigsCompat.EXTRA_CACHED_ENGINE_GROUP_ID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngineGroupCacheCompat;
import io.flutter.embedding.engine.FlutterShellArgs;

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

            return builder.build();
        }
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
}
