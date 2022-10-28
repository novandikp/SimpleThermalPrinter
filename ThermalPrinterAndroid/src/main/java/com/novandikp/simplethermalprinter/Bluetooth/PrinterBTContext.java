package com.novandikp.simplethermalprinter.Bluetooth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnections;
import com.novandikp.simplethermalprinter.PrintTextBuilder;
import com.novandikp.simplethermalprinter.Type.Printer58mm;
import com.novandikp.simplethermalprinter.TypePrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PrinterBTContext {
    private Context context;
    private BluetoothConnection deviceConnected = null;
    private BluetoothAdapter bAdapter;
    private PrintTextBuilder textBuilder = null;
    private boolean supportedDevice = false;
    private TypePrinter type;
    private List<DeviceBT> deviceBTS;
    private HashSet<String> deviceSet = new HashSet<>();
    public static PrinterBTContext instance;
    private SharedPreferences device_bt_simplethermalprinter;
    private SharedPreferences.Editor device_editor;


    private BroadcastReceiver receiverConnect;
    private OnConnect OnConnectEventListener;
    private OnScanDevice onScanDeviceListener;
    private BroadcastReceiver receiverScan;


    public interface OnScanDevice{
        void onScanStart();
        void onScanCompleted(List<DeviceBT> devices);
    }

    public interface OnConnect {
        void onConnect(State_Bluetooth state_bt);
    }

    public PrinterBTContext(Context context) {
        this.context = context;
        device_bt_simplethermalprinter = context.getSharedPreferences("device_bt_simplethermalprinter",Context.MODE_PRIVATE);
        device_editor = device_bt_simplethermalprinter.edit();
        deviceBTS = new ArrayList<>();
        type = Printer58mm.device();
        bAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bAdapter != null) {
            supportedDevice = true;
        }
    }

    public PrinterBTContext(Context context, PrintTextBuilder textBuilder) {
        this.context = context;
        device_bt_simplethermalprinter = context.getSharedPreferences("device_bt_simplethermalprinter",Context.MODE_PRIVATE);
        device_editor = device_bt_simplethermalprinter.edit();
        deviceBTS = new ArrayList<>();
        this.textBuilder = textBuilder;
        type = Printer58mm.device();
        bAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bAdapter != null) {
            supportedDevice = true;
        }
    }

    public static PrinterBTContext getInstance(Context context, PrintTextBuilder textBuilder) {
        if (instance == null) {
            instance = new PrinterBTContext(context, textBuilder);
        }
        return instance;
    }

    public static PrinterBTContext getInstance(Context context){
        if (instance == null) {
            instance = new PrinterBTContext(context);
        }
        return instance;
    }

    public void setTypePrinter(TypePrinter typePrinter) {
        this.type = typePrinter;
    }

    public void setPrinterText(PrintTextBuilder printerTextBuilder) {
        this.textBuilder = printerTextBuilder;
    }

    @SuppressLint("MissingPermission")
    private void addScanDevice(Intent intent){
        String action = intent.getAction();
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice deviceInfo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            BluetoothConnection connection = new BluetoothConnection(deviceInfo);
            BluetoothDevice device =connection.getDevice();
            String address =connection.getDevice().getAddress();
            if(!deviceSet.contains(address) && BluetoothPrintersConnections.isPrinter(deviceInfo)){
                deviceSet.add(address);
                deviceBTS.add(new DeviceBT(device.getName(),device.getAddress(),connection));
            }
        }
    }

    @SuppressLint("MissingPermission")
    public List<DeviceBT> getListBluetoothDevice() {

        if (isPermissionGranted()) {
            if (supportedDevice) {
                if (bAdapter.isEnabled()) {
                    getDeviceConnected();
                    BluetoothPrintersConnections bluetoothConnection = new BluetoothPrintersConnections();
                    for (BluetoothConnection connection : bluetoothConnection.getList()) {
                        BluetoothDevice device = connection.getDevice();
                        String address = device.getAddress();
                        if (!deviceSet.contains(address)) {
                            deviceSet.add(address);
                            DeviceBT btDev =new DeviceBT(device.getName(), address, connection);

                            if(btDev.getConnection().isConnected()){
                              btDev.setState(State_Bluetooth.CONNECTED);
                            }else if(deviceConnected!= null){
                                if(address.equals(deviceConnected.getDevice().getAddress())){
                                    btDev.setState(State_Bluetooth.CONNECTED);
                                }
                            }
                            deviceBTS.add(btDev);
                        }else{
                            if(deviceConnected!= null){
                                if(address.equals(deviceConnected.getDevice().getAddress())){
                                    setStateConnectedByAddress(address);
                                }
                            }
                        }
                    }
                }
            }
        }
        return deviceBTS;
    }
    
     @SuppressLint("MissingPermission")
    public List<DeviceBT> getListBluetoothDeviceSaved() {

        if (isPermissionGranted()) {
            if (supportedDevice) {
                if (bAdapter.isEnabled()) {
                    BluetoothPrintersConnections bluetoothConnection = new BluetoothPrintersConnections();
                    for (BluetoothConnection connection : bluetoothConnection.getList()) {
                        BluetoothDevice device = connection.getDevice();
                        String address = device.getAddress();
                        if (!deviceSet.contains(address)) {
                            deviceSet.add(address);
                            DeviceBT btDev =new DeviceBT(device.getName(), address, connection);

                            if(btDev.getConnection().isConnected()){
                              btDev.setState(State_Bluetooth.CONNECTED);
                            }else if(deviceConnected!= null){
                                if(address.equals(deviceConnected.getDevice().getAddress())){
                                    btDev.setState(State_Bluetooth.CONNECTED);
                                }
                            }
                            deviceBTS.add(btDev);
                        }else{
                            if(deviceConnected!= null){
                                if(address.equals(deviceConnected.getDevice().getAddress())){
                                    setStateConnectedByAddress(address);
                                }
                            }
                        }
                    }
                }
            }
        }
        return deviceBTS;
    }


    private void setStateConnectedByAddress(String address){
        for(DeviceBT device : deviceBTS){
            if(device.getAddress().equals(address)){
                device.setState(State_Bluetooth.CONNECTED);
            }else{
                device.setState(State_Bluetooth.NONE);
            }
            deviceBTS.set(deviceBTS.indexOf(device),device);
        }
    }

    public boolean isPermissionGranted() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else
            return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }


    public boolean isPermissionScanGranted() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }else if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else
            return android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S || ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private void getDeviceConnected() {
        if(deviceConnected==null) {
            deviceConnected = BluetoothPrintersConnections.selectFirstPaired(device_bt_simplethermalprinter);
        }
    }

    @SuppressLint("MissingPermission")
    public String getDeviceName() {
        try{
           getDeviceConnected();
            if (deviceConnected == null) {
                return "Not Connected";
            } else {
                return deviceConnected.getDevice().getName();
            }
        }catch (Exception e){
            return "Not Connected";
        }

    }

    public boolean isConnectedDevice(){
        getDeviceConnected();
        if(deviceConnected!=null){
            return deviceConnected.isConnected();
        }
        return  false;
    }

    public boolean isEnabled(){
        if(bAdapter!=null){
            return bAdapter.isEnabled();
        }
        return false;
    }


    private void setStateConnectListener(State_Bluetooth state){
        if(OnConnectEventListener != null){
            OnConnectEventListener.onConnect(state);
        }
    }

    @SuppressLint("MissingPermission")
    private void connect(DeviceBT device){
        BluetoothConnection btConnection= device.getConnection();
        try {
            if(deviceConnected== btConnection){
                setStateConnectListener(State_Bluetooth.CONNECTED);
            }else {
                if (btConnection.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                    btConnection.getDevice().createBond();
                    if(receiverConnect!=null){
                        context.unregisterReceiver(receiverConnect);
                    }
                    receiverConnect = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                                BluetoothDevice deviceBond = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                if (deviceBond.getBondState() == BluetoothDevice.BOND_BONDED) {
                                    setStateConnectListener(State_Bluetooth.PAIRED);
                                    context.unregisterReceiver(receiverConnect);
                                    receiverConnect=null;
                                    connect(device);
                                } else if (deviceBond.getBondState() == BluetoothDevice.BOND_NONE || deviceBond.getBondState() == BluetoothDevice.ERROR) {
                                    setStateConnectListener(State_Bluetooth.ERROR_BOUNDED);
                                    context.unregisterReceiver(receiverConnect);
                                    receiverConnect=null;
                                }
                            }
                        }
                    };
                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                    context.registerReceiver(receiverConnect, intentFilter);
                    setStateConnectListener(State_Bluetooth.BOUNDING);
                } else {

                    btConnection.connect();
                    device_editor.putString("address",btConnection.getDevice().getAddress());
                    device_editor.commit();
                    deviceConnected = btConnection;
                    setStateConnectedByAddress(btConnection.getDevice().getAddress());
                    setStateConnectListener(State_Bluetooth.CONNECTED);

                }
            }
        } catch (Exception e) {
            setStateConnectListener(State_Bluetooth.NOTCONNECTED);
        }
    }

    @SuppressLint("MissingPermission")
    private void scanPrinter(){
        if(isPermissionScanGranted()){
            if(receiverScan!=null){
                context.unregisterReceiver(receiverScan);
            }
            receiverScan = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    addScanDevice(intent);
                    final String action = intent.getAction();
                    if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                        context.unregisterReceiver(receiverScan);
                        receiverScan = null;
                        if(onScanDeviceListener!=null) {
                            onScanDeviceListener.onScanCompleted(getListBluetoothDevice());
                        }
                    }
                }
            };

            IntentFilter filter2 =  new IntentFilter(BluetoothDevice.ACTION_FOUND);
            filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter2.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            filter2.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
            context.registerReceiver(receiverScan, filter2);
            bAdapter.startDiscovery();
            if(onScanDeviceListener!=null) {
                onScanDeviceListener.onScanStart();
            }
        }
    }

    public void connectDevice(DeviceBT device, OnConnect connectListener){
        this.OnConnectEventListener = connectListener;
        OnConnectEventListener.onConnect(State_Bluetooth.BOUNDING);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connect(device);
            }
        },300);
    }

    public void discoveryDevice(OnScanDevice scanDeviceListener){
       onScanDeviceListener = scanDeviceListener;
       scanPrinter();
    }


    public void print(){
        if(textBuilder!=null) {
            if(deviceConnected == null){
                deviceConnected = BluetoothPrintersConnections.selectFirstPaired(device_bt_simplethermalprinter);
                if(deviceConnected!=null){
                    print();
                }
            }else{
                EscPosPrinter printer = null;
                try {
                    printer = new EscPosPrinter(deviceConnected, type.getPrinterDPI(), type.getPrinterWidth(), type.getMaxCharColumns());
                    printer.printFormattedText(textBuilder.build(type));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
