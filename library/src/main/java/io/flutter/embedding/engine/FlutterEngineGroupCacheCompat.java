package io.flutter.embedding.engine;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.dart.DartExecutor;

/**
 * Static singleton cache that holds {@link io.flutter.embedding.engine.FlutterEngineGroup}
 * instances identified by {@code String}s.
 *
 * <p>The ID of a given {@link io.flutter.embedding.engine.FlutterEngineGroup} can be whatever
 * {@code String} is desired.
 *
 * <p>{@link io.flutter.embedding.android.FlutterActivityCompat} and {@link
 * io.flutter.embedding.android.FlutterFragmentCompat} use the {@code FlutterEngineGroupCacheCompat} singleton
 * internally when instructed to use a cached {@link io.flutter.embedding.engine.FlutterEngineGroup}
 * based on a given ID. See {@link
 * io.flutter.embedding.android.FlutterActivityCompat.NewEngineInGroupIntentBuilder} and {@link
 * io.flutter.embedding.android.FlutterFragmentCompat.NewEngineInGroupFragmentBuilder} for related APIs.
 */
public class FlutterEngineGroupCacheCompat {
    private static volatile FlutterEngineGroupCacheCompat instance;
    private static volatile boolean firstReflectSdkCacheClass = true;
    private static Class<?> sdkCacheClass;

    /**
     * Returns the static singleton instance of {@code FlutterEngineGroupCache}.
     *
     * <p>Creates a new instance if one does not yet exist.
     */
    @NonNull
    public static FlutterEngineGroupCacheCompat getInstance() {
        if (instance == null) {
            synchronized (FlutterEngineGroupCacheCompat.class) {
                if (instance == null) {
                    instance = new FlutterEngineGroupCacheCompat();
                }
            }
        }
        return instance;
    }

    @Nullable
    public static Class<?> getSdkGroupCacheClass() {
        if (firstReflectSdkCacheClass) {
            synchronized (FlutterEngineGroupCacheCompat.class) {
                if (firstReflectSdkCacheClass) {
                    try {
                        sdkCacheClass = Class.forName("io.flutter.embedding.engine.FlutterEngineGroupCache");
                        firstReflectSdkCacheClass = false;
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
        return sdkCacheClass;
    }

    private final Map<String, FlutterEngineGroup> cachedEngineGroups = new HashMap<>();

    @VisibleForTesting
        /* package */ FlutterEngineGroupCacheCompat() {
    }

    /**
     * Returns {@code true} if a {@link io.flutter.embedding.engine.FlutterEngineGroup} in this cache
     * is associated with the given {@code engineGroupId}.
     */
    public boolean contains(@NonNull String engineGroupId) {
        return cachedEngineGroups.containsKey(engineGroupId);
    }

    /**
     * Returns the {@link io.flutter.embedding.engine.FlutterEngineGroup} in this cache that is
     * associated with the given {@code engineGroupId}, or {@code null} is no such {@link
     * io.flutter.embedding.engine.FlutterEngineGroup} exists.
     */
    @Nullable
    public FlutterEngineGroup get(@NonNull String engineGroupId) {
        return cachedEngineGroups.get(engineGroupId);
    }

    /**
     * Places the given {@link io.flutter.embedding.engine.FlutterEngineGroup} in this cache and
     * associates it with the given {@code engineGroupId}.
     *
     * <p>If a {@link io.flutter.embedding.engine.FlutterEngineGroup} is null, that {@link
     * io.flutter.embedding.engine.FlutterEngineGroup} is removed from this cache.
     */
    public void put(@NonNull String engineGroupId, @Nullable FlutterEngineGroup engineGroup) {
        if (engineGroup != null) {
            cachedEngineGroups.put(engineGroupId, engineGroup);
        } else {
            cachedEngineGroups.remove(engineGroupId);
        }
    }

    /**
     * Removes any {@link io.flutter.embedding.engine.FlutterEngineGroup} that is currently in the
     * cache that is identified by the given {@code engineGroupId}.
     */
    public void remove(@NonNull String engineGroupId) {
        put(engineGroupId, null);
    }

    /**
     * Removes all {@link io.flutter.embedding.engine.FlutterEngineGroup}'s that are currently in the
     * cache.
     */
    public void clear() {
        cachedEngineGroups.clear();
    }

    public static class Global {
        // global group, init isolate
        public final static String GROUP_NAME = "global";
        static final String DEFAULT_INITIAL_ROUTE = "_init";


        private static volatile Global instance;

        private final FlutterEngineGroup group;

        /**
         * Returns the static singleton instance of {@code FlutterEngineGroupCache}.
         *
         * <p>Creates a new instance if one does not yet exist.
         */
        @NonNull
        public static Global getInstance(Context context) {
            if (instance == null) {
                synchronized (Global.class) {
                    if (instance == null) {
                        instance = new Global(context);
                    }
                }
            }
            return instance;
        }

        public Global(Context context) {
            Context application = context.getApplicationContext();
            group = new FlutterEngineGroup(application);
            group.createAndRunEngine(application, DartExecutor.DartEntrypoint.createDefault(), DEFAULT_INITIAL_ROUTE);
            FlutterEngineGroupCacheCompat.getInstance().put(GROUP_NAME, group);
            pubFlutterEngineGroup();
        }

        void pubFlutterEngineGroup() {
            try {
                Class<?> clazz = getSdkGroupCacheClass();
                Method instanceMethod = clazz.getMethod("getInstance");
                Object instance = instanceMethod.invoke(null);
                Method putMethod = instance.getClass().getMethod("put", String.class, FlutterEngineGroup.class);
                putMethod.invoke(instance, GROUP_NAME, group);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public FlutterEngineGroup getGroup() {
            return group;
        }
    }
}
