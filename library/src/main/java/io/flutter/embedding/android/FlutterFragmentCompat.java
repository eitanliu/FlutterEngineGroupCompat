package io.flutter.embedding.android;

import static io.flutter.embedding.android.FlutterActivityLaunchConfigsCompat.EXTRA_CACHED_ENGINE_GROUP_ID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Field;
import java.util.List;

import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineGroupCacheCompat;

public class FlutterFragmentCompat extends FlutterFragment
        implements FlutterActivityAndFragmentDelegateCompat.Host {
    protected static final String ARG_CACHED_ENGINE_GROUP_ID = "cached_engine_group_id";
    /**
     * The Dart entrypoint method's URI that is executed upon initialization.
     */
    protected static final String ARG_DART_ENTRYPOINT_URI = "dart_entrypoint_uri";
    /**
     * The Dart entrypoint arguments that is executed upon initialization.
     */
    protected static final String ARG_DART_ENTRYPOINT_ARGS = "dart_entrypoint_args";

    /**
     * Returns a {@link NewEngineInGroupFragmentBuilder} to create a {@code FlutterFragmentCompat} with a
     * cached {@link io.flutter.embedding.engine.FlutterEngineGroup} in {@link
     * io.flutter.embedding.engine.FlutterEngineGroupCacheCompat}.
     *
     * <p>An {@code IllegalStateException} will be thrown during the lifecycle of the {@code
     * FlutterFragmentCompat} if a cached {@link io.flutter.embedding.engine.FlutterEngineGroup} is
     * requested but does not exist in the {@link
     * io.flutter.embedding.engine.FlutterEngineGroupCacheCompat}.
     */
    @NonNull
    public static NewEngineInGroupFragmentBuilder withNewEngineInGroupCompat(
            @NonNull String engineGroupId) {
        return new NewEngineInGroupFragmentBuilder(engineGroupId);
    }

    /**
     * Builder that creates a new {@code FlutterFragmentCompat} that uses a cached {@link
     * io.flutter.embedding.engine.FlutterEngineGroup} to create a new {@link
     * io.flutter.embedding.engine.FlutterEngine} with {@code arguments} that correspond to the values
     * set on this {@code Builder}.
     *
     * <p>Subclasses of {@code FlutterFragmentCompat} that do not introduce any new arguments can use this
     * {@code Builder} to construct instances of the subclass without subclassing this {@code
     * Builder}. {@code MyFlutterFragmentCompat f = new
     * FlutterFragmentCompat.NewEngineInGroupFragmentBuilder(MyFlutterFragmentCompat.class, engineGroupId)
     * .someProperty(...) .someOtherProperty(...) .build<MyFlutterFragmentCompat>(); }
     *
     * <p>Subclasses of {@code FlutterFragmentCompat} that introduce new arguments should subclass this
     * {@code NewEngineInGroupFragmentBuilder} to add the new properties:
     *
     * <ol>
     *   <li>Ensure the {@code FlutterFragmentCompat} subclass has a no-arg constructor.
     *   <li>Subclass this {@code NewEngineInGroupFragmentBuilder}.
     *   <li>Override the new {@code NewEngineInGroupFragmentBuilder}'s no-arg constructor and invoke
     *       the super constructor to set the {@code FlutterFragmentCompat} subclass: {@code public
     *       MyBuilder() { super(MyFlutterFragmentCompat.class); } }
     *   <li>Add appropriate property methods for the new properties.
     *   <li>Override {@link NewEngineInGroupFragmentBuilder#createArgs()}, call through to the super
     *       method, then add the new properties as arguments in the {@link Bundle}.
     * </ol>
     * <p>
     * Once a {@code NewEngineInGroupFragmentBuilder} subclass is defined, the {@code FlutterFragmentCompat}
     * subclass can be instantiated as follows. {@code MyFlutterFragmentCompat f = new MyBuilder()
     * .someExistingProperty(...) .someNewProperty(...) .build<MyFlutterFragmentCompat>(); }
     */
    public static class NewEngineInGroupFragmentBuilder {
        private final Class<? extends FlutterFragmentCompat> fragmentClass;
        private final String cachedEngineGroupId;
        private @NonNull String dartEntrypoint = "main";
        private @NonNull String initialRoute = "/";
        private @NonNull boolean handleDeeplinking = false;
        private @NonNull RenderMode renderMode = RenderMode.surface;
        private @NonNull TransparencyMode transparencyMode = TransparencyMode.transparent;
        private boolean shouldAttachEngineToActivity = true;
        private boolean shouldAutomaticallyHandleOnBackPressed = false;
        private boolean shouldDelayFirstAndroidViewDraw = false;

        public NewEngineInGroupFragmentBuilder(@NonNull String engineGroupId) {
            this(FlutterFragmentCompat.class, engineGroupId);
        }

        public NewEngineInGroupFragmentBuilder(
                @NonNull Class<? extends FlutterFragmentCompat> fragmentClass, @NonNull String engineGroupId) {
            this.fragmentClass = fragmentClass;
            this.cachedEngineGroupId = engineGroupId;
        }

        /**
         * The name of the initial Dart method to invoke, defaults to "main".
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder dartEntrypoint(@NonNull String dartEntrypoint) {
            this.dartEntrypoint = dartEntrypoint;
            return this;
        }

        /**
         * The initial route that a Flutter app will render in this {@link FlutterFragmentCompat}, defaults to
         * "/".
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder initialRoute(@NonNull String initialRoute) {
            this.initialRoute = initialRoute;
            return this;
        }

        /**
         * Whether to handle the deeplinking from the {@code Intent} automatically if the {@code
         * getInitialRoute} returns null.
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder handleDeeplinking(@NonNull boolean handleDeeplinking) {
            this.handleDeeplinking = handleDeeplinking;
            return this;
        }

        /**
         * Render Flutter either as a {@link RenderMode#surface} or a {@link RenderMode#texture}. You
         * should use {@code surface} unless you have a specific reason to use {@code texture}. {@code
         * texture} comes with a significant performance impact, but {@code texture} can be displayed
         * beneath other Android {@code View}s and animated, whereas {@code surface} cannot.
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder renderMode(@NonNull RenderMode renderMode) {
            this.renderMode = renderMode;
            return this;
        }

        /**
         * Support a {@link TransparencyMode#transparent} background within {@link
         * io.flutter.embedding.android.FlutterView}, or force an {@link TransparencyMode#opaque}
         * background.
         *
         * <p>See {@link TransparencyMode} for implications of this selection.
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder transparencyMode(
                @NonNull TransparencyMode transparencyMode) {
            this.transparencyMode = transparencyMode;
            return this;
        }

        /**
         * Whether or not this {@code FlutterFragmentCompat} should automatically attach its {@code Activity}
         * as a control surface for its {@link io.flutter.embedding.engine.FlutterEngine}.
         *
         * <p>Control surfaces are used to provide Android resources and lifecycle events to plugins
         * that are attached to the {@link io.flutter.embedding.engine.FlutterEngine}. If {@code
         * shouldAttachEngineToActivity} is true then this {@code FlutterFragmentCompat} will connect its
         * {@link io.flutter.embedding.engine.FlutterEngine} to the surrounding {@code Activity}, along
         * with any plugins that are registered with that {@link FlutterEngine}. This allows plugins to
         * access the {@code Activity}, as well as receive {@code Activity}-specific calls, e.g., {@link
         * android.app.Activity#onNewIntent(Intent)}. If {@code shouldAttachEngineToActivity} is false,
         * then this {@code FlutterFragmentCompat} will not automatically manage the connection between its
         * {@link io.flutter.embedding.engine.FlutterEngine} and the surrounding {@code Activity}. The
         * {@code Activity} will need to be manually connected to this {@code FlutterFragmentCompat}'s {@link
         * io.flutter.embedding.engine.FlutterEngine} by the app developer. See {@link
         * FlutterEngine#getActivityControlSurface()}.
         *
         * <p>One reason that a developer might choose to manually manage the relationship between the
         * {@code Activity} and {@link io.flutter.embedding.engine.FlutterEngine} is if the developer
         * wants to move the {@link FlutterEngine} somewhere else. For example, a developer might want
         * the {@link io.flutter.embedding.engine.FlutterEngine} to outlive the surrounding {@code
         * Activity} so that it can be used later in a different {@code Activity}. To accomplish this,
         * the {@link io.flutter.embedding.engine.FlutterEngine} will need to be disconnected from the
         * surrounding {@code Activity} at an unusual time, preventing this {@code FlutterFragmentCompat} from
         * correctly managing the relationship between the {@link
         * io.flutter.embedding.engine.FlutterEngine} and the surrounding {@code Activity}.
         *
         * <p>Another reason that a developer might choose to manually manage the relationship between
         * the {@code Activity} and {@link io.flutter.embedding.engine.FlutterEngine} is if the
         * developer wants to prevent, or explicitly control when the {@link
         * io.flutter.embedding.engine.FlutterEngine}'s plugins have access to the surrounding {@code
         * Activity}. For example, imagine that this {@code FlutterFragmentCompat} only takes up part of the
         * screen and the app developer wants to ensure that none of the Flutter plugins are able to
         * manipulate the surrounding {@code Activity}. In this case, the developer would not want the
         * {@link io.flutter.embedding.engine.FlutterEngine} to have access to the {@code Activity},
         * which can be accomplished by setting {@code shouldAttachEngineToActivity} to {@code false}.
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder shouldAttachEngineToActivity(
                boolean shouldAttachEngineToActivity) {
            this.shouldAttachEngineToActivity = shouldAttachEngineToActivity;
            return this;
        }

        /**
         * Whether or not this {@code FlutterFragmentCompat} should automatically receive {@link
         * #onBackPressed()} events, rather than requiring an explicit activity call through. Disabled
         * by default.
         *
         * <p>When enabled, the activity will automatically dispatch back-press events to the fragment's
         * {@link OnBackPressedCallback}, instead of requiring the activity to manually call {@link
         * #onBackPressed()} in client code. If enabled, do <b>not</b> invoke {@link #onBackPressed()}
         * manually.
         *
         * <p>This behavior relies on the implementation of {@link #popSystemNavigator()}. It's not
         * recommended to override that method when enabling this attribute, but if you do, you should
         * always fall back to calling {@code super.popSystemNavigator()} when not relying on custom
         * behavior.
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder shouldAutomaticallyHandleOnBackPressed(
                boolean shouldAutomaticallyHandleOnBackPressed) {
            this.shouldAutomaticallyHandleOnBackPressed = shouldAutomaticallyHandleOnBackPressed;
            return this;
        }

        /**
         * Whether to delay the Android drawing pass till after the Flutter UI has been displayed.
         *
         * <p>See {#link FlutterActivityAndFragmentDelegate#onCreateView} for more details.
         */
        @NonNull
        public NewEngineInGroupFragmentBuilder shouldDelayFirstAndroidViewDraw(
                @NonNull boolean shouldDelayFirstAndroidViewDraw) {
            this.shouldDelayFirstAndroidViewDraw = shouldDelayFirstAndroidViewDraw;
            return this;
        }

        /**
         * Creates a {@link Bundle} of arguments that are assigned to the new {@code FlutterFragmentCompat}.
         *
         * <p>Subclasses should override this method to add new properties to the {@link Bundle}.
         * Subclasses must call through to the super method to collect all existing property values.
         */
        @NonNull
        protected Bundle createArgs() {
            Bundle args = new Bundle();
            args.putString(ARG_CACHED_ENGINE_GROUP_ID, cachedEngineGroupId);
            args.putString(ARG_DART_ENTRYPOINT, dartEntrypoint);
            args.putString(ARG_INITIAL_ROUTE, initialRoute);
            args.putBoolean(ARG_HANDLE_DEEPLINKING, handleDeeplinking);
            args.putString(
                    ARG_FLUTTERVIEW_RENDER_MODE,
                    renderMode != null ? renderMode.name() : RenderMode.surface.name());
            args.putString(
                    ARG_FLUTTERVIEW_TRANSPARENCY_MODE,
                    transparencyMode != null ? transparencyMode.name() : TransparencyMode.transparent.name());
            args.putBoolean(ARG_SHOULD_ATTACH_ENGINE_TO_ACTIVITY, shouldAttachEngineToActivity);
            args.putBoolean(ARG_DESTROY_ENGINE_WITH_FRAGMENT, true);
            args.putBoolean(
                    ARG_SHOULD_AUTOMATICALLY_HANDLE_ON_BACK_PRESSED, shouldAutomaticallyHandleOnBackPressed);
            args.putBoolean(ARG_SHOULD_DELAY_FIRST_ANDROID_VIEW_DRAW, shouldDelayFirstAndroidViewDraw);
            return args;
        }

        /**
         * Constructs a new {@code FlutterFragmentCompat} (or a subclass) that is configured based on
         * properties set on this {@code Builder}.
         */
        @NonNull
        public <T extends FlutterFragmentCompat> T build() {
            try {
                @SuppressWarnings("unchecked")
                T frag = (T) fragmentClass.getDeclaredConstructor().newInstance();
                if (frag == null) {
                    throw new RuntimeException(
                            "The FlutterFragmentCompat subclass sent in the constructor ("
                                    + fragmentClass.getCanonicalName()
                                    + ") does not match the expected return type.");
                }

                Bundle args = createArgs();
                frag.setArguments(args);

                return frag;
            } catch (Exception e) {
                throw new RuntimeException(
                        "Could not instantiate FlutterFragmentCompat subclass (" + fragmentClass.getName() + ")", e);
            }
        }
    }

    /**
     * Returns the ID of a statically cached {@link io.flutter.embedding.engine.FlutterEngineGroup} to
     * use within this {@code FlutterFragmentCompat}, or {@code null} if this {@code FlutterFragmentCompat} does
     * not want to use a cached {@link io.flutter.embedding.engine.FlutterEngineGroup}.
     */
    @Override
    @Nullable
    public String getCachedEngineGroupId() {
        String groupId = getArguments().getString(EXTRA_CACHED_ENGINE_GROUP_ID);
        if (groupId == null || groupId.isEmpty()) {
            groupId = FlutterEngineGroupCacheCompat.Global.GROUP_NAME;
        }
        return groupId;
    }

    /**
     * Returns the Android App Component exclusively attached to {@link
     * io.flutter.embedding.engine.FlutterEngine}.
     */
    @Override
    public ExclusiveAppComponent<Activity> getExclusiveAppComponent() {
        return delegate;
    }

    @SuppressLint({"MissingSuperCall", "VisibleForTests"})
    @Override
    public void onAttach(@NonNull Context context) {
        setCalled(true);
        final Activity hostActivity = getActivity();
        if (hostActivity != null) {
            setCalled(false);
            onAttach(hostActivity);
        }
        delegate = new FlutterActivityAndFragmentDelegateCompat(this);
        delegate.onAttach(context);
        if (getArguments().getBoolean(ARG_SHOULD_AUTOMATICALLY_HANDLE_ON_BACK_PRESSED, false)) {
            requireActivity().getOnBackPressedDispatcher().addCallback(this, getOnBackPressedCallback());
        }
    }

    private void setCalled(boolean status) {
        try {
            Field field = getClass().getSuperclass().getSuperclass().getDeclaredField("mCalled");
            field.setAccessible(true);
            field.set(this, status);
        } catch (Throwable ignored) {
        }
    }

    private OnBackPressedCallback getOnBackPressedCallback() {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("onBackPressedCallback");
            field.setAccessible(true);
            return (OnBackPressedCallback) field.get(this);
        } catch (Throwable ignored) {
        }
        return null;
    }

    /**
     * The Dart entrypoint arguments will be passed as a list of string to Dart's entrypoint function.
     *
     * <p>A value of null means do not pass any arguments to Dart's entrypoint function.
     *
     * <p>Subclasses may override this method to directly control the Dart entrypoint arguments.
     */
    @Override
    @Nullable
    public List<String> getDartEntrypointArgs() {
        return getArguments().getStringArrayList(ARG_DART_ENTRYPOINT_ARGS);
    }

    /**
     * Returns the library URI of the Dart method that this {@code FlutterFragment} should execute to
     * start a Flutter app.
     *
     * <p>Defaults to null (example value: "package:foo/bar.dart").
     *
     * <p>Used by this {@code FlutterFragment}'s {@link FlutterActivityAndFragmentDelegate.Host}
     */
    @Override
    @Nullable
    public String getDartEntrypointLibraryUri() {
        return getArguments().getString(ARG_DART_ENTRYPOINT_URI);
    }

    /**
     * Give the host application a chance to take control of the app lifecycle events.
     *
     * <p>Return {@code false} means the host application dispatches these app lifecycle events, while
     * return {@code true} means the engine dispatches these events.
     *
     * <p>Defaults to {@code true}.
     */
    @Override
    public boolean shouldDispatchAppLifecycleState() {
        return true;
    }

}
