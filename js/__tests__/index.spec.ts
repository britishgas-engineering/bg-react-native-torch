import { describe, it } from "@jest/globals";

const mockGetIsTorchEnabled = jest.fn(() => {
  return true;
});
const mockGetIsTorchAvailable = jest.fn(() => {
  return true;
});
const mockRegisterTorchCallback = jest.fn();
const mockSetStateEnabled = jest.fn();

jest.mock("react-native", () => {
  const RN = jest.requireActual("react-native");

  RN.NativeModules.BgReactNativeTorch = {
    getIsTorchEnabled: () => mockGetIsTorchEnabled(),
    getIsTorchAvailable: () => mockGetIsTorchAvailable(),
    registerTorchCallback: () => mockRegisterTorchCallback(),
    setStateEnabled: (newState: boolean) => mockSetStateEnabled(newState),
  };

  return RN;
});

import Torch from "../index";

describe("Torch Module", () => {
  it("Should get enabled state", async () => {
    const enabledState = Torch.getEnabledState();
    expect(enabledState).toBeTruthy();
    expect(mockGetIsTorchEnabled).toHaveBeenCalled();
  });

  it("Should get available state", async () => {
    const availableState = Torch.getAvailableState();
    expect(availableState).toBeTruthy();
    expect(mockGetIsTorchAvailable).toHaveBeenCalled();
  });

  it("Should register torch callback", async () => {
    Torch.registerTorchCallback();
    expect(mockRegisterTorchCallback).toHaveBeenCalled();
  });

  it("Should set enabled state", async () => {
    Torch.setEnabledState(true);
    expect(mockSetStateEnabled).toHaveBeenCalledWith(true);

    Torch.setEnabledState(false);
    expect(mockSetStateEnabled).toHaveBeenCalledWith(false);
  });
});
