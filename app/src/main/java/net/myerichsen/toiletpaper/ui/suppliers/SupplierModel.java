package net.myerichsen.toiletpaper.ui.suppliers;
/*
 * Copyright (c) 2020. Michael Erichsen.
 */

import java.text.SimpleDateFormat;

/**
 * Class to encapsulate all toilet paper supplier data
 */
public class SupplierModel {
    private String supplier = "";
    private String chain = "";
    private String timestamp;

    /**
     * no-arg constructor
     */
    public SupplierModel() {
        Long tsLong = System.currentTimeMillis();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(tsLong);
    }

    /**
     * Constructor
     */
    public SupplierModel(String supplier, String chain) {
        this.supplier = supplier;
        this.chain = chain;
        Long tsLong = System.currentTimeMillis();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(tsLong);
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getChain() {
        return chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
