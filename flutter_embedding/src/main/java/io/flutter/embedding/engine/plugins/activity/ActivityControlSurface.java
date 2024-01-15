package io.flutter.embedding.engine.plugins.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;

import io.flutter.embedding.android.ExclusiveAppComponent;

public interface ActivityControlSurface {

    void attachToActivity(
            @NonNull ExclusiveAppComponent<Activity> exclusiveActivity, @NonNull Lifecycle lifecycle);

    void detachFromActivityForConfigChanges();

    void detachFromActivity();

    boolean onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult);

    boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    void onNewIntent(@NonNull Intent intent);

    void onUserLeaveHint();

    void onSaveInstanceState(@NonNull Bundle bundle);

    void onRestoreInstanceState(@Nullable Bundle bundle);
}
