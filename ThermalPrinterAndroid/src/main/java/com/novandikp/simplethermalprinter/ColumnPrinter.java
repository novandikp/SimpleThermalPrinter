package com.novandikp.simplethermalprinter;

public class ColumnPrinter {
    private String text;
    private AlignColumn alignColumn;


    public ColumnPrinter(String text) {
        this.text = text;
        alignColumn = AlignColumn.LEFT;
    }

    public ColumnPrinter(String text, AlignColumn alignColumn) {
        this.text = text;
        this.alignColumn = alignColumn;
    }

    public String getText() {
        return text;
    }

    public AlignColumn getAlignColumn() {
        return alignColumn;
    }
}