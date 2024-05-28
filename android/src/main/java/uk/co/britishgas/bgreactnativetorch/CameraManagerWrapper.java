package uk.co.britishgas.bgreactnativetorch;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;

public class CameraManagerWrapper {
    private CameraManager cameraManager;

    static {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Class.forName("android.hardware.camera2.CameraManager");
            } else {
              throw new RuntimeException("Android API version must be at least 23");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkAvailable() {}

    public CameraManagerWrapper(ReactApplicationContext reactContext) {
        cameraManager = (CameraManager) reactContext.getSystemService(Context.CAMERA_SERVICE);
    }

    public void registerTorchCallback(CameraManager.TorchCallback torchCallback, Handler handler) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.registerTorchCallback(torchCallback, handler);
        }
    };

    public String[] getCameraIdList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                return cameraManager.getCameraIdList();
            } catch (CameraAccessException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean getAvailabilityState() {
        try {
            String[] cameraIds = cameraManager.getCameraIdList();

            if (cameraIds == null) {
                return false;
            }
            if (cameraIds.length == 0) {
                return false;
            }

            return Boolean.TRUE.equals(cameraManager
                .getCameraCharacteristics(cameraIds[0])
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
            );

        } catch (CameraAccessException | NullPointerException e) {
            Log.e("TorchModule", "Error: " + e.getMessage());
            return false;
        }
    }

    public void setTorchMode(String cameraId, boolean enabled) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraManager.setTorchMode(cameraId, enabled);
            }
        } catch (CameraAccessException e) {
            Log.e("TorchModule", "Error: " + e.getMessage());
        }
    }
}
