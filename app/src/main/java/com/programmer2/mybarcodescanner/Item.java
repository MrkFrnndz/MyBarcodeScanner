package com.programmer2.mybarcodescanner;

/**
 * Created by PROGRAMMER2 on 5/2/2017.
 */
public class Item {
    private int id;
    private String barcode;
    private String description;

    public Item(int id, String barcode, String description) {
        this.id = id;
        this.barcode = barcode;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    Item(){

    }
}
