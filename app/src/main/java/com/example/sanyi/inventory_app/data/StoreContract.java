package com.example.sanyi.inventory_app.data;

import android.provider.BaseColumns;

/**
 * Created by Sanyi on 26/06/2017.
 */

public class StoreContract {
    private StoreContract(){}

    public static final class StoreEntry implements BaseColumns{
        public final static String TABLE_NAME="storage";

        public final static String _ID=BaseColumns._ID;
        public final static String COLUMN_ITEM_NAME="item_name";
        public final static String COLUMN_PRICE="price";
        public final static String COLUMN_SUPPLIER="supplier";
        public final static String COLUMN_PHONE="phone_number";
        public final static String COLUMN_URL="url_link";
        public final static String COLUMN_ITEMNUMBER="itemnuber";



    }
}
