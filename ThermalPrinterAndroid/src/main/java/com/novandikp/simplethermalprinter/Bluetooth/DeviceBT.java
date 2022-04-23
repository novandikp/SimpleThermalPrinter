package com.novandikp.simplethermalprinter.Bluetooth;

import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;

public class DeviceBT {
    private String name;
    private String address;
    private BluetoothConnection connection;
    private State_Bluetooth state;

    public DeviceBT(String name, String address, BluetoothConnection connection) {
        this.name = name;
        this.connection = connection;
        this.state = State_Bluetooth.NONE;
    }

    public String getAddress() {
        return address;
    }

    public void setState(State_Bluetooth state) {
        this.state = state;
    }

    public String getStatus(){
        String keterangan="";
        switch (state){
            case NOTCONNECTED:
                keterangan="Cannot Connect";
                break;
            case PAIRED:
                keterangan="Paired";
                break;
            case BOUNDING:
                keterangan="Bounding";
                break;
            case  ERROR_BOUNDED:
                keterangan="Error when bounding";
                break;
            case CONNECTED:
                keterangan="Connected";
                break;
            case NONE:
                keterangan="";
        }
        return keterangan;
    }

    public State_Bluetooth getState() {
        return state;
    }

    public String getName() {
        return name;
    }

    public BluetoothConnection getConnection() {
        return connection;
    }
}