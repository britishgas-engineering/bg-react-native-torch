package uk.co.britishgas.bgreactnativetorch;

import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import java.util.Objects;

/**
 * Controls the torch on a phone, and provides information about the torch's
 * current state
 *
 * @author Kieran Gajraj
 * @version 0.2.0
 */
public class BgReactNativeTorchModule extends ReactContextBaseJavaModule {
    private boolean cameraManagerAvailable;
    private CameraManagerWrapper cameraManagerWrapper;
    private BgReactNativeTorchCallback torchCallback;
    Boolean isTorchEnabled = false;

    /**
     * Constructor for BgReactNativeTorchModule
     *
     * @param reactContext The React Native Application context
     */
    public BgReactNativeTorchModule(ReactApplicationContext reactContext) {
        super(reactContext);

        try {
          CameraManagerWrapper.checkAvailable();
          cameraManagerAvailable = true;
        } catch (Throwable t) {
          cameraManagerAvailable = false;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManagerWrapper = new CameraManagerWrapper(reactContext);
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
      if (cameraManagerAvailable) {
        cameraManagerWrapper.registerTorchCallback(torchCallback, null);
      }
    }

    /**
     * Get the current mode of the torch (on or off)
     *
     * @return Whether the torch is currently enabled
     */
    @ReactMethod
    public void getIsTorchEnabled(Promise promise) {
      if (cameraManagerAvailable) {
        promise.resolve(checkEnabledState());
      } else {
        promise.resolve((false));
      }
    }

    public boolean checkEnabledState() {
        return isTorchEnabled;
    }

    /**
     * Get the current availability state of the torch
     *
     * @return Whether the torch is currently available
     */
    @ReactMethod
    public void getIsTorchAvailable(Promise promise) {
      if (cameraManagerAvailable) {
        promise.resolve(checkAvailabilityState());
      } else {
        promise.resolve(false);
      }
    }

    public boolean checkAvailabilityState() {
      if (cameraManagerAvailable) {
        return cameraManagerWrapper.getAvailabilityState();
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
        if (cameraManagerAvailable) {
            try {
                String cameraId = cameraManagerWrapper.getCameraIdList()[0];
                cameraManagerWrapper.setTorchMode(cameraId, newState);
            } catch (Exception e) {
                Log.e("TorchModule", "Error: " + e.getMessage());
            }
        }
    }

    /**
     * This is called when a BgReactNativeTorchModule NativeEventEmitter's listener
     * is removed.
     */
    @ReactMethod
    public void removeListeners(Integer count) {
        // Keep: Required for RN built in Event Emitter Calls
    }

    /**
     * This is called when a BgReactNativeTorchModule NativeEventEmitter's listener
     * is added.
     */
    @ReactMethod
    public void addListener(String eventName) {
        // Keep: Required for RN built in Event Emitter Calls
    }
}
