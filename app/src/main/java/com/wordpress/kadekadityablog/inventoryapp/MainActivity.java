package com.wordpress.kadekadityablog.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.wordpress.kadekadityablog.inventoryapp.data.ProductContract;
import com.wordpress.kadekadityablog.inventoryapp.data.ProductDbHelper;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static int PET_LOADER = 0;
    ProductCursorAdapter productCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        final ListView listViewProduct = (ListView) findViewById(R.id.list_view);

        View emptyView = findViewById(R.id.empty_view);
        listViewProduct.setEmptyView(emptyView);

        productCursorAdapter = new ProductCursorAdapter(this, null);
        listViewProduct.setAdapter(productCursorAdapter);
        getLoaderManager().initLoader(PET_LOADER, null, this);

        listViewProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("click","click");
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri uri = ContentUris.withAppendedId(ProductContract.ProductEntry.CONTENT_URI,id);
                intent.setData(uri);
                startActivity(intent);
            }
        });
    }

    public void deleteAllProduct(){
        getContentResolver().delete(ProductContract.ProductEntry.CONTENT_URI, null, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_deleteall:
                deleteAllProduct();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry.COLUMN_PRODUCT_ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY
        };
        return new CursorLoader(this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        productCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productCursorAdapter.swapCursor(null);
    }
}
