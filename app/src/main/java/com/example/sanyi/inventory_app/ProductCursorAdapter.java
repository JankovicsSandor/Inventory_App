package com.example.sanyi.inventory_app;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanyi.inventory_app.data.StoreContract.StoreEntry;
import com.example.sanyi.inventory_app.data.StoreDbHelper;

import java.io.File;

/**
 * Created by Sanyi on 27/06/2017.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.store_item_item,parent,false);
    }
    @Override
    public void bindView(final View view,final Context context, final Cursor cursor) {
        //Finding views
        TextView nameTextView=(TextView) view.findViewById(R.id.product_name_element);
        TextView provider=(TextView) view.findViewById(R.id.product_supplier_element);
        TextView unitPrice=(TextView) view.findViewById(R.id.unitPriceTextView);
        final TextView quantity=(TextView) view.findViewById(R.id.product_quantity);
        TextView total=(TextView) view.findViewById(R.id.priceTotalView);
        ImageView image=(ImageView) view.findViewById(R.id.product_image_element);
        Button sellButton=(Button) view.findViewById(R.id.sell);
        // Getting the indexes from DB
        int nameIndex=cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_NAME);
        int providerIndex=cursor.getColumnIndex(StoreEntry.COLUMN_SUPPLIER);
        int unitpriceIndex=cursor.getColumnIndex(StoreEntry.COLUMN_PRICE);
        int quantityIndex=cursor.getColumnIndex(StoreEntry.COLUMN_ITEM_NUMBER);
        int imageIndex=cursor.getColumnIndex(StoreEntry.COLUMN_PICTURE_PATH);

        String itemName=cursor.getString(nameIndex);
        int selectedSpinnerItem=cursor.getInt(providerIndex);
        Integer priceValue=cursor.getInt(unitpriceIndex);
        String imagepath=cursor.getString(imageIndex);
        final int quantityResult=cursor.getInt(quantityIndex);
        Uri uri=Uri.parse(imagepath);
        image.setImageURI(uri);
        nameTextView.setText(itemName);
        provider.setText(MainActivity.array[selectedSpinnerItem]);
        unitPrice.setText(String.valueOf(priceValue)+" EUR");
        total.setText(String.valueOf(priceValue*quantityResult)+" EUR");
        quantity.setText(String.valueOf(quantityResult));
        if(imagepath!=null || imagepath!=""){
            File f=new File(imagepath);
            if(f.exists()){
                Drawable drawable=Drawable.createFromPath(imagepath);
                image.setImageDrawable(drawable);
            }
        }
        final int position=cursor.getPosition();
        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cursor.moveToPosition(position);
                ContentValues values=new ContentValues();
                values.put(StoreEntry.COLUMN_ITEM_NUMBER,quantityResult-1);
                StoreDbHelper helper=new StoreDbHelper(view.getContext());
                SQLiteDatabase db=helper.getWritableDatabase();
                final long id=cursor.getLong(cursor.getColumnIndex(StoreEntry._ID));
                if(StoreEntry.isValidnumber(quantityResult-1)){
                    Uri toBeModified= ContentUris.withAppendedId(StoreEntry.CONTENT_URI,id);
                    int rowsAffected=context.getContentResolver().update(toBeModified,values,null,null);
                    Toast.makeText(context,"Successfully sold 1 item",Toast.LENGTH_SHORT).show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Item is about run out do you want to reorder?");
                    // User want to reorder from it and open editoractivity
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, EditorActivity.class);
                            Uri currentURi = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);
                            intent.setData(currentURi);
                            context.startActivity(intent);

                        }
                    });
                    // User doesnt want to reorder from the item
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null) {
                                dialog.dismiss();
                                Uri toBeModified=ContentUris.withAppendedId(StoreEntry.CONTENT_URI,id);
                                int rowsAffected=context.getContentResolver().delete(toBeModified,String.valueOf(quantity),null);
                                Toast.makeText(context,"Deleted item from the list",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCanceledOnTouchOutside(true);
                    alertDialog.show();
                }
            }
        });
    }
}
