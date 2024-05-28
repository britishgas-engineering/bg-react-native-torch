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
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.test.core.app.ApplicationProvider;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.JavaOnlyMap;
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
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.test.util.ReflectionTestUtils;

import uk.co.britishgas.bgreactnativetorch.BgReactNativeTorchCallback;
import uk.co.britishgas.bgreactnativetorch.BgReactNativeTorchModule;
import uk.co.britishgas.bgreactnativetorch.CameraManagerWrapper;

// TODO: Figure out testing for Android < 6

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class BgReactNativeTorchModuleTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private CameraManagerWrapper mockCameraManagerWrapper;

    private final ReactApplicationContext reactApplicationContext = spy(
            new ReactApplicationContext(ApplicationProvider.getApplicationContext()));

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
            when(mockCameraManagerWrapper.getAvailabilityState()).thenReturn(false);
            torchModule.getIsTorchAvailable(new MockBoolPromise(false));

            when(mockCameraManagerWrapper.getAvailabilityState()).thenReturn(false);
            torchModule.getIsTorchAvailable(new MockBoolPromise(false));

            when(mockCameraManagerWrapper.getAvailabilityState()).thenReturn(true);

            torchModule.getIsTorchAvailable(new MockBoolPromise(true));

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testGetIsTorchEnabled() {
        ReflectionTestUtils.setField(torchModule, "isTorchEnabled", false);
        torchModule.getIsTorchEnabled(new MockBoolPromise(false));
        ReflectionTestUtils.setField(torchModule, "isTorchEnabled", true);
        torchModule.getIsTorchEnabled(new MockBoolPromise(true));
    }

    @Test
    public void testOnTorchModeChanged() {
        try (MockedStatic<Arguments> mockArguments = mockStatic(Arguments.class)) {
            WritableMap eventParams;

            doReturn(mockDeviceEventEmitter).when(reactApplicationContext)
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

            doNothing()
                    .when(mockDeviceEventEmitter)
                    .emit(eq("TorchModule"), any(WritableMap.class));

            doNothing()
                    .when(mockCameraManagerWrapper)
                    .registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());

            when(mockCameraManagerWrapper.getAvailabilityState()).thenReturn(true);

            mockArguments.when(Arguments::createMap).thenReturn(new JavaOnlyMap());

            torchModule.registerTorchCallback();
            verify(mockCameraManagerWrapper).registerTorchCallback(callbackCaptor.capture(), isNull());
            BgReactNativeTorchCallback callbackCaptorValue = callbackCaptor.getValue();

            callbackCaptorValue.onTorchModeChanged("0", true);
            torchModule.getIsTorchEnabled(new MockBoolPromise(true));
            verify(mockDeviceEventEmitter, times(1))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("0", false);
            torchModule.getIsTorchEnabled(new MockBoolPromise(false));
            verify(mockDeviceEventEmitter, times(2))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("0", false);
            torchModule.getIsTorchEnabled(new MockBoolPromise(false));
            verify(mockDeviceEventEmitter, times(3))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("0", true);
            torchModule.getIsTorchEnabled(new MockBoolPromise(true));
            verify(mockDeviceEventEmitter, times(4))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("0", true);
            torchModule.getIsTorchEnabled(new MockBoolPromise(true));
            verify(mockDeviceEventEmitter, times(5))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("1", true);
            torchModule.getIsTorchEnabled(new MockBoolPromise(true));
            verify(mockDeviceEventEmitter, times(6))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertTrue(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeChanged("1", false);
            torchModule.getIsTorchEnabled(new MockBoolPromise(false));
            verify(mockDeviceEventEmitter, times(7))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertTrue(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

        }
    }

    @Test
    public void testOnTorchModeUnavailable() {
        try (MockedStatic<Arguments> mockArguments = mockStatic(Arguments.class)) {
            WritableMap eventParams;

            doReturn(mockDeviceEventEmitter).when(reactApplicationContext)
                    .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);

            doNothing()
                    .when(mockDeviceEventEmitter)
                    .emit(eq("TorchModule"), any(WritableMap.class));

            doNothing()
                    .when(mockCameraManagerWrapper)
                    .registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());

            ReflectionTestUtils.setField(torchModule, "isTorchEnabled", false);

            when(mockCameraManagerWrapper.getAvailabilityState()).thenReturn(false);

            mockArguments.when(Arguments::createMap).thenReturn(new JavaOnlyMap());

            torchModule.registerTorchCallback();
            verify(mockCameraManagerWrapper).registerTorchCallback(callbackCaptor.capture(), isNull());
            BgReactNativeTorchCallback callbackCaptorValue = callbackCaptor.getValue();

            callbackCaptorValue.onTorchModeUnavailable("0");
            verify(mockDeviceEventEmitter, times(1))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();
            assertFalse(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

            callbackCaptorValue.onTorchModeUnavailable("0");
            verify(mockDeviceEventEmitter, times(2))
                    .emit(eq("TorchStateChange"), eventParamsCaptor.capture());

            eventParams = eventParamsCaptor.getValue();

            assertFalse(eventParams.getBoolean("available"));
            assertFalse(eventParams.getBoolean("enabled"));

        }
    }

    @Test
    public void testRegisterTorchCallback() {
        doNothing()
                .when(mockCameraManagerWrapper)
                .registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());

        torchModule.registerTorchCallback();
        verify(mockCameraManagerWrapper, times(1))
                .registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());
    }

    @Test
    public void testRemoveListenersExists() {
        // Only need to check that this method exists
        torchModule.removeListeners(0);
    }

    @Test
    public void testSetStateEnabled() {
      when(mockCameraManagerWrapper.getCameraIdList()).thenReturn(new String[] { "0" });
      doNothing().when(mockCameraManagerWrapper).setTorchMode(eq("0"), anyBoolean());
      torchModule.setStateEnabled(true);
      verify(mockCameraManagerWrapper, times(1))
              .setTorchMode("0", true);

      torchModule.setStateEnabled(false);
      verify(mockCameraManagerWrapper, times(1))
              .setTorchMode("0", false);

    }

    @After
    public void tearDown() {
        reset(mockCameraManagerWrapper);
        reset(reactApplicationContext);
    }
}
