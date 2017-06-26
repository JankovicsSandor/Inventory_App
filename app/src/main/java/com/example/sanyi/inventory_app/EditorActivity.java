package com.example.sanyi.inventory_app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class EditorActivity extends AppCompatActivity {

    Integer REQUEST_CAMERA=1,SELECT_FILE=0;
    ImageView productImage;
    Bitmap photoToSet;

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
        productImage.setImageBitmap(photoToSet);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SelectImage();
            }
        });
    }
    private void SelectImage(){
        final CharSequence[] items={"Camera","Gallery","Cancel"};
        AlertDialog.Builder builder=new AlertDialog.Builder(EditorActivity.this);
        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(items[i].equals("Camera")){
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString());

                    try {
                        intent.putExtra("return-data",true);
                        startActivityForResult(intent,REQUEST_CAMERA);
                    }
                    catch (ActivityNotFoundException e){
                        Log.e("Activity","not found");
                    }

                }
                else if(items[i].equals("Gallery")){
                    Intent intent=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra("crop","true");
                    intent.putExtra("aspectX",0);
                    intent.putExtra("aspectY",0);
                    intent.putExtra("outputX",200);
                    intent.putExtra("outputY",150);
                    try {
                        intent.putExtra("return-data",true);
                        startActivityForResult(intent.createChooser(intent,"Select File"),SELECT_FILE);
                    }
                    catch (ActivityNotFoundException e){
                        Log.e("Activity","not found");
                    }

                }
                else if(items[i].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if(requestCode== REQUEST_CAMERA){
                Bundle bundle=data.getExtras();
                if(bundle!=null){
                    photoToSet=bundle.getParcelable("data");
                    productImage.setImageBitmap(photoToSet);
                }
            }
            else if(requestCode== SELECT_FILE){
                Bundle bundle=data.getExtras();
                if(bundle!=null){
                    photoToSet=bundle.getParcelable("data");
                    productImage.setImageBitmap(photoToSet);
                }
            }
        }
    }
}
