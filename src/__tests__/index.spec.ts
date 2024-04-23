import { describe, it } from '@jest/globals';

let mockGetIsTorchEnabled: () => boolean;
let mockGetIsTorchAvailable: () => boolean;
let mockRegisterTorchCallback: () => void;
let mockSetStateEnabled: (newState: boolean) => void;

jest.mock('react-native', () => {
  mockGetIsTorchEnabled = jest.fn(() => {
    return true;
  });
  mockGetIsTorchAvailable = jest.fn(() => {
    return true;
  });
  mockRegisterTorchCallback = jest.fn();
  mockSetStateEnabled = jest.fn();

  const RN = jest.requireActual('react-native');

  RN.NativeModules.BgReactNativeTorch = {
    getIsTorchEnabled: mockGetIsTorchEnabled,
    getIsTorchAvailable: mockGetIsTorchAvailable,
    registerTorchCallback: mockRegisterTorchCallback,
    setStateEnabled: mockSetStateEnabled,
  };

  return RN;
});

import Torch from '../index';

describe('Torch Module', () => {
  it('Should get enabled state', async () => {
    const enabledState = Torch.getEnabledState();
    expect(enabledState).toBeTruthy();
    expect(mockGetIsTorchEnabled).toHaveBeenCalled();
  });

  it('Should get available state', async () => {
    const availableState = Torch.getAvailableState();
    expect(availableState).toBeTruthy();
    expect(mockGetIsTorchAvailable).toHaveBeenCalled();
  });

  it('Should register torch callback', async () => {
    Torch.registerTorchCallback();
    expect(mockRegisterTorchCallback).toHaveBeenCalled();
  });

  it('Should set enabled state', async () => {
    Torch.setEnabledState(true);
    expect(mockSetStateEnabled).toHaveBeenCalledWith(true);

    Torch.setEnabledState(false);
    expect(mockSetStateEnabled).toHaveBeenCalledWith(false);
  });
});
