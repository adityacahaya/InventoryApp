package com.wordpress.kadekadityablog.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.icu.lang.UCharacter.JoiningGroup.PE;

/**
 * Created by I Kadek Aditya on 5/24/2017.
 */

public class ProductProvider extends ContentProvider {

    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS,PRODUCTS);
        sUriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#",PRODUCT_ID);
    }
    private ProductDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS :
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID :
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[]{ String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cant query URI : "+uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductContract.ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCTS :
                return insertProduct(uri,values);
            default:
                throw new IllegalArgumentException("Cant insert to : "+uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues contentValues){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long newRowId = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, contentValues);
        if (newRowId == -1) {
            Log.e("ProductProvider.java", "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri,newRowId);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                getContext().getContentResolver().notifyChange(uri,null);
                rowsDeleted = database.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS :
                return updateProduct(uri, values, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateProduct(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int updateNumber = database.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
        if (updateNumber != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateNumber;
    }
}
