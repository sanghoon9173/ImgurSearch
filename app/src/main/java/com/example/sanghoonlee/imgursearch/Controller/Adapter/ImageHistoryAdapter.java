package com.example.sanghoonlee.imgursearch.Controller.Adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.sanghoonlee.imgursearch.Controller.Storage.PersistenceHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanghoonlee on 2016-01-18.
 */
public class ImageHistoryAdapter {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME   = "Image_history";
    private static final String TABLE_NAME      = "Imgur_Image_history";
    private static final String COLUMN_NAME     = "searchString";
    private static final String COLUMN_IMG_URL      = "img_url";
    private static final String COLUMN_ID       = "id";
    private static final int MAX_IMAGE_COUNT    = 10;
    private static final String TABLE_CREATE    = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT, " + COLUMN_IMG_URL + " TEXT)";
    private static final String TAG             = "SearchHistoryDBAdapter";
    private PersistenceHelper mPersistenceHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;


    public ImageHistoryAdapter(Context context) {
        mContext = context;
    }

    public ImageHistoryAdapter open() throws SQLException {
        mPersistenceHelper = new PersistenceHelper(mContext, DATABASE_NAME, DATABASE_VERSION,
                TABLE_CREATE);
        mDatabase = mPersistenceHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        //close db
        if(mDatabase!=null) {
            mDatabase.close();
        }
        //close dbHelper
        if (mPersistenceHelper != null) {
            mPersistenceHelper.close();
        }
    }

    public void addSearchHistory(String searchString, String url){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, searchString);
        values.put(COLUMN_IMG_URL, url);

        // Inserting Row
        mDatabase.insert(TABLE_NAME, null, values);
    }

    public List<String> getSearchHistory(String searchString){
        List<String> list = new ArrayList<>();
        StringBuffer sb = new StringBuffer("SELECT ").append(COLUMN_IMG_URL).append(" FROM ")
                .append(TABLE_NAME).append(" WHERE ").append(COLUMN_NAME).append(" = '")
                .append(searchString).append("';");

        Cursor cursor = mDatabase.rawQuery(sb.toString(), null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //adding 2nd column data
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // close cursor
        cursor.close();
        return list;
    }

}
