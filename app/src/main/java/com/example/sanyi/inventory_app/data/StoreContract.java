package com.example.sanyi.inventory_app.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Sanyi on 26/06/2017.
 */

public class StoreContract {
    private StoreContract(){}
    public static final String CONTENT_AUTHORITY="com.example.sanyi.inventory_app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_STORE="storage";
    public static final class StoreEntry implements BaseColumns{

        public final static Uri CONTENT_URI=Uri.withAppendedPath(BASE_CONTENT_URI,PATH_STORE);
        // Link for a table of store query
        public final static String CONTENT_LIST_TYPE= ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_STORE;
        // Link for a single storeitem query
        public final static String CONTENT_ITEM_TYPE=ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+CONTENT_AUTHORITY+"/"+PATH_STORE;

        public final static String TABLE_NAME="storage";

        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME="item_name";
        public final static String COLUMN_PRICE="price";
        public final static String COLUMN_PICTURE_PATH="pic_path";
        public final static String COLUMN_SUPPLIER="supplier";
        public final static String COLUMN_PHONE="phone_number";
        public final static String COLUMN_URL="url_link";
        public final static String COLUMN_ITEM_NUMBER="itemnuber";

        public static boolean isValidnumber(int price){
            boolean valid=false;
            if (price>0){
                valid=true;
            }
            return valid;
        }
    }
}
