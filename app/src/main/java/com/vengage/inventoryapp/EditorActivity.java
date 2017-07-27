package com.vengage.inventoryapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

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
    }
}
