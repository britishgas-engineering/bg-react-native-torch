import AVFoundation
import React

@objc(BgReactNativeTorch)
class BgReactNativeTorch: RCTEventEmitter {
    @objc dynamic var device: AVCaptureDevice? = AVCaptureDevice.default(for: AVMediaType.video)
    var observer: BgReactNativeTorchObserver? = nil
  
    override init() {
      self.device = AVCaptureDevice.default(for: AVMediaType.video)
      self.observer = nil
      super.init()
    }
  
    init(
        device: AVCaptureDevice? = AVCaptureDevice.default(for: AVMediaType.video),
        observer: BgReactNativeTorchObserver? = nil
    ) {
        self.device = device
        self.observer = observer
        super.init()
    }

    @objc func registerTorchCallback() -> Void {
        if (device != nil) {
            observer = BgReactNativeTorchObserver(deviceToObserve: device!, torchModule: self)
        }
    }

    @objc(setStateEnabled:) func setStateEnabled(_ newState: Bool) -> Void {
          let newTorchMode: AVCaptureDevice.TorchMode = newState ? AVCaptureDevice.TorchMode.on : AVCaptureDevice.TorchMode.off
          if (device != nil && device?.isTorchModeSupported(newTorchMode) == true) {
              do {
                  try device?.lockForConfiguration()
                  device?.torchMode = newTorchMode
                  device?.unlockForConfiguration()
              } catch {
              }
          }
      }

    @objc(getIsTorchEnabled:rejecter:) func getIsTorchEnabled(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {
          resolve(checkEnabledState())
      }
      
    func checkEnabledState() -> Bool {
        return device?.isTorchActive ?? false
        // Should maybe be checking device.torchMode instead?
//        return device?.torchMode == AVCaptureDevice.TorchMode.on
    }

    @objc(getIsTorchAvailable:rejecter:) func getIsTorchAvailable(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {
        resolve(checkAvailabilityState())
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

