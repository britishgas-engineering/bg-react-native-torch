import {
  type EventSubscription,
  NativeEventEmitter,
  NativeModules,
} from 'react-native';
import type { TorchState } from './types';

const LINKING_ERROR =
  "The package 'bg-react-native-torch' doesn't seem to be linked. Make sure: \n\n" +
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

// Check whether the torch is turned on (true) or off (false)
const getEnabledState = (): Promise<boolean> => {
  return BgReactNativeTorch.getIsTorchEnabled();
};

// Check whether the torch is available (true) or unavailable (false)
const getAvailableState = (): Promise<boolean> => {
  return BgReactNativeTorch.getIsTorchAvailable();
};

// Register a callback to monitor the torch's state
const registerTorchCallback = () => {
  BgReactNativeTorch.registerTorchCallback();
};

// Set a callback function to be called when the torch's state changes
const onStateChange = (
  callback: (torchState: TorchState) => void
): EventSubscription => {
  const emitter = new NativeEventEmitter(BgReactNativeTorch);
  const subscription = emitter.addListener('TorchStateChange', callback);
  return subscription;
};

// Turn the torch's state to on (true) or off (false)
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
