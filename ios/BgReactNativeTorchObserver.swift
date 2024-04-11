import AVFoundation
import React

class BgReactNativeTorchObserver: NSObject {
    var device: AVCaptureDevice
    var availableObservation: NSKeyValueObservation?
    var activeObservation: NSKeyValueObservation?

    /// Initialise an observer object to track changes in torch state
    /// 
    /// - Parameters:
    ///     - deviceToObserve: The device for which the torch should be observed
    ///     - torchModule: The torch module object to emit events via
    init(deviceToObserve: AVCaptureDevice, torchModule: BgReactNativeTorch) {
        device = deviceToObserve
        super.init()
        
        availableObservation = observe(
            \.device.isTorchAvailable
        ) { object, change in
            torchModule.emitEvent()
        }

        activeObservation = observe(
            \.device.isTorchActive
        ) { object, change in
            torchModule.emitEvent()
        }
    }
}
