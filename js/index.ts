import {
    type EventSubscription,
    NativeEventEmitter,
    NativeModules,
  } from 'react-native';
import { TorchState } from './types';
  
  const LINKING_ERROR =
    `The package 'bg-react-native-torch' doesn't seem to be linked. Make sure: \n\n` +
    '- You rebuilt the app after installing the package\n' +
    '- You are not using Expo Go\n';
  
  const BgReactNativeTorch = NativeModules.BgReactNativeTorch
    ? NativeModules.BgReactNativeTorch
    : new Proxy(
        {},
        {
          get() {
            throw new Error(LINKING_ERROR);
          },
        }
      );
  
  BgReactNativeTorch.registerTorchCallback();
  
  const getEnabledState = (): boolean => {
    return BgReactNativeTorch.getIsTorchEnabled();
  };
  
  const getAvailableState = (): boolean => {
    return BgReactNativeTorch.getIsTorchAvailable();
  };
  
  const registerTorchCallback = () => {
    BgReactNativeTorch.registerTorchCallback();
  };
  
  const onStateChange = (
    callback: (torchState: TorchState) => void
  ): EventSubscription => {
    const emitter = new NativeEventEmitter(BgReactNativeTorch);
    const subscription = emitter.addListener('TorchStateChange', callback);
    return subscription;
  };
  
  const setEnabledState = (newState: boolean) => {
    BgReactNativeTorch.setStateEnabled(newState);
  };
  
  export default {
    getEnabledState,
    getAvailableState,
    setEnabledState,
    registerTorchCallback,
    onStateChange,
  };
  
  export type { TorchState };