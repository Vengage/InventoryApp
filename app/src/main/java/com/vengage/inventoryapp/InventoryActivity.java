package com.vengage.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.vengage.inventoryapp.data.ProductContract.ProductEntry;
import com.vengage.inventoryapp.data.ProductCursorAdapter;

import java.io.ByteArrayOutputStream;

public class InventoryActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    // Loader ID
    private static final int PRODUCT_LOADER_ID = 1;
    // Adapter for products
    private ProductCursorAdapter mProductCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Display the products from the database
        ListView mProductsListView = (ListView) findViewById(R.id.list_view_products);

        // Set the empty view for the list
        View emptyView = findViewById(R.id.empty_view);
        mProductsListView.setEmptyView(emptyView);

        mProductsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent editProductIntent = new Intent(InventoryActivity.this, EditorActivity.class);
                // "content://com.vengage.inventoryapp.products/products/#"
                editProductIntent.setData(ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id));
                startActivity(editProductIntent);
            }
        });

        // Set the adapter to the listview
        mProductCursorAdapter = new ProductCursorAdapter(InventoryActivity.this, null);
        mProductsListView.setAdapter(mProductCursorAdapter);

        getSupportLoaderManager().initLoader(PRODUCT_LOADER_ID, null, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyProduct();
                return true;
            case R.id.action_delete_all_entries:
                deleteProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteProducts() {
        // Delete all of the products from the table
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        if (rowsDeleted != 0) {
            Toast.makeText(this, R.string.all_products_deleted, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.delete_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void insertDummyProduct() {
        // Get the bitmap from the resource
        Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(this, R.mipmap.ic_launcher)).getBitmap();
        // Get the byte array for the image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Product");
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, byteArray);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, "15");
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, "10");
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Products");
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, "contact@products.com");

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        // Show the user the insert response message
        if (newUri == null) {
            Toast.makeText(this, R.string.error_save_product, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.product_saved, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
        };
        return new CursorLoader(InventoryActivity.this, ProductEntry.CONTENT_URI, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Populate the list with data from the new cursor
        mProductCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Reset the list data
        mProductCursorAdapter.swapCursor(null);
    }
}
