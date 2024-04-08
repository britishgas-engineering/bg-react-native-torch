import AVFoundation
import React

extension AVCaptureDevice: CameraProtocol {}

@objc(BgReactNativeTorch)
class BgReactNativeTorch: RCTEventEmitter {
    var device: CameraProtocol? = AVCaptureDevice.default(for: AVMediaType.video)
    var observer: BgReactNativeTorchObserver? = nil
    
    // TODO: Start here
    // JUST ADDED THIS
    // Can use protocols or something here
    // Then you extend AVCaptureDevice with the protocol?
    // And then you can pass in a mock of AVCaptureDevice (think you have to make it yourself)
    init(
        device: CameraProtocol? = AVCaptureDevice.default(for: AVMediaType.video),
        observer: BgReactNativeTorchObserver? = nil
    ) {
        print("Initialising BgReactNativeTorch")
        self.device = device
        self.observer = observer
        super.init()
    }

    @objc func registerTorchCallback() -> Void {
        print("Attempting to register torch callback")
        if (device != nil) {
            observer = BgReactNativeTorchObserver(deviceToObserve: device!, torchModule: self)
            print("Successfully registered callback")
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
        print("checkEnabledState() --> " + String(device?.isTorchActive ?? false))
        return device?.isTorchActive ?? false
        // Should maybe be checking device.torchMode instead?
    }

    @objc func getIsTorchAvailable(resolver: RCTPromiseResolveBlock, rejecter: RCTPromiseRejectBlock) -> Void {
        resolver(checkAvailabilityState())
    }
    
    func checkAvailabilityState() -> Bool {
        print("checkAvailabilityState() --> " + String(device?.isTorchAvailable ?? false))
        return device?.isTorchAvailable ?? false
    }
    
    override func supportedEvents() -> [String]! {
        return ["TorchStateChange"]
    }
    
    func emitEvent() {
        print("Emitting event with available: " + String(checkAvailabilityState()) + " and enabled: " + String(checkEnabledState()))
        sendEvent(
            withName: "TorchStateChange",
            body: [
                "available": checkAvailabilityState(),
                "enabled": checkEnabledState()
            ]
        )
    }
}

