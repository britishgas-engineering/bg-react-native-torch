package uk.co.britishgas.bgreactnativetorch;

import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.function.Function;

@RequiresApi(api = Build.VERSION_CODES.M)
public class BgReactNativeTorchCallback extends CameraManager.TorchCallback {
    private BgReactNativeTorchModule torchModule;
    private ReactApplicationContext reactContext;

    public BgReactNativeTorchCallback(BgReactNativeTorchModule torchModule, ReactApplicationContext reactContext) {
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
    public void onTorchModeUnavailable(String cameraId) {
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
    public void onTorchModeChanged(String cameraId, boolean enabled) {
        torchModule.isTorchEnabled = enabled;
        emitTorchEvent();
    }

    /**
     * Send an event with the current state of the torch, which is comprised of the
     * enabled state and the availability state
     */
    private void emitTorchEvent() {
        WritableMap params = new JavaOnlyMap();
        params.putBoolean("enabled", torchModule.getIsTorchEnabled());
        params.putBoolean("available", torchModule.getIsTorchAvailable());
        try {
            reactContext
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("TorchStateChange", params);
        } catch (IllegalStateException e) {
            Log.e("BgTorchModule", "Could not emit TorchStateChange event because React instance is not fully set up");
        }
    }
}
