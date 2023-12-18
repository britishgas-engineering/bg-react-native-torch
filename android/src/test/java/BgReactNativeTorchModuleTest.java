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
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;

import android.hardware.camera2.CameraManager;

import androidx.test.core.app.ApplicationProvider;

import com.facebook.react.bridge.ReactApplicationContext;

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

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class BgReactNativeTorchModuleTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    private CameraManager mockCameraManager;

    @Mock
    private CameraCharacteristics mockCameraCharacteristics;
    private ReactApplicationContext reactApplicationContext = new ReactApplicationContext(ApplicationProvider.getApplicationContext());

    @Captor
    ArgumentCaptor<BgReactNativeTorchCallback> callbackCaptor;

    @InjectMocks
    private BgReactNativeTorchModule torchModule = new BgReactNativeTorchModule(reactApplicationContext);

    @Before
    public void setUp() throws Exception {
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
        doNothing().when(mockCameraManager).registerTorchCallback(any(BgReactNativeTorchCallback.class), isNull());
        torchModule.registerTorchCallback();
        verify(mockCameraManager).registerTorchCallback(callbackCaptor.capture(), isNull());
        BgReactNativeTorchCallback callbackCaptorValue = callbackCaptor.getValue();

        callbackCaptorValue.onTorchModeChanged("0", true);
        assertTrue(torchModule.getIsTorchEnabled());

        callbackCaptorValue.onTorchModeChanged("0", false);
        assertFalse(torchModule.getIsTorchEnabled());

        callbackCaptorValue.onTorchModeChanged("0", false);
        assertFalse(torchModule.getIsTorchEnabled());

        callbackCaptorValue.onTorchModeChanged("0", true);
        assertTrue(torchModule.getIsTorchEnabled());

        callbackCaptorValue.onTorchModeChanged("0", true);
        assertTrue(torchModule.getIsTorchEnabled());

        callbackCaptorValue.onTorchModeChanged("1", true);
        assertTrue(torchModule.getIsTorchEnabled());

        callbackCaptorValue.onTorchModeChanged("1", false);
        assertFalse(torchModule.getIsTorchEnabled());
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
    }
}
