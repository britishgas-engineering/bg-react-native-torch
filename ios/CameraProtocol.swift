import AVFoundation

@objc public protocol CameraProtocol: NSObjectProtocol {
    var torchMode: AVCaptureDevice.TorchMode { get set }
    // These might need to have @objc for KVO, but that breaks something
    dynamic var isTorchActive: Bool { get }
    dynamic var isTorchAvailable: Bool { get }
    
    func isTorchModeSupported(_ torchMode: AVCaptureDevice.TorchMode) -> Bool
    func lockForConfiguration() throws
    func unlockForConfiguration()
}
