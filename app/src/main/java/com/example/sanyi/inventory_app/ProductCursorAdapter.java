package com.example.sanyi.inventory_app;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sanyi.inventory_app.data.StoreContract.StoreEntry;

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
    public void bindView(View view, Context context, Cursor cursor) {
        //Finding views
        TextView nameTextView=(TextView) view.findViewById(R.id.product_name_element);
        TextView provider=(TextView) view.findViewById(R.id.product_supplier_element);
        TextView unitPrice=(TextView) view.findViewById(R.id.unitPriceTextView);
        TextView quantity=(TextView) view.findViewById(R.id.product_quantity);
        TextView total=(TextView) view.findViewById(R.id.priceTotalView);
        ImageView image=(ImageView) view.findViewById(R.id.product_image_element);
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
        int quantityResult=cursor.getInt(quantityIndex);

        Uri uri=Uri.parse(imagepath);
        image.setImageURI(uri);
        nameTextView.setText(itemName);
        provider.setText(MainActivity.array[selectedSpinnerItem]);
        unitPrice.setText(String.valueOf(priceValue)+" EUR");
        total.setText(String.valueOf(priceValue*quantityResult)+" EUR");
        quantity.setText(String.valueOf(quantityResult));

        if(imagepath!=null && imagepath!=""){
            File f=new File(imagepath);
            if(f.exists()){
                Drawable drawable=Drawable.createFromPath(imagepath);
                image.setImageDrawable(drawable);
            }
        }

    }
}
