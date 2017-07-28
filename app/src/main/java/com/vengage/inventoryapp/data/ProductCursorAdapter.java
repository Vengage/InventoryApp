package com.vengage.inventoryapp.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vengage.inventoryapp.R;
import com.vengage.inventoryapp.data.ProductContract.ProductEntry;

/**
 * Created by Vengage on 7/27/2017.
 * ProductCursorAdapter class
 */

public class ProductCursorAdapter extends CursorAdapter {
    // Context
    private Context mContext;
    // Sale button OnClickListener
    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Get the id of the product stored in the tag of the view
            Object object = view.getTag();
            long id = Long.parseLong(object.toString());
            // Get the current quantity of the product
            String[] projection = {ProductEntry._ID, ProductEntry.COLUMN_PRODUCT_QUANTITY};
            Cursor mCursor = mContext.getContentResolver().query(
                    ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id),
                    projection,
                    null, null, null);
            mCursor.moveToFirst();
            int productQuantity = mCursor.getInt(mCursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
            // If there is more to buy
            if (productQuantity > 0) {
                // Put the new value
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity - 1);
                // Update the value from the database for the corresponding product
                mContext.getContentResolver().update(
                        ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id),
                        values, null, null);
            }
        }
    };

    // Constructor
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView mProductName = (TextView) view.findViewById(R.id.product_name);
        ImageView mProductImage = (ImageView) view.findViewById(R.id.product_image);
        TextView mProductPrice = (TextView) view.findViewById(R.id.product_price);
        TextView mProductQuantity = (TextView) view.findViewById(R.id.product_quantity);
        Button productBuy = (Button) view.findViewById(R.id.product_buy);

        String productName = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME));
        String productPrice = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE));
        String productQuantity = cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        byte[] productImage = cursor.getBlob(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_IMAGE));
        Bitmap bitmap = BitmapFactory.decodeByteArray(productImage, 0, productImage.length);

        mProductImage.setImageBitmap(bitmap);
        mProductName.setText(productName);
        mProductPrice.setText(productPrice);
        mProductQuantity.setText(productQuantity);

        int productQuantityInteger = Integer.parseInt(productQuantity);
        // Modify the sale button in case there are no products available
        if (productQuantityInteger == 0) {
            productBuy.setBackgroundResource(R.mipmap.ic_product_no_buy);
            productBuy.setEnabled(false);
        } else if (productQuantityInteger > 0) {
            productBuy.setBackgroundResource(R.mipmap.ic_product_buy);
            productBuy.setEnabled(true);
        }
        // Set the id of the row into the button tag
        Object object = cursor.getString(cursor.getColumnIndex(ProductEntry._ID));
        productBuy.setTag(object);

        productBuy.setOnClickListener(buttonClickListener);
    }
}
