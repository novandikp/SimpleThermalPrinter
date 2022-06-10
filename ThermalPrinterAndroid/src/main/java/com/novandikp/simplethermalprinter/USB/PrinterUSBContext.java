package com.novandikp.simplethermalprinter.USB;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.novandikp.simplethermalprinter.PrintTextBuilder;
import com.novandikp.simplethermalprinter.Type.Printer58mm;
import com.novandikp.simplethermalprinter.TypePrinter;

import java.lang.reflect.Type;

public class PrinterUSBContext {
    private  final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private Context context= null;
    private BroadcastReceiver usbReceiver;
    private PrintTextBuilder textBuilder = null;
    private UsbDevice usbDevice;
    private boolean connectedDevice = false;
    private UsbConnection usbConnection;
    private UsbManager usbManager;
    private boolean alreadyService;
    public static  PrinterUSBContext instance;

    public static PrinterUSBContext getInstance(Context context, PrintTextBuilder textBuilder){
        if(instance == null){
            instance = new PrinterUSBContext(context,textBuilder);
        }
        return instance;

    }


    public static PrinterUSBContext getInstance(Context context){
        if(instance == null){
            instance = new PrinterUSBContext(context);
        }
        return instance;
    }


    public static void setTextBuilder(PrintTextBuilder textBuilder){
        instance.textBuilder = textBuilder;
        instance.setTypePrinter(Printer58mm.device());
    }

    public static void setTextBuilder(PrintTextBuilder textBuilder, TypePrinter typePrinter){
        instance.textBuilder = textBuilder;
        instance.setTypePrinter(typePrinter);
    }

    public PrinterUSBContext(Context context, PrintTextBuilder textBuilder) {
        this.context = context;
        this.textBuilder = textBuilder;
        setTypePrinter(Printer58mm.device());
    }

    public PrinterUSBContext(Context context) {
        this.context = context;
    }

    public void setTypePrinter(TypePrinter type){
        this.usbReceiver = null;
        this.usbReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                        usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if (usbManager != null && usbDevice != null) {
                                EscPosPrinter printer = null;
                                try {
                                    printer = new EscPosPrinter(new UsbConnection(usbManager, usbDevice), type.getPrinterDPI(), type.getPrinterWidth(), type.getMaxCharColumns());
                                    printer
                                            .printFormattedText(
                                                    textBuilder.build(type)
                                            );
                                    connectedDevice = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        };
        connect();
    }

    public boolean isConnectedDevice(){
        connect();
        if (usbConnection == null || usbManager== null) {
            return  false;
        }else{
            return  connectedDevice;
        }
    }


    public String getDeviceName(){
        connect();
        if (usbConnection == null || usbManager== null) {
            return  "Not Connected";
        }else{
            return  usbConnection.getDevice().getDeviceName();
        }
    }

    public void connect(){
        usbConnection = UsbPrintersConnections.selectFirstConnected(context);
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbConnection != null && usbManager != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
            );
            usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
            connectedDevice = true;
         }
    }

    public void print(){
        usbConnection = UsbPrintersConnections.selectFirstConnected(context);
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        if (usbConnection != null && usbManager != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    new Intent(ACTION_USB_PERMISSION),
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
            );
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            context.registerReceiver(this.usbReceiver, filter);
            usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
            connectedDevice = true;
        }
    }


    public void removeReceiver(){
        if(usbReceiver!=null) {
            if(usbReceiver.isOrderedBroadcast()){
                context.unregisterReceiver(usbReceiver);
            }
        }
    }
}