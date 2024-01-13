package io.flutter.embedding.android;

import static io.flutter.embedding.android.FlutterActivityLaunchConfigs.DEFAULT_INITIAL_ROUTE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;

import io.flutter.FlutterInjector;
import io.flutter.Log;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.embedding.engine.FlutterEngineGroup;
import io.flutter.embedding.engine.FlutterEngineGroupCacheCompat;
import io.flutter.embedding.engine.dart.DartExecutor;

class FlutterActivityAndFragmentDelegateCompat extends FlutterActivityAndFragmentDelegate {
    private static final String TAG = "FlutterActivityAndFragmentDelegateCompat";
    private Host host;
    @Nullable
    private FlutterEngineGroup engineGroup;
    private boolean isAttached;

    FlutterActivityAndFragmentDelegateCompat(@NonNull Host host) {
        super(host);
        this.host = host;
    }

    FlutterActivityAndFragmentDelegateCompat(@NonNull Host host, @Nullable FlutterEngineGroup engineGroup) {
        this(host);
        this.engineGroup = engineGroup;
    }

    @SuppressLint("VisibleForTests")
    @Override
    void setupFlutterEngine() {
        if (FlutterEngineGroupCacheCompat.Global.GROUP_NAME.equals(host.getCachedEngineGroupId())) {
            FlutterEngineGroupCacheCompat.Global.getInstance(host.getContext());
        }

        if (FlutterEngineGroupCacheCompat.getSdkGroupCacheClass() != null) {
            super.setupFlutterEngine();
        } else {
            try {
                setupFlutterEngineCompat();
            } catch (Throwable e) {
                e.printStackTrace();
                super.setupFlutterEngine();
            }
        }
    }

    void setupFlutterEngineCompat() throws NoSuchFieldException, IllegalAccessException {
        Log.v(TAG, "Setting up FlutterEngine.");

        // First, check if the host wants to use a cached FlutterEngine.
        String cachedEngineId = host.getCachedEngineId();
        if (cachedEngineId != null) {
            setFlutterEngine(FlutterEngineCache.getInstance().get(cachedEngineId));
            isFlutterEngineFromHost(true);
            if (getFlutterEngine() == null) {
                throw new IllegalStateException(
                        "The requested cached FlutterEngine did not exist in the FlutterEngineCache: '"
                                + cachedEngineId
                                + "'");
            }
            return;
        }

        // Second, defer to subclasses for a custom FlutterEngine.
        setFlutterEngine(host.provideFlutterEngine(host.getContext()));
        if (getFlutterEngine() != null) {
            isFlutterEngineFromHost(true);
            return;
        }

        // Third, check if the host wants to use a cached FlutterEngineGroup
        // and create new FlutterEngine using FlutterEngineGroup#createAndRunEngine
        String cachedEngineGroupId = host.getCachedEngineGroupId();
        if (cachedEngineGroupId != null) {
            FlutterEngineGroup flutterEngineGroup =
                    FlutterEngineGroupCacheCompat.getInstance().get(cachedEngineGroupId);
            if (flutterEngineGroup == null) {
                throw new IllegalStateException(
                        "The requested cached FlutterEngineGroup did not exist in the FlutterEngineGroupCache: '"
                                + cachedEngineGroupId
                                + "'");
            }


            FlutterEngine flutterEngine = new FlutterEngineGroupCompat(flutterEngineGroup).createAndRunEngine(
                    addEntrypointOptionsCompat(new FlutterEngineGroupCompat.Options(host.getContext()))
            );
            setFlutterEngine(flutterEngine);
            isFlutterEngineFromHost(false);
            return;
        }

        // Our host did not provide a custom FlutterEngine. Create a FlutterEngine to back our
        // FlutterView.
        Log.v(
                TAG,
                "No preferred FlutterEngine was provided. Creating a new FlutterEngine for"
                        + " this FlutterFragment.");

        FlutterEngineGroup group =
                engineGroup == null
                        ? new FlutterEngineGroup(host.getContext(), host.getFlutterShellArgs().toArray())
                        : engineGroup;
        FlutterEngine flutterEngine = new FlutterEngineGroupCompat(group).createAndRunEngine(
                addEntrypointOptionsCompat(new FlutterEngineGroupCompat.Options(host.getContext()))
        );
        setFlutterEngine(flutterEngine);
        isFlutterEngineFromHost(false);
    }

    private void setFlutterEngine(FlutterEngine flutterEngine) throws IllegalAccessException, NoSuchFieldException {
        Class<?> parentClass = getClass().getSuperclass();
        //noinspection DataFlowIssue
        Field field = parentClass.getDeclaredField("flutterEngine");
        field.setAccessible(true);
        field.set(this, flutterEngine);
    }

    private void isFlutterEngineFromHost(boolean status) throws IllegalAccessException, NoSuchFieldException {
        Class<?> parentClass = getClass().getSuperclass();
        //noinspection DataFlowIssue
        Field field = parentClass.getDeclaredField("isFlutterEngineFromHost");
        field.setAccessible(true);
        field.set(this, status);
    }

    private FlutterEngineGroupCompat.Options addEntrypointOptionsCompat(FlutterEngineGroupCompat.Options options) {
        String appBundlePathOverride = host.getAppBundlePath();
        //noinspection ConstantValue
        if (appBundlePathOverride == null || appBundlePathOverride.isEmpty()) {
            appBundlePathOverride = FlutterInjector.instance().flutterLoader().findAppBundlePath();
        }

        DartExecutor.DartEntrypoint dartEntrypoint =
                new DartExecutor.DartEntrypoint(
                        appBundlePathOverride, host.getDartEntrypointFunctionName());
        String initialRoute = host.getInitialRoute();
        if (initialRoute == null) {
            //noinspection DataFlowIssue
            initialRoute = maybeGetInitialRouteFromIntentCompat(host.getActivity().getIntent());
            if (initialRoute == null) {
                initialRoute = DEFAULT_INITIAL_ROUTE;
            }
        }
        return options
                .setDartEntrypoint(dartEntrypoint)
                .setInitialRoute(initialRoute)
                ;
    }

    private String maybeGetInitialRouteFromIntentCompat(Intent intent) {
        if (host.shouldHandleDeeplinking()) {
            Uri data = intent.getData();
            if (data != null) {
                String fullRoute = data.getPath();
                if (fullRoute != null && !fullRoute.isEmpty()) {
                    if (data.getQuery() != null && !data.getQuery().isEmpty()) {
                        fullRoute += "?" + data.getQuery();
                    }
                    if (data.getFragment() != null && !data.getFragment().isEmpty()) {
                        fullRoute += "#" + data.getFragment();
                    }
                    return fullRoute;
                }
            }
        }
        return null;
    }

    @Override
    void release() {
        super.release();
        host = null;
    }

    public boolean isAttached() {
        return isAttached;
    }

    @Override
    void onAttach(@NonNull Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    void onDetach() {
        super.onDetach();
        isAttached = false;
    }

    interface Host extends FlutterActivityAndFragmentDelegate.Host, HostCompat {
    }

    interface HostCompat {

        // since 3.10.6
        @Nullable
        String getCachedEngineGroupId();

    }
}
