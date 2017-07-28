package com.vengage.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Vengage on 7/26/2017.
 * <p>
 * Product Contract
 */

public class ProductContract {
    // Product content provider authority
    public static final String CONTENT_AUTHORITY = "com.vengage.inventoryapp.products";
    // Base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Products table path
    public static final String PATH_PRODUCTS = "products";

    // Private Constructor
    private ProductContract() {
    }

    // Class for products table columns name
    public static final class ProductEntry implements BaseColumns {
        // Content Uri
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
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
