package net.myerichsen.toiletpaper.ui.suppliers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to encapsulate all toilet paper supplier data
 */
public class SupplierData {
    private String supplier = "";
    private String chain = "";
    private String timestamp;

    /**
     * no-arg constructor
     */
    public SupplierData() {
        Long tsLong = System.currentTimeMillis();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
    }

    /**
     * Constructor
     *
     * @param supplier
     * @param uri
     */
    public SupplierData(String supplier, String uri) {
        this.supplier = supplier;
        this.chain = uri;
        Long tsLong = System.currentTimeMillis();
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
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
