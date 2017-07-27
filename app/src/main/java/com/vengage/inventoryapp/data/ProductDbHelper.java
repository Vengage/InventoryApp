package com.vengage.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.vengage.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Vengage on 7/27/2017.
 *
 * ProductDbHelper class
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    // Database version
    private static final int DATABASE_VERSION = 1;
    // Database name
    private static final String DATABASE_NAME = "productsDB";

    // SQL string constants. Used to not make mistakes
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String AUTOINCREMENT = " AUTOINCREMENT";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String NOT_NULL = " NOT NULL";
    private static final String DEFAULT = " DEFAULT";
    private static final String COMMA_SEP = ",";

    // SQL string to create the products table
    private static final String SQL_CREATE_TABLE_PRODUCTS = "CREATE TABLE "
            + ProductEntry.TABLE_NAME + "( " + ProductEntry._ID + INTEGER_TYPE + PRIMARY_KEY + AUTOINCREMENT + COMMA_SEP
            + ProductEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP
            + ProductEntry.COLUMN_PRODUCT_IMAGE + BLOB_TYPE + COMMA_SEP
            + ProductEntry.COLUMN_PRODUCT_PRICE + TEXT_TYPE + NOT_NULL + COMMA_SEP
            + ProductEntry.COLUMN_PRODUCT_QUANTITY + TEXT_TYPE + NOT_NULL + COMMA_SEP
            + ProductEntry.COLUMN_SUPPLIER_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP
            + ProductEntry.COLUMN_SUPPLIER_EMAIL + TEXT_TYPE + NOT_NULL + ");";

    // SQL string to drop the products table
    private static final String SQL_DROP_TABLE_PRODUCTS = "DROP TABLE " + ProductEntry.TABLE_NAME + ";";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP_TABLE_PRODUCTS);
        onCreate(db);
    }
}
