package com.wordpress.kadekadityablog.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by I Kadek Aditya on 5/23/2017.
 */

public final class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.wordpress.kadekadityablog.inventoryapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    private ProductContract(){};

    public static class ProductEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";

        public static final int SUPPLIER_INDOMARET = 0;
        public static final int SUPPLIER_ALFAMART = 1;
        public static final int SUPPLIER_LOTTEMART = 2;
        public static final int SUPPLIER_HYPERMART = 3;

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
    }
}
