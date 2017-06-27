package com.example.sanyi.inventory_app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class EditorActivity extends AppCompatActivity {
    //Code to decide which action to be taken
    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;
    ImageView productImage;
    Bitmap photoToSet;
    Uri uriPathToPicture=null;
    String pathToPicture;

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
                //TODO implement save method
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        productImage = (ImageView) findViewById(R.id.itemPictureId);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
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
                    pathToPicture=uriPathToPicture.toString();
                    Log.e("Value of pathCamera ",pathToPicture);
                    productImage.setImageBitmap(photoToSet);
                }
            } else if (requestCode == SELECT_FILE) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    photoToSet = bundle.getParcelable("data");
                    // URi path of the picture
                    uriPathToPicture = getImageUri(getApplicationContext(), photoToSet);
                    pathToPicture=uriPathToPicture.toString();
                    Log.e("Value of pathFile",pathToPicture);
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
        String data=cursor.getString(idx);
        cursor.close();
        return data;
    }
}
