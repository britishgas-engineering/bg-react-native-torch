// BgReactNativeTorchBridge.m
#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>

//#import "BgReactNativeTorch.h"

@interface RCT_EXTERN_MODULE(BgReactNativeTorch, NSObject)

RCT_EXTERN_METHOD(registerTorchCallback)
RCT_EXTERN_METHOD(setStateEnabled: (BOOL)newState)
RCT_EXTERN_METHOD(getIsTorchEnabled:
    (RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject
)
RCT_EXTERN_METHOD(getIsTorchAvailable:
    (RCTPromiseResolveBlock)resolve
    rejecter:(RCTPromiseRejectBlock)reject
)

@end
