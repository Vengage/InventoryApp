package com.vengage.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import static com.vengage.inventoryapp.data.ProductContract.PATH_PRODUCTS;
import static com.vengage.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Vengage on 7/27/2017.
 *
 * ProductProvider class
 */

public class ProductProvider extends ContentProvider {

    // PRODUCTS case Uri code
    private static final int PRODUCTS = 1000;
    // PRODUCT_ID case Uri code
    private static final int PRODUCT_ID = 1001;
    // UriMatcher
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // Asociate Uri with the corresponding codes
        // "content://com.vengage.inventoryapp.products/products" to PRODUCTS 1000
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        // "content://com.vengage.inventoryapp.products/products/#" to PRODUCT_ID 1001
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    // Database helper
    private ProductDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Initialize mDbHelper
        mDbHelper = new ProductDbHelper(getContext());

        return true;
    }

    /**
     * Query database
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(ProductEntry.TABLE_NAME, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Can not query unknown URI: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insert is not supported for the given Uri: " + uri);
        }
    }

    /**
     * Insert a new product into the database
     */
    private Uri insertProduct(Uri uri, ContentValues contentValues) {
        // First we check that all of the required fields are set before insert
        // Check if product name was set
        String productName = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if(TextUtils.isEmpty(productName)){
            throw new IllegalArgumentException("Product requires a name");
        }
        // Check if the product Image was set
        byte[] productImage = contentValues.getAsByteArray(ProductEntry.COLUMN_PRODUCT_IMAGE);
        if(productImage.length == 0){
            throw new IllegalArgumentException("Product requires an image");
        }
        // Check if the product price was set
        String productPrice = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
        if(TextUtils.isEmpty(productPrice)){
            throw new IllegalArgumentException("Product requires a price");
        }
        // Check if the product quantity was set
        String productQuantity = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if(TextUtils.isEmpty(productQuantity)){
            throw new IllegalArgumentException("Product quantity needs to be set");
        }
        // Check if the product supplier name was set
        String productSupplierName = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
        if(TextUtils.isEmpty(productSupplierName)){
            throw new IllegalArgumentException("Product supplier name needs to be set");
        }
        // Check if the product supplier email address was set
        String productSupplierEmail = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);
        if(TextUtils.isEmpty(productSupplierEmail)){
            throw new IllegalArgumentException("Product supplier email address needs to be set");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ProductEntry.TABLE_NAME, null, contentValues);

        // Notify data changed
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Delete data from database
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Return value for the delete operation
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
        if(rowsDeleted != 0){
            // Rows were deleted. Notify that the data has changed
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    /**
     * Update data from database
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        // First we check that all of the required fields are set before update
        // Check if product name was set
        if(contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String productName = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(productName)) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }
        // Check if the product Image was set
        if(contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_IMAGE)) {
            byte[] productImage = contentValues.getAsByteArray(ProductEntry.COLUMN_PRODUCT_IMAGE);
            if (productImage.length == 0) {
                throw new IllegalArgumentException("Product requires an image");
            }
        }
        // Check if the product price was set
        if(contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            String productPrice = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (TextUtils.isEmpty(productPrice)) {
                throw new IllegalArgumentException("Product requires a price");
            }
        }
        // Check if the product quantity was set
        if(contentValues.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            String productQuantity = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (TextUtils.isEmpty(productQuantity)) {
                throw new IllegalArgumentException("Product quantity needs to be set");
            }
        }
        // Check if the product supplier name was set
        if(contentValues.containsKey(ProductEntry.COLUMN_SUPPLIER_NAME)) {
            String productSupplierName = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_NAME);
            if (TextUtils.isEmpty(productSupplierName)) {
                throw new IllegalArgumentException("Product supplier name needs to be set");
            }
        }
        // Check if the product supplier email address was set
        if(contentValues.containsKey(ProductEntry.COLUMN_SUPPLIER_EMAIL)) {
            String productSupplierEmail = contentValues.getAsString(ProductEntry.COLUMN_SUPPLIER_EMAIL);
            if (TextUtils.isEmpty(productSupplierEmail)) {
                throw new IllegalArgumentException("Product supplier email address needs to be set");
            }
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // Notify that the data has changed
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
