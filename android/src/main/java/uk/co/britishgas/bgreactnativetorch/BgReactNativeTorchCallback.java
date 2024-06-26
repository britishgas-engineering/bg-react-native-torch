package uk.co.britishgas.bgreactnativetorch;

import android.hardware.camera2.CameraManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

@RequiresApi(api = Build.VERSION_CODES.M)
public class BgReactNativeTorchCallback extends CameraManager.TorchCallback {
    private final BgReactNativeTorchModule torchModule;
    private final ReactApplicationContext reactContext;

    public BgReactNativeTorchCallback(
            BgReactNativeTorchModule torchModule,
            ReactApplicationContext reactContext) {
        super();
        this.torchModule = torchModule;
        this.reactContext = reactContext;
    }

    /**
     * When the torch becomes unavailable, send a torch event with the current torch
     * status
     *
     * @param cameraId The ID of the camera for which the torch has become
     *                 unavailable
     */
    @Override
    public void onTorchModeUnavailable(@NonNull String cameraId) {
        super.onTorchModeUnavailable(cameraId);
        torchModule.isTorchEnabled = false;
        emitTorchEvent();
    }

    /**
     * When the torch is turned on or off, send a torch event with the current torch
     * status
     *
     * @param cameraId The ID of the camera for which the torch mode has changed
     * @param enabled  The new mode of the torch
     */
    @Override
    public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
        super.onTorchModeChanged(cameraId, enabled);
        torchModule.isTorchEnabled = enabled;
        emitTorchEvent();
    }

    /**
     * Send an event with the current state of the torch, which is comprised of the
     * enabled state and the availability state
     */
    private void emitTorchEvent() {
        WritableMap eventBody = Arguments.createMap();
        eventBody.putBoolean("enabled", torchModule.checkEnabledState());
        eventBody.putBoolean("available", torchModule.checkAvailabilityState());
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("TorchStateChange", eventBody);
    }
}
