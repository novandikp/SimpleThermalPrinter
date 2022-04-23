package com.novandikp.printertesting;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.printertesting.R;
import com.novandikp.simplethermalprinter.AlignColumn;
import com.novandikp.simplethermalprinter.Bluetooth.PrinterBTContext;
import com.novandikp.simplethermalprinter.ColumnPrinter;
import com.novandikp.simplethermalprinter.PrintTextBuilder;
import com.novandikp.simplethermalprinter.USB.PrinterUSBContext;

public class MainActivity extends AppCompatActivity {
    final int PERMISSION_BLUETOOTH = 10;
    final int PERMISSION_BLUETOOTH_ADMIN = 11;
    final int PERMISSION_BLUETOOTH_CONNECT = 12;
    final int PERMISSION_BLUETOOTH_SCAN = 13;
    final int PERMISSION_BLUETOOTH_ENABLED = 14;

    PrintTextBuilder resultPrint;
    PrinterUSBContext printerUSBContext;
    PrinterBTContext printerBTContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    //    Build Struk
    public void setResultPrinter() {
        resultPrint.addTitle("Faktur Penjualan");
        resultPrint.addDivider();
        resultPrint.addTextPair("Pelanggan","Novandi Kevin Pratama");
        resultPrint.addTextPair("Faktur","000001");
        resultPrint.addDivider();
        for (int i = 0; i < 3; i++) {
            ColumnPrinter columnNama = new ColumnPrinter("Baju Korea Keren");
            ColumnPrinter columnSubHarga = new ColumnPrinter("12.000", AlignColumn.RIGHT);
            resultPrint.addColumn(columnNama, columnSubHarga);
            resultPrint.addText("x2");
            resultPrint.addEnter();
        }
        resultPrint.addDivider();
        resultPrint.addTextPair("Total","40.000", AlignColumn.RIGHT);
    }


    //Inisialisi variabel
    public void initialize() {
        resultPrint = new PrintTextBuilder();
        setResultPrinter();
        printerUSBContext = PrinterUSBContext.getInstance(this, resultPrint);
        printerBTContext = PrinterBTContext.getInstance(this, resultPrint);
        requestPermission();
    }

    public void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
        } else {
            if (!printerBTContext.isEnabled()) {
                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), PERMISSION_BLUETOOTH_ENABLED);
            }else{

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSION_BLUETOOTH_ENABLED && resultCode == RESULT_OK) {
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        printerUSBContext.removeReceiver();
    }

    public void printText(View view) {
        printerUSBContext.connect();
        printerUSBContext.print();
    }

    public void pindah(View view) {
        startActivity(new Intent(this,ScanDevice.class));
    }
}