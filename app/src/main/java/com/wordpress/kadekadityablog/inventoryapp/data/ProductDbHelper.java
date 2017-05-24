package com.wordpress.kadekadityablog.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.R.attr.version;
import static java.sql.Types.BLOB;

/**
 * Created by I Kadek Aditya on 5/23/2017.
 */

public class ProductDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Inventory.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " (" +
                    ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + TEXT_TYPE + " NOT NULL" + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER DEFAULT 0" + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0" + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER + " INTEGER NOT NULL" + COMMA_SEP +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE + " BLOB );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("IniPathNya",context.getDatabasePath(DATABASE_NAME).getAbsolutePath()+"");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
