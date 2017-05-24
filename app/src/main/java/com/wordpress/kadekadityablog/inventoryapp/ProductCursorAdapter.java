package com.wordpress.kadekadityablog.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.kadekadityablog.inventoryapp.data.ProductContract;
import com.wordpress.kadekadityablog.inventoryapp.data.ProductDbHelper;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.os.Build.VERSION_CODES.M;

/**
 * Created by I Kadek Aditya on 5/24/2017.
 */

public class ProductCursorAdapter extends CursorAdapter{

    private int quantityProduct = 0;
    private String productQuantity;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.layout_list_item, parent, false);
    }

    @Override
    public void bindView(final View view, final Context context, Cursor cursor) {
        TextView textViewProductName = (TextView)view.findViewById(R.id.layout_list_item_product_name);
        TextView textViewProductPrice = (TextView)view.findViewById(R.id.layout_list_item_product_price);
        final TextView textViewProductQuantity = (TextView)view.findViewById(R.id.layout_list_item_product_quantity);
        TextView buttonProductSale = (TextView) view.findViewById(R.id.layout_list_item_btnSale);

        final String idbv = "" + cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID));
        String productName = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String productPrice = "Rp." + cursor.getString(cursor.getColumnIndexOrThrow("price"));
        productQuantity = cursor.getString(cursor.getColumnIndexOrThrow("quantity"));

        textViewProductName.setText(productName);
        textViewProductPrice.setText(productPrice);
        textViewProductQuantity.setText(productQuantity);

        buttonProductSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityProduct = Integer.parseInt(textViewProductQuantity.getText().toString());
                quantityProduct--;
                if(quantityProduct >= 0) {
                    textViewProductQuantity.setText(quantityProduct + "");
                    Uri uri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI, Long.parseLong(idbv));
                    ContentValues values = new ContentValues();
                    values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(textViewProductQuantity.getText().toString()));

                    ProductDbHelper dbHelper = new ProductDbHelper(context);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    String selection = ProductContract.ProductEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    int newUpdate = db.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                }else{
                    Toast.makeText(context,"Quantity Must More Than 0",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
