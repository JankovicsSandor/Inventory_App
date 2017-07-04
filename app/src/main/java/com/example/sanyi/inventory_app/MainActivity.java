package com.example.sanyi.inventory_app;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sanyi.inventory_app.data.StoreContract.StoreEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEM_LOADER = 0;
    public static String[] array;
    ProductCursorAdapter mAdapter;

    FloatingActionButton modify;
    // Setting up a global FAB click listener
    View.OnClickListener floatingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, EditorActivity.class);
            startActivity(intent);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        array=getResources().getStringArray(R.array.array_supplier_options);
        modify = (FloatingActionButton) findViewById(R.id.orderNew);
        modify.setOnClickListener(floatingListener);

        ListView itemListView = (ListView) findViewById(R.id.itemsListView);

        View emptyview = findViewById(R.id.empty_view);
        itemListView.setEmptyView(emptyview);

        mAdapter = new ProductCursorAdapter(this, null);
        itemListView.setAdapter(mAdapter);
        // Onclick event handler--> goto editing page
        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentURi = ContentUris.withAppendedId(StoreEntry.CONTENT_URI, id);
                intent.setData(currentURi);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(ITEM_LOADER, null,this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all:
                deletallItem();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deletallItem() {
        int rowsDeleted=getContentResolver().delete(StoreEntry.CONTENT_URI,null,null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection the specifies the columns we care about
       String[] projection={
               StoreEntry._ID,
               StoreEntry.COLUMN_PICTURE_PATH,
               StoreEntry.COLUMN_ITEM_NAME,
               StoreEntry.COLUMN_PRICE,
               StoreEntry.COLUMN_ITEM_NUMBER,
               StoreEntry.COLUMN_SUPPLIER};
        // This will execute the ContentProvider's query method on the background thread
        return new CursorLoader(this,
                StoreEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
