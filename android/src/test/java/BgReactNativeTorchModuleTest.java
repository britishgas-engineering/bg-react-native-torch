import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;

import android.hardware.camera2.CameraManager;

import androidx.test.core.app.ApplicationProvider;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.test.util.ReflectionTestUtils;

import uk.co.britishgas.bgreactnativetorch.BgReactNativeTorchCallback;
import uk.co.britishgas.bgreactnativetorch.BgReactNativeTorchModule;

// TODO: Figure out testing for Android < 6
// To test the events being emitted, partial mocks?
// https://javadoc.io/static/org.mockito/mockito-core/5.8.0/org/mockito/Mockito.html
// Nope should actually be with Spy


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class BgReactNativeTorchModuleTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private CameraManager mockCameraManager;

    @Mock
    private CameraCharacteristics mockCameraCharacteristics;
    private ReactApplicationContext reactApplicationContext = spy(new ReactApplicationContext(ApplicationProvider.getApplicationContext()));

    @Mock
    private DeviceEventManagerModule.RCTDeviceEventEmitter mockDeviceEventEmitter;

    @Captor
    ArgumentCaptor<BgReactNativeTorchCallback> callbackCaptor;

    @Captor
    ArgumentCaptor<WritableMap> eventParamsCaptor;

    @InjectMocks
    private BgReactNativeTorchModule torchModule = new BgReactNativeTorchModule(reactApplicationContext);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testModuleName() {
        assertNotNull(torchModule);
        assertEquals(torchModule.getName(), "BgReactNativeTorch");
    }

    @Test
    public void testGetIsTorchAvailable() {
        try {
            when(mockCameraManager.getCameraIdList()).thenReturn(new String[]{});
            assertFalse(torchModule.getIsTorchAvailable());

            when(mockCameraManager.getCameraIdList()).thenReturn(new String[]{"0"});
            when(mockCameraManager.getCameraCharacteristics("0")).thenReturn(mockCameraCharacteristics);

            when(mockCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)).thenReturn(false);
            assertFalse(torchModule.getIsTorchAvailable());

            when(mockCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)).thenReturn(true);
            assertTrue(torchModule.getIsTorchAvailable());

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetIsTorchEnabled() {
        ReflectionTestUtils.setField(torchModule, "isTorchEnabled", false);
        assertFalse(torchModule.getIsTorchEnabled());
        ReflectionTestUtils.setField(torchModule, "isTorchEnabled", true);
        assertTrue(torchModule.getIsTorchEnabled());
    }

    @Test
    public void testOnTorchModeChanged() {
        try {
            WritableMap eventParams;

            doReturn(mockDeviceEventEmitter).when(reactApplicationContext).getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
            doNothing().when(mockDeviceEventEmitter).emit(eq("TorchModule"), any(WritableMap.class));
            doNothing().when(mockCameraManager).registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());

            when(mockCameraManager.getCameraIdList()).thenReturn(new String[]{"0"});
            when(mockCameraManager.getCameraCharacteristics("0")).thenReturn(mockCameraCharacteristics);
            when(mockCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)).thenReturn(true);

            torchModule.registerTorchCallback();
            verify(mockCameraManager).registerTorchCallback(callbackCaptor.capture(), isNull());
            BgReactNativeTorchCallback callbackCaptorValue = callbackCaptor.getValue();

            callbackCaptorValue.onTorchModeChanged("0", true);
            assertTrue(torchModule.getIsTorchEnabled());
            verify(mockDeviceEventEmitter, times(1)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("0", false);
            assertFalse(torchModule.getIsTorchEnabled());
            verify(mockDeviceEventEmitter, times(2)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));


            callbackCaptorValue.onTorchModeChanged("0", false);
            assertFalse(torchModule.getIsTorchEnabled());
            verify(mockDeviceEventEmitter, times(3)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("0", true);
            assertTrue(torchModule.getIsTorchEnabled());
            verify(mockDeviceEventEmitter, times(4)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("0", true);
            assertTrue(torchModule.getIsTorchEnabled());
            verify(mockDeviceEventEmitter, times(5)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("1", true);
            assertTrue(torchModule.getIsTorchEnabled());
            verify(mockDeviceEventEmitter, times(6)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("1", false);
            assertFalse(torchModule.getIsTorchEnabled());
            verify(mockDeviceEventEmitter, times(7)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

        } catch (CameraAccessException e) {
            fail();
        }
    }

    @Test
    public void testOnTorchModeUnavailable() {
        try {
            WritableMap eventParams;

            doReturn(mockDeviceEventEmitter).when(reactApplicationContext).getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
            doNothing().when(mockDeviceEventEmitter).emit(eq("TorchModule"), any(WritableMap.class));
            doNothing().when(mockCameraManager).registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());
            ReflectionTestUtils.setField(torchModule, "isTorchEnabled", false);

            when(mockCameraManager.getCameraIdList()).thenReturn(new String[]{"0"});
            when(mockCameraManager.getCameraCharacteristics("0")).thenReturn(mockCameraCharacteristics);
            when(mockCameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)).thenReturn(false);

            torchModule.registerTorchCallback();
            verify(mockCameraManager).registerTorchCallback(callbackCaptor.capture(), isNull());
            BgReactNativeTorchCallback callbackCaptorValue = callbackCaptor.getValue();

            callbackCaptorValue.onTorchModeUnavailable("0");
            verify(mockDeviceEventEmitter, times(1)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertFalse(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeUnavailable("0");
            verify(mockDeviceEventEmitter, times(2)).emit(eq("TorchStateChange"), eventParamsCaptor.capture());
            eventParams = eventParamsCaptor.getValue();
            assertFalse(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

        } catch (CameraAccessException e) {
            fail();
        }
    }

    @Test
    public void testRegisterTorchCallback() {
        doNothing().when(mockCameraManager).registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());
        torchModule.registerTorchCallback();
        verify(mockCameraManager, times(1)).registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());
    }

    @Test
    public void testRemoveListenersExists() {
        // Only need to check that this method exists
        torchModule.removeListeners(0);
    }

    @Test
    public void testSetStateEnabled() {
        try {
            when(mockCameraManager.getCameraIdList()).thenReturn(new String[]{"0"});
            doNothing().when(mockCameraManager).setTorchMode(eq("0"), anyBoolean());
            torchModule.setStateEnabled(true);
            verify(mockCameraManager, times(1)).setTorchMode("0", true);
            torchModule.setStateEnabled(false);
            verify(mockCameraManager, times(1)).setTorchMode("0", false);
        } catch (CameraAccessException e) {
            fail();
        }
    }

    @After
    public void tearDown() {
        reset(mockCameraManager);
        reset(mockCameraCharacteristics);
        reset(reactApplicationContext);
    }
}
