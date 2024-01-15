package io.flutter.plugin.platform;

public class PlatformPlugin {
    public interface PlatformPluginDelegate {
        boolean popSystemNavigator();

        default void setFrameworkHandlesBack(boolean frameworkHandlesBack) {
        }
    }
}
