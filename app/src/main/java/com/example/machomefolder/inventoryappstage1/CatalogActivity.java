package com.example.machomefolder.inventoryappstage1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.machomefolder.inventoryappstage1.data.BookContract;
import com.example.machomefolder.inventoryappstage1.data.BookDbHelper;

public class CatalogActivity extends AppCompatActivity {
    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mDbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};

        // Perform a query on the books table
        Cursor cursor = db.query(
                BookContract.BookEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // The sort order

        TextView displayView = findViewById(R.id.text_view_book);

        try {
            // Create a header in the Text View that looks like this:
            //
            // The books table contains <number of rows in Cursor> books.
            // _id - name - price - quantity - supplier name - supplier phone
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText(getString(R.string.the_books_table_contains) + cursor.getCount() + getString(R.string.books));
            displayView.append(BookContract.BookEntry._ID + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_NAME + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_PRICE + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int phoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentName + " - " +
                        currentPrice + " - " +
                        currentQuantity + " - " +
                        currentSupplier + " - " +
                        currentPhone));
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded book data into the database. For debugging purposes only.
     */
    private void insertBook () {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and book attributes are the values.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, "The Catcher in the Rye");
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, 7);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Alibris");
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "510-594-4568");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        Log.v("CatalogActivity", "menu inflated ");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertBook ();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}