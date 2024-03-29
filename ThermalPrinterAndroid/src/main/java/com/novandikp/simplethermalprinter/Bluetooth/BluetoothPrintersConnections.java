package com.novandikp.simplethermalprinter.Bluetooth;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;

public class BluetoothPrintersConnections extends BluetoothConnections {

    public static BluetoothConnection selectFirstPaired(SharedPreferences preferences) {
        com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections printers = new com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections();
        BluetoothConnection[] bluetoothPrinters = printers.getList();
        if (bluetoothPrinters != null && bluetoothPrinters.length > 0) {
            for (BluetoothConnection printer : bluetoothPrinters) {
                try {
                    if(isPrinter(printer.getDevice())   && printer.isConnected()){
                        return printer;
                    }else if(isPrinter(printer.getDevice()) && printer.getDevice().getAddress().equals(preferences.getString("address", ""))){
                        return  printer.connect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @SuppressLint("MissingPermission")
    public static boolean isPrinter(BluetoothDevice device){
         int majDeviceCl = device.getBluetoothClass().getMajorDeviceClass(),
                deviceCl = device.getBluetoothClass().getDeviceClass();
        boolean printer = false;
        try {
            printer =((majDeviceCl == BluetoothClass.Device.Major.IMAGING && (deviceCl == 1664 || deviceCl == BluetoothClass.Device.Major.IMAGING)) || device.getName().equals("InnerPrinter")) ;
        } catch (Exception e) {
        }
        return  printer;
    }

    @SuppressLint("MissingPermission")
    public BluetoothConnection[] getList() {
        BluetoothConnection[] bluetoothDevicesList = super.getList();

        if (bluetoothDevicesList == null) {
            return null;
        }

        int i = 0;
        BluetoothConnection[] printersTmp = new BluetoothConnection[bluetoothDevicesList.length];
        for (BluetoothConnection bluetoothConnection : bluetoothDevicesList) {
            BluetoothDevice device = bluetoothConnection.getDevice();

            if (isPrinter(device)) {
                printersTmp[i++] = new BluetoothConnection(device);
            }
        }
        BluetoothConnection[] bluetoothPrinters = new BluetoothConnection[i];
        System.arraycopy(printersTmp, 0, bluetoothPrinters, 0, i);
        return bluetoothPrinters;
    }

}
