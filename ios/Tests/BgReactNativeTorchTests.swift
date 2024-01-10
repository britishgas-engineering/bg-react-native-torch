import XCTest
import React
//import BgReactNativeTorch

final class BgReactNativeTorchTests: XCTestCase {
    var captureDevice: MockAVCaptureDevice?
    var torchModule: BgReactNativeTorch?

    override func setUp() {
        captureDevice = MockAVCaptureDevice()
        torchModule = BgReactNativeTorch(device: captureDevice)
    }
    
    func testGetIsTorchAvailable() {
        torchModule!.getIsTorchAvailable(resolver: mockPromiseExpectTrue, rejecter: mockPromiseRejecter)
    }
    
    func mockPromiseExpectTrue(result: Any?) -> Void {
        let boolResult: Bool = result as! Bool
        XCTAssertTrue(boolResult)
    }
    
    func mockPromiseExpectFalse(result: Any?) -> Void {
        let boolResult: Bool = result as! Bool
        XCTAssertFalse(boolResult)
    }
    
    func mockPromiseRejecter(_:String?, _: String?, _: Error?) -> Void {
        XCTFail()
    }
    

}
