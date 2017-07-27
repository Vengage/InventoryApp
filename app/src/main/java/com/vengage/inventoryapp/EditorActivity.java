package com.vengage.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vengage.inventoryapp.data.ProductContract;
import com.vengage.inventoryapp.data.ProductContract.ProductEntry;
import com.vengage.inventoryapp.data.ProductDbHelper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity {

    // Request read permissions to storage
    private final int REQUEST_READ_EXTERNAL_STORAGE = 501;
    // Product image
    private ImageView mProductImage;
    // Product empty text view
    private TextView mEmptyViewProductImage;
    // Product name
    private EditText mProductName;
    // Product price
    private EditText mProductPrice;
    // Product quantity
    private EditText mProductQuantity;
    // Product quantity increase button
    private Button mProductQuantityIncrease;
    // Product quantity decrease button
    private Button mProductQuantityDecrease;
    // Product supplier name
    private EditText mProductSupplierName;
    // Product supplier email
    private EditText mProductSupplierEmail;
    // Button for ordering more products
    private Button mOrderProduct;
    // Read image for gallery onClickListener
    private View.OnClickListener readImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ActivityCompat.requestPermissions(
                    EditorActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_STORAGE
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductImage = (ImageView) findViewById(R.id.product_image);
        mEmptyViewProductImage = (TextView) findViewById(R.id.mo_product_image_text_view);
        mProductName = (EditText) findViewById(R.id.edit_product_name);
        mProductPrice = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        mProductQuantityIncrease = (Button) findViewById(R.id.increase_product_quantity);
        mProductQuantityDecrease = (Button) findViewById(R.id.decrease_product_quantity);
        mProductSupplierName = (EditText) findViewById(R.id.edit_product_supplier_name);
        mProductSupplierEmail = (EditText) findViewById(R.id.edit_product_supplier_email);
        mOrderProduct = (Button) findViewById(R.id.product_order_supplier);

        // Increase value of the product quantity when the user presses the increase button
        mProductQuantityIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productQuantity = mProductQuantity.getText().toString().trim();
                // If the quantity edit text is empty we set the text to 1
                if (TextUtils.isEmpty(productQuantity)) {
                    mProductQuantity.setText("1");
                    // Increase the quantity by 1 if it less than 99
                } else if (Integer.parseInt(productQuantity) < 99) {
                    productQuantity = String.valueOf(Integer.parseInt(productQuantity) + 1);
                    mProductQuantity.setText(productQuantity);
                }
            }
        });
        // Decrease value of the product quantity when the user presses the decrease button
        mProductQuantityDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productQuantity = mProductQuantity.getText().toString().trim();
                // If the quantity is empty we set the text to 0
                if (TextUtils.isEmpty(productQuantity)) {
                    mProductQuantity.setText("0");
                }
                // Else decrease the the quantity by 1 if it is greater than 0
                else if (Integer.parseInt(productQuantity) > 0) {
                    productQuantity = String.valueOf(Integer.parseInt(productQuantity) - 1);
                    mProductQuantity.setText(productQuantity);
                }
            }
        });

        // Order product
        mOrderProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productSupplierEmail = mProductSupplierEmail.getText().toString().trim();
                // If the email is empty
                if (TextUtils.isEmpty(productSupplierEmail)) {
                    Toast.makeText(EditorActivity.this, "Product supplier email address is not set",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // If the email address is not valid
                if (!Patterns.EMAIL_ADDRESS.matcher(productSupplierEmail).matches()) {
                    Toast.makeText(EditorActivity.this, "Product supplier email address is not valid",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // Get the product name for email subject
                String productName = mProductName.getText().toString().trim();
                // Test product name is not empty
                if (TextUtils.isEmpty(productName)) {
                    Toast.makeText(EditorActivity.this,
                            "Product name is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                // If all of the above conditions are met create an email intent
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + productSupplierEmail));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[Order] Product: " + productName);
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Need an order for " + productName + ".");

                startActivity(Intent.createChooser(emailIntent, "Send Email"));
            }
        });

        // Select an image for the product
        mEmptyViewProductImage.setOnClickListener(readImageClickListener);
        mProductImage.setOnClickListener(readImageClickListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    // Permission denied
                    Toast.makeText(EditorActivity.this,
                            "You do not have permission read files permission",
                            Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE
                && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                // Set the image to the ImageView
                if (mProductImage.getVisibility() == View.GONE) {
                    mProductImage.setImageBitmap(bitmap);
                    mProductImage.setVisibility(View.VISIBLE);
                    mEmptyViewProductImage.setVisibility(View.GONE);
                } else {
                    mProductImage.setImageBitmap(bitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                // Save data for the product
                saveProduct();
                finish();
                return true;
            case R.id.action_delete:
                // Delete the product
                getProduct();
                return true;
            case android.R.id.home:
                // Go to parent activity
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getProduct() {
        SQLiteOpenHelper mDbHelper = new ProductDbHelper(EditorActivity.this);
        SQLiteDatabase productDb = mDbHelper.getReadableDatabase();
        Cursor cursor = productDb.query(ProductEntry.TABLE_NAME,
                new String[]{ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_EMAIL},
                null, null,
//                ProductEntry._ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        cursor.moveToLast();

        String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        byte[] productImage = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));
        String productPrice = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        String productQuantity =cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String productSupplierName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME));
        String productSupplierEmail = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL));

        Bitmap bitmap = BitmapFactory.decodeByteArray(productImage, 0, productImage.length);
        mProductImage.setImageBitmap(bitmap);
        mProductImage.setVisibility(View.VISIBLE);
        mEmptyViewProductImage.setVisibility(View.GONE);
        mProductName.setText(productName);
        mProductPrice.setText(productPrice);
        mProductQuantity.setText(productQuantity);
        mProductSupplierName.setText(productSupplierName);
        mProductSupplierEmail.setText(productSupplierEmail);
    }

    private boolean saveProduct(){
        // The product has been successfully saved
        String productName = mProductName.getText().toString().trim();
        String productPrice = mProductPrice.getText().toString().trim();
        String productQuantity = mProductQuantity.getText().toString().trim();
        String productSupplierName = mProductSupplierName.getText().toString().trim();
        String productSupplierEmail = mProductSupplierEmail.getText().toString().trim();


        SQLiteOpenHelper mDbHelper = new ProductDbHelper(EditorActivity.this);
        SQLiteDatabase productDb = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageViewToByteArray(mProductImage));
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, productSupplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, productSupplierEmail);

        long rowsAdded = productDb.insert(ProductEntry.TABLE_NAME, ProductEntry.COLUMN_PRODUCT_IMAGE,
                values);

        // Show a message to the user
        if(rowsAdded != -1){
            Toast.makeText(this, "Product saved", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(this, "Insert error", Toast.LENGTH_SHORT).show();
        }

        Log.v("EditorActivit", "Rows added: " + String.valueOf(rowsAdded));
        return true;
    }

    // Function that converts an imageView into a byte array in order to store it into the database
    private byte[] imageViewToByteArray(ImageView image){
        // Get the bitmap image from the imageView
        Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
        // Get the byte array for the image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // Return the result
        return byteArray;
    }
}
