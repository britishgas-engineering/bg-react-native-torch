package uk.co.britishgas.bgreactnativetorch;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraCharacteristics;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controls the torch on a phone, and provides information about the torch's
 * current state
 * 
 * @author Kieran Gajraj
 * @version 0.1.0
 */
public class BgReactNativeTorchModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private CameraManager cameraManager;
    private CameraManager.TorchCallback torchCallback;
    private Boolean isTorchEnabled;

    /**
     * Constructor for BgReactNativeTorchModule
     * 
     * @param reactContext The React Native Application context
     */
    BgReactNativeTorchModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager = (CameraManager) this.reactContext.getSystemService(Context.CAMERA_SERVICE);
            torchCallback = new CameraManager.TorchCallback() {
                /**
                 * When the torch becomes unavailable, send a torch event with the current torch
                 * status
                 * 
                 * @param cameraId The ID of the camera for which the torch has become
                 *                 unavailable
                 */
                @Override
                public void onTorchModeUnavailable(String cameraId) {
                    super.onTorchModeUnavailable(cameraId);
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
                    super.onTorchModeChanged(cameraId, enabled);
                    isTorchEnabled = enabled;
                    emitTorchEvent();
                }
            };
        }
    }

    /**
     * Get the name of the module
     * 
     * @return The module's name
     */
    @Override()
    public String getName() {
        return "BgReactNativeTorch";
    }

    /**
     * Registers a torch callback so that onTorchModeChanged and
     * onTorchModeUnavailable events will be received
     */
    @ReactMethod
    public void registerTorchCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.registerTorchCallback(torchCallback, null);
        }
    }

    /**
     * Get the current mode of the torch (on or off)
     * 
     * @return Whether the torch is currently enabled
     */
    @ReactMethod(isBlockingSynchronousMethod = true)
    public Boolean getIsTorchEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return isTorchEnabled;
        } else {
            // Need to test this with Android < 6
            return Camera.open().getParameters().getFlashMode() == Camera.Parameters.FLASH_MODE_TORCH;
        }
    }

    /**
     * Get the current availability state of the torch
     * 
     * @return Whether the torch is currently available
     */
    @ReactMethod(isBlockingSynchronousMethod = true)
    public Boolean getIsTorchAvailable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                return cameraManager.getCameraCharacteristics(cameraId)
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            } catch (CameraAccessException e) {
                Log.e("BgReactNativeTorchModule", "Error: " + e.getMessage());
                return false;
            }
        } else {
            return !(Camera.open() == null);
        }
    }

    /**
     * Turn the torch on or off
     * 
     * @param newState The new enabled state for the torch (true = on; false = off)
     */
    @ReactMethod
    public void setStateEnabled(Boolean newState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, newState);
            } catch (Exception e) {
                Log.e("BgReactNativeTorchModule", "Error: " + e.getMessage());
            }
        } else {
            Camera camera = Camera.open();
            if (camera == null) {
                return;
            }
            Camera.Parameters params = camera.getParameters();
            if (newState) {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(params);
                camera.startPreview();
            } else {
                params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(params);
                camera.stopPreview();
                camera.release();
            }
            isTorchEnabled = newState;
        }
    }

    /**
     * Send an event with the current state of the torch, which is comprised of the
     * enabled state and the availability state
     */
    private void emitTorchEvent() {
        WritableMap params = Arguments.createMap();
        params.putBoolean("enabled", isTorchEnabled);
        params.putBoolean("available", getIsTorchAvailable());
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("TorchStateChange", params);
    }

    /**
     * This is called when a BgReactNativeTorchModule NativeEventEmitter's listener is removed.
     */
    @ReactMethod
    public void removeListeners(Integer count) {
        //Keep: Required for RN built in Event Emitter Calls
    }
}
