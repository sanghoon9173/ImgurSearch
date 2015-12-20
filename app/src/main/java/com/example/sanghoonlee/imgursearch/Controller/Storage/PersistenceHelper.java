package com.example.sanghoonlee.imgursearch.Controller.Storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sanghoonlee on 2015-12-11.
 */

public class PersistenceHelper extends SQLiteOpenHelper {
    private String  mDBName;
    private String  mDBCreate;

    public PersistenceHelper(Context context, String dbName, int dbVersion, String dbCreate) {
        super(context, dbName, null, dbVersion);
        mDBName     = dbName;
        mDBCreate   = dbCreate;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(mDBCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if existed
        db.execSQL("DROP TABLE IF EXISTS " + mDBName);

        // Create a new table
        onCreate(db);
    }
}