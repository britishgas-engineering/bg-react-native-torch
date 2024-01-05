import AVFoundation
import React


@objc(BgReactNativeTorch)
class BgReactNativeTorch: RCTEventEmitter {
    let device: AVCaptureDevice? = AVCaptureDevice.default(for: AVMediaType.video)
    var observer: BgReactNativeTorchObserver? = nil

    @objc func registerTorchCallback() -> Void {
        if (device != nil) {
            observer = BgReactNativeTorchObserver(deviceToObserve: device!, torchModule: self)
        }
    }

    @objc func setStateEnabled(newState: Bool) -> Void {
        let newTorchMode: AVCaptureDevice.TorchMode = newState ? AVCaptureDevice.TorchMode.on : AVCaptureDevice.TorchMode.off
        if (device != nil && device?.isTorchModeSupported(newTorchMode) == true) {
            do {
                try device?.lockForConfiguration()
                device?.torchMode = newTorchMode
                device?.unlockForConfiguration()
            } catch {
                print("Unable to lock torch for configuration")
            }
        }
    }

    @objc func getIsTorchEnabled(resolver: RCTPromiseResolveBlock, rejecter: RCTPromiseRejectBlock) -> Void {
        resolver(checkEnabledState())
    }
    
    func checkEnabledState() -> Bool {
        return device?.isTorchActive ?? false
        // Should maybe be checking device.torchMode instead?
    }

    @objc func getIsTorchAvailable(resolver: RCTPromiseResolveBlock, rejecter: RCTPromiseRejectBlock) -> Void {
        resolver(checkAvailabilityState())
    }
    
    func checkAvailabilityState() -> Bool {
        return device?.isTorchAvailable ?? false
    }
    
    override func supportedEvents() -> [String]! {
        return ["TorchStateChange"]
    }
    
    func emitEvent() {
        sendEvent(
            withName: "TorchStateChange",
            body: [
                "available": checkAvailabilityState(),
                "enabled": checkEnabledState()
            ]
        )
    }
}

