package com.novandikp.simplethermalprinter.Type;

import com.novandikp.simplethermalprinter.TypePrinter;

public class Printer58mm implements TypePrinter
{
    public static Printer58mm printer58mm;

    public static Printer58mm device(){
        if(Printer58mm.printer58mm == null){
            Printer58mm.printer58mm = new Printer58mm();
        }
        return  Printer58mm.printer58mm;
    }

    @Override
    public float getPrinterWidth() {
        return 48f;
    }

    @Override
    public int getPrinterDPI() {
        return 231;
    }

    @Override
    public int getMaxCharColumns() {
        return 32;
    }
}