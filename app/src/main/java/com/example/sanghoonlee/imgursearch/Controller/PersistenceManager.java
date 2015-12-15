package com.example.sanghoonlee.imgursearch.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sanghoonlee on 2015-12-11.
 */

public class PersistenceManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Image_search_history";
    private static final String TABLE_NAME = "_Imgur_Image_search_history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "searchString";

    public PersistenceManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Category table create query
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_NAME + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void addSearchHistory(String searchString){
        if(!containsHistory(searchString)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, searchString);

            // Inserting Row
            //tableName, nullColumnHack, CotentValues
            db.insert(TABLE_NAME, null, values);
            // Closing database connection
            db.close();
        }
    }

    public List<String> getSearchHistory(){
        List<String> list = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        //selectQuery,selectedArguments
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //adding 2nd column data
                list.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
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
        SQLiteDatabase db = this.getReadableDatabase();
        //selectQuery,selectedArguments
        Cursor cursor = db.rawQuery(sb.toString(), null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //adding 2nd column data
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();
        return list;
    }

    public boolean containsHistory(String query){
        query = query.toLowerCase().trim();
        if(query.isEmpty()) {
            return true;
        }
        String selectQuery = "SELECT  "+COLUMN_NAME+" FROM " +
                TABLE_NAME+ " WHERE "+COLUMN_NAME+ " = '"+query+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        //selectQuery,selectedArguments
        Cursor cursor = db.rawQuery(selectQuery, null);
        boolean contains = cursor.getCount()>0;
        // closing connection
        cursor.close();
        db.close();
        return contains;
    }
}