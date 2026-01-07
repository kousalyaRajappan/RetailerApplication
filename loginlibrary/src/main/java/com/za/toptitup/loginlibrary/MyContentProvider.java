package com.za.toptitup.loginlibrary;

import android.content.ContentProvider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashMap;

public class MyContentProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "za.co.topitup";
    public static final String URL = "content://" + PROVIDER_NAME + "/products";
    public static Uri CONTENT_URI = Uri.parse(URL);

    private final HashMap<String, String> values = null;

    private SQLiteDatabase db = null;

    static final String id = "id";
    public static final String name = "name";
    public static final String price = "price";
    public static final int uriCode = 1;
    UriMatcher uriMatcher = null;

    final String DATABASE_NAME = "ProductDB";
    final String TABLE_NAME = "products";
    int DATABASE_VERSION = 1;

    public MyContentProvider() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PROVIDER_NAME, "products", uriCode);

        // to access a particular row
        // of the table
        uriMatcher.addURI(
                PROVIDER_NAME,
                "products/*",
                uriCode
        );
    }

    @Override
    public boolean onCreate() {
        MyProductDatabaseHelper dbHelper = new MyProductDatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();
        return db != null;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        if (uriMatcher.match(uri) == uriCode) {
            queryBuilder.setProjectionMap(values);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder.equals("")) {
            sortOrder = id;
        }

        Cursor cursor =
                queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long rowID = db.insert(TABLE_NAME, "", contentValues);
        if (rowID > 0) {
            Uri tempUri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(tempUri, null);
            return tempUri;
        }

        //for pocket pos application
        if (contentValues != null) {
            String receivedData = contentValues.getAsString("key");
            Log.d("MyContentProvider", "Received Data: " + receivedData);
        }

        throw new SQLiteException("Failed to add a record into " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        if (uriMatcher.match(uri) == uriCode) {
            count = db.update(TABLE_NAME, contentValues, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count;
        if (uriMatcher.match(uri) == uriCode) {
            count = db.delete(TABLE_NAME, selection, selectionArgs);
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        if (uriMatcher.match(uri) == uriCode) {
            return "vnd.android.cursor.dir/products";
        } else {
            throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    /*
     * Database helper class
     * */
    private class MyProductDatabaseHelper extends SQLiteOpenHelper {
        //sql query to create a new table
        String CREATE_DB_TABLE =
                (" CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, price NUMBER NOT NULL);");

        public MyProductDatabaseHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(CREATE_DB_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}
