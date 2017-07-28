package com.vengage.inventoryapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vengage.inventoryapp.data.ProductContract.ProductEntry;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {
    // Loader ID
    private static final int EDITOR_LOADER_ID = 2;
    // Request read permissions to storage
    private final int REQUEST_READ_EXTERNAL_STORAGE = 501;
    // Product image
    private ImageView mProductImage;
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
    // Product URI
    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        mProductImage = (ImageView) findViewById(R.id.product_image);
        mProductName = (EditText) findViewById(R.id.edit_product_name);
        mProductPrice = (EditText) findViewById(R.id.edit_product_price);
        mProductQuantity = (EditText) findViewById(R.id.edit_product_quantity);
        mProductQuantityIncrease = (Button) findViewById(R.id.increase_product_quantity);
        mProductQuantityDecrease = (Button) findViewById(R.id.decrease_product_quantity);
        mProductSupplierName = (EditText) findViewById(R.id.edit_product_supplier_name);
        mProductSupplierEmail = (EditText) findViewById(R.id.edit_product_supplier_email);
        mOrderProduct = (Button) findViewById(R.id.product_order_supplier);

        // Verify if data has changed
        mProductImage.setOnTouchListener(mTouchListener);
        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mProductQuantityIncrease.setOnTouchListener(mTouchListener);
        mProductQuantityDecrease.setOnTouchListener(mTouchListener);
        mProductSupplierEmail.setOnTouchListener(mTouchListener);
        mProductSupplierName.setOnTouchListener(mTouchListener);

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
        mProductImage.setOnClickListener(readImageClickListener);

        // If it is Edit product
        Intent editIntent = getIntent();
        // Set the product Uri
        mCurrentProductUri = editIntent.getData();
        if (mCurrentProductUri != null) {
            // Edit product
            setTitle(getString(R.string.title_edit_product));
            getSupportLoaderManager().initLoader(EDITOR_LOADER_ID, null, this).forceLoad();
        } else {
            // Add a product
            setTitle(getString(R.string.title_add_product));
            // Set the default image
            mProductImage.setImageResource(R.drawable.no_product_image);
            mProductImage.setTag(Boolean.valueOf(false));
            // Hide the delete menu item
            invalidateOptionsMenu();
        }
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
                            "You do not have permission to read from external storage",
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
                mProductImage.setImageBitmap(bitmap);
                mProductImage.setTag(Boolean.valueOf(true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
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
                if (saveProduct()) finish();
                return true;
            case R.id.action_delete:
                // Delete the product
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // Go to parent activity
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveProduct() {
        // The product has been successfully saved
        String productName = mProductName.getText().toString().trim();
        String productPrice = mProductPrice.getText().toString().trim();
        String productQuantity = mProductQuantity.getText().toString().trim();
        String productSupplierName = mProductSupplierName.getText().toString().trim();
        String productSupplierEmail = mProductSupplierEmail.getText().toString().trim();

        // If nothing has changed close activity
        if (TextUtils.isEmpty(productName)
                && (mCurrentProductUri == null && !((Boolean) mProductImage.getTag()))
                && TextUtils.isEmpty(productPrice)
                && TextUtils.isEmpty(productQuantity)
                && TextUtils.isEmpty(productSupplierName)
                && TextUtils.isEmpty(productSupplierEmail)) {
            return true;
        }

        // Check product name
        if (TextUtils.isEmpty(productName)) {
            Toast.makeText(EditorActivity.this, "Empty product name", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check product price
        if (TextUtils.isEmpty(productPrice)) {
            Toast.makeText(EditorActivity.this, "Empty product price", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check product quantity
        if (TextUtils.isEmpty(productQuantity)) {
            Toast.makeText(EditorActivity.this, "Empty product quantity", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check product supplier email
        if (TextUtils.isEmpty(productSupplierEmail)) {
            Toast.makeText(EditorActivity.this, "Empty product supplier email", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check product supplier name
        if (TextUtils.isEmpty(productSupplierName)) {
            Toast.makeText(EditorActivity.this, "Empty product supplier name", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Check product image
        if (mCurrentProductUri == null) {
            Object object = mProductImage.getTag();
            boolean initImage = (Boolean) object;
            if (!initImage) {
                Toast.makeText(EditorActivity.this, "Empty product image", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductEntry.COLUMN_PRODUCT_IMAGE, imageViewToByteArray(mProductImage));
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, productSupplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_EMAIL, productSupplierEmail);

        // If we have a new product
        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            // Show a message to the user
            if (newUri == null) {
                Toast.makeText(this, R.string.error_save_product, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.product_saved, Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {
            // If we edit a product
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
            // Show a message to the user
            if (rowsAffected != 0) {
                Toast.makeText(this, R.string.product_updated, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.error_update_product, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }

    /**
     * Function that converts an imageView into a byte array in order to store it into the database
     */
    private byte[] imageViewToByteArray(ImageView image) {
        // Get the bitmap image from the imageView
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        // Get the byte array for the image
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        // Return the result
        return byteArray;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_EMAIL};

        return new CursorLoader(EditorActivity.this, mCurrentProductUri, projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor productData) {
        // If there is no data into the cursor return
        if (productData.getCount() < 1) return;

        // Move the cursor to the first row
        productData.moveToFirst();
        // Get the values from the cursor
        String productName = productData.getString(productData.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        byte[] productImage = productData.getBlob(productData.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));
        String productPrice = productData.getString(productData.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        String productQuantity = productData.getString(productData.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String productSupplierName = productData.getString(productData.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME));
        String productSupplierEmail = productData.getString(productData.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_EMAIL));
        Bitmap bitmap = BitmapFactory.decodeByteArray(productImage, 0, productImage.length);

        // Set the values to the corresponding views
        mProductImage.setImageBitmap(bitmap);
        mProductName.setText(productName);
        mProductPrice.setText(productPrice);
        mProductQuantity.setText(productQuantity);
        mProductSupplierName.setText(productSupplierName);
        mProductSupplierEmail.setText(productSupplierEmail);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Set the values to default
        mProductImage.setImageResource(R.drawable.no_product_image);
        mProductName.setText("");
        mProductPrice.setText("");
        mProductQuantity.setText("");
        mProductSupplierName.setText("");
        mProductSupplierEmail.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(getString(R.string.discard), discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If no modifications have been made
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // If we are in edit mode
        if (mCurrentProductUri != null) {
            int rowsAffected = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsAffected != 0) {
                Toast.makeText(this, R.string.product_deleted_successful, Toast.LENGTH_SHORT).show();
                // Only if the delete was successful we stop the edit mode
                finish();
            } else {
                Toast.makeText(this, R.string.product_deleted_unsuccessful, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
