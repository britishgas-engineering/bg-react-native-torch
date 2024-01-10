import AVFoundation
//import BgReactNativeTorch

class MockAVCaptureDevice: NSObject {
    var torchMode: AVCaptureDevice.TorchMode
    @objc var isTorchActive: Bool
    @objc var isTorchAvailable: Bool
    
    override init() {
        torchMode = AVCaptureDevice.TorchMode.on
        isTorchActive = false
        isTorchAvailable = false
        super.init()
    }
    
    func isTorchModeSupported(_ torchMode: AVCaptureDevice.TorchMode) -> Bool {
        return false
    }
    
    func lockForConfiguration() throws {
        
    }
    
    func unlockForConfiguration() {
        
    }
}

extension MockAVCaptureDevice: CameraProtocol {}
