# bg-react-native-torch

Control the device's torch

---

## Install

`npm install bg-react-native-torch`

## Usage

### Turn the torch on or off

`setEnabledState(newState: boolean)`

Pass `true` to turn the torch on, or `false` to turn the torch off.


### Determine whether the torch is available

`getAvailableState(): boolean`

Returns `true` if torch is available, `false` otherwise.


### Determine whether the torch is enabled

`getEnabledState(): boolean`

Returns `true` if the torch is turned on, `false` otherwise.

Note: On iOS, this will not accurately track if the torch has been enabled using the Control Centre. i.e. if the torch has only been enabled through the Control Centre, this will still return `false`.


### Set a callback function for torch state changes

`onStateChange(callback: (torchState: TorchState) => void): EventSubscription`

Callback function will be called whenever the availability or enabled state of the torch changes. The `torchState` argument passed to the callback function contains the new states of the torch. When finished you should unsubscribe by calling `.remove()` on the returned `EventSubscription`.


### TorchState interface

```
interface TorchState {
    enabled: boolean;
    available: boolean;
}
```

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
