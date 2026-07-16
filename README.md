# ZK OTA Tools

This repository contains a suite of tools and libraries for performing Over‑The‑Air (OTA) firmware updates on ZK‑based Bluetooth Low Energy (BLE) smartwatches.

## Project Structure

- **python‑client/**: A Python‑based OTA client using `bleak` to communicate with your device and handle the OTA update process.
- **android‑module/**: A clean, documented Android module providing an OTA manager that can be integrated into any Android application.
- **protocol‑spec/**: Documentation detailing the reverse‑engineered OTA protocol, including opcodes, message formats, and the general OTA flow.

## Getting Started

### Python Client

1. **Requirements**:  
   - Python 3.8+  
   - `bleak` library for BLE communication

2. **Usage**:  
   - Navigate to the `python‑client/` directory.
   - Run `zk_ota_client.py` with the target device address and the firmware file.

### Android Module

1. **Integration**:  
   - Import the `android‑module/` directory as a module in your Android Studio project.
   - Implement the `OtaEventListener` interface in your BLE service to connect the OTA manager.

## License

This project is licensed under the MIT License.

## Contributions

Contributions are welcome! Feel free to fork this repo and submit pull requests.

---

This README will give anyone who visits your repo a clear sense of what’s inside and how to use each component.

Let’s move on to the protocol spec and I’ll get that ready for you next!
