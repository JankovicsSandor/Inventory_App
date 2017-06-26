package com.example.sanyi.inventory_app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.sanyi.inventory_app.data.StoreContract.StoreEntry;

/**
 * Created by Sanyi on 26/06/2017.
 */

public class StoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "shop.db";

    private static final int DATABASE_VERSION = 1;

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DATABASE_STORAGE_TABLE = "CREATE TABLE" + StoreEntry.TABLE_NAME + " ("
                + StoreEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + StoreEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_PRICE + " INTEGER NOT NULL,"
                + StoreEntry.COLUMN_ITEMNUMBER + " INTEGER NOT NULL, "
                + StoreEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, "
                + StoreEntry.COLUMN_PHONE + " TEXT, "
                + StoreEntry.COLUMN_URL + " TEXT);";
        db.execSQL(CREATE_DATABASE_STORAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
