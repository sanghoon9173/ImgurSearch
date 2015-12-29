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
 * can change to use FTS3 Virtual Table for fast searches
 * trade off: search by words not by character
 *
 * check commit history
 */
public class SearchHistoryDBAdapter {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME   = "Image_search_history";
    private static final String TABLE_NAME      = "Imgur_Image_search_history";
    private static final String COLUMN_NAME     = "searchString";
    private static final String COLUMN_ID       = "id";
    private static final String TABLE_CREATE    = "CREATE TABLE " + TABLE_NAME + "("
                                    + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT)";
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
        //close db
        if(mDatabase!=null) {
            mDatabase.close();
        }
        //close dbHelper
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
        Cursor cursor = mDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //adding 2nd column data
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // close cursor
        cursor.close();
        return list;
    }

    public List<String> getSearchHistory(String query){
        query = query.toLowerCase().trim();
        List<String> list = new ArrayList<>();
        if(query.isEmpty()) {
            return getSearchHistory();
        }
        String[] queryStrings = query.split(" ");
        StringBuffer sb = new StringBuffer("SELECT  ").append(COLUMN_NAME).append(" FROM ")
                .append(TABLE_NAME).append(" WHERE ").append(COLUMN_NAME).append(" LIKE '%")
                .append(queryStrings[0]).append("%'");
        for (int i=1; i<queryStrings.length;i++) {
            sb.append(" AND ").append(COLUMN_NAME).append(" LIKE '%").append(queryStrings[i]).append("%'");
        }
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

    public boolean containsHistory(String query){
        query = query.toLowerCase().trim();
        if(query.isEmpty()) {
            return true;
        }
        String selectQuery = "SELECT  "+COLUMN_NAME+" FROM " +
                TABLE_NAME+ " WHERE "+COLUMN_NAME+ " = '" + query + "'";

        Cursor cursor = mDatabase.rawQuery(selectQuery, null);
        boolean contains = cursor.getCount()>0;

        // close cursor
        cursor.close();
        return contains;
    }

}
