package com.vengage.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by Vengage on 7/26/2017.
 *
 * Product Contract
 */

public class ProductContract {
    // Private Constructor
    private ProductContract(){}

    // Class for products table columns name
    public static final class ProductEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "products";
        // Product unique ID
        public static final String _ID = BaseColumns._ID;
        // The name of the product
        public static final String COLUMN_PRODUCT_NAME = "name";
        // The image of the product
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        // The quantity of the product
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        // The price of the product
        public static final String COLUMN_PRODUCT_PRICE = "price";
        // The product supplier name
        public static final String COLUMN_SUPPLIER_NAME = "supplier_name";
        // The product supplier email
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
    }
}
