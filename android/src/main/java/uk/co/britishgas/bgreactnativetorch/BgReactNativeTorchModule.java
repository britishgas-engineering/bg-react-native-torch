package uk.co.britishgas.bgreactnativetorch;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;


/**
 * Controls the torch on a phone, and provides information about the torch's
 * current state
 * 
 * @author Kieran Gajraj
 * @version 0.1.0
 */
public class BgReactNativeTorchModule extends ReactContextBaseJavaModule {
    final boolean SUPPORT_LEGACY_TORCH = false;
//    private final ReactApplicationContext reactContext;
    private CameraManager cameraManager;
    private CameraManager.TorchCallback torchCallback;
    Boolean isTorchEnabled = false;

    /**
     * Constructor for BgReactNativeTorchModule
     * 
     * @param reactContext The React Native Application context
     */
    public BgReactNativeTorchModule(ReactApplicationContext reactContext) {
        super(reactContext);
//        this.reactContext = reactContext;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
            torchCallback = new BgReactNativeTorchCallback(this, reactContext);
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
        } else if (SUPPORT_LEGACY_TORCH) {
            // Need to test this with Android < 6
            return Camera.open().getParameters().getFlashMode() == Camera.Parameters.FLASH_MODE_TORCH;
        } else {
            return false;
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
                String[] cameraIds = cameraManager.getCameraIdList();

                if (cameraIds == null) {
                    return false;
                }
                if (cameraIds.length == 0) {
                    return false;
                }
                return cameraManager.getCameraCharacteristics(cameraIds[0])
                        .get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            } catch (CameraAccessException e) {
                Log.e("TorchModule", "Error: " + e.getMessage());
                return false;
            }
        } else if (SUPPORT_LEGACY_TORCH) {
            return !(Camera.open() == null);
        } else {
            return false;
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
                Log.e("TorchModule", "Error: " + e.getMessage());
            }
        } else if (SUPPORT_LEGACY_TORCH) {
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
     * This is called when a BgReactNativeTorchModule NativeEventEmitter's listener is removed.
     */
    @ReactMethod
    public void removeListeners(Integer count) {
        //Keep: Required for RN built in Event Emitter Calls
    }

    /**
     * This is called when a BgReactNativeTorchModule NativeEventEmitter's listener is added.
     */
    @ReactMethod
    public void addListener(String eventName) {
        //Keep: Required for RN built in Event Emitter Calls
    }
}
