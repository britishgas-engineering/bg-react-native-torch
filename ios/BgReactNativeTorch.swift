import AVFoundation
import React

@objc(BgReactNativeTorch)
class BgReactNativeTorch: RCTEventEmitter {
    @objc dynamic var device: AVCaptureDevice? = AVCaptureDevice.default(for: AVMediaType.video)
    var observer: BgReactNativeTorchObserver? = nil
  
    /// Initialise the module, setting the device using AVCaptureDevice.default
    /// 
    /// - Returns: A BgReactNativeTorch object to control the torch
    override init() {
      self.device = AVCaptureDevice.default(for: AVMediaType.video)
      self.observer = nil
      super.init()
    }
  
    /// Initialise the module, using the passed arguments to set device and observer
    /// 
    /// - Parameters:
    ///     - device: The device for which the torch should be controlled
    ///     - observer: An observer to use for monitoring torch state changes
    /// 
    /// - Returns: A BgReactNativeTorch object to control the torch
    init(
        device: AVCaptureDevice? = AVCaptureDevice.default(for: AVMediaType.video),
        observer: BgReactNativeTorchObserver? = nil
    ) {
        self.device = device
        self.observer = observer
        super.init()
    }

    /// Register a callback so that changes to state and availability will be tracked
    @objc func registerTorchCallback() -> Void {
        if (device != nil) {
            observer = BgReactNativeTorchObserver(deviceToObserve: device!, torchModule: self)
        }
    }

    /// Set the torch's state to be on or off
    /// 
    /// - Parameters:
    ///     - newState: Whether the torch should be turned on (true) or off (false)
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

    /// Asynchronous method to determine if the torch is enabled
    /// Resolves to true if the torch is enabled, or false if the torch is disabled
    /// 
    /// - Parameters:
    ///     - resolve: Resolve block
    ///     - reject: Reject block
    @objc(getIsTorchEnabled:rejecter:) func getIsTorchEnabled(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {
          resolve(checkEnabledState())
      }
      
    /// Synchronous method to determine if the torch is enabled
    /// 
    /// - Returns: Whether the torch is enabled (true) or disabled (false)
    func checkEnabledState() -> Bool {
        return device?.isTorchActive ?? false
        // Should maybe be checking device.torchMode instead?
//        return device?.torchMode == AVCaptureDevice.TorchMode.on
    }

    /// Asynchronous method to determine if the torch is available
    /// Resolves to true if the torch is available, or false if the torch is unavailable
    /// 
    /// - Parameters:
    ///     - resolve: Resolve block
    ///     - reject: Reject block
    @objc(getIsTorchAvailable:rejecter:) func getIsTorchAvailable(_ resolve: RCTPromiseResolveBlock, rejecter reject: RCTPromiseRejectBlock) -> Void {
        resolve(checkAvailabilityState())
    }
    
    /// Synchronous method to determine if the torch is available
    /// 
    /// - Returns: Whether the torch is available (true) or unavailable (false)
    func checkAvailabilityState() -> Bool {
        return device?.isTorchAvailable ?? false
    }

    /// Allows the module to emit TorchStateChange events
    override func supportedEvents() -> [String]! {
        return ["TorchStateChange"]
    }
    
    /// Emit the current enabled and availability states of the torch
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

