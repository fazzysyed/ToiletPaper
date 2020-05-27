package net.myerichsen.toiletpaper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.opencsv.CSVReader;

import net.myerichsen.toiletpaper.ui.products.ProductModel;
import net.myerichsen.toiletpaper.ui.suppliers.SupplierModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
 * Copyright (c) 2020. Michael Erichsen.
 *
 * The program is distributed under the terms of the GNU Affero General Public License v3.0
 */

/**
 * Database helper for product and supplier tables
 */
public class TPDbAdapter {
    private final TpDbHelper tpDbHelper;
    private final String[] pdColumns = {TpDbHelper.UID, TpDbHelper.LAYERS, TpDbHelper.PACKAGE_ROLLS,
            TpDbHelper.ROLL_SHEETS, TpDbHelper.SHEET_WIDTH, TpDbHelper.SHEET_LENGTH,
            TpDbHelper.SHEET_LENGTH_C, TpDbHelper.ROLL_LENGTH, TpDbHelper.ROLL_LENGTH_C,
            TpDbHelper.PACKAGE_PRICE, TpDbHelper.ROLL_PRICE, TpDbHelper.ROLL_PRICE_C,
            TpDbHelper.PAPER_WEIGHT, TpDbHelper.PAPER_WEIGHT_C,
            TpDbHelper.PACKAGE_WEIGHT, TpDbHelper.PACKAGE_WEIGHT_C,
            TpDbHelper.ROLL_WEIGHT, TpDbHelper.ROLL_WEIGHT_C,
            TpDbHelper.KILO_PRICE,
            TpDbHelper.KILO_PRICE_C, TpDbHelper.METER_PRICE, TpDbHelper.METER_PRICE_C,
            TpDbHelper.SHEET_PRICE, TpDbHelper.SHEET_PRICE_C, TpDbHelper.SUPPLIER,
            TpDbHelper.COMMENTS, TpDbHelper.ITEM_NO, TpDbHelper.BRAND, 
            TpDbHelper.TIME_STAMP};
    private final String[] sdColumns = {TpDbHelper.SUPPLIER, 
            TpDbHelper.CHAIN, TpDbHelper.TIME_STAMP};
    private final String[] countColumn = {"COUNT(*)"};

    public TPDbAdapter(Context context) {
        tpDbHelper = new TpDbHelper(context);
    }

    /**
     * Select from product table with selection arguments
     *
     * @param selection e.g. BRAND=?
     * @param column    Selection argument column
     * @return List of product models
     */
    public List<ProductModel> getProductModels(String selection, String column) {
        List<ProductModel> lpm = new ArrayList<>();

        SQLiteDatabase db = tpDbHelper.getReadableDatabase();

        String[] args = {column};
        Cursor cursor = db.query(TpDbHelper.TABLE_PRODUCT, pdColumns, selection, args, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                lpm.add(populateProductModel(cursor));
            }
        }
        cursor.close();

        return lpm;
    }

    /**
     * Select from product table with selection arguments ordered
     *
     * @param selection   e.g. BRAND=?
     * @param argColumn   Selection argument column
     * @param orderColumn Column to order by
     * @return List of product models
     */
    public List<ProductModel> getProductModels(String selection, String argColumn, String orderColumn) {
        List<ProductModel> lpm = new ArrayList<>();

        SQLiteDatabase db = tpDbHelper.getReadableDatabase();

        String[] args = {argColumn};
        Cursor cursor = db.query(TpDbHelper.TABLE_PRODUCT, pdColumns, selection, args, null, null, orderColumn);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                lpm.add(populateProductModel(cursor));
            }
        }
        cursor.close();

        return lpm;
    }

    /**
     * Select everything from product table with selection arguments ordered
     *
     * @param orderColumn Column to order by
     * @return List of product models
     */
    public List<ProductModel> getProductModels(String orderColumn) {
        List<ProductModel> lpm = new ArrayList<>();

        SQLiteDatabase db = tpDbHelper.getReadableDatabase();

        Cursor cursor = db.query(TpDbHelper.TABLE_PRODUCT, pdColumns, null, null, null, null, orderColumn);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                lpm.add(populateProductModel(cursor));
            }
        }
        cursor.close();

        return lpm;
    }

    /**
     * Insert a product row
     */
    public void insertData(ProductModel pm) {
        SQLiteDatabase db = tpDbHelper.getWritableDatabase();
        ContentValues contentValues = extractData(pm);
        db.insert(TpDbHelper.TABLE_PRODUCT, null, contentValues);
    }

    /**
     * Insert a supplier row
     */
    public void insertData(SupplierModel sm) {
        SQLiteDatabase db = tpDbHelper.getWritableDatabase();
        ContentValues contentValues = extractData(sm);
        db.insert(TpDbHelper.TABLE_SUPPLIER, null, contentValues);
    }

    /**
     * Select all data from product table
     *
     * @return List of columns in record
     */
    public List<ProductModel> getProductModels() {
        List<ProductModel> lpm = new ArrayList<>();

        SQLiteDatabase db = tpDbHelper.getReadableDatabase();

        Cursor cursor = db.query(TpDbHelper.TABLE_PRODUCT, pdColumns, null, 
                null, null, null, TpDbHelper.BRAND);

        while (cursor.moveToNext()) {
            lpm.add(populateProductModel(cursor));
        }
        cursor.close();
        return lpm;
    }

    /**
     * Do an initial load
     */
    public void doInitialLoad() throws Exception {
        tpDbHelper.loadInitialData();
    }

    /**
     * Get all data from supplier table
     *
     * @return List of supplier data
     */
    public List<SupplierModel> getSupplierModels() {
        List<SupplierModel> lsm = new ArrayList<>();

        SQLiteDatabase db = tpDbHelper.getReadableDatabase();

        Cursor cursor = db.query(TpDbHelper.TABLE_SUPPLIER, sdColumns, null, 
                null, null, null, TpDbHelper.SUPPLIER);

        while (cursor.moveToNext()) {
            lsm.add(populateSupplierModel(cursor));
        }

        cursor.close();
        return lsm;
    }

    /**
     * Extract supplier data into content value object
     *
     * @param sm Supplier model
     * @return ContentValues
     */
    private ContentValues extractData(SupplierModel sm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TpDbHelper.SUPPLIER, sm.getSupplier());
        contentValues.put(TpDbHelper.CHAIN, sm.getChain());
//        contentValues.put(TpDbHelper.TIME_STAMP, sm.getTimestamp());
        return contentValues;
    }

    /**
     * Extract product data into Content Value object
     *
     * @param pm Product data
     * @return ContentValues
     */
    private ContentValues extractData(ProductModel pm) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TpDbHelper.LAYERS, pm.getLayers());
        contentValues.put(TpDbHelper.PACKAGE_ROLLS, pm.getPackageRolls());
        contentValues.put(TpDbHelper.ROLL_SHEETS, pm.getRollSheets());
        contentValues.put(TpDbHelper.SHEET_WIDTH, pm.getSheetWidth());
        contentValues.put(TpDbHelper.SHEET_LENGTH, pm.getSheetLength());
        contentValues.put(TpDbHelper.SHEET_LENGTH_C, pm.getSheetLength_c());
        contentValues.put(TpDbHelper.ROLL_LENGTH, pm.getRollLength());
        contentValues.put(TpDbHelper.ROLL_LENGTH_C, pm.getRollLength_c());
        contentValues.put(TpDbHelper.PACKAGE_PRICE, pm.getPackagePrice());
        contentValues.put(TpDbHelper.ROLL_PRICE, pm.getRollPrice());
        contentValues.put(TpDbHelper.ROLL_PRICE_C, pm.getRollPrice_c());
        contentValues.put(TpDbHelper.PAPER_WEIGHT, pm.getPaperWeight());
        contentValues.put(TpDbHelper.PAPER_WEIGHT_C, pm.getPaperWeight_c());
        contentValues.put(TpDbHelper.PACKAGE_WEIGHT, pm.getPackageWeight());
        contentValues.put(TpDbHelper.PACKAGE_WEIGHT_C, pm.getPackageWeight_c());
        contentValues.put(TpDbHelper.ROLL_WEIGHT, pm.getRollWeight());
        contentValues.put(TpDbHelper.ROLL_WEIGHT_C, pm.getRollWeight_c());
        contentValues.put(TpDbHelper.KILO_PRICE, pm.getKiloPrice());
        contentValues.put(TpDbHelper.KILO_PRICE_C, pm.getKiloPrice_c());
        contentValues.put(TpDbHelper.METER_PRICE, pm.getMeterPrice());
        contentValues.put(TpDbHelper.METER_PRICE_C, pm.getMeterPrice_c());
        contentValues.put(TpDbHelper.SHEET_PRICE, pm.getSheetPrice());
        contentValues.put(TpDbHelper.SHEET_PRICE_C, pm.getSheetPrice_c());
        contentValues.put(TpDbHelper.SUPPLIER, pm.getSupplier());
        contentValues.put(TpDbHelper.COMMENTS, pm.getComments());
        contentValues.put(TpDbHelper.ITEM_NO, pm.getItemNo());
        contentValues.put(TpDbHelper.BRAND, pm.getBrand());
//        contentValues.put(TpDbHelper.TIME_STAMP, pm.getTimestamp());
        return contentValues;
    }

    /**
     * Populate supplier data from database table
     *
     * @param cursor Database cursor
     * @return Supplier data
     */
    private SupplierModel populateSupplierModel(Cursor cursor) {
        SupplierModel sm = new SupplierModel();
        sm.setSupplier(cursor.getString(cursor.getColumnIndex(TpDbHelper.SUPPLIER)));
        sm.setChain(cursor.getString(cursor.getColumnIndex(TpDbHelper.CHAIN)));
        sm.setTimestamp(cursor.getString(cursor.getColumnIndex(TpDbHelper.TIME_STAMP)));
        return sm;
    }

    /**
     * Populate product data from database table
     *
     * @param cursor Database cursor
     * @return product data
     */
    private ProductModel populateProductModel(Cursor cursor) {
        ProductModel pm = new ProductModel();
        pm.setUid(cursor.getInt(cursor.getColumnIndex(TpDbHelper.UID)));
        pm.setLayers(cursor.getInt(cursor.getColumnIndex(TpDbHelper.LAYERS)));
        pm.setPackageRolls(cursor.getInt(cursor.getColumnIndex(TpDbHelper.PACKAGE_ROLLS)));
        pm.setRollSheets(cursor.getInt(cursor.getColumnIndex(TpDbHelper.ROLL_SHEETS)));
        pm.setSheetWidth(cursor.getInt(cursor.getColumnIndex(TpDbHelper.SHEET_WIDTH)));
        pm.setSheetLength(cursor.getInt(cursor.getColumnIndex(TpDbHelper.SHEET_LENGTH)));
        pm.setSheetLength_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.SHEET_LENGTH_C)));
        pm.setRollLength(cursor.getInt(cursor.getColumnIndex(TpDbHelper.ROLL_LENGTH)));
        pm.setRollLength_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.ROLL_LENGTH_C)));
        pm.setPackagePrice(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.PACKAGE_PRICE)));
        pm.setRollPrice(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.ROLL_PRICE)));
        pm.setRollPrice_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.ROLL_PRICE_C)));
        pm.setPaperWeight(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.PAPER_WEIGHT)));
        pm.setPaperWeight_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.PAPER_WEIGHT_C)));
        pm.setPackageWeight(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.PACKAGE_WEIGHT)));
        pm.setPackageWeight_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.PACKAGE_WEIGHT_C)));
        pm.setRollWeight(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.ROLL_WEIGHT)));
        pm.setRollWeight_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.ROLL_WEIGHT_C)));
        pm.setKiloPrice(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.KILO_PRICE)));
        pm.setKiloPrice_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.KILO_PRICE_C)));
        pm.setMeterPrice(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.METER_PRICE)));
        pm.setMeterPrice_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.METER_PRICE_C)));
        pm.setSheetPrice(cursor.getFloat(cursor.getColumnIndex(TpDbHelper.SHEET_PRICE)));
        pm.setSheetPrice_c(cursor.getInt(cursor.getColumnIndex(TpDbHelper.SHEET_PRICE_C)));
        pm.setSupplier(cursor.getString(cursor.getColumnIndex(TpDbHelper.SUPPLIER)));
        pm.setComments(cursor.getString(cursor.getColumnIndex(TpDbHelper.COMMENTS)));
        pm.setItemNo(cursor.getString(cursor.getColumnIndex(TpDbHelper.ITEM_NO)));
        pm.setBrand(cursor.getString(cursor.getColumnIndex(TpDbHelper.BRAND)));
        pm.setTimestamp(cursor.getString(cursor.getColumnIndex(TpDbHelper.TIME_STAMP)));
        return pm;
    }

    public void deleteProduct(int uid) {
        SQLiteDatabase db = tpDbHelper.getWritableDatabase();
        String s = uid + "";
        String[] whereArgs = {s};
        db.delete(TpDbHelper.TABLE_PRODUCT, TpDbHelper.UID + " = ?", whereArgs);
    }

    public void deleteSupplier(String supplier) {
        SQLiteDatabase db = tpDbHelper.getWritableDatabase();
        String[] whereArgs = {supplier};
        db.delete(TpDbHelper.TABLE_SUPPLIER, TpDbHelper.SUPPLIER + " = ?", whereArgs);
    }

    public List<ProductModel> getProductModelsSorted(String sortKey, String sortFilter) {
        Cursor cursor;
        List<ProductModel> lpm = new ArrayList<>();
        SQLiteDatabase db = tpDbHelper.getReadableDatabase();

        if (sortFilter.equals("ALL")) {
            cursor = db.query(TpDbHelper.TABLE_PRODUCT, pdColumns, null, 
                    null, null, null, sortKey + " DESC");
        } else {
            String[] args = {sortFilter};
            cursor = db.query(TpDbHelper.TABLE_PRODUCT, pdColumns, "SUPPLIER=?", args, 
                    null, null, sortKey + " DESC");
        }

        while (cursor.moveToNext()) {
            lpm.add(populateProductModel(cursor));
        }

        cursor.close();

        return lpm;
    }

    public List<SupplierModel> getSupplierModels(String selection, String supplier) {
        List<SupplierModel> lsm = new ArrayList<>();

        SQLiteDatabase db = tpDbHelper.getReadableDatabase();

        String[] args = {supplier};
        Cursor cursor = db.query(TpDbHelper.TABLE_SUPPLIER, sdColumns, selection, args, 
                null, null, null);

        if (cursor.getCount() > 0) {
            if (cursor.moveToNext()) {
                lsm.add(populateSupplierModel(cursor));
            }
        }
        cursor.close();
        return lsm;
    }

    /**
     * Inner helper class
     */
    static class TpDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "TOILET_PAPER_DATABASE";
        private static final String TABLE_PRODUCT = "TABLE_PRODUCT";
        private static final String UID = "UID";
        private static final String LAYERS = "LAYERS";
        private static final String PACKAGE_ROLLS = "PACKAGE_ROLLS";
        private static final String ROLL_SHEETS = "ROLL_SHEETS";
        private static final String SHEET_WIDTH = "SHEET_WIDTH";
        private static final String SHEET_LENGTH = "SHEET_LENGTH";
        private static final String SHEET_LENGTH_C = "SHEET_LENGTH_C";
        private static final String ROLL_LENGTH = "ROLL_LENGTH";
        private static final String ROLL_LENGTH_C = "ROLL_LENGTH_C";
        private static final String PACKAGE_PRICE = "PACKAGE_PRICE";
        private static final String ROLL_PRICE = "ROLL_PRICE";
        private static final String ROLL_PRICE_C = "ROLL_PRICE_C";
        private static final String PAPER_WEIGHT = "PAPER_WEIGHT";
        private static final String PAPER_WEIGHT_C = "PAPER_WEIGHT_C";
        private static final String PACKAGE_WEIGHT = "PACKAGE_WEIGHT";
        private static final String PACKAGE_WEIGHT_C = "PACKAGE_WEIGHT_C";
        private static final String ROLL_WEIGHT = "ROLL_WEIGHT";
        private static final String ROLL_WEIGHT_C = "ROLL_WEIGHT_C";
        private static final String KILO_PRICE = "KILO_PRICE";
        private static final String KILO_PRICE_C = "KILO_PRICE_C";
        private static final String METER_PRICE = "METER_PRICE";
        private static final String METER_PRICE_C = "METER_PRICE_C";
        private static final String SHEET_PRICE = "SHEET_PRICE";
        private static final String SHEET_PRICE_C = "SHEET_PRICE_C";
        private static final String SUPPLIER = "SUPPLIER";
        private static final String COMMENTS = "COMMENTS";
        private static final String ITEM_NO = "ITEM_NO";
        private static final String BRAND = "BRAND";
        private static final String TIME_STAMP = "TIME_STAMP";
        private static final String TABLE_SUPPLIER = "TABLE_SUPPLIER";
        private static final String CHAIN = "CHAIN";
        private static final int DATABASE_Version = 5;    // Database Version
        private static final String CREATE_PRODUCT_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LAYERS + " INTEGER, " +
                PACKAGE_ROLLS + " INTEGER, " +
                ROLL_SHEETS + " INTEGER, " +
                SHEET_WIDTH + " INTEGER, " +
                SHEET_LENGTH + " INTEGER, " +
                SHEET_LENGTH_C + " INTEGER, " +
                ROLL_LENGTH + " NUMERIC, " +
                ROLL_LENGTH_C + " INTEGER, " +
                PACKAGE_PRICE + " NUMERIC, " +
                ROLL_PRICE + " NUMERIC, " +
                ROLL_PRICE_C + " INTEGER, " +
                PAPER_WEIGHT + " NUMERIC, " +
                PAPER_WEIGHT_C + " INTEGER, " +
                PACKAGE_WEIGHT + " NUMERIC, " +
                PACKAGE_WEIGHT_C + " INTEGER, " +
                ROLL_WEIGHT + " NUMERIC, " +
                ROLL_WEIGHT_C + " INTEGER, " +
                KILO_PRICE + " NUMERIC, " +
                KILO_PRICE_C + " INTEGER, " +
                METER_PRICE + " NUMERIC, " +
                METER_PRICE_C + " INTEGER, " +
                SHEET_PRICE + " NUMERIC, " +
                SHEET_PRICE_C + " INTEGER, " +
                SUPPLIER + " TEXT, " +
                COMMENTS + " TEXT, " +
                ITEM_NO + " TEXT, " +
                BRAND + " TEXT, " +
                TIME_STAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

        private static final String DROP_PRODUCT_TABLE = "DROP TABLE IF EXISTS " + TABLE_PRODUCT;
        private static final String CREATE_SUPPLIER_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_SUPPLIER +
                " (" + SUPPLIER + " TEXT PRIMARY KEY, " +
                CHAIN + " TEXT, " +
                TIME_STAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";

        private static final String DROP_SUPPLIER_TABLE = "DROP TABLE IF EXISTS " + TABLE_SUPPLIER;
        private final Context context;

        TpDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_SUPPLIER_TABLE);
            db.execSQL(CREATE_PRODUCT_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_PRODUCT_TABLE);
            db.execSQL(DROP_SUPPLIER_TABLE);
            onCreate(db);
        }

        private void loadInitialData() throws Exception {
            TPDbAdapter adapter = new TPDbAdapter(context);
            SQLiteDatabase db = getWritableDatabase();

            onCreate(db);

            Cursor cursor = db.query(TABLE_SUPPLIER, adapter.countColumn, null,
                    null, null, null, null, null);

            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));

                    if (count == 0) {
                        loadSuppliers(adapter);
                    } else {
                        throw new Exception("Butikstabel ikke indsat. Der er allerede " + count + " butikker");
                    }
                }
            }
            cursor.close();

            cursor = db.query(TABLE_PRODUCT, adapter.countColumn, null, 
                    null, null, null, null, null);
            if (cursor.getCount() > 0) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(cursor.getColumnIndex("COUNT(*)"));

                    if (count == 0) {
                        loadProducts(adapter);
                    } else {
                        throw new Exception("Produkttabel ikke indsat. Der er allerede " + count + " produkter");
                    }
                }
            }
            cursor.close();
        }

        /**
         * @param adapter The database adapter
         */
        private void loadProducts(TPDbAdapter adapter) {
            try {
                InputStream is = context.getAssets().open("products.csv");
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                CSVReader csvreader = new CSVReader(reader);
                List<String[]> csv = csvreader.readAll();
                ProductModel pm;
                String[] data;

                for (int i = 1; i < csv.size(); i++) {
                    data = csv.get(i);
                    pm = new ProductModel(
                            data[0],
                            data[1],
                            Integer.parseInt(data[2]),
                            Integer.parseInt(data[3]),
                            Integer.parseInt(data[4]),
                            Integer.parseInt(data[5]),
                            Integer.parseInt(data[6]),
                            Integer.parseInt(data[7]),
                            Float.parseFloat(data[8]),
                            Integer.parseInt(data[9]),
                            Float.parseFloat(data[10]),
                            Float.parseFloat(data[11]),
                            Integer.parseInt(data[12]),
                            Float.parseFloat(data[13]),
                            Integer.parseInt(data[14]),
                            Float.parseFloat(data[15]),
                            Integer.parseInt(data[16]),
                            Float.parseFloat(data[17]),
                            Integer.parseInt(data[18]),
                            Float.parseFloat(data[19]),
                            Integer.parseInt(data[20]),
                            Float.parseFloat(data[21]),
                            Integer.parseInt(data[22]),
                            Float.parseFloat(data[23]),
                            Integer.parseInt(data[24]),
                            data[25],
                            data[26]);
                    adapter.insertData(pm);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        private void loadProducts(TPDbAdapter adapter, int x) {
//            ProductModel pm;
//            pm = new ProductModel("5700384289095", "Irma Tusindfryd Toiletpapir",
//                    3, 8, 233, 97, 125, 0,
//                    (float) 29.1, 0, (float) 41.0, (float) 5.125, 1,
//                    (float) 0, 0, 48, 0,
//                    0, 0, (float) 31.64, 0,
//                    (float) 0.1761, 1, (float) 0.022, 1,
//                    "Kvickly Helsinge", "Produceret i Sverige");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("7311041080306", "First Price Toiletpapir 2-lags",
//                    2, 8, 220, 96, 125, 1,
//                    (float) 27.5, 0, 0, (float) 15.95, 0,
//                    (float) 1.99, 1,
//                    0, 0, 0, 0,
//                    36, 0,
//                    (float) 0.0725, 1,
//                    (float) 0.009, 1,
//                    "Spar Vejby Strand", "Produceret i Litauen");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("5705830002242", "REMA 1000 Toiletpapir",
//                    2, 8, 282, 97, 125, 0,
//                    (float) 35.25, 0, (float) 9.75, (float) 1.21875, 1,
//                    0, 0, (float) 32.6, 0, 0,
//                    0, (float) 10.93, 0, (float) 0.0346, 1, (float) 0.004322, 1,
//                    "Rema Vejby", "Produceret i Sverige");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("WW-166808", "Staples 29 m",
//                    2, 8, 250, 96, 115, 0,
//                    (float) 28.75, 0, (float) 24.94, (float) 3.12, 1,
//                    0, 0, (float) 16.50, 0,
//                    0, 0, (float) 188.94, 0,
//                    (float) 0.10843, 1, (float) 0.1247, 1,
//                    "Staples", "Online");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("WW-114649", "Tork Advanced Extra Soft T4",
//                    3, 42, 248, 94, 140, 1,
//                    (float) 34.70, 0, (float) 386.85, (float) 9.21, 1,
//                    0, 0, 0, 0,
//                    0, 0, 0, 0,
//                    (float) 0.26544, 1, (float) 0.03714, 1,
//                    "Staples", "Online");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("WW-101012", "Scott® Performance 350",
//                    3, 36, 350, 95, 125, 0, (float) 43.75, 0,
//                    (float) 589, (float) 9.21, 1,
//                    0, 0, 0, 0, 0, 0, 0, 0, (float) 0.46746, 1, (float) 0.48433, 1,
//                    "Staples", "Online");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Nemlig Plus",
//                    3, 8, 200, 96, 125, 1,
//                    (float) 25, 0, (float) 20.40, (float) 2.55, 1,
//                    0, 0, 45, 0, 0, 0, (float) 23.61, 1, (float) 0.102, 1, (float) 0.01275, 1,
//                    "nemlig.com", "Litauen");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Nemlig Basic",
//                    2, 8, 220, 96, 125, 1, (float) 27.5, 0,
//                    (float) 9.2, (float) 1.15, 1,
//                    0, 0, 36, 0, 0, 0, (float) 12.11, 0, (float) 0.041818182, 1, (float) 0.005227273, 1,
//                    "nemlig.com", "Litauen");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Lotus Comfort 8",
//                    3, 8, 155, 98, 125, 0, (float) 19.1, 0,
//                    (float) 22, (float) 2.75, 1, 0, 0,
//                    0, 0, 0, 0, (float) 29.06, 0,
//                    (float) 0.143979058, 1, (float) 0.017973856, 1,
//                    "nemlig.com", "");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("170190", "Lambi Classic 9",
//                    3, 9, 255, 0, 125, 1,
//                    (float) 31.9, 0, (float) 34.95, (float) 3.88, 1,
//                    0, 0, (float) 0, 0, 0,
//                    0, (float) 41.26, 0, (float) 0.1217, 1, (float) 0.01523, 1,
//                    "Rema Vejby", "Produceret i Sverige");
//            adapter.insertData(pm);
//
//            // New data
//
//            pm = new ProductModel("70225", "Budget", 2, 10, 0,
//                    0, 0, 0, (float) 0, 0, (float) 11.5, (float) 1.15,
//                    1, (float) 1, 0, (float) 1, 0, (float) 100, 1, (float) 11.5, 0, (float) 0, 0, (float) 0, 0, "Bilka", "Togo");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Coop Luxury", 4, 6, 0,
//                    0, 0, 0, (float) 0, 0, (float) 21.95, (float) 3.583335, 1, (float) 0, 0, (float) 0.804, 0, (float) 134, 1, (float) 27.3, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Irmas Blødt Toiletpapir", 5, 6, 0, 0, 0, 0, (float) 0, 0, (float) 34, (float) 5.66666666666667, 1, (float) 0, 0, (float) 0.733, 0, (float) 122.166666666667, 1, (float) 46.38, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Irmas Toiletpapir ", 3, 8, 0, 0, 0, 0, (float) 0, 0, (float) 22, (float) 2.75, 1, (float) 0, 0, (float) 0.771, 0, (float) 0.096375, 1, (float) 28.53, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Irmas Toiletpapir Ny Kvalitet", 3, 6, 0, 0, 0, 0, (float) 0, 0, (float) 36, (float) 6, 1, (float) 0, 0, (float) 0.835, 0, (float) 0.139166666666667, 1, (float) 43.11, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("171267", "Kompakt", 4, 8, 0, 0, 0, 0, (float) 0, 0, (float) 20, (float) 2.5, 1, (float) 0, 0, (float) 0.976, 0, (float) 0.122, 1, (float) 20.49, 0, (float) 0, 0, (float) 0, 0, "Rema Vejby", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("75532", "Kæmpekøb Luksus", 2, 24, 0, 0, 0, 0, (float) 0, 0, (float) 50, (float) 2.08333333333333, 1, (float) 0, 0, (float) 3, 0, (float) 0.125, 1, (float) 16.66, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("75111", "Lambi Classic 12", 3, 12, 0, 0, 0, 0, (float) 0, 0, (float) 39.95, (float) 3.32916666666667, 1, (float) 0, 0, (float) 1.16, 0, (float) 0.0966666666666667, 1, (float) 34.44, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("75533", "Lambi Decorated", 3, 18, 0, 0, 0, 0, (float) 0, 0, (float) 69.95, (float) 3.88611111111111, 1, (float) 0, 0, (float) 1.75, 0, (float) 0.0972222222222222, 1, (float) 39.97, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//            pm = new ProductModel("75534", "Lambi Extra Long", 3, 9, 0, 0, 0, 0, (float) 0, 0, (float) 35, (float) 3.88888888888889, 1, (float) 0, 0, (float) 1.31, 0, (float) 0.145555555555556, 1, (float) 26.71, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//            pm = new ProductModel("24951", "Lambi Sensitive", 4, 6, 0, 0, 0, 0, (float) 0, 0, (float) 34.95, (float) 5.825, 1, (float) 0, 0, (float) 0.731, 0, (float) 0.121833333333333, 1, (float) 47.81, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("70405", "Levevis ", 3, 8, 0, 0, 0, 0, (float) 0, 0, (float) 30, (float) 3.75, 1, (float) 0, 0, (float) 1085, 0, (float) 135.625, 1, (float) 27.64, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Lotus Comfort 8",
//                    3, 8, 155, 98, 125, 0, (float) 19.1, 0,
//                    (float) 35.95, (float) 4.49375, 1, (float) 0, 0,
//                    (float) 0.81, 0, (float) 0.10125, 1, (float) 44.38, 0,
//                    (float) 0, 0, (float) 0.028991936, 1, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("64865", "Lotus Comfort 16", 3, 16, 0, 0, 0, 0, (float) 0, 0, (float) 69.95, (float) 4.371875, 1,
//                    (float) 0, 0, (float) 1613, 0, (float) 100.8125, 1, (float) 43.37, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("73051", "Lotus Just1", 5, 12, 0, 0, 0, 0, (float) 0, 0, (float) 59.95, (float) 4.99583333333333, 1, (float) 0, 0, (float) 1.26, 0, (float) 0.105, 1, (float) 47.57, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("75535", "Lotus Royal", 3, 12, 0, 0, 0, 0, (float) 0, 0, (float) 46.95, (float) 39.125, 1, (float) 0, 0, (float) 1.14, 0, (float) 0.095, 1, (float) 41.18, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Nemlig Basic", 2, 8, 0, 0, 0, 0, (float) 27.5, 0, (float) 9.2, (float) 1.15, 1, (float) 36, 0, (float) 0, 0, (float) 0, 0, (float) 12.11, 0, (float) 0.041818182, 1, (float) 0.005227273, 1, " nemlig.com", " Litauen");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("170426", "Toiletpapir", 2, 8, 282, 97, 0, 0, (float) 35.2, 0, (float) 9.75, (float) 121.875, 1, (float) 32.6, 0, (float) 0.892, 0, (float) 111.5, 0, (float) 10.93, 0, (float) 0, 0, (float) 0, 0, "Rema Vejby", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("170209", "Toiletpapir Soft", 3, 6, 0, 0, 0, 0, (float) 0, 0, (float) 14, (float) 2.33333333333333, 1, (float) 0, 0, (float) 0.672, 0, (float) 0.112, 1, (float) 20.83, 0, (float) 0, 0, (float) 0, 0, "Rema Vejby", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("170016", "Toiletpapir Ultrasoft", 4, 10, 0, 0, 0, 0, (float) 21.8, 0, (float) 24, (float) 2.4, 1, (float) 62, 0, (float) 1.3, 0, (float) 130, 0, (float) 18.46, 0, (float) 0, 0, (float) 0, 0, "Rema Vejby", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("63981", "Vores Toiletpapir 3", 3, 10,
//                    0, 0, 0, 0, (float) 0, 0, (float) 25.5,
//                    (float) 2.55, 1, (float) 0, 0, (float) 1.3, 0,
//                    (float) 0.13, 1, (float) 196.15, 0, (float) 0, 0,
//                    (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("75536", "Vores Toiletpapir 4", 4, 10, 0, 0, 0, 0, (float) 0, 0, (float) 23.25, (float) 2.325, 1, (float) 0, 0, (float) 2, 0, (float) 0.2, 1, (float) 11.62, 0, (float) 0, 0, (float) 0, 0, "Bilka", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Xtra Toiletpapir", 2, 8, 0, 0, 0, 0, (float) 0, 0, (float) 9.5, (float) 11.875, 1, (float) 0, 0, (float) 0.664, 0, (float) 0.083, 1, (float) 14.31, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            pm = new ProductModel("?", "Änglamark", 3, 8, 0, 0, 0, 0, (float) 0, 0, (float) 37.95, (float) 474.375, 1, (float) 0, 0, (float) 1126, 0, (float) 140.75, 1, (float) 33.7, 0, (float) 0, 0, (float) 0, 0, "Kvickly Helsinge", " ");
//            adapter.insertData(pm);
//
//            // Test data for graph
//
//            try {
//                pm = new ProductModel("test", "test",
//                        3, 9, 255, 0, 125, 1,
//                        (float) 31.9, 0, (float) 34.95, (float) 3.88, 1,
//                        0, 0, (float) 0, 0, 0,
//                        0, (float) 41.26, 0, (float) 0.1217, 1, (float) 0.01523, 1,
//                        "Rema Vejby", "Produceret i Sverige");
//                adapter.insertData(pm);
//                TimeUnit.SECONDS.sleep(5);
//                pm = new ProductModel("test", "test",
//                        3, 9, 255, 0, 125, 1, (float) 31.9,
//                        0, (float) 38, (float) 3.88, 1, (float) 0, 0, 0, 0, 0,
//                        0, (float) 41.26, 0, (float) 0.1217, 1, (float) 0.01523, 1,
//                        "Rema Vejby", "Produceret i Sverige");
//                adapter.insertData(pm);
//                TimeUnit.SECONDS.sleep(1);
//                pm = new ProductModel("test", "test",
//                        3, 9, 255, 0, 125, 1, (float) 31.9,
//                        0, (float) 36, (float) 3.88, 1, (float) 0, 0, 0, 0, 0,
//                        0, (float) 41.26, 0, (float) 0.1217, 1, (float) 0.01523, 1,
//                        "Rema Vejby", "Produceret i Sverige");
//                adapter.insertData(pm);
//                TimeUnit.SECONDS.sleep(1);
//                pm = new ProductModel("test", "test",
//                        3, 9, 255, 0, 125, 1, (float) 31.9,
//                        0, (float) 39, (float) 3.88, 1, (float) 0, 0, 0, 0, 0,
//                        0, (float) 41.26, 0, (float) 0.1217, 1, (float) 0.01523, 1,
//                        "Rema Vejby", "Produceret i Sverige");
//                adapter.insertData(pm);
//                TimeUnit.SECONDS.sleep(1);
//                pm = new ProductModel("test", "test",
//                        3, 9, 255, 0, 125, 1, (float) 31.9,
//                        0, (float) 41, (float) 3.88, 1, (float) 0, 0, 0, 0, 0,
//                        0, (float) 41.26, 0, (float) 0.1217, 1, (float) 0.01523, 1,
//                        "Rema Vejby", "Produceret i Sverige");
//                adapter.insertData(pm);
//                TimeUnit.SECONDS.sleep(1);
//                pm = new ProductModel("test", "test",
//                        3, 9, 255, 0, 125, 1, (float) 31.9,
//                        0, (float) 39.95, (float) 3.88, 1, (float) 0, 0, 0, 0, 0,
//                        0, (float) 41.26, 0, (float) 0.1217, 1, (float) 0.01523, 1,
//                        "Rema Vejby", "Produceret i Sverige");
//                adapter.insertData(pm);
//                TimeUnit.SECONDS.sleep(1);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        private void loadSuppliers(TPDbAdapter tpHelper) {
            SupplierModel sm;
            sm = new SupplierModel("Bilka Hillerød", "Salling");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Føtex Hillerød", "Salling");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Kvickly Helsinge", "Coop");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Nemlig.com", "Nemlig");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Netto Vejby", "Salling");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Rema Vejby", "REMA 1000");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Staples", "Staples");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Spar Karsemose", "Dagrofa");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Spar Vejby Strand", "Dagrofa");
            tpHelper.insertData(sm);
            sm = new SupplierModel("SuperBest Allerød", "SuperBest");
            tpHelper.insertData(sm);
            sm = new SupplierModel("Superbrugsen Gilleleje", "Coop");
            tpHelper.insertData(sm);
        }
    }
}
