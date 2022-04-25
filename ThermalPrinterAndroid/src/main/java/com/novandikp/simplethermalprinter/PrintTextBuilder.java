package com.novandikp.simplethermalprinter;

import com.novandikp.simplethermalprinter.Type.Printer58mm;

public class PrintTextBuilder {
    private StringBuilder textBuilder;
    private TypePrinter printer;

    public PrintTextBuilder(TypePrinter typePrinter){
        printer = typePrinter;
        textBuilder = new StringBuilder();
    }

    public  PrintTextBuilder(){
        textBuilder = new StringBuilder();
        printer = Printer58mm.device();
    }


    public String setBold(String text){
        return "<b>"+text+"</b>";
    }

    public String setUnderline(String text){
        return "<u>"+text+"</u>";
    }


    public String setBigSize(String text){
        return "<font size='big'>"+text+"</font>";
    }

    public void setCenterAlign(){
        String centerAlign = "[C]";
        textBuilder.append(centerAlign);
    }
    public void setRightAlign(){
        String rightAlign = "[R]";
        textBuilder.append(rightAlign);
    }

    public void setLeftAlign(){
        String leftAlign = "[L]";
        textBuilder.append(leftAlign);
    }

    public void addEnter(){
        String enter = "\n";
        textBuilder.append(enter);
    }

    public void addDivider(){

        StringBuilder divider = new StringBuilder("==============================");
        if(printer != null){
            divider = new StringBuilder();
            for (int i = 0; i < printer.getMaxCharColumns(); i++) {
                divider.append("=");
            }
        }
        setCenterAlign();
        textBuilder.append(divider.toString());
        addEnter();
    }

    public void addTitle(String title){
        setCenterAlign();
        textBuilder.append(setBold(title));
        addEnter();
    }


    public void addText(String text){
        setLeftAlign();
        textBuilder.append(text);
        addEnter();
    }

    public void addText(String text, AlignColumn alignColumn){
        switch (alignColumn){
            case RIGHT:
                setRightAlign();
                break;
            case CENTER:
                setCenterAlign();
                break;
            default:
                setLeftAlign();
        }
        textBuilder.append(text);
        addEnter();

    }


    private String trimText(String value,int maxLength){

        value = value.trim();
        if(value.length()>=maxLength){
            value = value.substring(0, maxLength);
            int lastSpaceIndex = value.lastIndexOf(" ");
            if (lastSpaceIndex == -1) {
                value = value.substring(0, maxLength - 3) + "...";
            } else {
                value = value.substring(0, lastSpaceIndex);
            }
        }
        return value;
    }

    public void addTextPair(String key, String value,AlignColumn alignColumn){
        int maxLength = printer.getMaxCharColumns() - (key.length() +3);
        switch (alignColumn){
            case RIGHT:
                setRightAlign();
                break;
            case CENTER:
                setCenterAlign();
                break;
            default:
                setLeftAlign();
        }
        textBuilder.append(key);
        textBuilder.append(" : ");
        textBuilder.append(trimText(value,maxLength));
        addEnter();
    }

    public void addTextPair(String key, String value){

        int maxLength = printer.getMaxCharColumns() - (key.length() +3);
        setLeftAlign();
        textBuilder.append(key);
        textBuilder.append(" : ");
        textBuilder.append(trimText(value,maxLength));
        addEnter();
    }

    public void addColumn(ColumnPrinter... columns){
        for (ColumnPrinter columnPrinter:columns) {
            switch (columnPrinter.getAlignColumn()){
                case RIGHT:
                    setRightAlign();
                    break;
                case CENTER:
                    setCenterAlign();
                    break;
                default:
                    setLeftAlign();
            }
            textBuilder.append(columnPrinter.getText());
        }
        addEnter();
    }


    public String build(){
        return textBuilder.toString();
    }

}


