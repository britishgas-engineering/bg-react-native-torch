import AVFoundation
import React

class BgReactNativeTorchObserver: NSObject {
    var device: CameraProtocol
    var availableObservation: NSKeyValueObservation?
    var activeObservation: NSKeyValueObservation?

    init(deviceToObserve: CameraProtocol, torchModule: BgReactNativeTorch) {
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
