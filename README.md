# Simple Thermal Printer

Android Library Based ESC/POS Thermal Printer

Based : [ESC/POS Thermal Printer](https://github.com/DantSu/ESCPOS-ThermalPrinter-Android)


## Installation


**Step 1.** Add the [JitPack](https://jitpack.io/com/github/novandikp/SimpleThermalPrinter/v.1.0) repository to your build file. Add it in your root `/build.gradle` at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

**Step 2.** Add the dependency in `/app/build.gradle` :

```
dependencies {
    ...
    implementation 'com.github.novandikp:SimpleThermalPrinter:v.1.0'
}
```

## Print Builder

In making a receipt or other required several formats. With this library you can create easily.

### Class PrintTextBuilder

**Step 1.** Initialize Class
```java
    PrintTextBuilder resultPrint = new PrintTextBuilder();
```

**Step 2.** Add Text
```java
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
```

We can add Title, Text, Text Pair, Divider, and Table with method of `PrintTextBuilder`

## Bluetooth

### Bluetooth permission

Be sure your project has permission in `AndroidManifest.xml`
1. `android.permission.BLUETOOTH`
2. `android.permission.BLUETOOTH_ADMIN`
3. `android.permission.BLUETOOTH_CONNECT`
4. `android.permission.BLUETOOTH_SCAN`
5. `android.permission.ACCESS_COARSE_LOCATION`
6. `android.permission.ACCESS_FINE_LOCATION`

This sample code for requsting permission

`AndroidManifest.xml`
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.printertesting">
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
   <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
    <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
    <uses-feature android:name="android.hardware.bluetooth" android:required="false" />
    <uses-feature android:name="android.hardware.bluetooth_le" android:required="false" />

```

Request Permission
```java
 if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION);
}else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
}else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
} else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
} else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
} else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
} else {
        // Code
}
```
### Initialize Bluetooth Printer Class
```java
    PrinterBTContext printerBTContext = PrinterBTContext.getInstance(this, resultPrint);
```

To initialize need parameter Context and PrintTextBuilder

### Scan Bluetooth

```java
printerBTContext.discoveryDevice(new PrinterBTContext.OnScanDevice() {
    @Override
    public void onScanStart() {
        //  Code when start example loading dialog showing
    }

    @Override
    public void onScanCompleted() {
        //  Code when scan is complete example close loading dialog
    }
});
```

### Get Bluetooth Printer

```java
printerBTContext.getListBluetoothDevice()
```

`getListBluetoothDevice()` will return `List<DeviceBT>`

`DeviceBT` has 3 property :
1. String name
2. String address
3. BluetoothConnection connection


### Connect Printer
```java
printerBTContext.connectDevice(deviceBT,new PrinterBTContext.OnConnect() {
    @Override
    public void onConnect(State_Bluetooth state_bt) {
        // Code

    }
});
```


`State_Bluetooth` is enum 
- NONE
- CONNECTED
- PAIRED
- BOUNDING
- ERROR_BOUNDED
- NOTCONNECTED

### Another Function of PrinterBTContext

1. `getDeviceName()`

   Return Device Name Connected

2. `print()`
   
   For Print Text

3. `isEnabled()`
   
   Return is Bluetooth Enabled or Disabled

4. `isConnectedDevice()`
   
   Return is device connected