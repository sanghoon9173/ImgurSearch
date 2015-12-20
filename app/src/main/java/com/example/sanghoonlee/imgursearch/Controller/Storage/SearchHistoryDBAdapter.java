package com.example.sanghoonlee.imgursearch.Controller.Storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanghoonlee on 2015-12-17.
 * use FTS3 Virtual Table for fast searches
 * trade off: search by words not by character
 */
public class SearchHistoryDBAdapter {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME   = "Image_search_history";
    private static final String TABLE_NAME      = "Imgur_Image_search_history";
    private static final String COLUMN_NAME     = "searchString";
    private static final String TABLE_CREATE    = "CREATE VIRTUAL TABLE " + TABLE_NAME +
                                                    " USING fts3(" + COLUMN_NAME +");";
    private static final String TAG             = "SearchHistoryDBAdapter";
    private PersistenceHelper mPersistenceHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;


    public SearchHistoryDBAdapter(Context context) {
        mContext = context;
    }

    public SearchHistoryDBAdapter open() throws SQLException {
        mPersistenceHelper = new PersistenceHelper(mContext, DATABASE_NAME, DATABASE_VERSION,
                                                        TABLE_CREATE);
        mDatabase = mPersistenceHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        //close the db
        if(mDatabase!=null) {
            mDatabase.close();
        }
        //close the dbHelper
        if (mPersistenceHelper != null) {
            mPersistenceHelper.close();
        }
    }

    public void addSearchHistory(String searchString){
        if(!containsHistory(searchString)) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, searchString);

            // Inserting Row
            mDatabase.insert(TABLE_NAME, null, values);
        }
    }

    public List<String> getSearchHistory(){
        List<String> list = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        //selectQuery,selectedArguments
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //adding 2nd column data
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // close connection
        cursor.close();
        return list;
    }

    public List<String> getSearchHistory(String query){
        query = query.toLowerCase().trim();
        List<String> list = new ArrayList<>();
        if(query.isEmpty()) {
            return getSearchHistory();
        }
        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ " WHERE " + TABLE_NAME+ " MATCH '"
                                + query +"';";
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //adding 2nd column data
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // close connection
        cursor.close();
        return list;
    }

    public boolean containsHistory(String query){
        query = query.toLowerCase().trim();
        if(query.isEmpty()) {
            return true;
        }
        String selectQuery = "SELECT  "+COLUMN_NAME+" FROM " +
                TABLE_NAME+ " WHERE "+COLUMN_NAME+ " = '" + query + "'";

        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        boolean contains = cursor.getCount()>0;

        // close connection
        cursor.close();
        return contains;
    }
}
