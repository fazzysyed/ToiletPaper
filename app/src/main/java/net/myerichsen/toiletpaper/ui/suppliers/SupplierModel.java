package net.myerichsen.toiletpaper.ui.suppliers;
/*
 * Copyright (c) 2020. Michael Erichsen.
 *
 * The program is distributed under the terms of the GNU Affero General Public License v3.0
 */

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
    }

    /**
     * Constructor
     */
    public SupplierModel(String supplier, String chain) {
        this.supplier = supplier;
        this.chain = chain;
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
