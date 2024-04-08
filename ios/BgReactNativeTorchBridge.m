// BgReactNativeTorchBridge.m
#import <React/RCTBridgeModule.h>

//#import "BgReactNativeTorch.h"

@interface RCT_EXTERN_MODULE(BgReactNativeTorch, NSObject)

RCT_EXTERN_METHOD(registerTorchCallback)
RCT_EXTERN_METHOD(setStateEnabled: (NSBoolean))
RCT_EXTERN_METHOD(getIsTorchEnabled)
RCT_EXTERN_METHOD(getIsTorchAvailable)

@end