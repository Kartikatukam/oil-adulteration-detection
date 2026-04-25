package com.example.spectraoil;

import android.annotation.SuppressLint;
import android.bluetooth.*;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.util.UUID;

@SuppressLint("MissingPermission")
public class BLEManager {

    private static final String TAG = "BLE";

    private static BLEManager instance;

    public static BLEManager getInstance() {
        if (instance == null) {
            instance = new BLEManager();
        }
        return instance;
    }

    private BLEManager() {}

    private Context context; // ✅ store context

    private static final UUID SERVICE_UUID =
            UUID.fromString("12345678-1234-1234-1234-1234567890ab");

    private static final UUID CHARACTERISTIC_UUID =
            UUID.fromString("abcd1234-5678-1234-5678-abcdef123456");

    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic dataCharacteristic;

    public static String latestData = null;

    private int lastSequence = -1;
    private String buffer = "";

    // ===== CONNECT =====
    public void startScan(Context context) {

        this.context = context; // ✅ save context

        String macAddress = "00:4B:12:39:9F:7E";

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = adapter.getRemoteDevice(macAddress);

        connect(context, device);
    }

    public void connect(Context context, BluetoothDevice device) {
        bluetoothGatt = device.connectGatt(context, false, gattCallback);
    }

    // 🔥 MANUAL START
    public void startReading() {
        sendCommand("START");
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "Connected");

                // ✅ UPDATE UI HERE
                if (context instanceof Home) {
                    Home home = (Home) context;
                    home.runOnUiThread(() -> {
                        home.connectTxt.setText("Live");
                        home.deviceTxt.setText("");
                    });
                }

                gatt.discoverServices();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            BluetoothGattService service = gatt.getService(SERVICE_UUID);

            if (service == null) {
                Log.e(TAG, "Service not found");
                return;
            }

            dataCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID);

            if (dataCharacteristic == null) {
                Log.e(TAG, "Characteristic not found");
                return;
            }

            gatt.setCharacteristicNotification(dataCharacteristic, true);

            BluetoothGattDescriptor descriptor =
                    dataCharacteristic.getDescriptor(
                            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));

            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {

            String part = new String(characteristic.getValue()).trim();

            if (part.equals("END")) {

                latestData = buffer;
                Log.d(TAG, "FULL DATA: " + latestData);

                buffer = "";
                handleData(latestData);
                return;
            }

            buffer += part;
        }
    };

    private void handleData(String data) {

        try {
            if (!data.startsWith("#")) return;

            String[] parts = data.split(",");

            int sequence = Integer.parseInt(parts[0].substring(1));

            if (sequence == lastSequence) return;

            lastSequence = sequence;

            sendCommand("ACK");

        } catch (Exception e) {
            Log.e(TAG, "Parse error", e);
        }
    }

    private void sendCommand(String command) {

        if (bluetoothGatt == null || dataCharacteristic == null) return;

        byte[] value = command.getBytes();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bluetoothGatt.writeCharacteristic(
                    dataCharacteristic,
                    value,
                    BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            );
        } else {
            dataCharacteristic.setValue(value);
            bluetoothGatt.writeCharacteristic(dataCharacteristic);
        }
    }
}