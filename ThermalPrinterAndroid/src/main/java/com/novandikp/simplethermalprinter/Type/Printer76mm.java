package com.novandikp.simplethermalprinter.Type;

import com.novandikp.simplethermalprinter.TypePrinter;

public class Printer76mm implements TypePrinter
{
    public static Printer76mm printer76mm;

    public static Printer76mm device(){
        if(Printer76mm.printer76mm == null){
            Printer76mm.printer76mm = new Printer76mm();
        }
        return  Printer76mm.printer76mm;
    }

    @Override
    public float getPrinterWidth() {
        return 66f;
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