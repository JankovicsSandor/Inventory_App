package com.example.sanyi.inventory_app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sanyi.inventory_app.data.StoreContract.StoreEntry;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity {
    //Code to decide which action to be taken
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    ImageView productImage;
    Bitmap photoToSet;
    Uri uriPathToPicture = null;
    String pathToPicture;
    private boolean ItemHasChanged = false;
    EditText phoneNumber;
    EditText webadress;
    EditText itemName;
    Spinner supplierSpinner;
    EditText price;
    EditText quantity;
    ImageButton minusButton;
    ImageButton plusButton;
    private Uri CurrentItemUri;
    private View.OnClickListener changequantity = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.minusId:
                    changeQuantityValue(-1);
                    break;
                case R.id.plusId:
                    changeQuantityValue(1);
                    break;
            }
        }
    };

    private void changeQuantityValue(int i) {
        int currentValue = Integer.parseInt(quantity.getText().toString());
        currentValue += i;
        if (currentValue > 0) {
            quantity.setText(String.valueOf(currentValue));
        }

    }

    private int mSupplier;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ItemHasChanged = true;
            return false;
        }
    };

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // Insert mode is on
        if(CurrentItemUri==null){
            MenuItem menuItem=menu.findItem(R.id.delete);
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
            case R.id.delete:
                //TODO implement delete all method
                break;
            case R.id.save:
                saveItem();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Intent intent =getIntent();
        CurrentItemUri=intent.getData();
        if(CurrentItemUri==null){
            setTitle(getString(R.string.addItem));
        }
        else {
            setTitle(getString(R.string.editItem));
        }
        productImage = (ImageView) findViewById(R.id.itemPictureId);
        itemName = (EditText) findViewById(R.id.NameEditText);
        price = (EditText) findViewById(R.id.PriceEditText);
        supplierSpinner = (Spinner) findViewById(R.id.SupplierSpinner);
        phoneNumber = (EditText) findViewById(R.id.PhoneEditText);
        webadress = (EditText) findViewById(R.id.URLEditText);
        quantity = (EditText) findViewById(R.id.quantityEditText);
        minusButton = (ImageButton) findViewById(R.id.minusId);
        plusButton = (ImageButton) findViewById(R.id.plusId);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        minusButton.setOnClickListener(changequantity);
        plusButton.setOnClickListener(changequantity);
        setupSpinner();
    }

    private void setupSpinner() {

        ArrayAdapter supplierSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_options, android.R.layout.simple_spinner_dropdown_item);
        supplierSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        supplierSpinner.setAdapter(supplierSpinnerAdapter);
        supplierSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSupplier = parent.getSelectedItemPosition();
                Log.e("SupplerID", String.valueOf(mSupplier));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSupplier = 0;
            }
        });
    }

    private void saveItem() {
        String nameString = itemName.getText().toString().trim();
        String priceString = price.getText().toString().trim();
        int supplerId = mSupplier;
        String phoneString = phoneNumber.getText().toString().trim();
        String urlString = webadress.getText().toString().trim();
        int quantityId = Integer.parseInt(quantity.getText().toString());
        if (CurrentItemUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(String.valueOf(supplerId)) && TextUtils.isEmpty(phoneString) && TextUtils.isEmpty(urlString)
                && TextUtils.isEmpty(String.valueOf(quantityId))) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(StoreEntry.COLUMN_PICTURE_PATH, pathToPicture);
        values.put(StoreEntry.COLUMN_ITEM_NAME, nameString);
        values.put(StoreEntry.COLUMN_PRICE, priceString);
        values.put(StoreEntry.COLUMN_ITEM_NUMBER, quantityId);
        values.put(StoreEntry.COLUMN_SUPPLIER, supplerId);
        values.put(StoreEntry.COLUMN_PHONE, phoneString);
        values.put(StoreEntry.COLUMN_URL, urlString);
        // Coming from mainActivity to insert a new item
        if (CurrentItemUri == null) {
            Uri uri = getContentResolver().insert(StoreEntry.CONTENT_URI, values);

            if (uri == null) {
                Toast.makeText(this, getString(R.string.error_saving), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.succesfull_saving), Toast.LENGTH_SHORT).show();
            }
        }
        // Coming from MainActivity to edit some item
        else{
            int rowsAffected=getContentResolver().update(CurrentItemUri,values,null,null);

            if(rowsAffected==0){
                Toast.makeText(this, getString(R.string.error_updating), Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, getString(R.string.succesfull_update), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void SelectImage() {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

                    try {
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        Log.e("Activity", "not found");
                    }

                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra("crop", "true");
                    intent.putExtra("aspectX", 2);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 200);
                    intent.putExtra("outputY", 150);
                    try {
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, SELECT_FILE);
                    } catch (ActivityNotFoundException e) {
                        Log.e("Activity", "not found");
                    }

                } else if (items[i].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {


            if (requestCode == REQUEST_CAMERA) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    photoToSet = bundle.getParcelable("data");
                    // URi path of the picture
                    uriPathToPicture = getImageUri(getApplicationContext(), photoToSet);
                    pathToPicture = uriPathToPicture.toString();
                    Log.e("Value of pathCamera ", pathToPicture);
                    productImage.setImageBitmap(photoToSet);
                }
            } else if (requestCode == SELECT_FILE) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    photoToSet = bundle.getParcelable("data");
                    // URi path of the picture
                    uriPathToPicture = getImageUri(getApplicationContext(), photoToSet);
                    pathToPicture = uriPathToPicture.toString();
                    Log.e("Value of pathFile", pathToPicture);
                    productImage.setImageBitmap(photoToSet);
                }
            }
        }
    }

    private Uri getImageUri(Context applicationContext, Bitmap photoToSet) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        photoToSet.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), photoToSet, "Title", null);
        return Uri.parse(path);
    }

    private String getRealPathFromUri(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        String data = cursor.getString(idx);
        cursor.close();
        return data;
    }
}
