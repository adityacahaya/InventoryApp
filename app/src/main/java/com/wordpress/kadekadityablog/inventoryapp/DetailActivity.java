package com.wordpress.kadekadityablog.inventoryapp;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wordpress.kadekadityablog.inventoryapp.data.ProductContract;

import java.io.ByteArrayOutputStream;

import static android.R.attr.name;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Button btnEditImage;
    private Button btnGaleryImage;
    private Button btnDecrease;
    private Button btnIncrease;
    private EditText edProductName;
    private EditText edProductPrice;
    private ImageView imgEdit;
    private TextView tvQuantity;
    private Spinner spSupplier;
    private Bitmap imgProduct;

    private int supplier = ProductContract.ProductEntry.SUPPLIER_INDOMARET;

    private Context mContext;
    private Activity mActivity;
    private LinearLayout mRelativeLayout;
    private PopupWindow mPopupWindow;

    private static int PRODUCT_LOADER = 0;
    private Uri uri;

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
        setContentView(R.layout.activity_detail);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        btnEditImage = (Button) findViewById(R.id.btn_edit_image);
        btnGaleryImage = (Button) findViewById(R.id.btn_galery_image);
        btnDecrease = (Button) findViewById(R.id.btn_decrease);
        btnIncrease = (Button) findViewById(R.id.btn_increase);
        edProductName = (EditText) findViewById(R.id.edit_product_name);
        edProductPrice = (EditText) findViewById(R.id.edit_product_price);
        imgEdit = (ImageView) findViewById(R.id.edit_image);
        tvQuantity = (TextView) findViewById(R.id.label_product_quantity);
        spSupplier = (Spinner) findViewById(R.id.spinner_supplier);
        mContext = getApplicationContext();
        mActivity = DetailActivity.this;
        mRelativeLayout = (LinearLayout) findViewById(R.id.rl);

        edProductName.setOnTouchListener(mTouchListener);
        edProductPrice.setOnTouchListener(mTouchListener);
        btnEditImage.setOnTouchListener(mTouchListener);
        btnGaleryImage.setOnTouchListener(mTouchListener);
        btnDecrease.setOnTouchListener(mTouchListener);
        btnIncrease.setOnTouchListener(mTouchListener);
        spSupplier.setOnTouchListener(mTouchListener);

        setupSpinner();

        Intent intent = getIntent();
        uri = intent.getData();
        if(uri == null){
            setTitle("Add Product");
            invalidateOptionsMenu();
        }else{
            setTitle("Update Product");
            Log.i("uri nya",uri+"");
            getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
        }

        btnDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityClick(mRelativeLayout, "Number of Decrease");
            }
        });
        btnIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityClick(mRelativeLayout, "Number of Increase");
            }
        });

        btnEditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
            }
        });
        btnGaleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);
            }
        });
    }

    public void quantityClick(LinearLayout mRelativeLayout, final String hint){
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.layout_quantity,null);
        mPopupWindow = new PopupWindow(
                customView,
                1000,
                ActionBar.LayoutParams.WRAP_CONTENT,
                true
        );
        final EditText editText = (EditText)customView.findViewById(R.id.ed_decrease);
        editText.setHint(hint);
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }
        Button closeButton = (Button) customView.findViewById(R.id.btn_decrease_ok);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hint.equals("Number of Decrease")){
                    int decrease = Integer.valueOf(editText.getText().toString());
                    int quantity = Integer.valueOf(tvQuantity.getText().toString());
                    int quantityFinal = quantity - decrease;
                    if(quantityFinal < 0){
                        Toast.makeText(mContext,"Quantity Must Be More Than 0",Toast.LENGTH_SHORT).show();
                    }else {
                        tvQuantity.setText(String.valueOf(quantityFinal));
                    }
                }else{
                    int increase = Integer.valueOf(editText.getText().toString());
                    int quantity = Integer.valueOf(tvQuantity.getText().toString());
                    int quantityFinal = quantity + increase;
                    tvQuantity.setText(String.valueOf(quantityFinal));
                }
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation(mRelativeLayout, Gravity.CENTER,0,0);
    }

    private void setupSpinner() {
        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_item);
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spSupplier.setAdapter(supplierSpinnerAdapter);

        spSupplier.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.supplier_indomart))) {
                        supplier = ProductContract.ProductEntry.SUPPLIER_INDOMARET;
                    } else if (selection.equals(getString(R.string.supplier_alfamart))) {
                        supplier = ProductContract.ProductEntry.SUPPLIER_ALFAMART;
                    } else if (selection.equals(getString(R.string.supplier_lottemart))) {
                        supplier = ProductContract.ProductEntry.SUPPLIER_LOTTEMART;
                    } else {
                        supplier = ProductContract.ProductEntry.SUPPLIER_HYPERMART;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                supplier = ProductContract.ProductEntry.SUPPLIER_INDOMARET;
            }
        });
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private void saveProduct(){
        String nameProduct = edProductName.getText().toString().trim();
        String priceProductString = edProductPrice.getText().toString();
        int priceProduct = 0;
        byte[] imageProduct;
        int quantityProduct = Integer.parseInt(tvQuantity.getText().toString());
        int supplierProduct = supplier;
        if(nameProduct.equals("") || priceProductString.equals("") || imgProduct == null) {
            Toast.makeText(mContext,"Name, Price and Image Cant Be Empty",Toast.LENGTH_SHORT).show();
        }else{
            Log.i("save", "lakukan save");
            priceProduct = Integer.parseInt(priceProductString);
            imageProduct = getBytes(imgProduct);

            ContentValues values = new ContentValues();
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameProduct);
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceProduct);
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE, imageProduct);
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityProduct);
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER, supplierProduct);

            if (uri == null &&
                    TextUtils.isEmpty(nameProduct) && TextUtils.isEmpty(String.valueOf(priceProduct)) &&
                    TextUtils.isEmpty(String.valueOf(imageProduct))) {
                return;
            }

            Uri newUri = null;
            int newUpdate = 0;
            if (getTitle().equals("Add Product")) {
                newUri = getContentResolver().insert(ProductContract.ProductEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, "Insert Product Failed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Insert Product Success", Toast.LENGTH_SHORT).show();
                }
            } else {
                newUpdate = getContentResolver().update(uri, values, null, null);
                if (newUpdate == 0) {
                    Toast.makeText(this, "Update Product Failed",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Update Product Success",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void deleteProduct(){
        if (uri != null) {
            int newUpdate = getContentResolver().delete(uri, null, null);
            if (newUpdate == 0) {
                Toast.makeText(this, "Delete Product Failed",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Delete Product Success",
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    private void orderProduct(){
        String nameProduct = edProductName.getText().toString().trim();
        String priceProductString = edProductPrice.getText().toString();
        int priceProduct = 0;
        byte[] imageProduct;
        int quantityProduct = Integer.parseInt(tvQuantity.getText().toString());
        int supplierProduct = supplier;
        if(nameProduct.equals("") || priceProductString.equals("") || imgProduct == null) {
            Toast.makeText(mContext,"Name, Price and Image Cant Be Empty",Toast.LENGTH_SHORT).show();
        }else {
            Log.i("save", "send Email");
            priceProduct = Integer.parseInt(priceProductString);
            imageProduct = getBytes(imgProduct);

            String pesanan = "Order From AppInventory : "+
                    "\n Product Name : "+nameProduct+
                    "\n Product Price : "+priceProductString+
                    "\n quantity Product : "+quantityProduct;

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Product Order");
            intent.putExtra(Intent.EXTRA_TEXT, pesanan);

            if(supplierProduct == ProductContract.ProductEntry.SUPPLIER_INDOMARET){
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"indomaret@gmail.com"});
            }else if(supplierProduct == ProductContract.ProductEntry.SUPPLIER_ALFAMART){
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"alfamart@gmail.com"});
            }else if(supplierProduct == ProductContract.ProductEntry.SUPPLIER_LOTTEMART) {
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lottemart@gmail.com"});
            }else{
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hypermart@gmail.com"});
            }
            startActivity(Intent.createChooser(intent, "Send Email"));
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure delete this product ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imgProduct = photo;
            imgEdit.setImageBitmap(photo);
        }else if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            imgProduct = thumbnail;
            imgEdit.setImageBitmap(thumbnail);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (uri == null) {
            MenuItem menuItem = menu.findItem(R.id.menu_edit_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_save:
                saveProduct();
                finish();
                return true;
            case R.id.menu_edit_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.menu_edit_order_more:
                orderProduct();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit updating ?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Updating", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductContract.ProductEntry.COLUMN_PRODUCT_ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE,
                ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER
        };
        return new CursorLoader(this,
                uri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_IMAGE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            byte[] image = cursor.getBlob(imageColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            int supplier = cursor.getInt(supplierColumnIndex);

            edProductName.setText(name);
            edProductPrice.setText(String.valueOf(price));
            imgProduct = getImage(image);
            imgEdit.setImageBitmap(imgProduct);
            tvQuantity.setText(String.valueOf(quantity));

            switch (supplier) {
                case ProductContract.ProductEntry.SUPPLIER_INDOMARET:
                    spSupplier.setSelection(0);
                    break;
                case ProductContract.ProductEntry.SUPPLIER_ALFAMART:
                    spSupplier.setSelection(1);
                    break;
                case ProductContract.ProductEntry.SUPPLIER_LOTTEMART:
                    spSupplier.setSelection(2);
                    break;
                default:
                    spSupplier.setSelection(3);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {}
}
