package com.example.sanyi.inventory_app.data;

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

import com.example.sanyi.inventory_app.data.StoreContract.StoreEntry;

/**
 * Created by Sanyi on 27/06/2017.
 */

public class StoreProvider extends ContentProvider{

    //Tag for the log messages
    public static final String LOG_TAG = StoreProvider.class.getSimpleName();
    int matchCode;
    int rowsAffected;
    String itemName;
    Integer itemPrice;
    String itemSupplier;
    String itemPhoneNumber;
    String itemUrl;
    Integer itemNumber;
    SQLiteDatabase database;
    // setting up a code if we want to get the whole database
    private static final int ALL_ITEM=100;
    //Setting up a code if we want a particular item
    private static final int ONE_ITEM=101;

    private static final UriMatcher mUriMatcher=new UriMatcher(UriMatcher.NO_MATCH);

    static{
        mUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY,StoreContract.PATH_STORE,ALL_ITEM);

        mUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY,StoreContract.PATH_STORE+"/#",ONE_ITEM);
    }
    private StoreDbHelper mDbHelper;
    @Override
    public boolean onCreate() {
        mDbHelper=new StoreDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
       // Get readable databas
        database=mDbHelper.getReadableDatabase();

        Cursor cursor;

        matchCode=mUriMatcher.match(uri);
        switch (matchCode){
            case ALL_ITEM:
                cursor=database.query(StoreEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case ONE_ITEM:
                selection=StoreEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor=database.query(StoreEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI"+uri);
        }
        // Setting up a notifier so we can update our views only when the database is modiified
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        //Return cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        matchCode=mUriMatcher.match(uri);
        switch (matchCode){
            case ALL_ITEM:
                return StoreEntry.CONTENT_LIST_TYPE;
            case ONE_ITEM:
                return StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown Uri"+uri+"with match"+matchCode);
        }
    }
    // SQL insert
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        matchCode=mUriMatcher.match(uri);
        switch (matchCode){
            case ALL_ITEM:
                return insertItem(uri,values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for "+uri);
        }
    }
    //Insert helper method
    private Uri insertItem(Uri uri, ContentValues values) {

        itemName=values.getAsString(StoreEntry.COLUMN_ITEM_NAME);
        if(itemName==null){
            throw new IllegalArgumentException("Item requires name");
        }
        itemPrice=values.getAsInteger(StoreEntry.COLUMN_PRICE);
        if(itemPrice==null ||!StoreEntry.isValidnumber(itemPrice)){
            throw new IllegalArgumentException("Item must have valid price");
        }
        itemSupplier=values.getAsString(StoreEntry.COLUMN_SUPPLIER);
        if(itemSupplier==null){
            throw new IllegalArgumentException("Item must have supplier");
        }
        itemNumber=values.getAsInteger(StoreEntry.COLUMN_ITEM_NUMBER);
        if(itemNumber==null || !StoreEntry.isValidnumber(itemNumber)){
            throw new IllegalArgumentException("Item must have valid quantity");
        }
        database=mDbHelper.getWritableDatabase();

        long id=database.insert(StoreEntry.TABLE_NAME,null,values);
        if(id==-1){
            Log.e(LOG_TAG," Failed to insert row for "+uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        database=mDbHelper.getWritableDatabase();

        matchCode=mUriMatcher.match(uri);
        switch (matchCode){
            case ALL_ITEM:
                rowsAffected=database.delete(StoreEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case ONE_ITEM:
                selection=StoreEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsAffected=database.delete(StoreEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for "+uri);
        }
        if (rowsAffected!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        // Return how many rows were affected during query
        return rowsAffected;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        matchCode=mUriMatcher.match(uri);
        switch (matchCode){
            case ALL_ITEM:
                return updateItem(uri,values,selection,selectionArgs);
            case ONE_ITEM:
                selection=StoreEntry._ID+"=?";
                selectionArgs=new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for "+uri);
        }
    }

    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.containsKey(StoreEntry.COLUMN_ITEM_NAME)){
            itemName=values.getAsString(StoreEntry.COLUMN_ITEM_NAME);
            if(itemName==null){
                throw new IllegalArgumentException("Item must have a name");
            }
        }

        if(values.containsKey(StoreEntry.COLUMN_PRICE)){
            itemPrice=values.getAsInteger(StoreEntry.COLUMN_PRICE);
            if(itemPrice==null ||!StoreEntry.isValidnumber(itemPrice)){
                throw new IllegalArgumentException("Item must have a valid price");
            }
        }
        if(values.containsKey(StoreEntry.COLUMN_SUPPLIER)){
            itemSupplier=values.getAsString(StoreEntry.COLUMN_SUPPLIER);
            if(itemSupplier==null){
                throw new IllegalArgumentException("Item must have a name");
            }
        }
        if(values.containsKey(StoreEntry.COLUMN_ITEM_NUMBER)){
            itemNumber=values.getAsInteger(StoreEntry.COLUMN_ITEM_NUMBER);
            if(itemNumber==null || !StoreEntry.isValidnumber(itemNumber)){
                throw new IllegalArgumentException("Item must have a valid number");
            }
        }
        if (values.size()==0){
            return 0;
        }

        database=mDbHelper.getWritableDatabase();

        rowsAffected=database.update(StoreEntry.TABLE_NAME,values,selection,selectionArgs);

        if(rowsAffected!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsAffected;
    }
}
